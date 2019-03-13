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
package org.apache.ignite.internal.processors.cache;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteException;
import org.apache.ignite.IgniteLogger;
import org.apache.ignite.compute.ComputeJobMasterLeaveAware;
import org.apache.ignite.compute.ComputeTaskSession;
import org.apache.ignite.internal.IgniteEx;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.lang.IgniteRunnable;
import org.apache.ignite.resources.IgniteInstanceResource;
import org.apache.ignite.resources.LoggerResource;
import org.apache.ignite.testframework.GridTestUtils;
import org.junit.Test;


/**
 * Test to validate https://issues.apache.org/jira/browse/IGNITE-2310
 */
public class IgniteCacheLockPartitionOnAffinityRunTest extends IgniteCacheLockPartitionOnAffinityRunAbstractTest {
    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testMultipleCaches() throws Exception {
        final IgniteCacheLockPartitionOnAffinityRunTest.PersonsCountGetter personsCntGetter = new IgniteCacheLockPartitionOnAffinityRunTest.PersonsCountGetter() {
            @Override
            public int getPersonsCount(IgniteEx ignite, IgniteLogger log, int orgId) throws Exception {
                return IgniteCacheLockPartitionOnAffinityRunTest.getPersonsCountMultipleCache(ignite, log, orgId);
            }
        };
        // Run restart threads: start re-balancing.
        beginNodesRestart();
        IgniteInternalFuture<Long> affFut = null;
        try {
            final AtomicInteger threadNum = new AtomicInteger(0);
            affFut = GridTestUtils.runMultiThreadedAsync(new Runnable() {
                @Override
                public void run() {
                    if (((threadNum.getAndIncrement()) % 2) == 0) {
                        while ((System.currentTimeMillis()) < (IgniteCacheLockPartitionOnAffinityRunAbstractTest.endTime)) {
                            for (final int orgId : IgniteCacheLockPartitionOnAffinityRunAbstractTest.orgIds) {
                                if ((System.currentTimeMillis()) >= (IgniteCacheLockPartitionOnAffinityRunAbstractTest.endTime))
                                    break;

                                grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteCacheLockPartitionOnAffinityRunTest.TestAffinityRun(personsCntGetter, orgId));
                            }
                        } 
                    } else {
                        while ((System.currentTimeMillis()) < (IgniteCacheLockPartitionOnAffinityRunAbstractTest.endTime)) {
                            for (final int orgId : IgniteCacheLockPartitionOnAffinityRunAbstractTest.orgIds) {
                                if ((System.currentTimeMillis()) >= (IgniteCacheLockPartitionOnAffinityRunAbstractTest.endTime))
                                    break;

                                int personsCnt = grid(0).compute().affinityCall(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteCacheLockPartitionOnAffinityRunTest.TestAffinityCall(personsCntGetter, orgId));
                                assertEquals(IgniteCacheLockPartitionOnAffinityRunAbstractTest.PERS_AT_ORG_CNT, personsCnt);
                            }
                        } 
                    }
                }
            }, IgniteCacheLockPartitionOnAffinityRunAbstractTest.AFFINITY_THREADS_CNT, "affinity-run");
        } finally {
            if (affFut != null)
                affFut.get();

        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testCheckReservePartitionException() throws Exception {
        int orgId = primaryKey(grid(1).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        GridTestUtils.assertThrowsAnyCause(log, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.OTHER_CACHE_NAME), new Integer(orgId), new IgniteRunnable() {
                    @Override
                    public void run() {
                        // No-op.
                    }
                });
                return null;
            }
        }, IgniteException.class, "Failed partition reservation. Partition is not primary on the node.");
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionJobCompletesNormally() throws Exception {
        final int orgId = primaryKey(grid(1).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteRunnable() {
            @IgniteInstanceResource
            IgniteEx ignite;

            @Override
            public void run() {
                try {
                    IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(ignite, orgId, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Unexpected exception");
                }
            }
        });
        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
        grid(0).compute().affinityCall(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new org.apache.ignite.lang.IgniteCallable<Object>() {
            @IgniteInstanceResource
            IgniteEx ignite;

            @Override
            public Object call() {
                try {
                    IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(ignite, orgId, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("Unexpected exception");
                }
                return null;
            }
        });
        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionJobThrowsException() throws Exception {
        final int orgId = primaryKey(grid(1).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        try {
            grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteRunnable() {
                @IgniteInstanceResource
                IgniteEx ignite;

                @Override
                public void run() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(ignite, orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    throw new RuntimeException("Test job throws exception");
                }
            });
            fail("Exception must be thrown");
        } catch (Exception ignored) {
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
        }
        try {
            grid(0).compute().affinityCall(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new org.apache.ignite.lang.IgniteCallable<Object>() {
                @IgniteInstanceResource
                IgniteEx ignite;

                @Override
                public Object call() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(ignite, orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    throw new RuntimeException("Test job throws exception");
                }
            });
            fail("Exception must be thrown");
        } catch (Exception ignored) {
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionJobThrowsError() throws Exception {
        final int orgId = primaryKey(grid(1).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        try {
            grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteRunnable() {
                @IgniteInstanceResource
                IgniteEx ignite;

                @Override
                public void run() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(ignite, orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    throw new Error("Test job throws error");
                }
            });
            fail("Error must be thrown");
        } catch (Throwable ignored) {
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
        }
        try {
            grid(0).compute().affinityCall(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new org.apache.ignite.lang.IgniteCallable<Object>() {
                @IgniteInstanceResource
                IgniteEx ignite;

                @Override
                public Object call() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(ignite, orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    throw new Error("Test job throws error");
                }
            });
            fail("Error must be thrown");
        } catch (Throwable ignored) {
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionJobUnmarshalingFails() throws Exception {
        final int orgId = primaryKey(grid(1).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        try {
            grid(0).compute().affinityRun(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteCacheLockPartitionOnAffinityRunTest.JobFailUnmarshaling());
            fail("Unmarshaling exception must be thrown");
        } catch (Exception ignored) {
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(1), orgId, 0);
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionJobMasterLeave() throws Exception {
        final int orgId = primaryKey(grid(0).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        try {
            grid(1).compute().affinityRunAsync(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteRunnable() {
                @IgniteInstanceResource
                private Ignite ignite;

                @Override
                public void run() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(((IgniteEx) (ignite)), orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                        // No-op.
                    }
                }
            });
            stopGrid(1, true);
            Thread.sleep(3000);
            awaitPartitionMapExchange();
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(0), orgId, 0);
        } finally {
            startGrid(1);
            awaitPartitionMapExchange();
        }
        try {
            grid(1).compute().affinityCallAsync(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new org.apache.ignite.lang.IgniteCallable<Object>() {
                @IgniteInstanceResource
                private Ignite ignite;

                @Override
                public Object call() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(((IgniteEx) (ignite)), orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                        // No-op.
                    }
                    return null;
                }
            });
            stopGrid(1, true);
            Thread.sleep(3000);
            awaitPartitionMapExchange();
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(0), orgId, 0);
        } finally {
            startGrid(1);
            awaitPartitionMapExchange();
        }
    }

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testReleasePartitionJobImplementMasterLeave() throws Exception {
        final int orgId = primaryKey(grid(0).cache(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName()));
        try {
            grid(1).compute().affinityRunAsync(Arrays.asList(IgniteCacheLockPartitionOnAffinityRunAbstractTest.Organization.class.getSimpleName(), IgniteCacheLockPartitionOnAffinityRunAbstractTest.Person.class.getSimpleName()), new Integer(orgId), new IgniteCacheLockPartitionOnAffinityRunTest.RunnableWithMasterLeave() {
                @IgniteInstanceResource
                private Ignite ignite;

                @Override
                public void onMasterNodeLeft(ComputeTaskSession ses) throws IgniteException {
                    // No-op.
                }

                @Override
                public void run() {
                    try {
                        IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(((IgniteEx) (ignite)), orgId, 1);
                    } catch (Exception e) {
                        e.printStackTrace();
                        fail("Unexpected exception");
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {
                        // No-op.
                    }
                }
            });
            stopGrid(1, true);
            Thread.sleep(3000);
            awaitPartitionMapExchange();
            IgniteCacheLockPartitionOnAffinityRunAbstractTest.checkPartitionsReservations(grid(0), orgId, 0);
        } finally {
            startGrid(1);
            awaitPartitionMapExchange();
        }
    }

    /**
     *
     */
    private interface PersonsCountGetter {
        /**
         *
         *
         * @param ignite
         * 		Ignite.
         * @param log
         * 		Logger.
         * @param orgId
         * 		Org id.
         * @return Count of found Person object with specified orgId
         * @throws Exception
         * 		If failed.
         */
        int getPersonsCount(IgniteEx ignite, IgniteLogger log, int orgId) throws Exception;
    }

    /**
     *
     */
    interface RunnableWithMasterLeave extends ComputeJobMasterLeaveAware , IgniteRunnable {}

    /**
     *
     */
    private static class TestAffinityCall implements org.apache.ignite.lang.IgniteCallable<Integer> {
        /**
         * Persons count getter.
         */
        IgniteCacheLockPartitionOnAffinityRunTest.PersonsCountGetter personsCntGetter;

        /**
         * Org id.
         */
        int orgId;

        /**
         *
         */
        @IgniteInstanceResource
        private IgniteEx ignite;

        /**
         *
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         *
         */
        public TestAffinityCall() {
            // No-op.
        }

        /**
         *
         *
         * @param personsCntGetter
         * 		Object to count Person.
         * @param orgId
         * 		Organization Id.
         */
        public TestAffinityCall(IgniteCacheLockPartitionOnAffinityRunTest.PersonsCountGetter personsCntGetter, int orgId) {
            this.personsCntGetter = personsCntGetter;
            this.orgId = orgId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public Integer call() throws Exception {
            log.info(("Begin call. orgId=" + (orgId)));
            return personsCntGetter.getPersonsCount(ignite, log, orgId);
        }
    }

    /**
     *
     */
    private static class TestAffinityRun implements IgniteRunnable {
        /**
         * Persons count getter.
         */
        IgniteCacheLockPartitionOnAffinityRunTest.PersonsCountGetter personsCntGetter;

        /**
         * Org id.
         */
        int orgId;

        /**
         *
         */
        @IgniteInstanceResource
        private IgniteEx ignite;

        /**
         *
         */
        @LoggerResource
        private IgniteLogger log;

        /**
         *
         */
        public TestAffinityRun() {
            // No-op.
        }

        /**
         *
         *
         * @param personsCntGetter
         * 		Object to count Person.
         * @param orgId
         * 		Organization Id.
         */
        public TestAffinityRun(IgniteCacheLockPartitionOnAffinityRunTest.PersonsCountGetter personsCntGetter, int orgId) {
            this.personsCntGetter = personsCntGetter;
            this.orgId = orgId;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            try {
                log.info(("Begin run. orgId=" + (orgId)));
                int cnt = personsCntGetter.getPersonsCount(ignite, log, orgId);
                assertEquals(IgniteCacheLockPartitionOnAffinityRunAbstractTest.PERS_AT_ORG_CNT, cnt);
            } catch (Exception e) {
                throw new IgniteException(e);
            }
        }
    }

    /**
     *
     */
    static class JobFailUnmarshaling implements Externalizable , IgniteRunnable {
        /**
         * Default constructor (required by Externalizable).
         */
        public JobFailUnmarshaling() {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            // No-op.
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            throw new IOException("Test job unmarshaling fails");
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void run() {
            fail("Must not be executed");
        }
    }
}

