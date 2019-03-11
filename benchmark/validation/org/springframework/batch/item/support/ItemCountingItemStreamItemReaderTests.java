/**
 * Copyright 2006-2007 the original author or authors.
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
package org.springframework.batch.item.support;


import java.util.Arrays;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;


/**
 *
 *
 * @author Dave Syer
 */
public class ItemCountingItemStreamItemReaderTests {
    private ItemCountingItemStreamItemReaderTests.ItemCountingItemStreamItemReader reader = new ItemCountingItemStreamItemReaderTests.ItemCountingItemStreamItemReader();

    @Test
    public void testJumpToItem() throws Exception {
        jumpToItem(2);
        Assert.assertEquals(2, getCurrentItemCount());
        read();
        Assert.assertEquals(3, getCurrentItemCount());
    }

    @Test
    public void testGetCurrentItemCount() throws Exception {
        Assert.assertEquals(0, getCurrentItemCount());
        read();
        Assert.assertEquals(1, getCurrentItemCount());
    }

    @Test
    public void testClose() {
        close();
        Assert.assertTrue(reader.closeCalled);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testOpenWithoutName() {
        reader = new ItemCountingItemStreamItemReaderTests.ItemCountingItemStreamItemReader();
        reader.open(new ExecutionContext());
        Assert.assertFalse(reader.openCalled);
    }

    @Test
    public void testOpen() {
        reader.open(new ExecutionContext());
        Assert.assertTrue(reader.openCalled);
    }

    @Test
    public void testReadToEnd() throws Exception {
        read();
        read();
        read();
        Assert.assertNull(read());
    }

    @Test
    public void testUpdate() throws Exception {
        read();
        ExecutionContext context = new ExecutionContext();
        reader.update(context);
        Assert.assertEquals(1, context.size());
        Assert.assertEquals(1, context.getInt("foo.read.count"));
    }

    @Test
    public void testSetName() throws Exception {
        setName("bar");
        read();
        ExecutionContext context = new ExecutionContext();
        reader.update(context);
        Assert.assertEquals(1, context.getInt("bar.read.count"));
    }

    @Test
    public void testSetSaveState() throws Exception {
        read();
        ExecutionContext context = new ExecutionContext();
        reader.update(context);
        Assert.assertEquals(1, context.size());
    }

    @Test
    public void testReadToEndWithMax() throws Exception {
        ExecutionContext context = new ExecutionContext();
        context.putInt("foo.read.count.max", 1);
        reader.open(context);
        read();
        Assert.assertNull(read());
    }

    @Test
    public void testUpdateWithMax() throws Exception {
        ExecutionContext context = new ExecutionContext();
        context.putInt("foo.read.count.max", 1);
        reader.open(context);
        reader.update(context);
        Assert.assertEquals(2, context.size());
    }

    private static class ItemCountingItemStreamItemReader extends AbstractItemCountingItemStreamItemReader<String> {
        private boolean closeCalled = false;

        private boolean openCalled = false;

        private Iterator<String> items = Arrays.asList("a", "b", "c").iterator();

        @Override
        protected void doClose() throws Exception {
            closeCalled = true;
        }

        @Override
        protected void doOpen() throws Exception {
            openCalled = true;
        }

        @Override
        protected String doRead() throws Exception {
            if (!(items.hasNext())) {
                return null;
            }
            return items.next();
        }
    }
}

