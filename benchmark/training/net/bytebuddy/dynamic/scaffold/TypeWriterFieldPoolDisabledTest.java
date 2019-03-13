package net.bytebuddy.dynamic.scaffold;


import net.bytebuddy.description.field.FieldDescription;
import org.junit.Test;
import org.mockito.Mockito;


public class TypeWriterFieldPoolDisabledTest {
    @Test(expected = IllegalStateException.class)
    public void testCannotLookupField() {
        Disabled.target(Mockito.mock(FieldDescription.class));
    }
}

