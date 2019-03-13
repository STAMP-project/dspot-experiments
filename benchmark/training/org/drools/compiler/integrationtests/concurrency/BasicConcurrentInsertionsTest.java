/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.compiler.integrationtests.concurrency;


import org.junit.Test;


public class BasicConcurrentInsertionsTest extends AbstractConcurrentInsertionsTest {
    @Test(timeout = 10000)
    public void testConcurrentInsertionsFewObjectsManyThreads() throws InterruptedException {
        final String drl = ((((((("import " + (BasicConcurrentInsertionsTest.Bean.class.getCanonicalName())) + ";\n") + "\n") + "rule \"R\"\n") + "when\n") + "    $a : Bean( seed != 1 )\n") + "then\n") + "end";
        testConcurrentInsertions(drl, 1, 1000, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsManyObjectsFewThreads() throws InterruptedException {
        final String drl = ((((((("import " + (BasicConcurrentInsertionsTest.Bean.class.getCanonicalName())) + ";\n") + "\n") + "rule \"R\"\n") + "when\n") + "    $a : Bean( seed != 1 )\n") + "then\n") + "end";
        testConcurrentInsertions(drl, 1000, 4, false, false);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsNewSessionEachThreadUpdateFacts() throws InterruptedException {
        // This tests also ObjectTypeNode concurrency
        final String drl = (((((((((((((((("import " + (BasicConcurrentInsertionsTest.Bean.class.getCanonicalName())) + ";\n") + " query existsBeanSeed5More() \n") + "     Bean( seed > 5 ) \n") + " end \n") + "\n") + "rule \"R\"\n") + "when\n") + "    $a: Bean( seed != 1 )\n") + "    existsBeanSeed5More() \n") + "then\n") + "end \n") + "rule \"R2\"\n") + "when\n") + "    $a: Bean( seed != 1 )\n") + "then\n") + "end\n";
        testConcurrentInsertions(drl, 10, 1000, true, true);
    }

    @Test(timeout = 10000)
    public void testConcurrentInsertionsNewSessionEachThread() throws InterruptedException {
        final String drl = ((((((((((((((((((((((((((("import " + (BasicConcurrentInsertionsTest.Bean.class.getCanonicalName())) + ";\n") + " query existsBeanSeed5More() \n") + "     Bean( seed > 5 ) \n") + " end \n") + "\n") + "rule \"R\"\n") + "when\n") + "    $a: Bean( seed != 1 )\n") + "    $b: Bean( seed != 2 )\n") + "    existsBeanSeed5More() \n") + "then\n") + "end \n") + "rule \"R2\"\n") + "when\n") + "    $a: Bean( seed != 1 )\n") + "    $b: Bean( seed != 2 )\n") + "then\n") + "end\n") + "rule \"R3\"\n") + "when\n") + "    $a: Bean( seed != 3 )\n") + "    $b: Bean( seed != 4 )\n") + "    $c: Bean( seed != 5 )\n") + "    $d: Bean( seed != 6 )\n") + "    $e: Bean( seed != 7 )\n") + "then\n") + "end";
        testConcurrentInsertions(drl, 10, 1000, true, false);
    }

    public static class Bean {
        private final int seed;

        private final String threadName;

        public Bean(int seed) {
            this.seed = seed;
            threadName = Thread.currentThread().getName();
        }

        public int getSeed() {
            return seed;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof BasicConcurrentInsertionsTest.Bean))
                return false;

            return ((seed) == (((BasicConcurrentInsertionsTest.Bean) (other)).seed)) && (threadName.equals(((BasicConcurrentInsertionsTest.Bean) (other)).threadName));
        }

        @Override
        public int hashCode() {
            return (29 * (seed)) + (31 * (threadName.hashCode()));
        }

        @Override
        public String toString() {
            return (("Bean #" + (seed)) + " created by ") + (threadName);
        }
    }
}

