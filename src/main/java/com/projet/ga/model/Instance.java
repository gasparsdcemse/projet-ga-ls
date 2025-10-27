package com.projet.ga.model;

public class Instance {
    public int J, T;
    public double[] capacity, v;
    public double[][] c, h, a, pmin, pmax, gamma, b;

    public Instance(int J, int T, double[] capacity, double[] v,
                    double[][] c, double[][] h, double[][] a,
                    double[][] pmin, double[][] pmax, double[][] gamma, double[][] b) {
        this.J = J; this.T = T;
        this.capacity = capacity; this.v = v;
        this.c = c; this.h = h; this.a = a;
        this.pmin = pmin; this.pmax = pmax; this.gamma = gamma; this.b = b;
    }
}
