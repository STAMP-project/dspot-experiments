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
package org.springframework.batch.jsr.item;


import javax.batch.api.chunk.ItemProcessor;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ItemProcessorAdapterTests {
    private ItemProcessorAdapter<String, String> adapter;

    @Mock
    private ItemProcessor delegate;

    @Test(expected = IllegalArgumentException.class)
    public void testCreateWithNull() {
        adapter = new ItemProcessorAdapter(null);
    }

    @Test
    public void testProcess() throws Exception {
        String input = "input";
        String output = "output";
        Mockito.when(delegate.processItem(input)).thenReturn(output);
        Assert.assertEquals(output, adapter.process(input));
    }
}

