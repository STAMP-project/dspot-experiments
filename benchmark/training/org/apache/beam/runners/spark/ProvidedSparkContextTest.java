/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.spark;


import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableList;
import org.apache.beam.vendor.guava.v20_0.com.google.common.collect.ImmutableSet;
import org.apache.spark.api.java.JavaSparkContext;
import org.junit.Assert;
import org.junit.Test;


/**
 * Provided Spark Context tests.
 */
public class ProvidedSparkContextTest {
    private static final String[] WORDS_ARRAY = new String[]{ "hi there", "hi", "hi sue bob", "hi sue", "", "bob hi" };

    private static final ImmutableList<String> WORDS = ImmutableList.copyOf(ProvidedSparkContextTest.WORDS_ARRAY);

    private static final ImmutableSet<String> EXPECTED_COUNT_SET = ImmutableSet.of("hi: 5", "there: 1", "sue: 2", "bob: 2");

    private static final String PROVIDED_CONTEXT_EXCEPTION = "The provided Spark context was not created or was stopped";

    /**
     * Provide a context and call pipeline run.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWithProvidedContext() throws Exception {
        JavaSparkContext jsc = new JavaSparkContext("local[*]", "Existing_Context");
        testWithValidProvidedContext(jsc);
        // A provided context must not be stopped after execution
        Assert.assertFalse(jsc.sc().isStopped());
        jsc.stop();
    }

    /**
     * Provide a context and call pipeline run.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWithNullContext() throws Exception {
        testWithInvalidContext(null);
    }

    /**
     * A SparkRunner with a stopped provided Spark context cannot run pipelines.
     *
     * @throws Exception
     * 		
     */
    @Test
    public void testWithStoppedProvidedContext() throws Exception {
        JavaSparkContext jsc = new JavaSparkContext("local[*]", "Existing_Context");
        // Stop the provided Spark context directly
        jsc.stop();
        testWithInvalidContext(jsc);
    }
}

