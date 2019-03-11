package fj;


import fj.data.Validation;
import fj.function.TryEffect0;
import fj.function.TryEffect1;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class TryEffectTest {
    @Test
    public void testTryEffect0Success() {
        F<TryEffect0<TryEffectTest.TryEffectException>, Validation<TryEffectTest.TryEffectException, Unit>> f = TryEffect.f(TryEffect0::f);
        Validation<TryEffectTest.TryEffectException, Unit> v = f.f(new TryEffectTest.AlwaysSucceed0());
        Assert.assertThat(v.isSuccess(), Is.is(true));
        Assert.assertThat(v.success(), Is.is(Unit.unit()));
    }

    @Test
    public void testTryEffect0Fail() {
        F<TryEffect0<TryEffectTest.TryEffectException>, Validation<TryEffectTest.TryEffectException, Unit>> f = TryEffect.f(TryEffect0::f);
        Validation<TryEffectTest.TryEffectException, Unit> v = f.f(new TryEffectTest.AlwaysFail0());
        Assert.assertThat(v.isFail(), Is.is(true));
        Assert.assertThat(v.fail(), Is.is(new TryEffectTest.TryEffectException()));
    }

    @Test
    public void testTryEffect1Success() {
        F2<TryEffect1<Integer, TryEffectTest.TryEffectException>, Integer, Validation<TryEffectTest.TryEffectException, Unit>> f = TryEffect.f(TryEffect1<Integer, TryEffectTest.TryEffectException>::f);
        Validation<TryEffectTest.TryEffectException, Unit> v = f.f(new TryEffectTest.AlwaysSucceed1(), 1);
        Assert.assertThat(v.isSuccess(), Is.is(true));
        Assert.assertThat(v.success(), Is.is(Unit.unit()));
    }

    @Test
    public void testTryEffect1Fail() {
        F2<TryEffect1<Integer, TryEffectTest.TryEffectException>, Integer, Validation<TryEffectTest.TryEffectException, Unit>> f = TryEffect.f(TryEffect1<Integer, TryEffectTest.TryEffectException>::f);
        Validation<TryEffectTest.TryEffectException, Unit> v = f.f(new TryEffectTest.AlwaysFail1(), 1);
        Assert.assertThat(v.isFail(), Is.is(true));
        Assert.assertThat(v.fail(), Is.is(new TryEffectTest.TryEffectException()));
    }

    class AlwaysSucceed0 implements TryEffect0<TryEffectTest.TryEffectException> {
        @Override
        public void f() throws TryEffectTest.TryEffectException {
            // SUCCESS
        }
    }

    class AlwaysSucceed1 implements TryEffect1<Integer, TryEffectTest.TryEffectException> {
        @Override
        public void f(Integer i) throws TryEffectTest.TryEffectException {
            // SUCCESS;
        }
    }

    class AlwaysFail0 implements TryEffect0<TryEffectTest.TryEffectException> {
        @Override
        public void f() throws TryEffectTest.TryEffectException {
            throw new TryEffectTest.TryEffectException();
        }
    }

    class AlwaysFail1 implements TryEffect1<Integer, TryEffectTest.TryEffectException> {
        @Override
        public void f(Integer i) throws TryEffectTest.TryEffectException {
            throw new TryEffectTest.TryEffectException();
        }
    }

    class TryEffectException extends Exception {
        @Override
        public boolean equals(Object obj) {
            return obj instanceof TryEffectTest.TryEffectException;
        }
    }
}

