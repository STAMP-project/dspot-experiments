/**
 * Copyright (c) 2011-2017 Pivotal Software Inc, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactor.core;


import Exceptions.ReactorRejectedExecutionException;
import java.io.IOException;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.junit.Assert;
import org.junit.Test;
import reactor.test.util.RaceTestUtils;


/**
 *
 *
 * @author Stephane Maldini
 */
public class ExceptionsTest {
    @Test
    public void testWrapping() throws Exception {
        Throwable t = new Exception("test");
        Throwable w = Exceptions.bubble(Exceptions.propagate(t));
        Assert.assertTrue(((Exceptions.unwrap(w)) == t));
    }

    @Test
    public void testNullWrapping() throws Exception {
        Throwable w = Exceptions.bubble(null);
        Assert.assertTrue(((Exceptions.unwrap(w)) == w));
    }

    @Test
    public void allOverflowIsIllegalState() {
        IllegalStateException overflow1 = Exceptions.failWithOverflow();
        IllegalStateException overflow2 = Exceptions.failWithOverflow("foo");
        Assert.assertTrue(Exceptions.isOverflow(overflow1));
        Assert.assertTrue(Exceptions.isOverflow(overflow2));
    }

    @Test
    public void allIllegalStateIsntOverflow() {
        IllegalStateException ise = new IllegalStateException("foo");
        Assert.assertFalse(Exceptions.isOverflow(ise));
    }

    @Test
    public void multipleWithNullVararg() {
        // noinspection ConstantConditions
        assertThat(Exceptions.multiple(((Throwable[]) (null)))).isInstanceOf(RuntimeException.class).hasMessage("Multiple exceptions").hasNoSuppressedExceptions();
    }

    @Test
    public void multipleWithOneVararg() {
        IOException e1 = new IOException("boom");
        assertThat(Exceptions.multiple(e1)).isInstanceOf(RuntimeException.class).hasMessage("Multiple exceptions").hasSuppressedException(e1);
    }

    @Test
    public void multipleWithTwoVararg() {
        IOException e1 = new IOException("boom");
        IllegalArgumentException e2 = new IllegalArgumentException("boom");
        assertThat(Exceptions.multiple(e1, e2)).isInstanceOf(RuntimeException.class).hasMessage("Multiple exceptions").hasSuppressedException(e1).hasSuppressedException(e2);
    }

    @Test
    public void isMultiple() {
        Exception e1 = new IllegalStateException("1");
        Exception e2 = new IllegalArgumentException("2");
        Exception composite = Exceptions.multiple(e1, e2);
        assertThat(Exceptions.isMultiple(composite)).isTrue();
        assertThat(Exceptions.isMultiple(Exceptions.failWithCancel())).isFalse();
        assertThat(Exceptions.isMultiple(null)).isFalse();
    }

    @Test
    public void unwrapMultipleNull() {
        assertThat(Exceptions.unwrapMultiple(null)).isEmpty();
    }

    @Test
    public void unwrapMultipleNotComposite() {
        RuntimeException e1 = Exceptions.failWithCancel();
        assertThat(Exceptions.unwrapMultiple(e1)).containsExactly(e1);
    }

    volatile Throwable addThrowable;

    static final AtomicReferenceFieldUpdater<ExceptionsTest, Throwable> ADD_THROWABLE = AtomicReferenceFieldUpdater.newUpdater(ExceptionsTest.class, Throwable.class, "addThrowable");

    @Test
    public void addThrowable() {
        Throwable e1 = new IllegalStateException("add1");
        Throwable e2 = new IllegalArgumentException("add2");
        Throwable e3 = new OutOfMemoryError("add3");
        assertThat(addThrowable).isNull();
        Exceptions.addThrowable(ExceptionsTest.ADD_THROWABLE, this, e1);
        assertThat(addThrowable).isSameAs(e1);
        Exceptions.addThrowable(ExceptionsTest.ADD_THROWABLE, this, e2);
        assertThat(Exceptions.isMultiple(addThrowable)).isTrue();
        assertThat(addThrowable).hasSuppressedException(e1).hasSuppressedException(e2);
        Exceptions.addThrowable(ExceptionsTest.ADD_THROWABLE, this, e3);
        assertThat(Exceptions.isMultiple(addThrowable)).isTrue();
        assertThat(addThrowable).hasSuppressedException(e1).hasSuppressedException(e2).hasSuppressedException(e3);
    }

    @Test
    public void addThrowableRace() throws Exception {
        for (int i = 0; i < 10; i++) {
            final int idx = i;
            RaceTestUtils.race(() -> Exceptions.addThrowable(ADD_THROWABLE, this, new IllegalStateException(("boomState" + idx))), () -> Exceptions.addThrowable(ADD_THROWABLE, this, new IllegalArgumentException(("boomArg" + idx))));
        }
        assertThat(addThrowable.getSuppressed()).hasSize(20);
    }

    @Test
    public void addSuppressedToNormal() {
        Exception original = new Exception("foo");
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isSameAs(original).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedToRejectedInstance() {
        Throwable original = new RejectedExecutionException("foo");
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isSameAs(original).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedToSame() {
        Throwable original = new Exception("foo");
        assertThat(Exceptions.addSuppressed(original, original)).isSameAs(original).hasNoSuppressedExceptions();
    }

    @Test
    public void addSuppressedToRejectedSingleton1() {
        Throwable original = Exceptions.REJECTED_EXECUTION;
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isNotSameAs(original).hasMessage(original.getMessage()).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedToRejectedSingleton2() {
        Throwable original = Exceptions.NOT_TIME_CAPABLE_REJECTED_EXECUTION;
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isNotSameAs(original).hasMessage(original.getMessage()).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedToTERMINATED() {
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(Exceptions.TERMINATED, suppressed)).hasNoSuppressedExceptions().isSameAs(Exceptions.TERMINATED);
    }

    @Test
    public void addSuppressedRuntimeToNormal() {
        RuntimeException original = new RuntimeException("foo");
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isSameAs(original).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedRuntimeToRejectedInstance() {
        RuntimeException original = new RejectedExecutionException("foo");
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isSameAs(original).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedRuntimeToSame() {
        RuntimeException original = new RuntimeException("foo");
        assertThat(Exceptions.addSuppressed(original, original)).isSameAs(original).hasNoSuppressedExceptions();
    }

    @Test
    public void addSuppressedRuntimeToRejectedSingleton1() {
        RuntimeException original = Exceptions.REJECTED_EXECUTION;
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isNotSameAs(original).hasMessage(original.getMessage()).hasSuppressedException(suppressed);
    }

    @Test
    public void addSuppressedRuntimeToRejectedSingleton2() {
        RuntimeException original = Exceptions.NOT_TIME_CAPABLE_REJECTED_EXECUTION;
        Exception suppressed = new IllegalStateException("boom");
        assertThat(Exceptions.addSuppressed(original, suppressed)).isNotSameAs(original).hasMessage(original.getMessage()).hasSuppressedException(suppressed);
    }

    @Test
    public void failWithRejectedNormalWraps() {
        Throwable test = new IllegalStateException("boom");
        assertThat(Exceptions.failWithRejected(test)).isInstanceOf(ReactorRejectedExecutionException.class).hasMessage("Scheduler unavailable").hasCause(test);
    }

    @Test
    public void failWithRejectedSingletonReeWraps() {
        Throwable test = Exceptions.REJECTED_EXECUTION;
        assertThat(Exceptions.failWithRejected(test)).isInstanceOf(ReactorRejectedExecutionException.class).hasMessage("Scheduler unavailable").hasCause(test);
    }

    @Test
    public void failWithRejectedNormalReeWraps() {
        Throwable test = new RejectedExecutionException("boom");
        assertThat(Exceptions.failWithRejected(test)).isInstanceOf(ReactorRejectedExecutionException.class).hasMessage("Scheduler unavailable").hasCause(test);
    }

    @Test
    public void failWithRejectedReactorReeNoOp() {
        Throwable test = new Exceptions.ReactorRejectedExecutionException("boom", Exceptions.REJECTED_EXECUTION);
        assertThat(Exceptions.failWithRejected(test)).isSameAs(test).hasCause(Exceptions.REJECTED_EXECUTION);
    }
}

