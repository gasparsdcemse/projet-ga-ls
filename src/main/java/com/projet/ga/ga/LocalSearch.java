package com.projet.ga.ga;

import com.projet.ga.model.*;
import java.util.Random;

public class LocalSearch {

    // ---- Paramètres LS ----
    private static final int    MAX_NO_IMPROVE   = 30;   // arrête si pas d'amélioration
    private static final double STEP_FRAC        = 0.10; // pas = 10% de l’intervalle [pmin,pmax]
    private static final double STEP_DECAY       = 0.5;  // rétrécit le pas si pas d’amélioration

    /* ====================== API ====================== */

    // Petite LS utilisée sur les enfants (déjà en place)
    public static void apply(Instance ins, Solution s, Random rnd){
        // micro-perturbation locale (garde si améliore)
        int j = rnd.nextInt(ins.J);
        int t = rnd.nextInt(ins.T);
        double span = ins.pmax[j][t] - ins.pmin[j][t];
        double old = s.P[j][t];
        double np  = clamp(old + (rnd.nextDouble()-0.5)*STEP_FRAC*span, ins.pmin[j][t], ins.pmax[j][t]);

        double f0 = evaluate(ins, s);
        s.P[j][t] = np;
        double f1 = evaluate(ins, s);

        if (f1 < f0) { // revert si pire
            s.P[j][t] = old;
            s.fitness = f0;
        } else {
            s.fitness = f1;
        }
    }

    // VND sur le meilleur
    public static void vndOnBest(Instance ins, Solution s, Random rnd){
        double bestF = evaluate(ins, s);
        boolean improved = true;

        while (improved){
            improved = false;

            // 1) LS_product : bouge toute la trajectoire de prix d’un produit j*
            if (lsProduct(ins, s)) {
                bestF = s.fitness;
                improved = true;
            }

            // 2) LS_period : bouge tous les prix pour une période t*
            if (lsPeriod(ins, s)) {
                bestF = s.fitness;
                improved = true;
            }
        }
        s.fitness = bestF;
    }

    /* ================== Neighborhoods ================== */

    // Déplace toute la série P[j][0..T-1] (un produit à la fois), pas décroissant
    private static boolean lsProduct(Instance ins, Solution s){
        boolean anyImprove = false;
        for (int j = 0; j < ins.J; j++){
            double step = avgSpanProduct(ins, j) * STEP_FRAC;
            int noImp = 0;
            while (noImp < MAX_NO_IMPROVE && step > 1e-9){
                boolean improved = false;

                // Essaye +step sur toutes les périodes du produit j
                double f0 = s.fitness >= -1e308 ? s.fitness : evaluate(ins, s);
                double[] old = s.P[j].clone();
                addToProduct(ins, s, j, +step);
                double fPlus = evaluate(ins, s);

                if (fPlus > f0){
                    s.fitness = fPlus;
                    improved = true;
                } else {
                    // Essaye -step
                    System.arraycopy(old, 0, s.P[j], 0, old.length);
                    addToProduct(ins, s, j, -step);
                    double fMinus = evaluate(ins, s);
                    if (fMinus > f0){
                        s.fitness = fMinus;
                        improved = true;
                    } else {
                        // revert
                        System.arraycopy(old, 0, s.P[j], 0, old.length);
                        s.fitness = f0;
                    }
                }

                if (improved){
                    anyImprove = true;
                    noImp = 0; // on garde le même pas tant que ça améliore
                } else {
                    noImp++;
                    step *= STEP_DECAY; // rétrécit le pas si rien n’améliore
                }
            }
        }
        return anyImprove;
    }

    // Déplace tous les P[0..J-1][t] (une période à la fois), pas décroissant
    private static boolean lsPeriod(Instance ins, Solution s){
        boolean anyImprove = false;
        for (int t = 0; t < ins.T; t++){
            double step = avgSpanPeriod(ins, t) * STEP_FRAC;
            int noImp = 0;
            while (noImp < MAX_NO_IMPROVE && step > 1e-9){
                boolean improved = false;

                double f0 = s.fitness >= -1e308 ? s.fitness : evaluate(ins, s);
                double[] oldCol = new double[ins.J];
                for (int j=0;j<ins.J;j++) oldCol[j] = s.P[j][t];

                // +step
                addToPeriod(ins, s, t, +step);
                double fPlus = evaluate(ins, s);

                if (fPlus > f0){
                    s.fitness = fPlus;
                    improved = true;
                } else {
                    // -step
                    for (int j=0;j<ins.J;j++) s.P[j][t] = oldCol[j];
                    addToPeriod(ins, s, t, -step);
                    double fMinus = evaluate(ins, s);
                    if (fMinus > f0){
                        s.fitness = fMinus;
                        improved = true;
                    } else {
                        // revert
                        for (int j=0;j<ins.J;j++) s.P[j][t] = oldCol[j];
                        s.fitness = f0;
                    }
                }

                if (improved){
                    anyImprove = true;
                    noImp = 0;
                } else {
                    noImp++;
                    step *= STEP_DECAY;
                }
            }
        }
        return anyImprove;
    }

    /* ================== Helpers ================== */

    private static double evaluate(Instance ins, Solution s){
        double[][] D = Demand.allDemands(ins, s.P);
        Feasibility.recomputeInventory(ins, s, D);
        double f = Objective.fitness(ins, s, D);
        s.fitness = f;
        return f;
    }

    private static double clamp(double x, double lo, double hi){
        return Math.max(lo, Math.min(hi, x));
    }

    private static void addToProduct(Instance ins, Solution s, int j, double step){
        for (int t=0; t<ins.T; t++)
            s.P[j][t] = clamp(s.P[j][t] + step, ins.pmin[j][t], ins.pmax[j][t]);
    }

    private static void addToPeriod(Instance ins, Solution s, int t, double step){
        for (int j=0; j<ins.J; j++)
            s.P[j][t] = clamp(s.P[j][t] + step, ins.pmin[j][t], ins.pmax[j][t]);
    }

    private static double avgSpanProduct(Instance ins, int j){
        double sum=0;
        for (int t=0;t<ins.T;t++) sum += (ins.pmax[j][t]-ins.pmin[j][t]);
        return sum/Math.max(1, ins.T);
    }

    private static double avgSpanPeriod(Instance ins, int t){
        double sum=0;
        for (int j=0;j<ins.J;j++) sum += (ins.pmax[j][t]-ins.pmin[j][t]);
        return sum/Math.max(1, ins.J);
    }
}
