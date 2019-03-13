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


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by peter.lawrey on 06/12/14.
 */
public class LargeEntriesTest {
    @Test
    public void testLargeStrings() throws IOException, InterruptedException, ExecutionException {
        final int ENTRIES = 250;
        final int ENTRY_SIZE = 100 * 1024;
        File file = File.createTempFile(("largeEntries" + (System.currentTimeMillis())), ".deleteme");
        file.deleteOnExit();
        try (final ChronicleMap<String, String> map = // to force an error.
        // .valueReaderAndDataAccess(, SnappyStringMarshaller.INSTANCE, )
        ChronicleMapBuilder.of(String.class, String.class).actualSegments(1).entries(ENTRIES).averageKeySize(10).averageValueSize(ENTRY_SIZE).putReturnsNull(true).createPersistedTo(file)) {
            warmUpCompression(ENTRY_SIZE);
            int threads = 4;// Runtime.getRuntime().availableProcessors();

            ExecutorService es = Executors.newFixedThreadPool(threads);
            final int block = ENTRIES / threads;
            for (int i = 0; i < 3; i++) {
                long start = System.currentTimeMillis();
                List<Future<?>> futureList = new ArrayList<>();
                for (int t = 0; t < threads; t++) {
                    final int finalT = t;
                    futureList.add(es.submit(new Runnable() {
                        @Override
                        public void run() {
                            exerciseLargeStrings(map, (finalT * block), ((finalT * block) + block), ENTRY_SIZE);
                        }
                    }));
                }
                for (Future<?> future : futureList) {
                    future.get();
                }
                long time = (System.currentTimeMillis()) - start;
                long operations = 3;
                System.out.printf("Put/Get rate was %.1f MB/s%n", ((((operations * ENTRIES) * ENTRY_SIZE) / 1000000.0) / (time / 1000.0)));
            }
            es.shutdown();
            es.awaitTermination(1, TimeUnit.MINUTES);
            Assert.assertTrue(es.isTerminated());
        }
        file.delete();
    }
}

