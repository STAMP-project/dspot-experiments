package net.bytebuddy.description.type;


import net.bytebuddy.test.utility.MockitoRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.mockito.Mock;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.NON_GENERIC;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Reifying.INHERITING;
import static net.bytebuddy.description.type.TypeDescription.Generic.Visitor.Reifying.INITIATING;


public class TypeDescriptionGenericVisitorReifyingTest {
    @Rule
    public TestRule mockitoRule = new MockitoRule(this);

    @Mock
    private TypeDescription.Generic generic;

    @Test
    public void testInitiatingParameterizedType() throws Exception {
        Assert.assertThat(INITIATING.onParameterizedType(generic), CoreMatchers.sameInstance(generic));
    }

    @Test
    public void testInitiatingGenerifiedNonGenericType() throws Exception {
        Mockito.when(generic.asErasure()).thenReturn(TypeDescription.OBJECT);
        Assert.assertThat(INITIATING.onNonGenericType(generic), CoreMatchers.sameInstance(generic));
    }

    @Test
    public void testInitiatingNonGenerifiedNonGenericType() throws Exception {
        Mockito.when(generic.asErasure()).thenReturn(of(TypeDescriptionGenericVisitorReifyingTest.Foo.class));
        Assert.assertThat(INITIATING.onNonGenericType(generic), CoreMatchers.not(CoreMatchers.sameInstance(generic)));
        Assert.assertThat(INITIATING.onNonGenericType(generic).getSort(), CoreMatchers.is(NON_GENERIC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitiatingTypeVariable() throws Exception {
        INITIATING.onTypeVariable(generic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitiatingGenericArray() throws Exception {
        INITIATING.onGenericArray(generic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInitiatingWildcard() throws Exception {
        INITIATING.onWildcard(generic);
    }

    @Test
    public void testInheritingParameterizedType() throws Exception {
        Assert.assertThat(INHERITING.onParameterizedType(generic), CoreMatchers.not(CoreMatchers.sameInstance(generic)));
    }

    @Test
    public void testInheritingGenerifiedNonGenericType() throws Exception {
        Mockito.when(generic.asErasure()).thenReturn(TypeDescription.OBJECT);
        Assert.assertThat(INHERITING.onNonGenericType(generic), CoreMatchers.sameInstance(generic));
    }

    @Test
    public void testInheritingNonGenerifiedNonGenericType() throws Exception {
        Mockito.when(generic.asErasure()).thenReturn(of(TypeDescriptionGenericVisitorReifyingTest.Foo.class));
        Assert.assertThat(INHERITING.onNonGenericType(generic), CoreMatchers.not(CoreMatchers.sameInstance(generic)));
        Assert.assertThat(INHERITING.onNonGenericType(generic).getSort(), CoreMatchers.is(NON_GENERIC));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInheritingTypeVariable() throws Exception {
        INHERITING.onTypeVariable(generic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInheritingGenericArray() throws Exception {
        INHERITING.onGenericArray(generic);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInheritingWildcard() throws Exception {
        INHERITING.onWildcard(generic);
    }

    /* empty */
    private static class Foo<T> {}
}

