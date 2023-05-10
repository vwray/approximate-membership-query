package cmsc701.amq.task3;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import edu.berkeley.cs.succinct.util.vector.IntVector;
import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;

class MPHWithFingerprintArrayTest {

    @Test
    void testMask() {
        assertEquals(7, 15 & 7);
        assertEquals(3, 67 & 7);
        assertEquals(5, (5 + 16 + 32) & 7);
    }

    @Test
    void test() throws IOException {
        MPHWithFingerprintArray.evaluateMinimalPerfectHashFingerprintArray(100, .5, 7);
    }

    @Test
    void testFalsePositivesGetPlot() throws IOException, PythonExecutionException {
        int precisionBits = 10;
        double expectedFalsePositiveRate = 1.0 / Math.pow(2, precisionBits);
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        List<Double> runtimesForOverlap3 = new ArrayList<Double>();
        List<Double> runtimesForOverlap5 = new ArrayList<Double>();
        List<Double> runtimesForOverlap9 = new ArrayList<Double>();

        // get average false positives for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            double sum = 0.0;
            for (int i = 0; i < 5; i++) {
                sum += MPHWithFingerprintArray.evaluateMinimalPerfectHashFingerprintArray(n, .3, precisionBits);
            }
            runtimesForOverlap3.add(sum / 5.0);

            sum = 0.0;
            for (int i = 0; i < 5; i++) {
                sum += MPHWithFingerprintArray.evaluateMinimalPerfectHashFingerprintArray(n, .5, precisionBits);
            }
            runtimesForOverlap5.add(sum / 5.0);

            sum = 0.0;
            for (int i = 0; i < 5; i++) {
                sum += MPHWithFingerprintArray.evaluateMinimalPerfectHashFingerprintArray(n, .9, precisionBits);
            }
            runtimesForOverlap9.add(sum / 5.0);
        }

        Plot plt = Plot.create();

        plt.plot()
                .add(Arrays.asList(1000, 10000, 100000),
                        Arrays.asList(expectedFalsePositiveRate, expectedFalsePositiveRate, expectedFalsePositiveRate))
                .label("Expected False Positive Rate");
        plt.plot().add(valuesOfN, runtimesForOverlap3, "o").linestyle("-").label("Overlap 30%");
        plt.plot().add(valuesOfN, runtimesForOverlap5, "o").linestyle("-").label("Overlap 50%");
        plt.plot().add(valuesOfN, runtimesForOverlap9, "o").linestyle("-").label("Overlap 90%");

        plt.title("Actual vs Expected False Positive Rates: False Positive Rate Low");
        plt.xlabel("Number of Elements in the Minimal Perfect Hash");
        plt.ylabel("False Positive Rate");
        plt.legend();
        plt.show();
    }

    @Test
    void testRuntimesQueryGetPlot() throws IOException, PythonExecutionException {
        int precisionBits = 10;
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        List<Long> runtimesForOverlap3 = new ArrayList<Long>();
        List<Long> runtimesForOverlap5 = new ArrayList<Long>();
        List<Long> runtimesForOverlap9 = new ArrayList<Long>();

        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = MPHWithFingerprintArray
                .constructMinimalPerfectHash(10000, .5, k, kPrime);
        IntVector auxiliaryArray = MPHWithFingerprintArray.buildAuxiliaryArray(10000, minimalPerfectHash, k, kPrime,
                precisionBits);
        // throw these runs away
        for (int i = 0; i < 100; i++) {
            MPHWithFingerprintArray.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime, auxiliaryArray,
                    precisionBits);
        }

        // get average runtime for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);

            k = new HashSet<>();
            kPrime = new HashSet<>();
            minimalPerfectHash = MPHWithFingerprintArray.constructMinimalPerfectHash(n, .3, k, kPrime);
            IntVector auxiliaryArray1 = MPHWithFingerprintArray.buildAuxiliaryArray(n, minimalPerfectHash, k, kPrime,
                    precisionBits);
            double sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += MPHWithFingerprintArray.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime,
                        auxiliaryArray1, precisionBits);
            }
            runtimesForOverlap3.add((long) (sum / 50.0));

            k = new HashSet<>();
            kPrime = new HashSet<>();
            minimalPerfectHash = MPHWithFingerprintArray.constructMinimalPerfectHash(n, .5, k, kPrime);
            IntVector auxiliaryArray2 = MPHWithFingerprintArray.buildAuxiliaryArray(n, minimalPerfectHash, k, kPrime,
                    precisionBits);
            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += MPHWithFingerprintArray.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime,
                        auxiliaryArray2, precisionBits);
            }
            runtimesForOverlap5.add((long) (sum / 50.0));

            k = new HashSet<>();
            kPrime = new HashSet<>();
            minimalPerfectHash = MPHWithFingerprintArray.constructMinimalPerfectHash(n, .9, k, kPrime);
            IntVector auxiliaryArray3 = MPHWithFingerprintArray.buildAuxiliaryArray(n, minimalPerfectHash, k, kPrime,
                    precisionBits);
            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += MPHWithFingerprintArray.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime,
                        auxiliaryArray3, precisionBits);
            }
            runtimesForOverlap9.add((long) (sum / 50.0));
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, runtimesForOverlap3, "o").linestyle("-").label("Overlap 30%");
        plt.plot().add(valuesOfN, runtimesForOverlap5, "o").linestyle("-").label("Overlap 50%");
        plt.plot().add(valuesOfN, runtimesForOverlap9, "o").linestyle("-").label("Overlap 90%");

        plt.title("Runtime with Fingerprint Array: False Positive Rate Low");
        plt.xlabel("Number of Elements in the Minimal Perfect Hash");
        plt.ylabel("Runtime");
        plt.legend();
        plt.show();
    }

    @Test
    void testMemoryGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        List<Long> memoryUsages7 = new ArrayList<Long>();
        List<Long> memoryUsages8 = new ArrayList<Long>();
        List<Long> memoryUsages10 = new ArrayList<Long>();

        // get memory for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            memoryUsages7.add(MPHWithFingerprintArray.evaluateMinimalPerfectHashGetMemory(n, .5, 7));
            memoryUsages8.add(MPHWithFingerprintArray.evaluateMinimalPerfectHashGetMemory(n, .5, 8));
            memoryUsages10.add(MPHWithFingerprintArray.evaluateMinimalPerfectHashGetMemory(n, .5, 10));
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, memoryUsages7, "o").linestyle("-").label("7 bits");
        plt.plot().add(valuesOfN, memoryUsages8, "o").linestyle("-").label("8 bits");
        plt.plot().add(valuesOfN, memoryUsages10, "o").linestyle("-").label("10 bits");

        plt.title("Memory Usage of Minimal Perfect Hash with Fingerprint Array");
        plt.xlabel("Number of Elements in the Minimal Perfect Hash");
        plt.ylabel("Memory usage in bits");
        plt.legend();
        plt.show();
    }

}
