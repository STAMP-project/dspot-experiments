/**
 * *  Copyright 2010-2017 OrientDB LTD (http://orientdb.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://orientdb.com
 */
package com.orientechnologies.orient.core.db;


import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.index.OIndex;
import com.orientechnologies.orient.core.record.impl.ODocument;
import java.io.File;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Sergey Sitnikov
 */
public class FreezeAndRecordInsertAtomicityTest {
    private static final String URL;

    private static final int THREADS = (Runtime.getRuntime().availableProcessors()) * 2;

    private static final int ITERATIONS = 100;

    static {
        String buildDirectory = System.getProperty("buildDirectory");
        if (buildDirectory == null)
            buildDirectory = "./target";

        URL = (("plocal:" + buildDirectory) + (File.separator)) + (FreezeAndRecordInsertAtomicityTest.class.getSimpleName());
    }

    private Random random;

    private ODatabaseDocumentTx db;

    private ExecutorService executorService;

    private CountDownLatch countDownLatch;

    @Test
    public void test() throws InterruptedException, ExecutionException {
        final Set<Future<?>> futures = new HashSet<Future<?>>();
        for (int i = 0; i < (FreezeAndRecordInsertAtomicityTest.THREADS); ++i) {
            final int thread = i;
            futures.add(executorService.submit(() -> {
                try {
                    final ODatabaseDocumentTx db = new ODatabaseDocumentTx(FreezeAndRecordInsertAtomicityTest.URL);
                    db.open("admin", "admin");
                    final OIndex<?> index = db.getMetadata().getIndexManager().getIndex("Person.name");
                    for (int i1 = 0; i1 < (FreezeAndRecordInsertAtomicityTest.ITERATIONS); ++i1)
                        switch (random.nextInt(3)) {
                            case 0 :
                                db.newInstance("Person").field("name", ((("name-" + thread) + "-") + i1)).save();
                                break;
                            case 1 :
                                db.begin();
                                db.newInstance("Person").field("name", ((("name-" + thread) + "-") + i1)).save();
                                db.commit();
                                break;
                            case 2 :
                                db.freeze();
                                try {
                                    for (ODocument document : db.browseClass("Person"))
                                        Assert.assertEquals(document.getIdentity(), index.get(document.field("name")));

                                } finally {
                                    db.release();
                                }
                                break;
                        }

                } catch (RuntimeException | Error e) {
                    e.printStackTrace();
                    throw e;
                } finally {
                    countDownLatch.countDown();
                }
            }));
        }
        countDownLatch.await();
        for (Future<?> future : futures)
            future.get();
        // propagate exceptions, if there are any

    }
}

