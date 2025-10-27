package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

public class GA3 extends GeneticAlgorithm {
    public GA3(Instance ins){ super(ins); }

    @Override
    protected void crossover(Solution a, Solution b){
        // GA3 = problem_specific_crossover + prices_crossover (Table 3 )
        
        // 1. Applique l'opérateur "problem specific" (partie swap production)
        op_ProblemSpecific_SwapProd(a, b);

        // 2. Applique l'opérateur "price blend"
        op_PriceBlend(a, b);
    }
    
    // 'clamp' est maintenant dans GeneticAlgorithm.java
}