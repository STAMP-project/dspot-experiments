/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2014 Red Hat, Inc., and individual contributors
 * as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.undertow.servlet.test.streams;


import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import org.junit.Test;


/**
 *
 *
 * @author Stuart Douglas
 */
public abstract class AbstractServletInputStreamTestCase {
    public static final String HELLO_WORLD = "Hello World";

    public static final String BLOCKING_SERVLET = "blockingInput";

    public static final String ASYNC_SERVLET = "asyncInput";

    @Test
    public void testBlockingServletInputStream() {
        StringBuilder builder = new StringBuilder((1000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 1000; ++j) {
                    builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
                }
                String message = builder.toString();
                runTest(message, AbstractServletInputStreamTestCase.BLOCKING_SERVLET, false, false);
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletInputStream() {
        // for(int h = 0; h < 20 ; ++h) {
        StringBuilder builder = new StringBuilder((1000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
                }
                String message = builder.toString();
                runTest(message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, false, false);
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
        // }
    }

    @Test
    public void testAsyncServletInputStreamWithPreamble() {
        StringBuilder builder = new StringBuilder((2000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
                }
                String message = builder.toString();
                runTest(message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, true, false);
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletInputStreamInParallel() throws Exception {
        StringBuilder builder = new StringBuilder((100000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int j = 0; j < 100000; ++j) {
            builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
        }
        String message = builder.toString();
        runTestParallel(20, message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, false, false);
    }

    @Test
    public void testAsyncServletInputStreamInParallelOffIoThread() throws Exception {
        StringBuilder builder = new StringBuilder((100000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int j = 0; j < 100000; ++j) {
            builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
        }
        String message = builder.toString();
        runTestParallel(20, message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, false, true);
    }

    @Test
    public void testAsyncServletInputStreamOffIoThread() {
        StringBuilder builder = new StringBuilder((2000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
                }
                String message = builder.toString();
                runTest(message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, false, true);
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletInputStreamOffIoThreadWithPreamble() {
        StringBuilder builder = new StringBuilder((2000 * (AbstractServletInputStreamTestCase.HELLO_WORLD.length())));
        for (int i = 0; i < 10; ++i) {
            try {
                for (int j = 0; j < 10000; ++j) {
                    builder.append(AbstractServletInputStreamTestCase.HELLO_WORLD);
                }
                String message = builder.toString();
                runTest(message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, true, true);
            } catch (Throwable e) {
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    @Test
    public void testAsyncServletInputStreamWithEmptyRequestBody() {
        String message = "";
        try {
            runTest(message, AbstractServletInputStreamTestCase.ASYNC_SERVLET, false, false);
        } catch (Throwable e) {
            throw new RuntimeException("test failed", e);
        }
    }

    @Test
    public void testAsyncServletInputStream3() {
        String message = "to_user_id=7999&msg_body=msg3";
        for (int i = 0; i < 200; ++i) {
            try {
                runTestViaJavaImpl(message, AbstractServletInputStreamTestCase.ASYNC_SERVLET);
            } catch (Throwable e) {
                System.out.println(("test failed with i equal to " + i));
                e.printStackTrace();
                throw new RuntimeException(("test failed with i equal to " + i), e);
            }
        }
    }

    private static final class RateLimitedInputStream extends InputStream {
        private final InputStream in;

        private int count;

        RateLimitedInputStream(InputStream in) {
            this.in = in;
        }

        @Override
        public int read() throws IOException {
            if ((((count)++) % 1000) == 0) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new InterruptedIOException();
                }
            }
            return in.read();
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }
}

