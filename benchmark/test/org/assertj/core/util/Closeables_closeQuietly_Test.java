/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.util;


import java.io.Closeable;
import java.io.IOException;
import org.junit.jupiter.api.Test;


/**
 * Tests for {@link Closeables#closeQuietly(Closeable...)}.
 *
 * @author Yvonne Wang
 */
public class Closeables_closeQuietly_Test {
    @Test
    public void should_close_Closeables() {
        Closeables_closeQuietly_Test.CloseableStub[] toClose = new Closeables_closeQuietly_Test.CloseableStub[]{ new Closeables_closeQuietly_Test.CloseableStub(), new Closeables_closeQuietly_Test.CloseableStub() };
        Closeables.closeQuietly(toClose);
        assertClosed(toClose);
    }

    @Test
    public void should_ignore_thrown_errors() {
        Closeables_closeQuietly_Test.CloseableStub[] toClose = new Closeables_closeQuietly_Test.CloseableStub[]{ new Closeables_closeQuietly_Test.CloseableStub(new IOException("")), new Closeables_closeQuietly_Test.CloseableStub() };
        Closeables.closeQuietly(toClose);
        assertClosed(toClose);
    }

    @Test
    public void should_ignore_null_Closeables() {
        Closeables_closeQuietly_Test.CloseableStub c = new Closeables_closeQuietly_Test.CloseableStub();
        Closeables_closeQuietly_Test.CloseableStub[] toClose = new Closeables_closeQuietly_Test.CloseableStub[]{ null, c };
        Closeables.closeQuietly(toClose);
        assertClosed(c);
    }

    private static class CloseableStub implements Closeable {
        boolean closed;

        IOException toThrow;

        public CloseableStub() {
        }

        public CloseableStub(IOException toThrow) {
            this.toThrow = toThrow;
        }

        @Override
        public void close() throws IOException {
            closed = true;
            if ((toThrow) != null) {
                throw toThrow;
            }
        }
    }
}

