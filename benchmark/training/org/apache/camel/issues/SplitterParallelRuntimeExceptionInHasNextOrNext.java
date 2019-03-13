/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.issues;


import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.apache.camel.ContextTestSupport;
import org.apache.camel.Exchange;
import org.junit.Test;


public class SplitterParallelRuntimeExceptionInHasNextOrNext extends ContextTestSupport {
    /**
     * Tests that only one aggregator thread is created if a RuntimeException in
     * the hasNext method of a custom iterator occurs.
     */
    @Test
    public void testSplitErrorInHasNext() throws Exception {
        execute("direct:errorInHasNext");
    }

    /**
     * Tests that only one aggregator thread is created if a RuntimeException in
     * the next method of a custom iterator occurs.
     */
    @Test
    public void testSplitErrorInNext() throws Exception {
        execute("direct:errorInNext");
    }

    public static class SplitterImpl {
        public Iterator<String> errorInHasNext(InputStream request, Exchange exchange) {
            return new SplitterParallelRuntimeExceptionInHasNextOrNext.CustomIterator(exchange, request, true);
        }

        public Iterator<String> errorInNext(InputStream request, Exchange exchange) {
            return new SplitterParallelRuntimeExceptionInHasNextOrNext.CustomIterator(exchange, request, false);
        }
    }

    static class CustomIterator implements Closeable , Iterator<String> {
        private int index;

        private InputStream request;

        private boolean errorInHasNext;

        CustomIterator(Exchange exchange, InputStream request, boolean errorInHasNext) {
            this.request = request;
            this.errorInHasNext = errorInHasNext;
        }

        @Override
        public boolean hasNext() {
            if ((index) < 7) {
                return true;
            }
            if (errorInHasNext) {
                throw new RuntimeException("Exception thrown");
            } else {
                return false;
            }
        }

        @Override
        public String next() {
            (index)++;
            if ((index) < 7) {
                return ("<a>" + (index)) + "</a>";
            }
            if (!(errorInHasNext)) {
                throw new RuntimeException("Exception thrown");
            } else {
                return ("<a>" + (index)) + "</a>";
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void close() throws IOException {
            request.close();
        }
    }
}

