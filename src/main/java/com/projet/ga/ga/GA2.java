package com.projet.ga.ga;

import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

public class GA2 extends GeneticAlgorithm {
    public GA2(Instance ins){ super(ins); }

    @Override
    protected void crossover(Solution a, Solution b){
        // GA2 = single_point_crossover + prices_crossover (Table 3 )

        // 1. Applique l'opérateur "single point temporel"
        op_SinglePointTemporal(a, b);

        // 2. Applique l'opérateur "price blend"
        op_PriceBlend(a, b);
        
        // X sera inchangé par l'opérateur 2, mais sera réévalué par evaluate()
    }
    
    // 'clamp' est maintenant dans GeneticAlgorithm.java
}