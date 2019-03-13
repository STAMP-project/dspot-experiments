/**
 * Copyright 2006-2014 the original author or authors.
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
package org.springframework.batch.sample.common;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;


/**
 * Unit test class that was used as part of the Reference Documentation.  I'm only including it in the
 * code to help keep the reference documentation up to date as the code base shifts.
 *
 * @author Lucas Ward
 */
public class CustomItemReaderTests {
    private ItemReader<String> itemReader;

    @Test
    public void testRead() throws Exception {
        Assert.assertEquals("1", itemReader.read());
        Assert.assertEquals("2", itemReader.read());
        Assert.assertEquals("3", itemReader.read());
        Assert.assertNull(itemReader.read());
    }

    @Test
    public void testRestart() throws Exception {
        ExecutionContext executionContext = new ExecutionContext();
        ((ItemStream) (itemReader)).open(executionContext);
        Assert.assertEquals("1", itemReader.read());
        ((ItemStream) (itemReader)).update(executionContext);
        List<String> items = new ArrayList<>();
        items.add("1");
        items.add("2");
        items.add("3");
        itemReader = new CustomItemReaderTests.CustomItemReader(items);
        ((ItemStream) (itemReader)).open(executionContext);
        Assert.assertEquals("2", itemReader.read());
    }

    public static class CustomItemReader<T> implements ItemReader<T> , ItemStream {
        private static final String CURRENT_INDEX = "current.index";

        private List<T> items;

        private int currentIndex = 0;

        public CustomItemReader(List<T> items) {
            this.items = items;
        }

        @Override
        public T read() throws Exception {
            if ((currentIndex) < (items.size())) {
                return items.get(((currentIndex)++));
            }
            return null;
        }

        @Override
        public void open(ExecutionContext executionContext) throws ItemStreamException {
            if (executionContext.containsKey(CustomItemReaderTests.CustomItemReader.CURRENT_INDEX)) {
                currentIndex = executionContext.getInt(CustomItemReaderTests.CustomItemReader.CURRENT_INDEX);
            } else {
                currentIndex = 0;
            }
        }

        @Override
        public void close() throws ItemStreamException {
        }

        @Override
        public void update(ExecutionContext executionContext) throws ItemStreamException {
            executionContext.putInt(CustomItemReaderTests.CustomItemReader.CURRENT_INDEX, currentIndex);
        }
    }
}

