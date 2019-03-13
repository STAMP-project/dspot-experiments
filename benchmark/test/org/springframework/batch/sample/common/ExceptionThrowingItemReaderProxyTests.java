/**
 * Copyright 2008-2014 the original author or authors.
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
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.UnexpectedJobExecutionException;
import org.springframework.batch.repeat.context.RepeatContextSupport;
import org.springframework.batch.repeat.support.RepeatSynchronizationManager;
import org.springframework.batch.sample.support.ExceptionThrowingItemReaderProxy;


public class ExceptionThrowingItemReaderProxyTests {
    // expected call count before exception is thrown (exception should be thrown in next iteration)
    private static final int ITER_COUNT = 5;

    @SuppressWarnings("serial")
    @Test
    public void testProcess() throws Exception {
        // create module and set item processor and iteration count
        ExceptionThrowingItemReaderProxy<String> itemReader = new ExceptionThrowingItemReaderProxy();
        itemReader.setDelegate(new org.springframework.batch.item.support.ListItemReader(new ArrayList<String>() {
            {
                add("a");
                add("b");
                add("c");
                add("d");
                add("e");
                add("f");
            }
        }));
        itemReader.setThrowExceptionOnRecordNumber(((ExceptionThrowingItemReaderProxyTests.ITER_COUNT) + 1));
        RepeatSynchronizationManager.register(new RepeatContextSupport(null));
        // call process method multiple times and verify whether exception is thrown when expected
        for (int i = 0; i <= (ExceptionThrowingItemReaderProxyTests.ITER_COUNT); i++) {
            try {
                itemReader.read();
                Assert.assertTrue((i < (ExceptionThrowingItemReaderProxyTests.ITER_COUNT)));
            } catch (UnexpectedJobExecutionException bce) {
                Assert.assertEquals(ExceptionThrowingItemReaderProxyTests.ITER_COUNT, i);
            }
        }
    }
}

