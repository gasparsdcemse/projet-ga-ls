package com.projet.ga.model;

public class Feasibility {
    public static void recomputeInventory(Instance ins, Solution s, double[][] D){
        for(int j=0;j<ins.J;j++){
            double prevI = 0;
            for(int t=0;t<ins.T;t++){
                s.Y[j][t] = (s.X[j][t] > 0) ? 1 : 0;
                s.I[j][t] = prevI + s.X[j][t] - D[j][t];
                prevI = s.I[j][t];
            }
        }
    }
}
