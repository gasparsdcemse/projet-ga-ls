package com.projet.ga.model;

public class Objective {
    public static double profit(Instance ins, Solution s, double[][] D){
        double val = 0;
        for(int j=0;j<ins.J;j++){
            for(int t=0;t<ins.T;t++){
                val += s.P[j][t]*D[j][t]
                     - ins.c[j][t]*s.X[j][t]
                     - ins.h[j][t]*Math.max(0,s.I[j][t])
                     - ins.a[j][t]*s.Y[j][t];
            }
        }
        return val;
    }

    public static double fitness(Instance ins, Solution s, double[][] D){
        return profit(ins, s, D);
    }
}
