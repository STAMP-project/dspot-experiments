/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.itest.jmh;


import Mode.SampleTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.camel.util.CaseInsensitiveMap;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


/**
 * Tests {@link CaseInsensitiveMap}
 */
public class CaseInsensitiveMapTest {
    @Test
    public void launchBenchmark() throws Exception {
        Options opt = // Set the following options as needed
        // Specify which benchmarks to run.
        // You can be more specific if you'd like to run only one benchmark per test.
        new OptionsBuilder().include(((this.getClass().getName()) + ".*")).mode(SampleTime).timeUnit(TimeUnit.MILLISECONDS).warmupTime(TimeValue.seconds(1)).warmupIterations(2).measurementTime(TimeValue.seconds(5)).measurementIterations(5).threads(1).forks(1).shouldFailOnError(true).shouldDoGC(true).measurementBatchSize(1000000).build();
        run();
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class MapsBenchmarkState {
        CaseInsensitiveMap camelMap;

        CaseInsensitiveMap cedarsoftMap;

        HashMap hashMap;

        @Setup(Level.Trial)
        public void initialize() {
            camelMap = new CaseInsensitiveMap();
            cedarsoftMap = new com.cedarsoftware.util.CaseInsensitiveMap();
            hashMap = new HashMap();
        }
    }

    @State(Scope.Benchmark)
    public static class MapsSourceDataBenchmarkState {
        Map<String, Object> map1 = generateRandomMap(10);

        Map<String, Object> map2 = generateRandomMap(10);

        private Map<String, Object> generateRandomMap(int size) {
            return IntStream.range(0, size).boxed().collect(Collectors.toMap(( i) -> RandomStringUtils.randomAlphabetic(10), ( i) -> RandomStringUtils.randomAlphabetic(10)));
        }
    }
}

