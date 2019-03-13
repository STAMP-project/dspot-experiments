package fj.control.parallel;


import Enumerator.intEnumerator;
import Ord.intOrd;
import fj.P;
import fj.P1;
import fj.Unit;
import fj.data.List;
import fj.data.Stream;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class StrategyTest {
    @Test
    public void testStrategySeq() {
        final Stream<Integer> s = Stream.range(intEnumerator, 99, (-99), (-1));
        Assert.assertThat(s.sort(intOrd, seqStrategy()), Is.is(s.sort(intOrd)));
    }

    @Test
    public void testStrategyThread() {
        final Stream<Integer> s = Stream.range(intEnumerator, 99, (-99), (-1));
        Assert.assertThat(s.sort(intOrd, simpleThreadStrategy()), Is.is(s.sort(intOrd)));
    }

    @Test
    public void testStrategyExecutor() {
        final Stream<Integer> s = Stream.range(intEnumerator, 99, (-99), (-1));
        final ExecutorService es = Executors.newFixedThreadPool(10);
        Assert.assertThat(s.sort(intOrd, executorStrategy(es)), Is.is(s.sort(intOrd)));
    }

    @Test
    public void testStrategyCompletion() {
        final Stream<Integer> s = Stream.range(intEnumerator, 99, (-99), (-1));
        final ExecutorService es = Executors.newFixedThreadPool(10);
        final CompletionService<Unit> cs = new ExecutorCompletionService<>(es);
        Assert.assertThat(s.sort(intOrd, completionStrategy(cs)), Is.is(s.sort(intOrd)));
    }

    @Test
    public void testStrategyMergeAll() {
        final List<Integer> l = List.range(0, 100);
        final List<P1<Integer>> p1s = mergeAll(l.map(( x) -> CompletableFuture.supplyAsync(() -> x)));
        Assert.assertThat(P1.sequence(p1s)._1(), Is.is(l));
    }

    @Test
    public void testStrategyCallables() throws Exception {
        final Strategy.Strategy<Callable<Integer>> s = strategy(( c) -> c);
        final Strategy.Strategy<Callable<Integer>> cs = callableStrategy(s);
        Assert.assertThat(callableStrategy(s).par(P.p(Callables.callable(1)))._1().call(), Is.is(1));
    }
}

