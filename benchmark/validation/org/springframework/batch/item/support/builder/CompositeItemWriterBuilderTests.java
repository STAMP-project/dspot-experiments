/**
 * Copyright 2017-2018 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.springframework.batch.item.support.builder;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;


/**
 *
 *
 * @author Glenn Renfro
 * @author Drummond Dawson
 */
public class CompositeItemWriterBuilderTests {
    @Test
    @SuppressWarnings("unchecked")
    public void testProcess() throws Exception {
        final int NUMBER_OF_WRITERS = 10;
        List<Object> data = Collections.singletonList(new Object());
        List<ItemWriter<? super Object>> writers = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_WRITERS; i++) {
            ItemWriter<? super Object> writer = Mockito.mock(ItemWriter.class);
            writers.add(writer);
        }
        CompositeItemWriter<Object> itemWriter = new CompositeItemWriterBuilder().delegates(writers).build();
        itemWriter.write(data);
        for (ItemWriter<? super Object> writer : writers) {
            Mockito.verify(writer).write(data);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testProcessVarargs() throws Exception {
        List<Object> data = Collections.singletonList(new Object());
        List<ItemWriter<? super Object>> writers = new ArrayList<>();
        ItemWriter<? super Object> writer1 = Mockito.mock(ItemWriter.class);
        writers.add(writer1);
        ItemWriter<? super Object> writer2 = Mockito.mock(ItemWriter.class);
        writers.add(writer2);
        CompositeItemWriter<Object> itemWriter = new CompositeItemWriterBuilder().delegates(writer1, writer2).build();
        itemWriter.write(data);
        for (ItemWriter<? super Object> writer : writers) {
            Mockito.verify(writer).write(data);
        }
    }

    @Test
    public void isStreamOpen() throws Exception {
        ignoreItemStream(false);
        ignoreItemStream(true);
    }
}

