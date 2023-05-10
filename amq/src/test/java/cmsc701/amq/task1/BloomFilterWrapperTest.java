package cmsc701.amq.task1;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

class BloomFilterWrapperTest {

    @Test
    void test() {
        BloomFilterWrapper.evaluateBloomFilter(1000, .5, .01);
    }

    @Test
    void testFalsePositiveRatesGetPlot() throws IOException, PythonExecutionException {
        FileWriter fileWriter = new FileWriter("src/test/resources/outputFalsePositives.txt");
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 100000);
        List<Double> percentageOverlaps = Arrays.asList(.3, .5, .9);
        List<Double> expectedFalsePositiveRates = Arrays.asList(1.0 / Math.pow(2, 7), 1.0 / Math.pow(2, 8),
                1.0 / Math.pow(2, 10));
        double[][][] actualFalsePositives = new double[3][3][3];
        fileWriter.write("Size Overlap ExpectedFalsePos ActualFalsePos \n");

        for (int i = 0; i < valuesOfN.size(); i++) {
            for (int j = 0; j < percentageOverlaps.size(); j++) {
                for (int k = 0; k < expectedFalsePositiveRates.size(); k++) {
                    double actualFalsePositive = BloomFilterWrapper.evaluateBloomFilter(valuesOfN.get(i),
                            percentageOverlaps.get(j), expectedFalsePositiveRates.get(k)) / ((double) valuesOfN.get(i));
                    fileWriter.write(new StringBuilder().append(valuesOfN.get(i)).append(" ")
                            .append(percentageOverlaps.get(j)).append(" ").append(expectedFalsePositiveRates.get(k))
                            .append(" ").append(actualFalsePositive).append("\n").toString());
                    actualFalsePositives[i][j][k] = actualFalsePositive;
                }
            }
        }
        fileWriter.close();

        Plot plt = Plot.create();

        List<Double> actualFalsePositiveList = new ArrayList<>();
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 3; k++) {
                actualFalsePositiveList.add(actualFalsePositives[k][j][2]);
            }
        }

        plt.plot().add(Arrays.asList(1000, 1000, 1000, 10000, 10000, 10000, 100000, 100000, 100000),
                actualFalsePositiveList, "o").label("Actual False Positive Rates");
        plt.plot()
                .add(Arrays.asList(1000, 10000, 100000),
                        Arrays.asList(1.0 / Math.pow(2, 10), 1.0 / Math.pow(2, 10), 1.0 / Math.pow(2, 10)))
                .label("Expected False Positive Rates");

        plt.title("Expected vs Actual False Positive Rate");
        plt.xlabel("Number of Elements in the Bloom Filter");
        plt.ylabel("False Positive Rate");
        plt.legend();
        plt.show();
    }

    @Test
    void testFalsePositivesGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        double expectedFalsePositiveRate = 1.0 / Math.pow(2, 10);
        List<Double> falsePositivesForOverlap3 = new ArrayList<Double>();
        List<Double> falsePositivesForOverlap5 = new ArrayList<Double>();
        List<Double> falsePositivesForOverlap9 = new ArrayList<Double>();

        // throw these runs away
        for (int i = 0; i < 300; i++) {
            BloomFilterWrapper.evaluateBloomFilter(10000, .5, expectedFalsePositiveRate);
        }

        // get average runtime for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            double sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += BloomFilterWrapper.evaluateBloomFilter(n, .3, expectedFalsePositiveRate);
            }
            falsePositivesForOverlap3.add(sum / 50.0);

            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += BloomFilterWrapper.evaluateBloomFilter(n, .5, expectedFalsePositiveRate);
            }
            falsePositivesForOverlap5.add(sum / 50.0);

            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += BloomFilterWrapper.evaluateBloomFilter(n, .9, expectedFalsePositiveRate);
            }
            falsePositivesForOverlap9.add(sum / 50.0);
        }

        Plot plt = Plot.create();
        plt.plot()
                .add(Arrays.asList(1000, 10000, 100000),
                        Arrays.asList(expectedFalsePositiveRate, expectedFalsePositiveRate, expectedFalsePositiveRate))
                .label("Expected False Positive Rate");
        plt.plot().add(valuesOfN, falsePositivesForOverlap3, "o").linestyle("-").label("Overlap 30%");
        plt.plot().add(valuesOfN, falsePositivesForOverlap5, "o").linestyle("-").label("Overlap 50%");
        plt.plot().add(valuesOfN, falsePositivesForOverlap9, "o").linestyle("-").label("Overlap 90%");

        plt.title("Actual vs Expected False Positive Rates: False Positive Rate Low");
        plt.xlabel("Number of Elements in the Bloom Filter");
        plt.ylabel("False Positive Rate");
        plt.legend();
        plt.show();
    }

    @Test
    void testRuntimesGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        double expectedFalsePositiveRate = 1.0 / Math.pow(2, 10);
        List<Long> runtimesForOverlap3 = new ArrayList<Long>();
        List<Long> runtimesForOverlap5 = new ArrayList<Long>();
        List<Long> runtimesForOverlap9 = new ArrayList<Long>();

        // throw these runs away
        for (int i = 0; i < 100; i++) {
            BloomFilterWrapper.evaluateBloomFilterGetTime(10000, .5, expectedFalsePositiveRate);
        }

        // get average runtime for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            double sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += BloomFilterWrapper.evaluateBloomFilterGetTime(n, .3, expectedFalsePositiveRate);
            }
            runtimesForOverlap3.add((long) (sum / 50));

            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += BloomFilterWrapper.evaluateBloomFilterGetTime(n, .5, expectedFalsePositiveRate);
            }
            runtimesForOverlap5.add((long) (sum / 50));

            sum = 0;
            for (int i = 0; i < 50; i++) {
                sum += BloomFilterWrapper.evaluateBloomFilterGetTime(n, .9, expectedFalsePositiveRate);
            }
            runtimesForOverlap9.add((long) (sum / 50));
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, runtimesForOverlap3, "o").linestyle("-").label("Overlap 30%");
        plt.plot().add(valuesOfN, runtimesForOverlap5, "o").linestyle("-").label("Overlap 50%");
        plt.plot().add(valuesOfN, runtimesForOverlap9, "o").linestyle("-").label("Overlap 90%");

        plt.title("Runtime of Querying Bloom Filter: False Positive Rate Low");
        plt.xlabel("Number of Elements in the Bloom Filter");
        plt.ylabel("Runtime");
        plt.legend();
        plt.show();
    }

    @Test
    void testMemoryGetPlot() throws IOException, PythonExecutionException {
        List<Integer> valuesOfN = Arrays.asList(1000, 10000, 50000, 70000, 100000);
        double expectedFalsePositiveRate = 1.0 / Math.pow(2, 10);
        List<Long> runtimesForFalsePositiveRateHigh = new ArrayList<Long>();
        List<Long> runtimesForFalsePositiveRateMedium = new ArrayList<Long>();
        List<Long> runtimesForFalsePositiveRateLow = new ArrayList<Long>();

        // get average runtime for each value of n
        for (int n : valuesOfN) {
            System.out.println("N = " + n);
            runtimesForFalsePositiveRateHigh
                    .add(BloomFilterWrapper.evaluateBloomFilterGetMemory(n, 1.0 / Math.pow(2, 7)));
            runtimesForFalsePositiveRateMedium
                    .add(BloomFilterWrapper.evaluateBloomFilterGetMemory(n, 1.0 / Math.pow(2, 8)));
            runtimesForFalsePositiveRateLow
                    .add(BloomFilterWrapper.evaluateBloomFilterGetMemory(n, 1.0 / Math.pow(2, 10)));
        }

        Plot plt = Plot.create();

        plt.plot().add(valuesOfN, runtimesForFalsePositiveRateHigh, "o").linestyle("-")
                .label("False Positive Rate High");
        plt.plot().add(valuesOfN, runtimesForFalsePositiveRateMedium, "o").linestyle("-")
                .label("False Positive Rate Medium");
        plt.plot().add(valuesOfN, runtimesForFalsePositiveRateLow, "o").linestyle("-").label("False Positive Rate Low");

        plt.title("Memory Usage of Bloom Filter");
        plt.xlabel("Number of Elements in the Bloom Filter");
        plt.ylabel("Memory usage in bits");
        plt.legend();
        plt.show();
    }
}
