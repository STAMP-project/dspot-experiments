package net.bytebuddy.description.enumeration;


import java.lang.reflect.Method;
import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mockito;


public abstract class AbstractEnumerationDescriptionTest {
    private Method annotationMethod;

    @Test
    public void testPrecondition() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).getEnumerationType().represents(AbstractEnumerationDescriptionTest.Sample.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).getEnumerationType().represents(AbstractEnumerationDescriptionTest.Sample.class), CoreMatchers.is(true));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).getEnumerationType().represents(AbstractEnumerationDescriptionTest.Other.class), CoreMatchers.is(false));
    }

    @Test
    public void testValue() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).getValue(), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.FIRST.name()));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).getValue(), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.SECOND.name()));
    }

    @Test
    public void testName() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).getActualName(), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.FIRST.name()));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).getActualName(), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.SECOND.name()));
    }

    @Test
    public void testToString() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).toString(), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.FIRST.toString()));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).toString(), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.SECOND.toString()));
    }

    @Test
    public void testType() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).getEnumerationType(), CoreMatchers.is(((TypeDescription) (of(AbstractEnumerationDescriptionTest.Sample.class)))));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).getEnumerationType(), CoreMatchers.is(((TypeDescription) (of(AbstractEnumerationDescriptionTest.Sample.class)))));
    }

    @Test
    public void testHashCode() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).hashCode(), CoreMatchers.is(((AbstractEnumerationDescriptionTest.Sample.FIRST.name().hashCode()) + (31 * (of(AbstractEnumerationDescriptionTest.Sample.class).hashCode())))));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).hashCode(), CoreMatchers.is(((AbstractEnumerationDescriptionTest.Sample.SECOND.name().hashCode()) + (31 * (of(AbstractEnumerationDescriptionTest.Sample.class).hashCode())))));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).hashCode(), CoreMatchers.not(describe(AbstractEnumerationDescriptionTest.Sample.SECOND).hashCode()));
    }

    @Test
    public void testEquals() throws Exception {
        EnumerationDescription identical = describe(AbstractEnumerationDescriptionTest.Sample.FIRST);
        MatcherAssert.assertThat(identical, CoreMatchers.is(identical));
        EnumerationDescription equalFirst = Mockito.mock(EnumerationDescription.class);
        Mockito.when(equalFirst.getValue()).thenReturn(AbstractEnumerationDescriptionTest.Sample.FIRST.name());
        Mockito.when(equalFirst.getEnumerationType()).thenReturn(of(AbstractEnumerationDescriptionTest.Sample.class));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST), CoreMatchers.is(equalFirst));
        EnumerationDescription equalSecond = Mockito.mock(EnumerationDescription.class);
        Mockito.when(equalSecond.getValue()).thenReturn(AbstractEnumerationDescriptionTest.Sample.SECOND.name());
        Mockito.when(equalSecond.getEnumerationType()).thenReturn(of(AbstractEnumerationDescriptionTest.Sample.class));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.SECOND), CoreMatchers.is(equalSecond));
        EnumerationDescription equalFirstTypeOnly = Mockito.mock(EnumerationDescription.class);
        Mockito.when(equalFirstTypeOnly.getValue()).thenReturn(AbstractEnumerationDescriptionTest.Sample.SECOND.name());
        Mockito.when(equalFirstTypeOnly.getEnumerationType()).thenReturn(of(AbstractEnumerationDescriptionTest.Sample.class));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST), CoreMatchers.not(equalFirstTypeOnly));
        EnumerationDescription equalFirstNameOnly = Mockito.mock(EnumerationDescription.class);
        Mockito.when(equalFirstNameOnly.getValue()).thenReturn(AbstractEnumerationDescriptionTest.Sample.FIRST.name());
        Mockito.when(equalFirstNameOnly.getEnumerationType()).thenReturn(of(AbstractEnumerationDescriptionTest.Other.class));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST), CoreMatchers.not(equalFirstNameOnly));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST), CoreMatchers.not(equalSecond));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST), CoreMatchers.not(new Object()));
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST), CoreMatchers.not(CoreMatchers.equalTo(null)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIncompatible() throws Exception {
        describe(AbstractEnumerationDescriptionTest.Sample.FIRST).load(AbstractEnumerationDescriptionTest.Other.class);
    }

    @Test
    public void testLoad() throws Exception {
        MatcherAssert.assertThat(describe(AbstractEnumerationDescriptionTest.Sample.FIRST).load(AbstractEnumerationDescriptionTest.Sample.class), CoreMatchers.is(AbstractEnumerationDescriptionTest.Sample.FIRST));
    }

    public enum Sample {

        FIRST,
        SECOND;}

    private enum Other {

        INSTANCE;}

    public @interface Carrier {
        AbstractEnumerationDescriptionTest.Sample value();
    }

    @AbstractEnumerationDescriptionTest.Carrier(AbstractEnumerationDescriptionTest.Sample.FIRST)
    private static class FirstCarrier {}

    @AbstractEnumerationDescriptionTest.Carrier(AbstractEnumerationDescriptionTest.Sample.SECOND)
    private static class SecondCarrier {}
}

