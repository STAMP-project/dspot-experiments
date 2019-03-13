package com.baeldung.removingdecimals;


import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import static Mode.Throughput;
import static Scope.Benchmark;


/**
 * This benchmark compares some of the approaches to formatting a floating-point
 * value into a {@link String} while removing the decimal part.
 *
 * To run, simply run the {@link RemovingDecimalsManualTest#runBenchmarks()} test
 * at the end of this class.
 *
 * The benchmark takes about 15 minutes to run. Since it is using {@link Mode#Throughput},
 * higher numbers mean better performance.
 */
@BenchmarkMode(Throughput)
@Warmup(iterations = 5)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Benchmark)
public class RemovingDecimalsManualTest {
    @Param({ "345.56", "345345345.56", "345345345345345345.56" })
    double doubleValue;

    NumberFormat nf;

    DecimalFormat df;

    @Test
    public void runBenchmarks() throws Exception {
        Options options = new OptionsBuilder().include(this.getClass().getSimpleName()).threads(1).forks(1).shouldFailOnError(true).shouldDoGC(true).jvmArgs("-server").build();
        run();
    }
}

