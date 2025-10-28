-----

# Projet Java — Algorithmes génétiques avec recherche locale 🧬

**Sujet :** Joint Lot-Sizing and Dynamic Pricing Problem with Nonlinear Demand 💰
**Source :** *Matheuristics vs. Metaheuristics for Joint Lot-Sizing and Dynamic Pricing Problem with Nonlinear Demand*

-----

## 1. Objectif du projet

Implémenter **trois algorithmes génétiques (GA1, GA2, GA3)** avec **recherche locale (LS)** pour résoudre un problème de planification conjointe des prix et des lots de production avec demande non linéaire.

Chaque version d'AG est différenciée par la **combinaison d'opérateurs de croisement** utilisée, conformément à l'article (Table 3) :

| Algorithme | Combinaison d'Opérateurs de Croisement |
| :---: | :--- |
| **GA1** | **Single-point temporel** (échange fin d'horizon) + **Problem-specific** (échange production 1 période) |
| **GA2** | **Single-point temporel** (échange fin d'horizon) + **Prices-crossover** (mélange prix 1 produit) |
| **GA3** | **Problem-specific** (échange production 1 période) + **Prices-crossover** (mélange prix 1 produit) |

Une **recherche locale hybride (VND - Variable Neighborhood Descent)** améliore les solutions via deux voisinages :

  * `LS_product` : ajustement progressif de tous les prix d’un produit.
  * `LS_period` : ajustement progressif de tous les prix d’une période.

-----

## 2. Conception (Diagramme de Classes)

Voici l'architecture logicielle du projet, montrant les relations entre les classes principales :

```mermaid
classDiagram
    direction LR

    class Main {
        +main(String[] args)
    }

    class GeneticAlgorithm {
        <<abstract>>
        #Instance ins
        #List~Solution~ population
        +run() : Solution
        #initPopulation()
        #evaluate(Solution)
        #crossover(Solution, Solution)*
        #op_SinglePointTemporal(Solution, Solution)
        #op_PriceBlend(Solution, Solution)
        #op_ProblemSpecific_SwapProd(Solution, Solution)
    }

    class GA1 {
        +crossover(Solution, Solution)
    }
    class GA2 {
        +crossover(Solution, Solution)
    }
    class GA3 {
        +crossover(Solution, Solution)
    }

    class Instance {
        +int J, T
        +double[] capacity
        +double[][] pmin, pmax
        +double[][] c, h, a, b, gamma
    }
    class Solution {
        +double[][] P, X, I
        +int[][] Y
        +double fitness
        +clone() : Solution
        +emptyLike(Instance) : Solution
    }

    class LocalSearch {
        <<static>>
        +apply(Instance, Solution, Random)
        +vndOnBest(Instance, Solution, Random)
        -lsProduct(Instance, Solution)
        -lsPeriod(Instance, Solution)
    }
    
    class ModelUtils {
        <<Utility Classes>>
        +Objective.fitness(...)
        +Demand.allDemands(...)
        +Feasibility.recomputeInventory(...)
    }

    Main ..> GeneticAlgorithm : crée et exécute
    Main ..> DemoInstances : obtient
    DemoInstances ..> Instance : crée

    GeneticAlgorithm <|-- GA1
    GeneticAlgorithm <|-- GA2
    GeneticAlgorithm <|-- GA3

    GeneticAlgorithm o-- "1" Instance : détient
    GeneticAlgorithm o-- "*" Solution : "population"
    
    GeneticAlgorithm ..> LocalSearch : utilise
    GeneticAlgorithm ..> ModelUtils : utilise

    LocalSearch ..> ModelUtils : utilise
    LocalSearch ..> Instance : utilise
    LocalSearch ..> Solution : modifie
    
    ModelUtils ..> Instance : dépend de
    ModelUtils ..> Solution : dépend de
````

-----

## 3\. Structure du projet

Le projet est organisé comme suit :

```
projet-ga-ls/
├── src/
│ └── main/java/com/projet/ga/
│ ├── app/
│ │ └── Main.java → Point d’entrée de l'application
│ ├── ga/
│ │ ├── GA1.java → Implémentation de l'AG 1
│ │ ├── GA2.java → Implémentation de l'AG 2
│ │ ├── GA3.java → Implémentation de l'AG 3
│ │ ├── GeneticAlgorithm.java → Classe mère de l'AG
│ │ ├── LocalSearch.java → Logique de la Recherche Locale (VND)
│ │ └── MutationOps.java → Mutation simple sur les prix
│ ├── io/
│ │ └── DemoInstances.java → Génération d'une instance de test
│ ├── model/ → Classes du modèle (Instance, Solution, etc.)
│ │ ├── Demand.java
│ │ ├── Feasibility.java
│ │ ├── Instance.java
│ │ ├── Objective.java
│ │ └── Solution.java
│ └── util/
│ └── RandomUtils.java
├── data/
│ ├── instance_demo.json → Fichier d'instance de démonstration
│ └── results.csv ← Fichier de sortie automatique des résultats
└── README.md ← Ce document
```

-----

## 4. Compilation et exécution (PowerShell)

### 1. Compilation

Depuis le dossier racine `projet-ga-ls` :

```powershell
# Suppression et création du dossier de sortie 'bin'
Remove-Item -Recurse -Force .\bin -ErrorAction SilentlyContinue
mkdir bin | Out-Null

# Liste des fichiers à exclure de la compilation (facultatif, à adapter)
$exclude = @('InstanceReader.java','Selection.java','AdaptiveWeights.java','ModelTests.java')

# Récupération de tous les fichiers .java à compiler
$files = Get-ChildItem -Recurse -Path src\main\java -Filter *.java |
  	 	 	Where-Object { $exclude -notcontains $_.Name } |
  	 	 	ForEach-Object { $_.FullName }

# Compilation des fichiers
javac -encoding UTF-8 -d bin $files
```

### 2. Exécution

La commande générale d'exécution est :

```powershell
java -cp bin com.projet.ga.app.Main [algo] [seed] [maxIter] [pop] [pc] [pm] [pls]
```

| Argument | Description | Exemple de valeur |
| :--- | :--- | :--- |
| `[algo]` | Algorithme à exécuter (**GA1, GA2** ou **GA3**) | `GA3` |
| `[seed]` | Graine pour la reproductibilité | `1234` |
| `[maxIter]` | Nombre maximum d'itérations | `40` |
| `[pop]` | Taille de la population | `70` |
| `[pc]` | Probabilité de croisement (crossover) | `0.9` |
| `[pm]` | Probabilité de mutation | `0.2` |
| `[pls]` | Probabilité d'appliquer la recherche locale | `0.2` |

**Exemples d'exécution :**

```powershell
java -cp bin com.projet.ga.app.Main GA3 1234 40 70 0.9 0.2 0.2
java -cp bin com.projet.ga.app.Main GA2 1234 40 70 0.9 0.2 0.2
java -cp bin com.projet.ga.app.Main GA1 1234 40 70 0.9 0.2 0.2
```

-----

## 5. Résultats

### Affichage Console

À chaque exécution, le programme affichera un résumé dans la console :

```
GA3 | best=12345.678 | feasible=true | time=1253 ms
```

### Fichier de sortie

Les résultats détaillés sont automatiquement ajoutés dans **`data/results.csv`** :

```csv
algo,seed,pop,pc,pm,pls,maxIter,bestFitness,feasible,timeMs
GA1,1234,70,0.9,0.2,0.2,40,11543.233,true,1082
GA2,1234,70,0.9,0.2,0.2,40,11988.674,true,1103
GA3,1234,70,0.9,0.2,0.2,40,12345.821,true,1120
```

-----

## 6. Notes méthodologiques


| Concept | Détails |
| :--- | :--- |
| **Encodage** | Codage $X\_P$ (chromosomes contenant les variables de production et de prix). |
| **Initialisation** | Prix tirés aléatoirement dans $[P_{\min}, P_{\max}]$ ; production = demande initiale. |
| **Fitness** | **Profit total** (Recettes – Coûts de production – Coûts de *setup* – Coûts de *stock*). |
| **Mutation** | Perturbation $\pm 10\ \%$ sur un prix spécifique ($P[j][t]$). |
| **Recherche locale** | **VND (Variable Neighborhood Descent)** combinant les voisinages `LS_product` et `LS_period`. |

### Paramètres par défaut

| Paramètre | Valeur par défaut |
| :--- | :---: |
| Population | $70$ |
| $P_c$ (Probabilité de croisement) | $0.9$ |
| $P_m$ (Probabilité de mutation) | $0.2$ |
| $P_{ls}$ (Probabilité d'appliquer la recherche locale) | $0.2$ |
| Max iterations | $40$ |

-----


## 7. Validation et Tests

Pour peupler le fichier `results.csv` avec plusieurs exécutions (pour une analyse statistique des performances) :

```powershell
foreach ($s in 1,2,3,4,5) {
  java -cp bin com.projet.ga.app.Main GA1 $s 40 70 0.9 0.2 0.2
  java -cp bin com.projet.ga.app.Main GA2 $s 40 70 0.9 0.2 0.2
  java -cp bin com.projet.ga.app.Main GA3 $s 40 70 0.9 0.2 0.2
}
```

Les données du fichier `results.csv` peuvent ensuite être analysées (par exemple, dans **Excel**) pour comparer :

  * La moyenne des fitness.
  * La variabilité des résultats.
  * Le temps d'exécution moyen.

-----


## 8. Auteurs / Encadrement

**Projet Java** — Implémentation des 3 Algorithmes Génétiques avec Recherche Locale

  * **Encadré par** : Pierre UNY
  * **Développé par** : Gaspar SAUTY DE CHALON & Ivan LARREA

<!-- end list -->

```
```
