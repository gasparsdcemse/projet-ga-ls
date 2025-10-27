package com.projet.ga.io;

import com.projet.ga.model.Instance;

public class DemoInstances {
    public static Instance demo() {
        int J = 2, T = 6;
        double[] capacity = {100,100,100,100,100,100};
        double[] v = {1.0, 1.0};
        double[][] c = {
            {3,3,3,3,3,3},
            {4,4,4,4,4,4}
        };
        double[][] h = {
            {0.5,0.5,0.5,0.5,0.5,0.5},
            {0.6,0.6,0.6,0.6,0.6,0.6}
        };
        double[][] a = {
            {20,20,20,20,20,20},
            {25,25,25,25,25,25}
        };
        double[][] pmin = {
            {5,5,5,5,5,5},
            {5,5,5,5,5,5}
        };
        double[][] pmax = {
            {15,15,15,15,15,15},
            {16,16,16,16,16,16}
        };
        double[][] gamma = {
            {200,180,160,140,120,100},
            {160,160,160,160,160,160}
        };
        double[][] b = {
            {-1.2, 0.2},
            {0.1, -1.1}
        };
        return new Instance(J, T, capacity, v, c, h, a, pmin, pmax, gamma, b);
    }
}