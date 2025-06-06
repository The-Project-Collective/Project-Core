package com.collective.projectcore.entities.genetics;

import com.collective.projectcore.util.UtilMethods;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * An interface for the calculation and application of genetics for entities that require them.
 * First, instantiate this class in each creature class that requires it.
 * Then, for each gene, create a record in your entity class which implements...
 * @see Gene
 * Then fill in the details for the record by implementing the required methods.
 * Use AmericanRedFoxEntity in Project Wildlife as an example.
 */
public interface GeneticContext {

    /**
     * Contains all entity-specific possible gene records.
     */
    List<Gene> genes();

    Random random = new Random();
    String stopCodon = "Z";

    /**
     * @return exact number of possible genes.
     */
    default int haploidLengthMin() {
        return genes().size();
    }

    /**
     * @return exact number of possible genes, plus two stop codons.
     */
    default int haploidLengthMax() {
        return haploidLengthMin() + 2;
    }

    /**
     * Calculates a complete genome based on the genes supplied in genes().
     *
     * @param wildOnly controls if the gene selection is limited to wild alleles, or completely random.
     * @return a complete genome.
     */
    default String setRandomGenes(boolean wildOnly) {
        String haploid1 = "";
        StringBuilder builder1 = new StringBuilder();
        for (Gene gene : genes()) {
            List<String> usableAlleles = gene.alleles();
            if (wildOnly) {
                 usableAlleles = gene.wildAlleles();
            }
            haploid1 = builder1.append(usableAlleles.get(random.nextInt(usableAlleles.size()))).toString();
        }
        String haploid2 = "";
        StringBuilder builder2 = new StringBuilder();
        for (Gene gene : genes()) {
            List<String> usableAlleles = gene.alleles();
            if (wildOnly) {
                usableAlleles = gene.wildAlleles();
            }
            haploid2 = builder2.append(usableAlleles.get(random.nextInt(usableAlleles.size()))).toString();
        }
        return stopCodon + haploid1 + stopCodon + haploid2;
    }

    /**
     * Calculates the alleles for a specific gene index in the genome.
     *
     * @param genome the entity specific genome.
     * @param index the index of the specific gene in the genome.
     * @return the required allele from both haploids.
     */
    default String getAlleles(String genome, int index) {
        String alleles;
        alleles = String.valueOf(genome.charAt(index + 1)) + genome.charAt(index + haploidLengthMax());
        return UtilMethods.sortStringUppercase(alleles);
    }

    /**
     * Calculates the genome of the offspring based on the genetics of both parents.
     *
     * @param parent1 the genome of the first parent.
     * @param parent2 the genome of the second parent.
     * @return the genome of the resulting offspring.
     */
    default String calculateGenes(String parent1, String parent2) {
        if (parent1 == null || parent2 == null) {
            parent1 = setRandomGenes(true);
            parent2 = setRandomGenes(true);
        }
        String haploid1 = "";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < haploidLengthMin(); i++) {
            char allele;
            if (shouldMutate()) {
                allele = mutateGenes(i);
            } else {
                String temp = "" + parent1.charAt(i + 1) + parent1.charAt(i + haploidLengthMax());
                allele = temp.charAt(random.nextInt(temp.length()));
            }
            haploid1 = builder.append(allele).toString();
        }
        String haploid2 = "";
        StringBuilder builder1 = new StringBuilder();
        for (int i = 0; i < haploidLengthMin(); i++) {
            char allele;
            if (shouldMutate()) {
                allele = mutateGenes(i);
            } else {
                String temp = "" + parent2.charAt(i + 1) + parent2.charAt(i + haploidLengthMax());
                allele = temp.charAt(random.nextInt(temp.length()));
            }
            haploid2 = builder1.append(allele).toString();
        }
        return stopCodon + haploid1 + stopCodon + haploid2;
    }

    /**
     * Determines if an allele should mutate based on a 1/300 or 0.3% chance.
     *
     * @return whether the allele should mutate or not.
     */
    default boolean shouldMutate() {
        return random.nextInt(300) == 0;
    }

    /**
     * Mutates a specific allele using the whole available allele range for that gene.
     *
     * @param index the index of the gene.
     * @return a randomly chosen allele from the list of alleles available for that gene index.
     */
    default char mutateGenes(int index) {
        return genes().get(index).alleles().get(random.nextInt(genes().get(index).alleles().size())).charAt(0);
    }

    /**
     * Determines whether a specific gene has homozygous alleles.
     *
     * @param genome the entity specific genome.
     * @param index the index of the desired gene.
     * @return whether the alleles for both haploids are homozygous.
     */
    default boolean isHomozygous(String genome, int index) {
        String s1a = genome.substring(0, (genome.length()/2));
        String s1b = genome.substring((genome.length()/2));
        char allele1 = s1a.charAt(index);
        char allele2 = s1b.charAt(index);
        return allele1 == allele2;
    }

    /**
     * Checks whether a gene at a certain index on the genome (combination of both alleles) is homozygous lethal.
     *
     * @param genome of the entity.
     * @return whether the allele combination for that entity is lethal or not.
     */
    default boolean isHomozygousLethal(String genome) {
        for (Gene gene : genes()) {
            if (gene.homozygousLethal()) {
                List<String> lethalGenes = gene.lethalGenes();
                int geneIndex = genes().indexOf(gene);
                for (String genes : lethalGenes) {
                    StringBuilder builder = new StringBuilder();
                    builder.append(genes);
                    String genesR = builder.reverse().toString();
                    if (this.getAlleles(genome, geneIndex).equals(genes) || this.getAlleles(genome, geneIndex).equals(genesR)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Adjusts the value of an entity stat based on that entity's genetics.
     *
     * @param statValue of that entity's specific stat.
     * @param genome of the entity.
     * @param geneIndex of the stat gene for that specific stat.
     * @param coefficient of the stat change. E.g. each allele increases / decreases the stat by 0.1, or 0.2, etc.
     * @return the adjusted stat value.
     */
    default double calculateStats(double statValue, String genome, int geneIndex, float coefficient) {
        String alleles = this.getAlleles(genome, geneIndex);
        List<String> alleleList = List.of(alleles.substring(0, 0), alleles.substring(1, 1));
        List<Float> coeffList = new ArrayList<>();
        for (String allele : alleleList) {
            float coeff = switch (allele) {
                case "A" -> 1 + (coefficient * 4);
                case "B" -> 1 + (coefficient * 3);
                case "C" -> 1 + (coefficient * 2);
                case "D" -> 1 + coefficient;
                case "d" -> 1 - coefficient;
                case "c" -> 1 - (coefficient * 2);
                case "b" -> 1 - (coefficient * 3);
                case "a" -> 1 - (coefficient * 4);
                default -> 1;
            };
            coeffList.add(coeff);
        }
        if (coeffList.size() == 2) {
            return statValue * ((coeffList.getFirst() + coeffList.get(1)) / 2);
        } else {
            return statValue;
        }
    }

    /**
     * Implemented by gene records created in specific entity classes to be used by <code>GeneticContext</code>.
     * VERY IMPORTANT that they are added to the genes() list in the same order they should appear in the genome!!!
     * All alleles within a gene are case-sensitive - Uppercase are DOMINANT, lowercase are RECESSIVE.
     */
    interface Gene {
        String name();
        List<String> alleles();
        List<String> wildAlleles();
        boolean dominant();
        boolean partialDominant();
        boolean homozygousLethal();
        List<String> lethalGenes();
    }
}
