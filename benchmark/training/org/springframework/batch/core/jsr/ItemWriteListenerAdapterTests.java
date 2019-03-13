/**
 * Copyright 2013 the original author or authors.
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


import java.util.ArrayList;
import java.util.List;
import javax.batch.api.chunk.listener.ItemWriteListener;
import javax.batch.operations.BatchRuntimeException;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class ItemWriteListenerAdapterTests {
    private ItemWriteListenerAdapter<String> adapter;

    @Mock
    private ItemWriteListener delegate;

    private List items = new ArrayList();

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        adapter = new ItemWriteListenerAdapter(null);
    }

    @Test
    public void testBeforeWrite() throws Exception {
        adapter.beforeWrite(items);
        Mockito.verify(delegate).beforeWrite(items);
    }

    @Test(expected = BatchRuntimeException.class)
    public void testBeforeTestWriteException() throws Exception {
        Mockito.doThrow(new Exception("expected")).when(delegate).beforeWrite(items);
        adapter.beforeWrite(items);
    }

    @Test
    public void testAfterWrite() throws Exception {
        adapter.afterWrite(items);
        Mockito.verify(delegate).afterWrite(items);
    }

    @Test(expected = BatchRuntimeException.class)
    public void testAfterTestWriteException() throws Exception {
        Mockito.doThrow(new Exception("expected")).when(delegate).afterWrite(items);
        adapter.afterWrite(items);
    }

    @Test
    public void testOnWriteError() throws Exception {
        Exception cause = new Exception("cause");
        adapter.onWriteError(cause, items);
        Mockito.verify(delegate).onWriteError(items, cause);
    }

    @Test(expected = BatchRuntimeException.class)
    public void testOnWriteErrorException() throws Exception {
        Exception cause = new Exception("cause");
        Mockito.doThrow(new Exception("expected")).when(delegate).onWriteError(items, cause);
        adapter.onWriteError(cause, items);
    }
}

