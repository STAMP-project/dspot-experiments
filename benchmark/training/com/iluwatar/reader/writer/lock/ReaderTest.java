/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Sepp?l?
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.reader.writer.lock;


import com.iluwatar.reader.writer.lock.utils.InMemoryAppender;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author hongshuwei@gmail.com
 */
public class ReaderTest {
    private InMemoryAppender appender;

    private static final Logger LOGGER = LoggerFactory.getLogger(ReaderTest.class);

    /**
     * Verify that multiple readers can get the read lock concurrently
     */
    @Test
    public void testRead() throws Exception {
        ExecutorService executeService = Executors.newFixedThreadPool(2);
        ReaderWriterLock lock = new ReaderWriterLock();
        Reader reader1 = Mockito.spy(new Reader("Reader 1", lock.readLock()));
        Reader reader2 = Mockito.spy(new Reader("Reader 2", lock.readLock()));
        executeService.submit(reader1);
        Thread.sleep(150);
        executeService.submit(reader2);
        executeService.shutdown();
        try {
            executeService.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            ReaderTest.LOGGER.error("Error waiting for ExecutorService shutdown", e);
        }
        // Read operation will hold the read lock 250 milliseconds, so here we prove that multiple reads
        // can be performed in the same time.
        Assertions.assertTrue(appender.logContains("Reader 1 begin"));
        Assertions.assertTrue(appender.logContains("Reader 2 begin"));
        Assertions.assertTrue(appender.logContains("Reader 1 finish"));
        Assertions.assertTrue(appender.logContains("Reader 2 finish"));
    }
}

