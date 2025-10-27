package com.projet.ga.model;

public class Demand {
    public static double djt(Instance ins, double[][] P, int j, int t){
        double prod = ins.gamma[j][t];
        for(int i=0;i<ins.J;i++)
            prod *= Math.pow(P[i][t], ins.b[j][i]);
        return Math.max(0, prod);
    }

    public static double[][] allDemands(Instance ins, double[][] P){
        double[][] D = new double[ins.J][ins.T];
        for(int j=0;j<ins.J;j++)
            for(int t=0;t<ins.T;t++)
                D[j][t] = djt(ins, P, j, t);
        return D;
    }
}
