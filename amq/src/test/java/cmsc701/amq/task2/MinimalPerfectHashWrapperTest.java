package cmsc701.amq.task2;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import it.unimi.dsi.sux4j.mph.GOVMinimalPerfectHashFunction;

class MinimalPerfectHashWrapperTest {

    @Test
    void test() throws IOException {
        MinimalPerfectHashWrapper.evaluateMinimalPerfectHash(100, .5);
    }

    @Test
    void testFalsePositivesGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        List<Double> runtimesForOverlap3 = new ArrayList<Double>();
        List<Double> runtimesForOverlap5 = new ArrayList<Double>();
        List<Double> runtimesForOverlap9 = new ArrayList<Double>();

        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = MinimalPerfectHashWrapper
                .constructMinimalPerfectHash(10000, .5, k, kPrime);
        // throw these runs away
        for (int i = 0; i < 100; i++) {
            MinimalPerfectHashWrapper.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime);
        }

        // get average false positives for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            double sum = 0.0;
            for (int i = 0; i < 5; i++) {
                sum += MinimalPerfectHashWrapper.evaluateMinimalPerfectHash(n, .3);
            }
            runtimesForOverlap3.add(sum / 5.0);

            sum = 0.0;
            for (int i = 0; i < 5; i++) {
                sum += MinimalPerfectHashWrapper.evaluateMinimalPerfectHash(n, .5);
            }
            runtimesForOverlap5.add(sum / 5.0);

            sum = 0.0;
            for (int i = 0; i < 5; i++) {
                sum += MinimalPerfectHashWrapper.evaluateMinimalPerfectHash(n, .9);
            }
            runtimesForOverlap9.add(sum / 5.0);
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, runtimesForOverlap3, "o").linestyle("-").label("Overlap 30%");
        plt.plot().add(valuesOfN, runtimesForOverlap5, "o").linestyle("-").label("Overlap 50%");
        plt.plot().add(valuesOfN, runtimesForOverlap9, "o").linestyle("-").label("Overlap 90%");
        plt.ylim(0.0, 1.1);

        plt.title("Actual False Positive Rates");
        plt.xlabel("Number of Elements in the Minimal Perfect Hash");
        plt.ylabel("False Positive Rate");
        plt.legend();
        plt.show();
    }

    @Test
    void testRuntimesQueryGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        List<Long> runtimesForOverlap3 = new ArrayList<Long>();
        List<Long> runtimesForOverlap5 = new ArrayList<Long>();
        List<Long> runtimesForOverlap9 = new ArrayList<Long>();

        Set<String> k = new HashSet<>();
        Set<String> kPrime = new HashSet<>();
        GOVMinimalPerfectHashFunction<String> minimalPerfectHash = MinimalPerfectHashWrapper
                .constructMinimalPerfectHash(10000, .5, k, kPrime);
        // throw these runs away
        for (int i = 0; i < 100; i++) {
            MinimalPerfectHashWrapper.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime);
        }

        // get average runtime for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);

            k = new HashSet<>();
            kPrime = new HashSet<>();
            minimalPerfectHash = MinimalPerfectHashWrapper.constructMinimalPerfectHash(n, .3, k, kPrime);
            double sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += MinimalPerfectHashWrapper.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime);
            }
            runtimesForOverlap3.add((long) (sum / 50.0));

            k = new HashSet<>();
            kPrime = new HashSet<>();
            minimalPerfectHash = MinimalPerfectHashWrapper.constructMinimalPerfectHash(n, .5, k, kPrime);
            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += MinimalPerfectHashWrapper.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime);
            }
            runtimesForOverlap5.add((long) (sum / 50.0));

            k = new HashSet<>();
            kPrime = new HashSet<>();
            minimalPerfectHash = MinimalPerfectHashWrapper.constructMinimalPerfectHash(n, .9, k, kPrime);
            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += MinimalPerfectHashWrapper.queryMinimalPerfectHashGetTime(minimalPerfectHash, k, kPrime);
            }
            runtimesForOverlap9.add((long) (sum / 50.0));
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, runtimesForOverlap3, "o").linestyle("-").label("Overlap 30%");
        plt.plot().add(valuesOfN, runtimesForOverlap5, "o").linestyle("-").label("Overlap 50%");
        plt.plot().add(valuesOfN, runtimesForOverlap9, "o").linestyle("-").label("Overlap 90%");

        plt.title("Runtime of Querying Minimal Perfect Hash");
        plt.xlabel("Number of Elements in the Minimal Perfect Hash");
        plt.ylabel("Runtime");
        plt.legend();
        plt.show();
    }

    @Test
    void testMemoryGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        List<Long> memoryUsages = new ArrayList<Long>();

        // get memory for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            memoryUsages.add(MinimalPerfectHashWrapper.evaluateMinimalPerfectHashGetMemory(n, .5));
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, memoryUsages, "o").linestyle("-");
        // .label("False Positive Rate High");

        plt.title("Memory Usage of Minimal Perfect Hash");
        plt.xlabel("Number of Elements in the Minimal Perfect Hash");
        plt.ylabel("Memory usage in bits");
        plt.legend();
        plt.show();
    }

}
