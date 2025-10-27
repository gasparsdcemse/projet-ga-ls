package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

public class GA1 extends GeneticAlgorithm {
    public GA1(Instance ins){ super(ins); }

    @Override
    protected void crossover(Solution a, Solution b){
        // GA1 = single_point_crossover + problem_specific_crossover (Table 3 )
        
        // 1. Applique l'opérateur "single point temporel"
        op_SinglePointTemporal(a, b);
        
        // 2. Applique l'opérateur "problem specific" (partie swap production)
        op_ProblemSpecific_SwapProd(a, b);
    }
    
    // 'clamp' est maintenant dans GeneticAlgorithm.java
}