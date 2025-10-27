package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

public class GA1 extends GeneticAlgorithm {
    public GA1(Instance ins){ super(ins); }

    @Override
    protected void crossover(Solution a, Solution b){
        int cut = rnd.nextInt(ins.T);
        for (int j=0;j<ins.J;j++){
            for (int t=cut;t<ins.T;t++){
                // swap prices with clamp
                double tp = a.P[j][t]; a.P[j][t] = clamp(b.P[j][t], ins.pmin[j][t], ins.pmax[j][t]); 
                b.P[j][t] = clamp(tp,          ins.pmin[j][t], ins.pmax[j][t]);
                // swap production
                double tx = a.X[j][t]; a.X[j][t] = b.X[j][t]; b.X[j][t] = tx;
            }
        }
    }
    private double clamp(double x,double lo,double hi){ return Math.max(lo, Math.min(hi, x)); }
}
