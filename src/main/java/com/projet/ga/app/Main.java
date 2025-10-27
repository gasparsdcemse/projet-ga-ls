package com.projet.ga.app;

import com.projet.ga.ga.*;
import com.projet.ga.io.DemoInstances;
import com.projet.ga.model.Instance;
import com.projet.ga.model.Solution;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;

public class Main {

    // Args : [algo] [seed] [maxIter] [pop] [pc] [pm] [pls]
    // Ex:    GA3   1234     40        70     0.9  0.2  0.2
    public static void main(String[] args) throws Exception {
        String algo = (args.length>0)? args[0].toUpperCase() : "GA3";
        long   seed = (args.length>1)? Long.parseLong(args[1]) : 1234L;
        int  maxIt  = (args.length>2)? Integer.parseInt(args[2]) : 40;
        int  pop    = (args.length>3)? Integer.parseInt(args[3]) : 70;
        double pc   = (args.length>4)? Double.parseDouble(args[4]) : 0.9;
        double pm   = (args.length>5)? Double.parseDouble(args[5]) : 0.2;
        double pls  = (args.length>6)? Double.parseDouble(args[6]) : 0.2;

        Instance ins = DemoInstances.demo();
        GeneticAlgorithm ga = switch (algo) {
            case "GA1" -> new GA1(ins);
            case "GA2" -> new GA2(ins);
            default    -> new GA3(ins);
        };
        ga.withPopulationSize(pop)
          .withPc(pc).withPm(pm).withPls(pls)
          .withMaxIter(maxIt).withMaxNoImprovement(Math.max(10, maxIt/2))
          .verbose(false).logEvery(10)
          .seed(seed);

        // Exécution
        long t0 = System.currentTimeMillis();
        Solution best = ga.run();
        long t1 = System.currentTimeMillis();

        // Console
        System.out.printf("%s | best=%,.3f | feasible=%s | time=%d ms%n",
                algo, best.fitness, best.isFeasible, (t1 - t0));

        // CSV
        Path out = Path.of("data", "results.csv");
        if (!Files.exists(out)) {
            Files.createDirectories(out.getParent());
            Files.writeString(out, "algo,seed,pop,pc,pm,pls,maxIter,bestFitness,feasible,timeMs\n");
        }
        try (FileWriter fw = new FileWriter(out.toFile(), true)) {
            fw.write(String.format("%s,%d,%d,%.3f,%.3f,%.3f,%d,%.6f,%s,%d%n",
                    algo, seed, pop, pc, pm, pls, maxIt, best.fitness, best.isFeasible, (t1 - t0)));
        }
        System.out.println("Résultats sauvés dans: " + out.toAbsolutePath());
    }
}
