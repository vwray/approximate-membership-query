package cmsc701.amq.task3;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.profiler.Profiler;

import cmsc701.amq.task2.MinimalPerfectHashWrapper;
import edu.berkeley.cs.succinct.util.vector.IntVector;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;

/**
 * A class to extend a minimal perfect hash implementation to act as an
 * approximate membership query by including an auxiliary fingerprint array.
 * 
 * @author Valerie Wray
 *
 */
public class MPHWithFingerprintArray extends MinimalPerfectHashWrapper {

    public static double evaluateMinimalPerfectHashFingerprintArray(int size, double percentageOverlap,
            int precisionBits) throws IOException {
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = constructMinimalPerfectHash(size, percentageOverlap,
                k, kPrime);
        IntVector auxiliaryArray = buildAuxiliaryArray(size, minimalPerfectHash, k, kPrime, precisionBits);
        return queryMinimalPerfectHash(minimalPerfectHash, k, kPrime, auxiliaryArray, precisionBits);
    }

    private static double queryMinimalPerfectHash(GOVMinimalPerfectHashFunction<String> minimalPerfectHash,
            Set<String> k, Set<String> kPrime, IntVector auxiliaryArray, int precisionBits) {
        int falsePositiveCount = 0;
        int notInK = 0;
        int mask = (int) (Math.pow(2, precisionBits) - 1);

        for (String kmer : kPrime) {
            long index = minimalPerfectHash.apply(kmer);
            if (!k.contains(kmer)) {
                notInK++;
                if (index < k.size() && index >= 0 && (auxiliaryArray.get((int) index) == (kmer.hashCode() & mask))) {
                    falsePositiveCount++;
                }
            }
        }
        return ((double) falsePositiveCount) / ((double) notInK);

    }

    public static long queryMinimalPerfectHashGetTime(GOVMinimalPerfectHashFunction<String> minimalPerfectHash,
            Set<String> k, Set<String> kPrime, IntVector auxiliaryArray, int precisionBits) throws IOException {
        Profiler profiler = new Profiler("MPH query profiler");
        profiler.start("Timing Bloom filter query");
        queryMinimalPerfectHash(minimalPerfectHash, k, kPrime, auxiliaryArray, precisionBits);
        profiler.stop();
        return profiler.elapsedTime();
    }

    public static long evaluateMinimalPerfectHashGetMemory(int size, double percentageOverlap, int precisionBits)
            throws IOException {
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = constructMinimalPerfectHash(size, percentageOverlap,
                k, kPrime);
        IntVector auxiliaryArray = buildAuxiliaryArray(size, minimalPerfectHash, k, kPrime, precisionBits);
        return minimalPerfectHash.numBits() + auxiliaryArray.serializedSize();
    }

    protected static IntVector buildAuxiliaryArray(int size, GOVMinimalPerfectHashFunction<String> minimalPerfectHash,
            Set<String> k, Set<String> kPrime, int precisionBits) {
        IntVector auxiliaryArray = new IntVector(size, precisionBits);
        int mask = (int) (Math.pow(2, precisionBits) - 1);
        for (String kmer : k) {
            int value = kmer.hashCode() & mask;
            auxiliaryArray.add(minimalPerfectHash.apply(kmer).intValue(), value);
        }

        return auxiliaryArray;

    }
}
