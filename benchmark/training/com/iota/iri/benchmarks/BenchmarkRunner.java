package com.iota.iri.benchmarks;


import Mode.AverageTime;
import Mode.Throughput;
import com.iota.iri.benchmarks.dbbenchmark.RocksDbBenchmark;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;


public class BenchmarkRunner {
    @Test
    public void launchDbBenchmarks() throws RunnerException {
        Options opts = new OptionsBuilder().include(((RocksDbBenchmark.class.getName()) + ".*")).mode(AverageTime).timeUnit(TimeUnit.MILLISECONDS).warmupIterations(5).forks(1).measurementIterations(10).shouldFailOnError(true).shouldDoGC(false).build();
        // possible to do assertions over run results
        run();
    }

    @Test
    public void launchCryptoBenchmark() throws RunnerException {
        Options opts = new OptionsBuilder().include(((this.getClass().getPackage().getName()) + ".crypto")).mode(Throughput).timeUnit(TimeUnit.SECONDS).warmupIterations(5).forks(1).measurementIterations(10).shouldFailOnError(true).shouldDoGC(false).build();
        run();
    }
}

