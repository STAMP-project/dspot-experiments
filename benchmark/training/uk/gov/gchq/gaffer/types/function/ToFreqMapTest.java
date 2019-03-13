package uk.gov.gchq.gaffer.types.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.types.FreqMap;
import uk.gov.gchq.koryphe.function.FunctionTest;


public class ToFreqMapTest extends FunctionTest {
    @Test
    public void shouldConvertStringToFreqMap() {
        // Given
        final ToFreqMap function = new ToFreqMap();
        final Object value = "value1";
        // When
        final FreqMap result = function.apply(value);
        // Then
        Assert.assertEquals(new FreqMap(value.toString()), result);
    }

    @Test
    public void shouldConvertObjectToFreqMap() {
        // Given
        final ToFreqMap function = new ToFreqMap();
        final Object value = 1L;
        // When
        final FreqMap result = function.apply(value);
        // Then
        Assert.assertEquals(new FreqMap(value.toString()), result);
    }

    @Test
    public void shouldConvertNullToFreqMap() {
        // Given
        final ToFreqMap function = new ToFreqMap();
        final Object value = null;
        // When
        final FreqMap result = function.apply(value);
        // Then
        Assert.assertEquals(new FreqMap(((String) (null))), result);
    }
}

