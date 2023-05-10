package cmsc701.amq.task1;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.slf4j.profiler.Profiler;

import com.google.common.base.Charsets;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

/**
 * A class to evaluate an existing bloom filter implementation.
 * 
 * @author Valerie Wray
 *
 */
public class BloomFilterWrapper {
    private static int KMER_LENGTH = 31;
    private static char[] DNA_CHARS = new char[] { 'A', 'C', 'G', 'T' };

    public static double evaluateBloomFilter(int size, double percentageOverlap, double falsePositiveRate) {
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        BloomFilter<String> bloomFilter = constructBloomFilter(size, percentageOverlap, falsePositiveRate, k, kPrime);
        return queryBloomFilter(bloomFilter, k, kPrime);
    }

    public static long evaluateBloomFilterGetTime(int size, double percentageOverlap, double falsePositiveRate) {
        Profiler profiler = new Profiler("Bloom Filter profiler");
        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        BloomFilter<String> bloomFilter = constructBloomFilter(size, percentageOverlap, falsePositiveRate, k, kPrime);
        profiler.start("Timing Bloom filter query");
        queryBloomFilter(bloomFilter, k, kPrime);
        profiler.stop();
        return profiler.elapsedTime();
    }

    public static long evaluateBloomFilterGetMemory(int size, double falsePositiveRate) {
        // calculate the size of m, the array of bits that makes up the bloom filter,
        // using formula derived in class
        long bits = (int) Math.ceil(size * Math.log(falsePositiveRate) / -Math.log(Math.pow(2, Math.log(2))));
        return bits;
    }

    private static double queryBloomFilter(BloomFilter<String> bloomFilter, Set<String> k, Set<String> kPrime) {
        int falsePositiveCount = 0;
        int falseNegativeCount = 0;
        int notInK = 0;

        for (String kmer : kPrime) {
            boolean mightContain = bloomFilter.mightContain(kmer);
            if (!k.contains(kmer)) {
                notInK++;
                if (mightContain) {
                    falsePositiveCount++;
                }
            } else if (!mightContain && k.contains(kmer)) {
                falseNegativeCount++;
            }
        }
        // System.out.println("False positive count: " + falsePositiveCount);
        // System.out.println("False negative count: " + falseNegativeCount);
        return (double) falsePositiveCount / (double) notInK;
    }

    private static BloomFilter<String> constructBloomFilter(int size, double percentageOverlap,
            double falsePositiveRate, Set<String> k, Set<String> kPrime) {
        BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_16), size,
                falsePositiveRate);

        generateDataSets(k, kPrime, size, percentageOverlap);

        for (String kmer : k) {
            bloomFilter.put(kmer);
        }
        return bloomFilter;
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
