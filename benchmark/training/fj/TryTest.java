package fj;


import fj.data.Validation;
import fj.function.Try0;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class TryTest {
    @Test
    public void testTrySuccess() {
        F<Try0<Integer, TryTest.TryException>, Validation<TryTest.TryException, Integer>> f = Try.f(Try0::f);
        Validation<TryTest.TryException, Integer> v = f.f(new TryTest.AlwaysSucceed());
        Assert.assertThat(v.isSuccess(), Is.is(true));
        Assert.assertThat(v.success(), Is.is(99));
    }

    @Test
    public void testTryFail() {
        F<Try0<Integer, TryTest.TryException>, Validation<TryTest.TryException, Integer>> f = Try.f(Try0::f);
        Validation<TryTest.TryException, Integer> v = f.f(new TryTest.AlwaysFail());
        Assert.assertThat(v.isFail(), Is.is(true));
        Assert.assertThat(v.fail(), Is.is(new TryTest.TryException()));
    }

    class AlwaysSucceed implements Try0<Integer, TryTest.TryException> {
        @Override
        public Integer f() throws TryTest.TryException {
            return 99;
        }
    }

    class AlwaysFail implements Try0<Integer, TryTest.TryException> {
        @Override
        public Integer f() throws TryTest.TryException {
            throw new TryTest.TryException();
        }
    }

    class TryException extends Exception {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof TryTest.TryException;
        }
    }
}

