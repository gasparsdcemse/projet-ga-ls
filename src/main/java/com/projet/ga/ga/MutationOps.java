package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

import java.util.Random;

public class MutationOps {

    // Mutation simple : petite variation de prix sur (j,t)
    public static void mutate(Instance ins, Solution s, Random rnd){
        int j = rnd.nextInt(ins.J);
        int t = rnd.nextInt(ins.T);
        double span = ins.pmax[j][t] - ins.pmin[j][t];
        double np = s.P[j][t] + (rnd.nextDouble() - 0.5) * 0.2 * span; // ±10% intervalle
        s.P[j][t] = Math.max(ins.pmin[j][t], Math.min(ins.pmax[j][t], np));
    }
}
