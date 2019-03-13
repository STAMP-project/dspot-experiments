package net.bytebuddy.description.type;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;


public class TypeDescriptionLatentTest {
    private static final String FOO = "foo";

    private static final int MODIFIERS = 42;

    @Rule
    public MockitoRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic superClass;

    @Mock
    private TypeDescription.Generic interfaceType;

    @Test
    public void testName() throws Exception {
        MatcherAssert.assertThat(new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getName(), CoreMatchers.is(TypeDescriptionLatentTest.FOO));
    }

    @Test
    public void testModifiers() throws Exception {
        MatcherAssert.assertThat(new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getModifiers(), CoreMatchers.is(TypeDescriptionLatentTest.MODIFIERS));
    }

    @Test
    public void testSuperType() throws Exception {
        MatcherAssert.assertThat(new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getSuperClass(), CoreMatchers.is(superClass));
    }

    @Test
    public void testInterfaceTypes() throws Exception {
        MatcherAssert.assertThat(new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getInterfaces().size(), CoreMatchers.is(1));
        MatcherAssert.assertThat(new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getInterfaces().getOnly(), CoreMatchers.is(interfaceType));
    }

    @Test(expected = IllegalStateException.class)
    public void testFields() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getDeclaredFields();
    }

    @Test(expected = IllegalStateException.class)
    public void testMethods() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getDeclaredMethods();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnnotations() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getDeclaredAnnotations();
    }

    @Test(expected = IllegalStateException.class)
    public void testTypeVariables() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getTypeVariables();
    }

    @Test(expected = IllegalStateException.class)
    public void testMemberClass() throws Exception {
        isMemberType();
    }

    @Test(expected = IllegalStateException.class)
    public void testAnoynmousClass() throws Exception {
        isAnonymousType();
    }

    @Test(expected = IllegalStateException.class)
    public void testLocalClass() throws Exception {
        isLocalType();
    }

    @Test(expected = IllegalStateException.class)
    public void testEnclosingMethod() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getEnclosingMethod();
    }

    @Test(expected = IllegalStateException.class)
    public void testEnclosingType() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getEnclosingType();
    }

    @Test(expected = IllegalStateException.class)
    public void testDeclaredTypes() throws Exception {
        new TypeDescription.Latent(TypeDescriptionLatentTest.FOO, TypeDescriptionLatentTest.MODIFIERS, superClass, interfaceType).getDeclaredTypes();
    }
}

