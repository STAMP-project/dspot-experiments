/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.master.journal;


import org.junit.Test;


/**
 * Unit tests for {@link AsyncJournalWriter}.
 */
public class AsyncJournalWriterTest {
    private JournalWriter mMockJournalWriter;

    private AsyncJournalWriter mAsyncJournalWriter;

    @Test(timeout = 10000)
    public void writesAndFlushes() throws Exception {
        writesAndFlushesInternal(false);
    }

    @Test(timeout = 10000)
    public void writesAndFlushesWithBatching() throws Exception {
        writesAndFlushesInternal(true);
    }

    @Test(timeout = 10000)
    public void failedWrite() throws Exception {
        failedWriteInternal(false);
    }

    @Test(timeout = 10000)
    public void failedWriteWithBatching() throws Exception {
        failedWriteInternal(true);
    }

    @Test(timeout = 10000)
    public void failedFlush() throws Exception {
        failedFlushInternal(false);
    }

    @Test(timeout = 10000)
    public void failedFlushWithBatching() throws Exception {
        failedFlushInternal(true);
    }
}

