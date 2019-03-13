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
package org.springframework.batch.repeat.exception;


import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.springframework.batch.repeat.RepeatContext;


public class CompositeExceptionHandlerTests extends TestCase {
    private CompositeExceptionHandler handler = new CompositeExceptionHandler();

    public void testNewHandler() throws Throwable {
        try {
            handler.handleException(null, new RuntimeException());
        } catch (RuntimeException e) {
            TestCase.fail("Unexpected RuntimeException");
        }
    }

    public void testDelegation() throws Throwable {
        final List<String> list = new ArrayList<>();
        handler.setHandlers(new ExceptionHandler[]{ new ExceptionHandler() {
            @Override
            public void handleException(RepeatContext context, Throwable throwable) throws RuntimeException {
                list.add("1");
            }
        }, new ExceptionHandler() {
            @Override
            public void handleException(RepeatContext context, Throwable throwable) throws RuntimeException {
                list.add("2");
            }
        } });
        handler.handleException(null, new RuntimeException());
        TestCase.assertEquals(2, list.size());
        TestCase.assertEquals("1", list.get(0));
        TestCase.assertEquals("2", list.get(1));
    }
}

