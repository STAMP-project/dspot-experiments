package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDescription;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class HasSuperTypeMatcherTest extends AbstractElementMatcherTest<HasSuperTypeMatcher<?>> {
    @Mock
    private ElementMatcher<? super TypeDescription.Generic> typeMatcher;

    @Mock
    private TypeDescription typeDescription;

    @Mock
    private TypeDescription.Generic superType;

    @Mock
    private TypeDescription.Generic interfaceType;

    @Mock
    private TypeDescription.Generic implicitInterfaceType;

    @SuppressWarnings("unchecked")
    public HasSuperTypeMatcherTest() {
        super(((Class<HasSuperTypeMatcher<?>>) ((Object) (HasSuperTypeMatcher.class))), "hasSuperType");
    }

    @Test
    public void testMatchSuperClass() throws Exception {
        Mockito.when(typeMatcher.matches(superType)).thenReturn(true);
        MatcherAssert.assertThat(new HasSuperTypeMatcher<TypeDescription>(typeMatcher).matches(typeDescription), CoreMatchers.is(true));
    }

    @Test
    public void testMatchSuperInterface() throws Exception {
        Mockito.when(typeMatcher.matches(interfaceType)).thenReturn(true);
        MatcherAssert.assertThat(new HasSuperTypeMatcher<TypeDescription>(typeMatcher).matches(typeDescription), CoreMatchers.is(true));
    }

    @Test
    public void testMatchSuperInterfaceImplicit() throws Exception {
        Mockito.when(typeMatcher.matches(implicitInterfaceType)).thenReturn(true);
        MatcherAssert.assertThat(new HasSuperTypeMatcher<TypeDescription>(typeMatcher).matches(typeDescription), CoreMatchers.is(true));
    }

    @Test
    public void testNoMatch() throws Exception {
        MatcherAssert.assertThat(new HasSuperTypeMatcher<TypeDescription>(typeMatcher).matches(typeDescription), CoreMatchers.is(false));
    }

    @Test
    public void testNoMatchRecursive() throws Exception {
        Mockito.when(typeDescription.getSuperClass()).thenReturn(superType);
        Mockito.when(superType.getSuperClass()).thenReturn(superType);
        MatcherAssert.assertThat(new HasSuperTypeMatcher<TypeDescription>(typeMatcher).matches(typeDescription), CoreMatchers.is(false));
    }
}

