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


import Mode.All;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;


/**
 * Tests the {@link StringHelper}.
 * <p/>
 * Thanks to this SO answer: https://stackoverflow.com/questions/30485856/how-to-run-jmh-from-inside-junit-tests
 */
public class ContainsIgnoreCaseTest {
    @Test
    public void launchBenchmark() throws Exception {
        Options opt = // Set the following options as needed
        // Specify which benchmarks to run.
        // You can be more specific if you'd like to run only one benchmark per test.
        new OptionsBuilder().include(((this.getClass().getName()) + ".*")).mode(All).timeUnit(TimeUnit.MICROSECONDS).warmupTime(TimeValue.seconds(1)).warmupIterations(2).measurementTime(TimeValue.seconds(1)).measurementIterations(2).threads(2).forks(1).shouldFailOnError(true).shouldDoGC(true).build();
        run();
    }

    // The JMH samples are the best documentation for how to use it
    // http://hg.openjdk.java.net/code-tools/jmh/file/tip/jmh-samples/src/main/java/org/openjdk/jmh/samples/
    @State(Scope.Thread)
    public static class BenchmarkState {
        @Setup(Level.Trial)
        public void initialize() {
        }
    }
}

