package net.bytebuddy.utility;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import junit.framework.TestCase;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;


public class CompoundListTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private Object first;

    @Mock
    private Object second;

    @Mock
    private Object third;

    @Mock
    private Object forth;

    @Test(expected = UnsupportedOperationException.class)
    public void testConstruction() throws Throwable {
        Constructor<?> constructor = CompoundList.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try {
            constructor.newInstance();
            TestCase.fail();
        } catch (InvocationTargetException exception) {
            throw exception.getCause();
        }
    }

    @Test
    public void testElementAndList() throws Exception {
        List<Object> list = CompoundList.of(first, Arrays.asList(second, third, forth));
        Assert.assertThat(list.size(), CoreMatchers.is(4));
        Assert.assertThat(list.get(0), CoreMatchers.is(first));
        Assert.assertThat(list.get(1), CoreMatchers.is(second));
        Assert.assertThat(list.get(2), CoreMatchers.is(third));
        Assert.assertThat(list.get(3), CoreMatchers.is(forth));
    }

    @Test
    public void testListAndElement() throws Exception {
        List<Object> list = CompoundList.of(Arrays.asList(first, second, third), forth);
        Assert.assertThat(list.size(), CoreMatchers.is(4));
        Assert.assertThat(list.get(0), CoreMatchers.is(first));
        Assert.assertThat(list.get(1), CoreMatchers.is(second));
        Assert.assertThat(list.get(2), CoreMatchers.is(third));
        Assert.assertThat(list.get(3), CoreMatchers.is(forth));
    }

    @Test
    public void testListAndList() throws Exception {
        List<Object> list = CompoundList.of(Arrays.asList(first, second), Arrays.asList(third, forth));
        Assert.assertThat(list.size(), CoreMatchers.is(4));
        Assert.assertThat(list.get(0), CoreMatchers.is(first));
        Assert.assertThat(list.get(1), CoreMatchers.is(second));
        Assert.assertThat(list.get(2), CoreMatchers.is(third));
        Assert.assertThat(list.get(3), CoreMatchers.is(forth));
    }

    @Test
    public void testListAndListAndList() throws Exception {
        List<Object> list = CompoundList.of(Collections.singletonList(first), Collections.singletonList(second), Collections.singletonList(third));
        Assert.assertThat(list.size(), CoreMatchers.is(3));
        Assert.assertThat(list.get(0), CoreMatchers.is(first));
        Assert.assertThat(list.get(1), CoreMatchers.is(second));
        Assert.assertThat(list.get(2), CoreMatchers.is(third));
    }
}

