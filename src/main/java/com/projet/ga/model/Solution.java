package com.projet.ga.model;

public class Solution implements Cloneable {
    public double[][] P, X, I;
    public int[][] Y;
    public double fitness;
    public boolean isFeasible;

    public Solution(int J, int T) {
        P = new double[J][T];
        X = new double[J][T];
        I = new double[J][T];
        Y = new int[J][T];
    }

    @Override public Solution clone() {
        Solution s = new Solution(P.length, P[0].length);
        for (int j=0;j<P.length;j++) { s.P[j]=P[j].clone(); s.X[j]=X[j].clone(); s.I[j]=I[j].clone(); }
        for (int j=0;j<Y.length;j++) s.Y[j]=Y[j].clone();
        s.fitness = fitness; s.isFeasible = isFeasible;
        return s;
    }

    public static Solution emptyLike(com.projet.ga.model.Instance ins){
        return new Solution(ins.J, ins.T);
    }
}
