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
package alluxio.master.file;


import alluxio.exception.status.UnavailableException;
import alluxio.master.journal.JournalContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;


/**
 * Unit tests for {@link RpcContext}.
 */
public final class RpcContextTest {
    private BlockDeletionContext mMockBDC = Mockito.mock(BlockDeletionContext.class);

    private JournalContext mMockJC = Mockito.mock(JournalContext.class);

    private RpcContext mRpcContext;

    @Test
    public void success() throws Throwable {
        mRpcContext.close();
    }

    @Test
    public void order() throws Throwable {
        List<Object> order = new ArrayList<>();
        Mockito.doAnswer(( unused) -> order.add(mMockJC)).when(mMockJC).close();
        Mockito.doAnswer(( unused) -> order.add(mMockBDC)).when(mMockBDC).close();
        mRpcContext.close();
        Assert.assertEquals(Arrays.asList(mMockJC, mMockBDC), order);
    }

    @Test
    public void throwTwoRuntimeExceptions() throws Throwable {
        Exception bdcException = new IllegalStateException("block deletion context exception");
        Exception jcException = new IllegalArgumentException("journal context exception");
        Mockito.doThrow(bdcException).when(mMockBDC).close();
        Mockito.doThrow(jcException).when(mMockJC).close();
        try {
            mRpcContext.close();
            Assert.fail("Expected an exception to be thrown");
        } catch (RuntimeException e) {
            Assert.assertEquals(jcException, e);
            // journal context is closed first, so the block deletion context exception should be
            // suppressed.
            Assert.assertEquals(bdcException, e.getSuppressed()[0]);
        }
    }

    @Test
    public void throwTwoUnavailableExceptions() throws Throwable {
        Exception bdcException = new UnavailableException("block deletion context exception");
        Exception jcException = new UnavailableException("journal context exception");
        Mockito.doThrow(bdcException).when(mMockBDC).close();
        Mockito.doThrow(jcException).when(mMockJC).close();
        try {
            mRpcContext.close();
            Assert.fail("Expected an exception to be thrown");
        } catch (UnavailableException e) {
            Assert.assertEquals(jcException, e);
            // journal context is closed first, so the block deletion context exception should be
            // suppressed.
            Assert.assertEquals(bdcException, e.getSuppressed()[0]);
        }
    }

    @Test
    public void blockDeletionContextThrows() throws Throwable {
        Exception bdcException = new UnavailableException("block deletion context exception");
        Mockito.doThrow(bdcException).when(mMockBDC).close();
        checkClose(bdcException);
    }

    @Test
    public void journalContextThrows() throws Throwable {
        Exception jcException = new UnavailableException("journal context exception");
        Mockito.doThrow(jcException).when(mMockJC).close();
        checkClose(jcException);
    }
}

