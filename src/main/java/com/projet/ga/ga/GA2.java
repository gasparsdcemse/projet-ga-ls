package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

public class GA2 extends GeneticAlgorithm {
    public GA2(Instance ins){ super(ins); }

    @Override
    protected void crossover(Solution a, Solution b){
        int j = rnd.nextInt(ins.J);
        for (int t=0;t<ins.T;t++){
            double r = rnd.nextDouble();
            double pa = a.P[j][t], pb = b.P[j][t];
            a.P[j][t] = clamp(r*pa + (1-r)*pb, ins.pmin[j][t], ins.pmax[j][t]);
            b.P[j][t] = clamp((1-r)*pa + r*pb, ins.pmin[j][t], ins.pmax[j][t]);
        }
        // X inchangé (il sera réparé/évalué par evaluate)
    }
    private double clamp(double x,double lo,double hi){ return Math.max(lo, Math.min(hi, x)); }
}
