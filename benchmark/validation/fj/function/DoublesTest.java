package fj.function;


import Doubles.abs;
import Doubles.add;
import Doubles.multiply;
import fj.data.List;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


public class DoublesTest {
    @Test
    public void testSum() {
        Assert.assertThat(Doubles.sum(List.list(3.0, 4.0, 5.0)), Is.is(12.0));
    }

    @Test
    public void testProduct() {
        Assert.assertThat(Doubles.product(List.list(3.0, 4.0, 5.0)), Is.is(60.0));
    }

    @Test
    public void testAdd() {
        Assert.assertThat(add.f(10.0).f(20.0), Is.is(30.0));
    }

    @Test
    public void testMultiply() {
        Assert.assertThat(multiply.f(3.0).f(5.0), Is.is(15.0));
    }

    @Test
    public void testAbs() {
        Assert.assertThat(abs.f((-5.0)), Is.is(5.0));
    }

    @Test
    public void testFromString() {
        Assert.assertThat(Doubles.fromString().f("-123.45").some(), Is.is((-123.45)));
    }

    @Test
    public void testCannotInstantiate() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        Constructor<Doubles> constructor = Doubles.class.getDeclaredConstructor(new Class[0]);
        constructor.setAccessible(true);
        try {
            constructor.newInstance(new Object[0]);
            Assert.fail("expected InvocationTargetException");
        } catch (InvocationTargetException ite) {
            Assert.assertTrue(((ite.getCause()) instanceof UnsupportedOperationException));
        }
    }
}

