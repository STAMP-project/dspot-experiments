/**
 * Copyright 2006-2013 the original author or authors.
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
package org.springframework.batch.core.step.item;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;


/**
 *
 *
 * @author Dave Syer
 */
public class ChunkMonitorTests {
    private static final int CHUNK_SIZE = 5;

    private ChunkMonitor monitor = new ChunkMonitor();

    private int count = 0;

    private boolean closed = false;

    @Test
    public void testIncrementOffset() {
        Assert.assertEquals(0, monitor.getOffset());
        monitor.incrementOffset();
        Assert.assertEquals(1, monitor.getOffset());
    }

    @Test
    public void testResetOffsetManually() {
        monitor.incrementOffset();
        monitor.resetOffset();
        Assert.assertEquals(0, monitor.getOffset());
    }

    @Test
    public void testResetOffsetAutomatically() {
        for (int i = 0; i < (ChunkMonitorTests.CHUNK_SIZE); i++) {
            monitor.incrementOffset();
        }
        Assert.assertEquals(0, monitor.getOffset());
    }

    @Test
    public void testClose() {
        monitor.incrementOffset();
        monitor.close();
        Assert.assertTrue(closed);
        Assert.assertEquals(0, monitor.getOffset());
    }

    @Test
    public void testOpen() {
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.putInt(((ChunkMonitor.class.getName()) + ".OFFSET"), 2);
        monitor.open(executionContext);
        Assert.assertEquals(2, count);
        Assert.assertEquals(0, monitor.getOffset());
    }

    @Test
    public void testOpenWithNullReader() {
        monitor.setItemReader(null);
        ExecutionContext executionContext = new ExecutionContext();
        monitor.open(executionContext);
        Assert.assertEquals(0, monitor.getOffset());
    }

    @Test(expected = ItemStreamException.class)
    public void testOpenWithErrorInReader() {
        monitor.setItemReader(new org.springframework.batch.item.ItemReader<String>() {
            @Override
            public String read() throws Exception, ParseException, UnexpectedInputException {
                throw new IllegalStateException("Expected");
            }
        });
        ExecutionContext executionContext = new ExecutionContext();
        executionContext.putInt(((ChunkMonitor.class.getName()) + ".OFFSET"), 2);
        monitor.open(executionContext);
    }

    @Test
    public void testUpdateOnBoundary() {
        monitor.resetOffset();
        ExecutionContext executionContext = new ExecutionContext();
        monitor.update(executionContext);
        Assert.assertEquals(0, executionContext.size());
        executionContext.put(((ChunkMonitor.class.getName()) + ".OFFSET"), 3);
        monitor.update(executionContext);
        Assert.assertEquals(0, executionContext.size());
    }

    @Test
    public void testUpdateVanilla() {
        monitor.incrementOffset();
        ExecutionContext executionContext = new ExecutionContext();
        monitor.update(executionContext);
        Assert.assertEquals(1, executionContext.size());
    }

    @Test
    public void testUpdateWithNoStream() throws Exception {
        monitor = new ChunkMonitor();
        monitor.setItemReader(new org.springframework.batch.item.ItemReader<String>() {
            @Override
            public String read() throws Exception, ParseException, UnexpectedInputException {
                return "" + ((count)++);
            }
        });
        monitor.setChunkSize(ChunkMonitorTests.CHUNK_SIZE);
        monitor.incrementOffset();
        ExecutionContext executionContext = new ExecutionContext();
        monitor.update(executionContext);
        Assert.assertEquals(0, executionContext.size());
    }
}

