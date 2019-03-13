/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.flume.channel;


import java.util.concurrent.Future;
import org.apache.flume.Event;
import org.apache.flume.Transaction;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.flume.channel.AbstractBasicChannelSemanticsTest.TestChannel.Mode.SLEEP;


public class TestBasicChannelSemantics extends AbstractBasicChannelSemanticsTest {
    @Test
    public void testHappyPath() {
        for (int i = 0; i < (AbstractBasicChannelSemanticsTest.events.size()); ++i) {
            Transaction transaction = getTransaction();
            transaction.begin();
            channel.put(AbstractBasicChannelSemanticsTest.events.get(i));
            transaction.commit();
            transaction.close();
        }
        for (int i = 0; i < (AbstractBasicChannelSemanticsTest.events.size()); ++i) {
            Transaction transaction = getTransaction();
            transaction.begin();
            Assert.assertSame(AbstractBasicChannelSemanticsTest.events.get(i), take());
            transaction.commit();
            transaction.close();
        }
    }

    @Test
    public void testMultiThreadedHappyPath() throws Exception {
        final int testLength = 1000;
        Future<?> producer = executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                    for (int i = 0; i < testLength; ++i) {
                        Transaction transaction = getTransaction();
                        transaction.begin();
                        channel.put(AbstractBasicChannelSemanticsTest.events.get((i % (AbstractBasicChannelSemanticsTest.events.size()))));
                        transaction.commit();
                        transaction.close();
                        Thread.sleep(1);
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Assert.fail();
                }
            }
        });
        int i = 0;
        while (!(producer.isDone())) {
            Transaction transaction = getTransaction();
            transaction.begin();
            Event event = channel.take();
            if (event != null) {
                Assert.assertSame(AbstractBasicChannelSemanticsTest.events.get((i % (AbstractBasicChannelSemanticsTest.events.size()))), event);
                ++i;
            }
            transaction.commit();
            transaction.close();
        } 
        Assert.assertEquals(testLength, i);
        producer.get();
    }

    @Test
    public void testGetTransaction() throws Exception {
        final Transaction transaction = getTransaction();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotSame(transaction, channel.getTransaction());
            }
        }).get();
        Assert.assertSame(transaction, channel.getTransaction());
        transaction.begin();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotSame(transaction, channel.getTransaction());
            }
        }).get();
        Assert.assertSame(transaction, channel.getTransaction());
        transaction.commit();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotSame(transaction, channel.getTransaction());
            }
        }).get();
        Assert.assertSame(transaction, channel.getTransaction());
        transaction.close();
        executor.submit(new Runnable() {
            @Override
            public void run() {
                Assert.assertNotSame(transaction, channel.getTransaction());
            }
        }).get();
        Assert.assertNotSame(transaction, channel.getTransaction());
    }

    @Test
    public void testBegin() throws Exception {
        final Transaction transaction = getTransaction();
        testExceptions(new Runnable() {
            @Override
            public void run() {
                transaction.begin();
            }
        });
        transaction.begin();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.begin();
            }
        });
        transaction.commit();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.begin();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.begin();
            }
        });
    }

    @Test
    public void testPut1() throws Exception {
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
        Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
        transaction.begin();
        channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
        testIllegalArgument(new Runnable() {
            @Override
            public void run() {
                put(null);
            }
        });
        testExceptions(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
        transaction.commit();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
    }

    @Test
    public void testPut2() throws Exception {
        Transaction transaction = getTransaction();
        transaction.begin();
        channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
    }

    @Test
    public void testPut3() throws Exception {
        Transaction transaction = getTransaction();
        transaction.begin();
        channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
        final Transaction finalTransaction = transaction;
        testChannelException(new Runnable() {
            @Override
            public void run() {
                finalTransaction.commit();
            }
        });
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                channel.put(AbstractBasicChannelSemanticsTest.events.get(0));
            }
        });
    }

    @Test
    public void testTake1() throws Exception {
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        transaction.begin();
        Assert.assertNull(take());
        for (int i = 0; i < 1000; ++i) {
            channel.put(AbstractBasicChannelSemanticsTest.events.get((i % (AbstractBasicChannelSemanticsTest.events.size()))));
        }
        Assert.assertNotNull(take());
        testWrongThread(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        testBasicExceptions(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        testMode(SLEEP, new Runnable() {
            @Override
            public void run() {
                interruptTest(new Runnable() {
                    @Override
                    public void run() {
                        Assert.assertNull(take());
                        Assert.assertTrue(Thread.interrupted());
                    }
                });
            }
        });
        Assert.assertNotNull(take());
        transaction.commit();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
    }

    @Test
    public void testTake2() throws Exception {
        Transaction transaction = getTransaction();
        transaction.begin();
        take();
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
    }

    @Test
    public void testTake3() throws Exception {
        Transaction transaction = getTransaction();
        transaction.begin();
        take();
        final Transaction finalTransaction = transaction;
        testChannelException(new Runnable() {
            @Override
            public void run() {
                finalTransaction.commit();
            }
        });
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                take();
            }
        });
    }

    @Test
    public void testCommit1() throws Exception {
        final Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
        transaction.begin();
        testExceptions(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
        transaction.commit();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
    }

    @Test
    public void testCommit2() throws Exception {
        final Transaction transaction = getTransaction();
        transaction.begin();
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
    }

    @Test
    public void testRollback1() throws Exception {
        final Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.begin();
        testWrongThread(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testRollback2() throws Exception {
        final Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.begin();
        testError(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testRollback3() throws Exception {
        final Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.begin();
        testRuntimeException(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testRollback4() throws Exception {
        final Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.begin();
        testChannelException(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testRollback5() throws Exception {
        final Transaction transaction = getTransaction();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.begin();
        testInterrupt(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testRollback6() throws Exception {
        final Transaction transaction = getTransaction();
        transaction.begin();
        transaction.commit();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testRollback7() throws Exception {
        final Transaction transaction = getTransaction();
        transaction.begin();
        testExceptions(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
        transaction.rollback();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
        transaction.close();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.rollback();
            }
        });
    }

    @Test
    public void testClose1() throws Exception {
        final Transaction transaction = getTransaction();
        testError(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
    }

    @Test
    public void testClose2() throws Exception {
        final Transaction transaction = getTransaction();
        testRuntimeException(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
    }

    @Test
    public void testClose3() throws Exception {
        final Transaction transaction = getTransaction();
        testChannelException(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
    }

    @Test
    public void testClose4() throws Exception {
        final Transaction transaction = getTransaction();
        transaction.begin();
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
    }

    @Test
    public void testClose5() throws Exception {
        final Transaction transaction = getTransaction();
        transaction.begin();
        testChannelException(new Runnable() {
            @Override
            public void run() {
                transaction.commit();
            }
        });
        testIllegalState(new Runnable() {
            @Override
            public void run() {
                transaction.close();
            }
        });
    }
}

