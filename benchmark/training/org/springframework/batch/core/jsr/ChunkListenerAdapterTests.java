/**
 * Copyright 2013-2014 the original author or authors.
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
package org.springframework.batch.core.jsr;


import javax.batch.api.chunk.listener.ChunkListener;
import javax.batch.operations.BatchRuntimeException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.batch.core.ChunkListener.ROLLBACK_EXCEPTION_KEY;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.UncheckedTransactionException;


public class ChunkListenerAdapterTests {
    private ChunkListenerAdapter adapter;

    @Mock
    private ChunkListener delegate;

    @Mock
    private ChunkContext context;

    @Test(expected = IllegalArgumentException.class)
    public void testNullDelegate() {
        adapter = new ChunkListenerAdapter(null);
    }

    @Test
    public void testBeforeChunk() throws Exception {
        adapter.beforeChunk(null);
        Mockito.verify(delegate).beforeChunk();
    }

    @Test(expected = UncheckedTransactionException.class)
    public void testBeforeChunkException() throws Exception {
        Mockito.doThrow(new Exception("This is expected")).when(delegate).beforeChunk();
        adapter.beforeChunk(null);
    }

    @Test
    public void testAfterChunk() throws Exception {
        adapter.afterChunk(null);
        Mockito.verify(delegate).afterChunk();
    }

    @Test(expected = UncheckedTransactionException.class)
    public void testAfterChunkException() throws Exception {
        Mockito.doThrow(new Exception("This is expected")).when(delegate).afterChunk();
        adapter.afterChunk(null);
    }

    @Test(expected = BatchRuntimeException.class)
    public void testAfterChunkErrorNullContext() throws Exception {
        adapter.afterChunkError(null);
    }

    @Test(expected = UncheckedTransactionException.class)
    public void testAfterChunkErrorException() throws Exception {
        Mockito.doThrow(new Exception("This is expected")).when(delegate).afterChunk();
        adapter.afterChunk(null);
    }

    @Test
    public void testAfterChunkError() throws Exception {
        Exception exception = new Exception("This was expected");
        Mockito.when(context.getAttribute(ROLLBACK_EXCEPTION_KEY)).thenReturn(exception);
        adapter.afterChunkError(context);
        Mockito.verify(delegate).onError(exception);
    }
}

