package fj.data;


import Enumerator.intEnumerator;
import fj.F0;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.Test;


public class EvalTest {
    @Test
    public void testNow() {
        Eval<Integer> eval = Eval.now(1);
        Assert.assertEquals(eval.value().intValue(), 1);
        Assert.assertEquals(eval.map(( a) -> a.toString()).value(), "1");
        Assert.assertEquals(eval.bind(( a) -> Eval.now((a * 3))).value().intValue(), 3);
    }

    @Test
    public void testLater() {
        EvalTest.InvocationTrackingF<Integer> tracker = new EvalTest.InvocationTrackingF<>(1);
        Eval<Integer> eval = Eval.later(tracker);
        Assert.assertEquals(tracker.getInvocationCounter(), 0);
        Assert.assertEquals(eval.value().intValue(), 1);
        Assert.assertEquals(tracker.getInvocationCounter(), 1);
        eval.value();
        Assert.assertEquals(tracker.getInvocationCounter(), 1);
    }

    @Test
    public void testAlways() {
        EvalTest.InvocationTrackingF<Integer> tracker = new EvalTest.InvocationTrackingF<>(1);
        Eval<Integer> eval = Eval.always(tracker);
        Assert.assertEquals(tracker.getInvocationCounter(), 0);
        Assert.assertEquals(eval.value().intValue(), 1);
        Assert.assertEquals(tracker.getInvocationCounter(), 1);
        eval.value();
        eval.value();
        Assert.assertEquals(tracker.getInvocationCounter(), 3);
    }

    @Test
    public void testDefer() {
        // Make sure that a recursive computation is actually stack-safe.
        int targetValue = 200000;
        Iterator<Integer> it = intEnumerator.toStream(0).iterator();
        Eval<Boolean> result = EvalTest.foldRight(it, ( v, acc) -> v == targetValue ? Eval.now(true) : acc, false);
        Assert.assertTrue(result.value());
    }

    private static class InvocationTrackingF<A> implements F0<A> {
        private final A value;

        private int invocationCounter;

        public InvocationTrackingF(A value) {
            this.value = value;
            this.invocationCounter = 0;
        }

        @Override
        public A f() {
            (invocationCounter)++;
            return value;
        }

        public int getInvocationCounter() {
            return invocationCounter;
        }
    }
}

