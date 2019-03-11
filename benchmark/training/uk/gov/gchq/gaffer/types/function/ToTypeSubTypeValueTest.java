package uk.gov.gchq.gaffer.types.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.types.TypeSubTypeValue;
import uk.gov.gchq.koryphe.function.FunctionTest;


public class ToTypeSubTypeValueTest extends FunctionTest {
    @Test
    public void shouldConvertStringToTypeSubTypeValue() {
        // Given
        final ToTypeSubTypeValue function = new ToTypeSubTypeValue();
        final Object value = "value1";
        // When
        final TypeSubTypeValue result = function.apply(value);
        // Then
        Assert.assertEquals(new TypeSubTypeValue(null, null, value.toString()), result);
    }

    @Test
    public void shouldConvertObjectToTypeSubTypeValue() {
        // Given
        final ToTypeSubTypeValue function = new ToTypeSubTypeValue();
        final Object value = 1L;
        // When
        final TypeSubTypeValue result = function.apply(value);
        // Then
        Assert.assertEquals(new TypeSubTypeValue(null, null, value.toString()), result);
    }

    @Test
    public void shouldConvertNullToTypeSubTypeValue() {
        // Given
        final ToTypeSubTypeValue function = new ToTypeSubTypeValue();
        final Object value = null;
        // When
        final TypeSubTypeValue result = function.apply(value);
        // Then
        Assert.assertEquals(new TypeSubTypeValue(null, null, null), result);
    }
}

