package net.bytebuddy.description.type;


import java.lang.reflect.Type;
import org.junit.Test;
import org.mockito.Mockito;

import static net.bytebuddy.description.type.TypeDefinition.Sort.describe;


public class TypeDefinitionSortOtherTest {
    @Test(expected = IllegalArgumentException.class)
    public void testUnknownType() throws Exception {
        describe(Mockito.mock(Type.class));
    }
}

