/**
 * Copyright 2012-2018 Chronicle Map Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.map;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;


public class EntryCountMapTest {
    static final String TMP = System.getProperty("java.io.tmpdir");

    static final int ecmTests = Integer.getInteger("ecm.tests", 5);

    double score = 0;

    int scoreCount = 0;

    @Test
    public void testSmall() throws IOException, InterruptedException, ExecutionException {
        System.out.print("testSmall seeds");
        int procs = Runtime.getRuntime().availableProcessors();
        ExecutorService es = Executors.newFixedThreadPool(procs);
        for (int t = 0; t < (EntryCountMapTest.ecmTests); t++) {
            List<Future<?>> futures = new ArrayList<>();
            {
                int s = 8;
                futures.add(testEntriesMaxSize(es, s, 250, 450, t));
                futures.add(testEntriesMaxSize(es, s, 500, 840, t));
                futures.add(testEntriesMaxSize(es, s, 1000, 1550, t));
                futures.add(testEntriesMaxSize(es, s, 2000, 3200, t));
            }
            // regression test.
            for (int s : new int[]{ 8, 16 }) {
                futures.add(testEntriesMaxSize(es, s, 3000, 5000, t));
                futures.add(testEntriesMaxSize(es, s, 5000, 7500, t));
                futures.add(testEntriesMaxSize(es, s, 10000, 15000, t));
            }
            for (int s : new int[]{ 32, 64, 128 })
                futures.add(testEntriesMaxSize(es, s, (250 * s), (((250 * s) * 5) / 3), t));

            int s = 32;
            for (int e : new int[]{ 16000, 25000, 50000 })
                futures.add(testEntriesMaxSize(es, s, e, ((e * 5) / 3), t));

            s = 64;
            for (int e : new int[]{ 25000, 50000, 100000 })
                futures.add(testEntriesMaxSize(es, s, e, ((e * 5) / 3), t));

            s = 128;
            for (int e : new int[]{ 50000, 100000, 200000 })
                futures.add(testEntriesMaxSize(es, s, e, ((e * 5) / 3), t));

            for (Future<?> future : futures) {
                System.out.print(".");
                future.get();
            }
        }
        es.shutdown();
        // hyperbolic average gives more weight to small numbers.
        System.out.printf(" Score: %.2f%n", ((scoreCount) / (score)));
    }
}

