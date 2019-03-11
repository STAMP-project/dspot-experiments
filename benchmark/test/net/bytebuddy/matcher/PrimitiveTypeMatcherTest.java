package net.bytebuddy.matcher;


import net.bytebuddy.description.type.TypeDefinition;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public class PrimitiveTypeMatcherTest extends AbstractElementMatcherTest<PrimitiveTypeMatcher<?>> {
    @Mock
    private TypeDefinition typeDefinition;

    @SuppressWarnings("unchecked")
    public PrimitiveTypeMatcherTest() {
        super(((Class<PrimitiveTypeMatcher<?>>) ((Object) (PrimitiveTypeMatcher.class))), "isPrimitive");
    }

    @Test
    public void testIsPrimitive() {
        Mockito.when(typeDefinition.isPrimitive()).thenReturn(true);
        MatcherAssert.assertThat(new PrimitiveTypeMatcher<TypeDefinition>().matches(typeDefinition), Is.is(true));
    }

    @Test
    public void testIsNotPrimitive() {
        MatcherAssert.assertThat(new PrimitiveTypeMatcher<TypeDefinition>().matches(typeDefinition), Is.is(false));
    }
}

