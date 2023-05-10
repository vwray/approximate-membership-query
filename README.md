# approximate-membership-query
A project evaluating different approximate membership queries: a Bloom filter, a minimal perfect hash, and a minimal perfect hash augmented with a fingerprint array.
This program is written in Java, and dependencies are managed via Maven.

## Components

### Bloom filter
The `cmsc701.amq.task1` package contains the [BloomFilterWrapper](/amq/src/main/java/cmsc701/amq/task1/BloomFilterWrapper.java) class containing the evaluation of the [Guava Bloom filter](https://github.com/google/guava/wiki/HashingExplained#bloomfilter). To use this class, call the `evaluateBloomFilter` method with the desired number of elements, percentage of overlap, and false positive rate like so:
```
BloomFilterWrapper.evaluateBloomFilter(1000, .5, .01);
```
This will construct and query a Bloom filter containing 1000 elements which are all k-mers of length 31, where there is an overlap of 50% between the set used for construction and the set used for querying, and an expected false positive rate of .01.

### Minimal Perfect Hash
The `cmsc701.amq.task2` package contains the [MinimalPerfectHashWrapper](/amq/src/main/java/cmsc701/amq/task2/MinimalPerfectHashWrapper.java) class containing the evaluation of the [sux4j minimal perfect hash](https://sux.di.unimi.it/docs/it/unimi/dsi/sux4j/mph/GOVMinimalPerfectHashFunction.html). To use this class, call the `evaluateMinimalPerfectHash` method with the desired number of elements and percentage of overlap like so:
```
MinimalPerfectHashWrapper.evaluateMinimalPerfectHash(100, .5);
```
This will construct and query a minimal perfect hash containing 1000 elements which are all k-mers of length 31, where there is an overlap of 50% between the set used for construction and the set used for querying.

### Minimal Perfect Hash Augmented with Fingerprint Array
The `cmsc701.amq.task3` package contains the [MPHWithFingerprintArray](/amq/src/main/java/cmsc701/amq/task3/MPHWithFingerprintArray.java) class containing the evaluation of the [sux4j minimal perfect hash](https://sux.di.unimi.it/docs/it/unimi/dsi/sux4j/mph/GOVMinimalPerfectHashFunction.html) augmented with a fingerprint array stored as a succinct [IntVector](https://github.com/amplab/succinct/blob/master/core/src/main/java/edu/berkeley/cs/succinct/util/vector/IntVector.java). To use this class, call the `evaluateMinimalPerfectHashFingerprintArray` method with the desired number of elements, percentage of overlap, and number of bits to store in the fingerprint array like so:
```
MPHWithFingerprintArray.evaluateMinimalPerfectHashFingerprintArray(1000, .5, 7);
```
This will construct and query a minimal perfect hash containing 1000 elements which are all k-mers of length 31, where there is an overlap of 50% between the set used for construction and the set used for querying, and there are 7 bits of hash values stored in the fingerprint array, which translates to an expected false positive rate of 2<sup>-7</sup>.

## Running the code
Recommended steps to run the code:

1. Check out the code from Github.
2. Import approximate-membership-query into Eclipse as a Maven project.
3. Run `mvn clean install` in Eclipse to clean and build the project.
4. Add this dependency to your project's pom file:
```
<dependency>
    <artifactId>amq</artifactId>
    <groupId>cmsc701.hw3</groupId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
5. Include imports for the desired classes into your class files and start using the methods as described above.

## Resources
For generating plots, I consulted [matplotlib4j](https://github.com/sh0nk/matplotlib4j).

See links included above for implementations of the Bloom filter and minimal perfect hash which I consulted and used in my code.
