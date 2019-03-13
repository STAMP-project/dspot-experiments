package net.bytebuddy.agent.builder;


import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import net.bytebuddy.test.utility.FieldByFieldComparison;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.BatchReallocator.splitting;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.ErrorEscalating.FAIL_FAST;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.ErrorEscalating.FAIL_LAST;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.NoOp.INSTANCE;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemError;
import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.Listener.StreamWriting.toSystemOut;


public class AgentBuilderRedefinitionStrategyListenerTest {
    @Test
    public void testNoOp() throws Exception {
        INSTANCE.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        INSTANCE.onError(0, Collections.<Class<?>>emptyList(), new Throwable(), Collections.<Class<?>>emptyList());
        INSTANCE.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
    }

    @Test
    public void testYielding() throws Exception {
        AgentBuilder.RedefinitionStrategy.Listener.Yielding.INSTANCE.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        AgentBuilder.RedefinitionStrategy.Listener.Yielding.INSTANCE.onBatch(1, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        AgentBuilder.RedefinitionStrategy.Listener.Yielding.INSTANCE.onError(0, Collections.<Class<?>>emptyList(), new Throwable(), Collections.<Class<?>>emptyList());
        AgentBuilder.RedefinitionStrategy.Listener.Yielding.INSTANCE.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
    }

    @Test
    public void testPausing() throws Exception {
        AgentBuilder.RedefinitionStrategy.Listener listener = new AgentBuilder.RedefinitionStrategy.Listener.Pausing(1L);
        listener.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        listener.onBatch(1, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        listener.onError(0, Collections.<Class<?>>emptyList(), new Throwable(), Collections.<Class<?>>emptyList());
        listener.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
    }

    @Test
    public void testStreamWriting() throws Exception {
        PrintStream printStream = Mockito.mock(PrintStream.class);
        AgentBuilder.RedefinitionStrategy.Listener listener = new AgentBuilder.RedefinitionStrategy.Listener.StreamWriting(printStream);
        listener.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        Throwable throwable = Mockito.mock(Throwable.class);
        listener.onError(0, Collections.<Class<?>>emptyList(), throwable, Collections.<Class<?>>emptyList());
        listener.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
        Mockito.verify(printStream, Mockito.times(3)).printf(ArgumentMatchers.any(String.class), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt(), ArgumentMatchers.anyInt());
        Mockito.verifyNoMoreInteractions(printStream);
        Mockito.verify(throwable).printStackTrace(printStream);
        Mockito.verifyNoMoreInteractions(throwable);
    }

    @Test
    public void testStreamWritingFactories() throws Exception {
        MatcherAssert.assertThat(toSystemOut(), FieldByFieldComparison.hasPrototype(((AgentBuilder.RedefinitionStrategy.Listener) (new AgentBuilder.RedefinitionStrategy.Listener.StreamWriting(System.out)))));
        MatcherAssert.assertThat(toSystemError(), FieldByFieldComparison.hasPrototype(((AgentBuilder.RedefinitionStrategy.Listener) (new AgentBuilder.RedefinitionStrategy.Listener.StreamWriting(System.err)))));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCompound() throws Exception {
        Throwable throwable = new Throwable();
        AgentBuilder.RedefinitionStrategy.Listener first = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        AgentBuilder.RedefinitionStrategy.Listener second = Mockito.mock(AgentBuilder.RedefinitionStrategy.Listener.class);
        Mockito.when(first.onError(0, Collections.<Class<?>>emptyList(), throwable, Collections.<Class<?>>emptyList())).thenReturn(((Iterable) (Collections.singleton(Collections.singletonList(Object.class)))));
        Mockito.when(second.onError(0, Collections.<Class<?>>emptyList(), throwable, Collections.<Class<?>>emptyList())).thenReturn(((Iterable) (Collections.singleton(Collections.singletonList(Void.class)))));
        AgentBuilder.RedefinitionStrategy.Listener listener = new AgentBuilder.RedefinitionStrategy.Listener.Compound(first, second);
        listener.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        Iterator<? extends List<Class<?>>> batched = listener.onError(0, Collections.<Class<?>>emptyList(), throwable, Collections.<Class<?>>emptyList()).iterator();
        MatcherAssert.assertThat(batched.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(batched.next(), CoreMatchers.is(Collections.<Class<?>>singletonList(Object.class)));
        MatcherAssert.assertThat(batched.hasNext(), CoreMatchers.is(true));
        MatcherAssert.assertThat(batched.next(), CoreMatchers.is(Collections.<Class<?>>singletonList(Void.class)));
        MatcherAssert.assertThat(batched.hasNext(), CoreMatchers.is(false));
        listener.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
        Mockito.verify(first).onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        Mockito.verify(first).onError(0, Collections.<Class<?>>emptyList(), throwable, Collections.<Class<?>>emptyList());
        Mockito.verify(first).onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
        Mockito.verifyNoMoreInteractions(first);
        Mockito.verify(second).onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        Mockito.verify(second).onError(0, Collections.<Class<?>>emptyList(), throwable, Collections.<Class<?>>emptyList());
        Mockito.verify(second).onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
        Mockito.verifyNoMoreInteractions(second);
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorFailFast() throws Exception {
        FAIL_FAST.onError(0, Collections.<Class<?>>emptyList(), Mockito.mock(Throwable.class), Collections.<Class<?>>emptyList());
    }

    @Test
    public void testFailFastNoOp() throws Exception {
        FAIL_FAST.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        FAIL_FAST.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorFailLast() throws Exception {
        FAIL_LAST.onComplete(0, Collections.<Class<?>>emptyList(), Collections.singletonMap(Collections.<Class<?>>emptyList(), Mockito.mock(Throwable.class)));
    }

    @Test
    public void testFailLastNoOp() throws Exception {
        FAIL_LAST.onBatch(0, Collections.<Class<?>>emptyList(), Collections.<Class<?>>emptyList());
        FAIL_LAST.onError(0, Collections.<Class<?>>emptyList(), Mockito.mock(Throwable.class), Collections.<Class<?>>emptyList());
        FAIL_LAST.onComplete(0, Collections.<Class<?>>emptyList(), Collections.<List<Class<?>>, Throwable>emptyMap());
    }

    @Test(expected = NoSuchElementException.class)
    public void testEmptyIterator() throws Exception {
        new AgentBuilder.RedefinitionStrategy.Listener.Compound.CompoundIterable.CompoundIterator(Collections.<Iterable<? extends List<Class<?>>>>emptyList()).next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testIteratorRemove() throws Exception {
        new AgentBuilder.RedefinitionStrategy.Listener.Compound.CompoundIterable.CompoundIterator(Collections.<Iterable<? extends List<Class<?>>>>emptyList()).remove();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBatchReallocatorNonBatchable() throws Exception {
        AgentBuilder.RedefinitionStrategy.BatchAllocator delegate = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        AgentBuilder.RedefinitionStrategy.Listener listener = new AgentBuilder.RedefinitionStrategy.Listener.BatchReallocator(delegate);
        MatcherAssert.assertThat(listener.onError(0, Collections.<Class<?>>singletonList(Object.class), new Throwable(), Collections.<Class<?>>emptyList()), CoreMatchers.is(((Iterable) (Collections.emptyList()))));
        Mockito.verifyZeroInteractions(delegate);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testBatchReallocatorBatchable() throws Exception {
        AgentBuilder.RedefinitionStrategy.BatchAllocator delegate = Mockito.mock(AgentBuilder.RedefinitionStrategy.BatchAllocator.class);
        Mockito.when(delegate.batch(Arrays.<Class<?>>asList(Object.class, Void.class))).thenReturn(((Iterable) (Collections.emptyList())));
        AgentBuilder.RedefinitionStrategy.Listener listener = new AgentBuilder.RedefinitionStrategy.Listener.BatchReallocator(delegate);
        MatcherAssert.assertThat(listener.onError(0, Arrays.asList(Object.class, Void.class), new Throwable(), Collections.<Class<?>>emptyList()), CoreMatchers.is(((Iterable) (Collections.emptyList()))));
        Mockito.verify(delegate).batch(Arrays.asList(Object.class, Void.class));
        Mockito.verifyNoMoreInteractions(delegate);
    }

    @Test
    public void testSplittingBatchReallocator() throws Exception {
        MatcherAssert.assertThat(splitting(), FieldByFieldComparison.hasPrototype(((AgentBuilder.RedefinitionStrategy.Listener) (new AgentBuilder.RedefinitionStrategy.Listener.BatchReallocator(new AgentBuilder.RedefinitionStrategy.BatchAllocator.Partitioning(2))))));
    }
}

