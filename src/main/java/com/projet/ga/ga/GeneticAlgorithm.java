package com.projet.ga.ga;

import com.projet.ga.model.*;
import com.projet.ga.util.RandomUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class GeneticAlgorithm {
    protected final Instance ins;

    protected int popSize = 30;
    protected int maxIter = 40;
    protected int maxNoImp = 20;
    protected double pc = 0.9;
    protected double pm = 0.2;
    protected double pls = 0.2;
    protected boolean verbose = true;
    protected int logEvery = 5; // afficher 1 ligne toutes les 5 itérations
    protected long seed = 1234L;
    protected Random rnd = new Random(seed);

    protected List<Solution> population = new ArrayList<>();

    public GeneticAlgorithm(Instance ins){ this.ins = ins; }

    public GeneticAlgorithm withPopulationSize(int n){ this.popSize = n; return this; }
    public GeneticAlgorithm withPc(double x){ this.pc = x; return this; }
    public GeneticAlgorithm withPm(double x){ this.pm = x; return this; }
    public GeneticAlgorithm withPls(double x){ this.pls = x; return this; }
    public GeneticAlgorithm withMaxIter(int x){ this.maxIter = x; return this; }
    public GeneticAlgorithm withMaxNoImprovement(int x){ this.maxNoImp = x; return this; }
    public GeneticAlgorithm seed(long s){ this.seed = s; this.rnd = new Random(s); return this; }
    public GeneticAlgorithm verbose(boolean v){ this.verbose = v; return this; }
    public GeneticAlgorithm logEvery(int k){ this.logEvery = Math.max(1,k); return this; }

    public Solution run(){
        initPopulation();
        Solution best = copyOf(population.get(0));
        int noImp = 0;

        for(int it=0; it<maxIter && noImp<maxNoImp; it++){
            List<Solution> parents = elitismAndRoulette(population, popSize);

            List<Solution> offspring = new ArrayList<>();
            for(int k=0; k<popSize/2; k++){
                Solution p1 = parents.get(rnd.nextInt(parents.size()));
                Solution p2 = parents.get(rnd.nextInt(parents.size()));
                Solution c1 = copyOf(p1);
                Solution c2 = copyOf(p2);

                if (rnd.nextDouble() < pc) crossover(c1, c2);

                if (rnd.nextDouble() < pls) LocalSearch.apply(ins, c1, rnd);
                if (rnd.nextDouble() < pls) LocalSearch.apply(ins, c2, rnd);

                evaluate(c1);
                evaluate(c2);
                offspring.add(c1);
                offspring.add(c2);
            }

            // mu + lambda : on mélange et on garde les meilleurs
            List<Solution> pool = new ArrayList<>(population.size()+offspring.size());
            pool.addAll(population);
            pool.addAll(offspring);
            pool.sort(Comparator.comparingDouble(s -> -s.fitness));
            population = new ArrayList<>(pool.subList(0, Math.min(popSize, pool.size())));

            if (population.get(0).fitness > best.fitness) { best = copyOf(population.get(0)); noImp = 0; }
            else noImp++;

            LocalSearch.vndOnBest(ins, best, rnd);
            evaluate(best);

            if (verbose && (it % logEvery == 0 || it == maxIter-1)) {
                System.out.printf("Iter %d | best=%.3f%n", it, best.fitness);
            }
        }
        return best;
    }

    /* ================= Helpers ================= */

    protected void initPopulation(){
        population.clear();
        for (int i=0;i<popSize;i++){
            Solution s = Solution.emptyLike(ins);

            // prix aléatoires
            for (int j=0;j<ins.J;j++)
                for (int t=0;t<ins.T;t++)
                    s.P[j][t] = RandomUtils.uniform(rnd, ins.pmin[j][t], ins.pmax[j][t]);

            // prod = demande
            double[][] D = Demand.allDemands(ins, s.P);
            for (int j=0;j<ins.J;j++)
                for (int t=0;t<ins.T;t++)
                    s.X[j][t] = D[j][t];

            Feasibility.recomputeInventory(ins, s, D);
            s.fitness = Objective.fitness(ins, s, D);
            s.isFeasible = true;

            population.add(s);
        }
        population.sort(Comparator.comparingDouble(x -> -x.fitness));
    }

    protected void evaluate(Solution s){
        double[][] D = Demand.allDemands(ins, s.P);
        Feasibility.recomputeInventory(ins, s, D);
        s.fitness = Objective.fitness(ins, s, D);
        s.isFeasible = true;
    }

    private Solution copyOf(Solution s){ return s.clone(); }

    private List<Solution> elitismAndRoulette(List<Solution> pop, int size){
        List<Solution> sorted = new ArrayList<>(pop);
        sorted.sort(Comparator.comparingDouble(s -> -s.fitness));

        int elite = Math.max(2, size/10);
        List<Solution> parents = new ArrayList<>(size);

        // élite
        for (int i=0;i<elite;i++) parents.add(copyOf(sorted.get(i)));

        // roulette par rang
        double[] probs = new double[sorted.size()];
        double sum = 0;
        for (int i=0;i<sorted.size();i++){ probs[i] = (sorted.size()-i); sum += probs[i]; }

        for (int k=elite; k<size; k++){
            double r = rnd.nextDouble()*sum, acc=0;
            int pick = sorted.size()-1;
            for(int i=0;i<sorted.size();i++){ acc+=probs[i]; if(acc>=r){ pick=i; break; } }
            parents.add(copyOf(sorted.get(pick)));
        }
        return parents;
    }

    // Sous-classes (GA1/GA2/GA3) peuvent surcharger
    protected void crossover(Solution c1, Solution c2) { /* no-op par défaut */ }

    // --- Opérateurs de croisement de base (ajoutés) ---

    /**
     * Opérateur 1: Single-point temporel (de GA1)
     * Échange P et X à partir d'une période "cut" aléatoire.
     * Correspond au 'single_point_crossover' de l'article (Fig. 3, gauche [cite: 527]).
     */
    protected void op_SinglePointTemporal(Solution a, Solution b){
        int cut = rnd.nextInt(ins.T);
        for (int j=0;j<ins.J;j++){
            for (int t=cut;t<ins.T;t++){
                // swap prices with clamp
                double tp = a.P[j][t];
                a.P[j][t] = clamp(b.P[j][t], ins.pmin[j][t], ins.pmax[j][t]); 
                b.P[j][t] = clamp(tp,          ins.pmin[j][t], ins.pmax[j][t]);
                // swap production
                double tx = a.X[j][t];
                a.X[j][t] = b.X[j][t];
                b.X[j][t] = tx;
            }
        }
    }

    /**
     * Opérateur 2: Mélange de prix pour un produit (de GA2)
     * Mélange convexe (blend) des prix pour un produit j* aléatoire.
     * Correspond au 'prices_crossover' de l'article (Eq. 18 [cite: 648]).
     */
    protected void op_PriceBlend(Solution a, Solution b){
        int j = rnd.nextInt(ins.J);
        for (int t=0; t<ins.T; t++){
            double r = rnd.nextDouble();
            double pa = a.P[j][t], pb = b.P[j][t];
            a.P[j][t] = clamp(r*pa + (1-r)*pb, ins.pmin[j][t], ins.pmax[j][t]);
            b.P[j][t] = clamp((1-r)*pa + r*pb, ins.pmin[j][t], ins.pmax[j][t]);
        }
    }

    /**
     * Opérateur 3: Échange de production pour une période (de GA3)
     * Échange les productions X pour une période t* aléatoire.
     * Correspond à la partie "production" du 'problem_specific_crossover' de l'article (Fig. 3, droite [cite: 527, 566]).
     */
    protected void op_ProblemSpecific_SwapProd(Solution a, Solution b){
        int t = rnd.nextInt(ins.T);
        for (int j=0;j<ins.J;j++){
            double tx = a.X[j][t];
            a.X[j][t] = b.X[j][t];
            b.X[j][t] = tx;
        }
    }

    /**
     * Utilitaire pour borner une valeur (ajouté)
     */
    protected double clamp(double x,double lo,double hi){
        return Math.max(lo, Math.min(hi, x));
    }
}