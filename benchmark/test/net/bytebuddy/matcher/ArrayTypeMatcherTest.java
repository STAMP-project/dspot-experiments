package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDefinition;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class ArrayTypeMatcherTest extends AbstractElementMatcherTest<ArrayTypeMatcher<?>> {
    @Mock
    private TypeDefinition typeDefinition;

    @SuppressWarnings("unchecked")
    public ArrayTypeMatcherTest() {
        super(((Class<ArrayTypeMatcher<?>>) ((Object) (ArrayTypeMatcher.class))), "isArray");
    }

    @Test
    public void testIsArray() {
        Mockito.when(typeDefinition.isArray()).thenReturn(true);
        MatcherAssert.assertThat(new ArrayTypeMatcher<TypeDefinition>().matches(typeDefinition), Is.is(true));
    }

    @Test
    public void testIsNoArray() {
        MatcherAssert.assertThat(new ArrayTypeMatcher<TypeDefinition>().matches(typeDefinition), Is.is(false));
    }
}

