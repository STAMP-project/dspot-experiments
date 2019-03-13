package net.bytebuddy.description.method;


import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;

import static net.bytebuddy.description.type.TypeDescription.Generic.VOID;


public class MethodDescriptionLatentTypeInitializerTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription typeDescription;

    private MethodDescription.InDefinedShape typeInitializer;

    @Test
    public void testDeclaringType() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getDeclaringType(), CoreMatchers.is(typeDescription));
    }

    @Test
    public void testName() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getInternalName(), CoreMatchers.is(MethodDescription.TYPE_INITIALIZER_INTERNAL_NAME));
    }

    @Test
    public void testModifiers() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getModifiers(), CoreMatchers.is(MethodDescription.TYPE_INITIALIZER_MODIFIER));
    }

    @Test
    public void testAnnotations() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getDeclaredAnnotations().size(), CoreMatchers.is(0));
    }

    @Test
    public void testExceptions() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getExceptionTypes().size(), CoreMatchers.is(0));
    }

    @Test
    public void testParameters() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getParameters().size(), CoreMatchers.is(0));
    }

    @Test
    public void testReturnType() throws Exception {
        MatcherAssert.assertThat(typeInitializer.getReturnType(), CoreMatchers.is(VOID));
    }
}

