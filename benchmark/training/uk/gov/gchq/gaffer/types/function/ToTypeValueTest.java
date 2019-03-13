package uk.gov.gchq.gaffer.types.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.types.TypeValue;
import uk.gov.gchq.koryphe.function.FunctionTest;


public class ToTypeValueTest extends FunctionTest {
    @Test
    public void shouldConvertStringToTypeValue() {
        // Given
        final ToTypeValue function = new ToTypeValue();
        final Object value = "value1";
        // When
        final TypeValue result = function.apply(value);
        // Then
        Assert.assertEquals(new TypeValue(null, value.toString()), result);
    }

    @Test
    public void shouldConvertObjectToTypeValue() {
        // Given
        final ToTypeValue function = new ToTypeValue();
        final Object value = 1L;
        // When
        final TypeValue result = function.apply(value);
        // Then
        Assert.assertEquals(new TypeValue(null, value.toString()), result);
    }

    @Test
    public void shouldConvertNullToTypeValue() {
        // Given
        final ToTypeValue function = new ToTypeValue();
        final Object value = null;
        // When
        final TypeValue result = function.apply(value);
        // Then
        Assert.assertEquals(new TypeValue(null, null), result);
    }
}

