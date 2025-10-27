package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

public class GA3 extends GeneticAlgorithm {
    public GA3(Instance ins){ super(ins); }

    @Override
    protected void crossover(Solution a, Solution b){
        // 1) swap production of one period t* for all products
        int t = rnd.nextInt(ins.T);
        for (int j=0;j<ins.J;j++){
            double tx = a.X[j][t]; a.X[j][t] = b.X[j][t]; b.X[j][t] = tx;
        }

        // 2) prices-only blend for one product j*
        int j = rnd.nextInt(ins.J);
        for (int tt=0; tt<ins.T; tt++){
            double r = rnd.nextDouble();
            double pa = a.P[j][tt], pb = b.P[j][tt];
            a.P[j][tt] = clamp(r*pa + (1-r)*pb, ins.pmin[j][tt], ins.pmax[j][tt]);
            b.P[j][tt] = clamp((1-r)*pa + r*pb, ins.pmin[j][tt], ins.pmax[j][tt]);
        }
    }
    private double clamp(double x,double lo,double hi){ return Math.max(lo, Math.min(hi, x)); }
}
