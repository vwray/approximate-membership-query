package cmsc701.amq.task2;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.profiler.Profiler;

import it.unimi.dsi.bits.TransformationStrategies;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;

/**
 * A class to evaluate a minimal perfect hash implementation.
 * 
 * @author Valerie Wray
 *
 */
public class MinimalPerfectHashWrapper {
    private static int KMER_LENGTH = 31;
    private static char[] DNA_CHARS = new char[] { 'A', 'C', 'G', 'T' };

    public static double evaluateMinimalPerfectHash(int size, double percentageOverlap) throws IOException {
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = constructMinimalPerfectHash(size, percentageOverlap,
                k, kPrime);
        return queryMinimalPerfectHash(minimalPerfectHash, k, kPrime);
    }

    public static long evaluateMinimalPerfectHashGetTime(int size, double percentageOverlap) throws IOException {
        Profiler profiler = new Profiler("MPH profiler");
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = constructMinimalPerfectHash(size, percentageOverlap,
                k, kPrime);
        profiler.start("Timing Bloom filter query");
        queryMinimalPerfectHash(minimalPerfectHash, k, kPrime);
        profiler.stop();
        return profiler.elapsedTime();
    }

    public static long queryMinimalPerfectHashGetTime(GOVMinimalPerfectHashFunction<String> minimalPerfectHash,
            Set<String> k, Set<String> kPrime) throws IOException {
        Profiler profiler = new Profiler("MPH query profiler");
        profiler.start("Timing Bloom filter query");
        queryMinimalPerfectHash(minimalPerfectHash, k, kPrime);
        profiler.stop();
        return profiler.elapsedTime();
    }

    public static long evaluateMinimalPerfectHashGetMemory(int size, double percentageOverlap) throws IOException {
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = constructMinimalPerfectHash(size, percentageOverlap,
                k, kPrime);
        return minimalPerfectHash.numBits();
    }

    public static GOVMinimalPerfectHashFunction<String> constructMinimalPerfectHash(int size, double percentageOverlap,
            Set<String> k, Set<String> kPrime) throws IOException {
        generateDataSets(k, kPrime, size, percentageOverlap);

        GOVMinimalPerfectHashFunction<String> minimalPerfectHashFunction = new GOVMinimalPerfectHashFunction.Builder()
                .keys(k).transform(TransformationStrategies.rawUtf32()).build();

        return minimalPerfectHashFunction;
    }

    protected static double queryMinimalPerfectHash(GOVMinimalPerfectHashFunction<String> minimalPerfectHash,
            Set<String> k, Set<String> kPrime) {
        int falsePositiveCount = 0;
        int notInK = 0;

        for (String kmer : kPrime) {
            long index = minimalPerfectHash.apply(kmer);
            if (!k.contains(kmer)) {
                notInK++;
                if (index < k.size() && index >= 0) {
                    falsePositiveCount++;
                }
            }
        }
        return ((double) falsePositiveCount) / ((double) notInK);
    }

    private static void generateDataSets(Set<String> k, Set<String> kPrime, int size, double percentageOverlap) {
        int numberOfOverlap = (int) (size * percentageOverlap);
        Random random = new Random();
        while (k.size() < size) {
            StringBuilder kmer = new StringBuilder();
            for (int i = 0; i < KMER_LENGTH; i++) {
                kmer.append(DNA_CHARS[random.nextInt(0, 4)]);
            }
            String kmerString = kmer.toString();
            k.add(kmerString);
            if (kPrime.size() < numberOfOverlap) {
                kPrime.add(kmerString);
            }
        }

        while (kPrime.size() < size) {
            StringBuilder kmer = new StringBuilder();
            for (int i = 0; i < KMER_LENGTH; i++) {
                kmer.append(DNA_CHARS[random.nextInt(0, 4)]);
            }
            String kmerString = kmer.toString();
            if (!k.contains(kmerString)) {
                kPrime.add(kmerString);
            }
        }

    }
}
