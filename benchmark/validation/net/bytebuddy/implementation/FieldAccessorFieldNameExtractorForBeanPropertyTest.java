package net.bytebuddy.implementation;


import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.implementation.FieldAccessor.FieldNameExtractor.ForBeanProperty.INSTANCE;


public class FieldAccessorFieldNameExtractorForBeanPropertyTest {
    private static final String FOO = "foo";

    private static final String FOO_CAPITAL = "Foo";

    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private MethodDescription methodDescription;

    @Test
    public void testGetterMethod() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn(("get" + (FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO_CAPITAL)));
        MatcherAssert.assertThat(INSTANCE.resolve(methodDescription), CoreMatchers.is(FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO));
    }

    @Test
    public void testSetterMethod() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn(("set" + (FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO_CAPITAL)));
        MatcherAssert.assertThat(INSTANCE.resolve(methodDescription), CoreMatchers.is(FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO));
    }

    @Test
    public void testGetterMethodBooleanPrefix() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn(("is" + (FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO_CAPITAL)));
        MatcherAssert.assertThat(INSTANCE.resolve(methodDescription), CoreMatchers.is(FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyGetter() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn("get");
        INSTANCE.resolve(methodDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptySetter() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn("set");
        INSTANCE.resolve(methodDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyGetterBooleanPrefix() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn("is");
        INSTANCE.resolve(methodDescription);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIllegalName() throws Exception {
        Mockito.when(methodDescription.getInternalName()).thenReturn(FieldAccessorFieldNameExtractorForBeanPropertyTest.FOO);
        INSTANCE.resolve(methodDescription);
    }
}

