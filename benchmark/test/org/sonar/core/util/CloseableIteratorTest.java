/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.core.util;


import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


public class CloseableIteratorTest {
    @Test
    public void iterate() {
        CloseableIteratorTest.SimpleCloseableIterator it = new CloseableIteratorTest.SimpleCloseableIterator();
        assertThat(it.isClosed).isFalse();
        // multiple calls to hasNext() moves only once the cursor
        assertThat(hasNext()).isTrue();
        assertThat(hasNext()).isTrue();
        assertThat(hasNext()).isTrue();
        assertThat(next()).isEqualTo(1);
        assertThat(it.isClosed).isFalse();
        assertThat(hasNext()).isTrue();
        assertThat(next()).isEqualTo(2);
        assertThat(it.isClosed).isFalse();
        assertThat(hasNext()).isFalse();
        // automatic close
        assertThat(it.isClosed).isTrue();
        // explicit close does not fail
        close();
        assertThat(it.isClosed).isTrue();
    }

    @Test
    public void call_next_without_hasNext() {
        CloseableIteratorTest.SimpleCloseableIterator it = new CloseableIteratorTest.SimpleCloseableIterator();
        assertThat(next()).isEqualTo(1);
        assertThat(next()).isEqualTo(2);
        try {
            next();
            Assert.fail();
        } catch (NoSuchElementException expected) {
        }
    }

    @Test
    public void automatic_close_if_traversal_error() {
        CloseableIteratorTest.FailureCloseableIterator it = new CloseableIteratorTest.FailureCloseableIterator();
        try {
            next();
            Assert.fail();
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("expected failure");
            assertThat(it.isClosed).isTrue();
        }
    }

    @Test
    public void remove_is_not_supported_by_default() {
        CloseableIteratorTest.SimpleCloseableIterator it = new CloseableIteratorTest.SimpleCloseableIterator();
        try {
            remove();
            Assert.fail();
        } catch (UnsupportedOperationException expected) {
            assertThat(it.isClosed).isTrue();
        }
    }

    @Test
    public void remove_can_be_overridden() {
        CloseableIteratorTest.RemovableCloseableIterator it = new CloseableIteratorTest.RemovableCloseableIterator();
        remove();
        assertThat(it.isRemoved).isTrue();
    }

    @Test
    public void has_next_should_not_call_do_next_when_already_closed() {
        CloseableIteratorTest.DoNextShouldNotBeCalledWhenClosedIterator it = new CloseableIteratorTest.DoNextShouldNotBeCalledWhenClosedIterator();
        next();
        next();
        assertThat(hasNext()).isFalse();
        // this call to hasNext close the stream
        assertThat(hasNext()).isFalse();
        assertThat(it.isClosed).isTrue();
        // calling hasNext should not fail
        hasNext();
    }

    @Test
    public void emptyIterator_has_next_is_false() {
        assertThat(hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void emptyIterator_next_throws_NoSuchElementException() {
        next();
    }

    @Test(expected = NullPointerException.class)
    public void from_iterator_throws_early_NPE_if_arg_is_null() {
        CloseableIterator.from(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_iterator_throws_IAE_if_arg_is_a_CloseableIterator() {
        CloseableIterator.from(new CloseableIteratorTest.SimpleCloseableIterator());
    }

    @Test(expected = IllegalArgumentException.class)
    public void from_iterator_throws_IAE_if_arg_is_a_AutoCloseable() {
        CloseableIterator.from(new CloseableIteratorTest.CloseableIt());
    }

    @Test
    public void wrap_closeables() throws Exception {
        AutoCloseable closeable1 = Mockito.mock(AutoCloseable.class);
        AutoCloseable closeable2 = Mockito.mock(AutoCloseable.class);
        CloseableIterator iterator = new CloseableIteratorTest.SimpleCloseableIterator();
        CloseableIterator wrapper = CloseableIterator.wrap(iterator, closeable1, closeable2);
        assertThat(wrapper.next()).isEqualTo(1);
        assertThat(wrapper.next()).isEqualTo(2);
        assertThat(wrapper.hasNext()).isFalse();
        assertThat(wrapper.isClosed).isTrue();
        assertThat(iterator.isClosed).isTrue();
        InOrder order = Mockito.inOrder(closeable1, closeable2);
        order.verify(closeable1).close();
        order.verify(closeable2).close();
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrap_fails_if_iterator_declared_in_other_closeables() {
        CloseableIterator iterator = new CloseableIteratorTest.SimpleCloseableIterator();
        CloseableIterator.wrap(iterator, iterator);
    }

    @Test(expected = NullPointerException.class)
    public void wrap_fails_if_null_closeable() {
        CloseableIterator.wrap(new CloseableIteratorTest.SimpleCloseableIterator(), ((AutoCloseable) (null)));
    }

    private static class CloseableIt implements AutoCloseable , Iterator<String> {
        private final Iterator<String> delegate = Collections.<String>emptyList().iterator();

        @Override
        public void remove() {
            delegate.remove();
        }

        @Override
        public String next() {
            return delegate.next();
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public void close() {
            // no need to implement it for real
        }
    }

    @Test
    public void verify_has_next_from_iterator_with_empty_iterator() {
        assertThat(hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void verify_next_from_iterator_with_empty_iterator() {
        next();
    }

    static class SimpleCloseableIterator extends CloseableIterator<Object> {
        int count = 0;

        boolean isClosed = false;

        @Override
        protected Object doNext() {
            (count)++;
            if ((count) < 3) {
                return count;
            }
            return null;
        }

        @Override
        protected void doClose() {
            isClosed = true;
        }
    }

    static class FailureCloseableIterator extends CloseableIterator {
        boolean isClosed = false;

        @Override
        protected Object doNext() {
            throw new IllegalStateException("expected failure");
        }

        @Override
        protected void doClose() {
            isClosed = true;
        }
    }

    static class RemovableCloseableIterator extends CloseableIterator {
        boolean isClosed = false;

        boolean isRemoved = false;

        @Override
        protected Object doNext() {
            return "foo";
        }

        @Override
        protected void doRemove() {
            isRemoved = true;
        }

        @Override
        protected void doClose() {
            isClosed = true;
        }
    }

    static class DoNextShouldNotBeCalledWhenClosedIterator extends CloseableIteratorTest.SimpleCloseableIterator {
        @Override
        protected Object doNext() {
            if (!(isClosed)) {
                return super.doNext();
            } else {
                throw new IllegalStateException("doNext should not be called when already closed");
            }
        }
    }
}

