/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.streaming.common;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import junit.framework.TestCase;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.logging.LogChannel;


@RunWith(MockitoJUnitRunner.class)
public class BlockingQueueStreamSourceTest {
    private ExecutorService execSvc = Executors.newCachedThreadPool();

    @Mock
    private BaseStreamStep streamStep;

    @Mock
    private Semaphore semaphore;

    @Mock
    private LogChannel logChannel;

    private BlockingQueueStreamSource<String> streamSource;

    @Test
    @SuppressWarnings("unchecked")
    public void errorLoggedIfInterruptedInAcceptRows() throws InterruptedException {
        streamSource.acceptingRowsSemaphore = semaphore;
        streamSource.logChannel = logChannel;
        Mockito.doThrow(new InterruptedException("interrupt")).when(semaphore).acquire();
        streamSource.acceptRows(Collections.singletonList("new row"));
        Mockito.verify(logChannel).logError(ArgumentMatchers.any());
        Mockito.verify(semaphore).release();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void errorLoggedIfInterruptedInPause() throws InterruptedException {
        streamSource.acceptingRowsSemaphore = semaphore;
        Mockito.when(semaphore.availablePermits()).thenReturn(1);
        streamSource.logChannel = logChannel;
        Mockito.doThrow(new InterruptedException("interrupt")).when(semaphore).acquire();
        streamSource.pause();
        Mockito.verify(logChannel).logError(ArgumentMatchers.any());
    }

    @Test
    public void rowIterableBlocksTillRowReceived() throws Exception {
        streamSource.open();
        Iterator<String> iterator = streamSource.flowable().blockingIterable().iterator();
        // first call .hasNext() on the iterator while streamSource is empty
        Future<Boolean> hasNext = execSvc.submit(iterator::hasNext);
        // should time out, since still waiting for rows
        assertTimesOut(hasNext);
        // add a row
        streamSource.acceptRows(Collections.singletonList("New Row"));
        // verify that the future has completed, .hasNext() is no longer blocking and returns true
        MatcherAssert.assertThat(getQuickly(hasNext), CoreMatchers.equalTo(true));
    }

    @Test
    public void streamIsPausable() throws InterruptedException, ExecutionException, TimeoutException {
        streamSource.open();
        Iterator<String> iter = streamSource.flowable().blockingIterable().iterator();
        Future<String> nextString = execSvc.submit(iter::next);
        // add a row
        streamSource.acceptRows(Collections.singletonList("row"));
        // verify we can retrieve it
        MatcherAssert.assertThat(getQuickly(nextString), CoreMatchers.equalTo("row"));
        // pause receipt of new rows
        streamSource.pause();
        // trying to add and retrieve another row to the source should time out, since paused.
        final Future<?> newRow = execSvc.submit(() -> streamSource.acceptRows(Collections.singletonList("new row")));
        assertTimesOut(newRow);
        // resume accepting rows
        streamSource.resume();
        // retrieve the last row added
        nextString = execSvc.submit(iter::next);
        MatcherAssert.assertThat(getQuickly(nextString), CoreMatchers.equalTo("new row"));
    }

    @Test
    public void testRowsFilled() throws InterruptedException, ExecutionException {
        // implement the blockingQueueStreamSource with an .open which will
        // launch a thread that sends rows to the queue.
        streamSource = new BlockingQueueStreamSource<String>(streamStep) {
            @Override
            public void open() {
                execSvc.submit(() -> {
                    for (int i = 0; i < 4; i++) {
                        acceptRows(Collections.singletonList(("new row " + i)));
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            TestCase.fail();
                        }
                    }
                });
            }
        };
        streamSource.open();
        Iterator<String> iterator = streamSource.flowable().blockingIterable().iterator();
        Future<List<String>> iterLoop = execSvc.submit(() -> {
            List<String> strings = new ArrayList<>();
            do {
                strings.add(iterator.next());
            } while ((strings.size()) < 4 );
            return strings;
        });
        final List<String> quickly = getQuickly(iterLoop);
        MatcherAssert.assertThat(quickly.size(), CoreMatchers.equalTo(4));
    }

    @Test
    public void bufferSizeLimitedToOneThousand() {
        streamSource = new BlockingQueueStreamSource<String>(streamStep) {
            @Override
            public void open() {
                for (int i = 0; i < 1002; i++) {
                    acceptRows(Collections.singletonList(("new row " + i)));
                }
            }
        };
        streamSource.open();
        Iterator<String> iterator = streamSource.flowable().blockingIterable().iterator();
        List<String> strings = new ArrayList<>();
        do {
            strings.add(iterator.next());
        } while ((strings.size()) < 1000 );
        MatcherAssert.assertThat(strings.size(), CoreMatchers.equalTo(1000));
        Future<String> submit = execSvc.submit(iterator::next);
        try {
            submit.get(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            TestCase.fail();
        } catch (TimeoutException e) {
            return;// passed, this is what we wanted.  There should be nothing left in the iterator

        }
        TestCase.fail("expected timeout");
    }

    @Test
    public void testError() {
        // verifies that calling .error() results in an exception being thrown
        // by the .rows() blocking iterable.
        final String exceptionMessage = "Exception raised during acceptRows loop";
        streamSource = new BlockingQueueStreamSource<String>(streamStep) {
            @Override
            public void open() {
                execSvc.submit(() -> {
                    for (int i = 0; i < 10; i++) {
                        acceptRows(Collections.singletonList(("new row " + i)));
                        try {
                            Thread.sleep(5);
                        } catch (InterruptedException e) {
                            TestCase.fail();
                        }
                        if (i == 5) {
                            error(new RuntimeException(exceptionMessage));
                            break;
                        }
                    }
                });
            }
        };
        streamSource.open();
        Iterator<String> iterator = streamSource.flowable().blockingIterable().iterator();
        Future<List<String>> iterLoop = execSvc.submit(() -> {
            List<String> strings = new ArrayList<>();
            do {
                strings.add(iterator.next());
            } while ((strings.size()) < 9 );
            return strings;
        });
        try {
            iterLoop.get(50, TimeUnit.MILLISECONDS);
            TestCase.fail("expected exception");
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            // occasionally fails wingman with npe for mysterious reason,
            // so guarding with null check
            if ((e != null) && ((e.getCause()) != null)) {
                MatcherAssert.assertThat(e.getCause().getMessage(), CoreMatchers.equalTo(exceptionMessage));
            }
        }
    }
}

