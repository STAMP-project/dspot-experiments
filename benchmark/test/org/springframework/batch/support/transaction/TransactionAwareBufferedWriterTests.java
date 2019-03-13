/**
 * Copyright 2006-2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.batch.support.transaction;


import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.transaction.PlatformTransactionManager;


/**
 *
 *
 * @author Dave Syer
 * @author Michael Minella
 * @author Will Schipp
 */
public class TransactionAwareBufferedWriterTests {
    private FileChannel fileChannel;

    private TransactionAwareBufferedWriter writer;

    private PlatformTransactionManager transactionManager = new ResourcelessTransactionManager();

    /**
     * Test method for
     * {@link org.springframework.batch.support.transaction.TransactionAwareBufferedWriter#write(java.lang.String)}
     * .
     */
    @Test
    public void testWriteOutsideTransaction() throws Exception {
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(3);
        writer.write("foo");
        writer.flush();
        // Not closed yet
        String s = getStringFromByteBuffer(bb.getValue());
        Assert.assertEquals("foo", s);
        Mockito.verify(fileChannel, Mockito.never()).force(false);
    }

    @Test
    public void testWriteOutsideTransactionForceSync() throws Exception {
        writer.setForceSync(true);
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(3);
        writer.write("foo");
        writer.flush();
        // Not closed yet
        String s = getStringFromByteBuffer(bb.getValue());
        Assert.assertEquals("foo", s);
        Mockito.verify(fileChannel, Mockito.times(1)).force(false);
    }

    @Test
    public void testBufferSizeOutsideTransaction() throws Exception {
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(3);
        writer.write("foo");
        Assert.assertEquals(0, writer.getBufferSize());
    }

    @Test
    public void testCloseOutsideTransaction() throws Exception {
        ArgumentCaptor<ByteBuffer> byteBufferCaptor = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(byteBufferCaptor.capture())).thenAnswer(( invocation) -> ((ByteBuffer) (invocation.getArguments()[0])).remaining());
        writer.write("foo");
        writer.close();
        Assert.assertEquals("foo", getStringFromByteBuffer(byteBufferCaptor.getAllValues().get(0)));
        Assert.assertEquals("c", getStringFromByteBuffer(byteBufferCaptor.getAllValues().get(1)));
    }

    @Test
    public void testFlushInTransaction() throws Exception {
        Mockito.when(fileChannel.write(ArgumentMatchers.any(ByteBuffer.class))).thenReturn(3);
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                writer.write("foo");
                writer.flush();
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            assertEquals(3, writer.getBufferSize());
            return null;
        })));
        Mockito.verify(fileChannel, Mockito.never()).force(false);
    }

    @Test
    public void testFlushInTransactionForceSync() throws Exception {
        writer.setForceSync(true);
        Mockito.when(fileChannel.write(ArgumentMatchers.any(ByteBuffer.class))).thenReturn(3);
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                writer.write("foo");
                writer.flush();
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            assertEquals(3, writer.getBufferSize());
            return null;
        })));
        Mockito.verify(fileChannel, Mockito.times(1)).force(false);
    }

    @Test
    public void testWriteWithCommit() throws Exception {
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(3);
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                writer.write("foo");
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            assertEquals(3, writer.getBufferSize());
            return null;
        })));
        Assert.assertEquals(0, writer.getBufferSize());
    }

    @Test
    public void testBufferSizeInTransaction() throws Exception {
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(3);
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                writer.write("foo");
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            assertEquals(3, writer.getBufferSize());
            return null;
        })));
        Assert.assertEquals(0, writer.getBufferSize());
    }

    // BATCH-1959
    @Test
    public void testBufferSizeInTransactionWithMultiByteCharacterUTF8() throws Exception {
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(5);
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                writer.write("f??");
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            assertEquals(5, writer.getBufferSize());
            return null;
        })));
        Assert.assertEquals(0, writer.getBufferSize());
    }

    // BATCH-1959
    @Test
    public void testBufferSizeInTransactionWithMultiByteCharacterUTF16BE() throws Exception {
        writer.setEncoding("UTF-16BE");
        ArgumentCaptor<ByteBuffer> bb = ArgumentCaptor.forClass(ByteBuffer.class);
        Mockito.when(fileChannel.write(bb.capture())).thenReturn(6);
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                writer.write("f??");
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            assertEquals(6, writer.getBufferSize());
            return null;
        })));
        Assert.assertEquals(0, writer.getBufferSize());
    }

    @Test
    public void testWriteWithRollback() throws Exception {
        try {
            execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
                try {
                    writer.write("foo");
                } catch ( e) {
                    throw new <e>IllegalStateException("Unexpected IOException");
                }
                throw new RuntimeException("Planned failure");
            })));
            Assert.fail("Exception was not thrown");
        } catch (RuntimeException e) {
            // expected
            String message = e.getMessage();
            Assert.assertEquals(("Wrong message:  " + message), "Planned failure", message);
        }
        Assert.assertEquals(0, writer.getBufferSize());
    }

    @Test
    public void testCleanUpAfterRollback() throws Exception {
        testWriteWithRollback();
        testWriteWithCommit();
    }

    @Test
    public void testExceptionOnFlush() throws Exception {
        writer = new TransactionAwareBufferedWriter(fileChannel, () -> {
        });
        try {
            execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
                try {
                    writer.write("foo");
                } catch ( e) {
                    throw new <e>IllegalStateException("Unexpected IOException");
                }
                return null;
            })));
            Assert.fail("Exception was not thrown");
        } catch (FlushFailedException ffe) {
            Assert.assertEquals("Could not write to output buffer", ffe.getMessage());
        }
    }

    // BATCH-2018
    @Test
    public void testResourceKeyCollision() throws Exception {
        final int limit = 5000;
        final TransactionAwareBufferedWriter[] writers = new TransactionAwareBufferedWriter[limit];
        final String[] results = new String[limit];
        for (int i = 0; i < limit; i++) {
            final int index = i;
            @SuppressWarnings("resource")
            FileChannel fileChannel = Mockito.mock(FileChannel.class);
            Mockito.when(fileChannel.write(ArgumentMatchers.any(ByteBuffer.class))).thenAnswer(( invocation) -> {
                ByteBuffer buffer = ((ByteBuffer) (invocation.getArguments()[0]));
                String val = new String(buffer.array(), "UTF-8");
                if ((results[index]) == null) {
                    results[index] = val;
                } else {
                    results[index] += val;
                }
                return buffer.limit();
            });
            writers[i] = new TransactionAwareBufferedWriter(fileChannel, null);
        }
        execute(((org.springframework.transaction.support.TransactionCallback<Void>) (( status) -> {
            try {
                for (int i = 0; i < limit; i++) {
                    writers[i].write(String.valueOf(i));
                }
            } catch ( e) {
                throw new <e>IllegalStateException("Unexpected IOException");
            }
            return null;
        })));
        for (int i = 0; i < limit; i++) {
            Assert.assertEquals(String.valueOf(i), results[i]);
        }
    }
}

