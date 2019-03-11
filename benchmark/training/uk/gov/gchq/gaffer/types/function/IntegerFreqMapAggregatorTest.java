package uk.gov.gchq.gaffer.types.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.types.IntegerFreqMap;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class IntegerFreqMapAggregatorTest extends BinaryOperatorTest {
    @Test
    public void shouldMergeFreqMaps() {
        // Given
        final IntegerFreqMapAggregator aggregator = new IntegerFreqMapAggregator();
        final IntegerFreqMap freqMap1 = new IntegerFreqMap();
        freqMap1.put("1", 2);
        freqMap1.put("2", 3);
        final IntegerFreqMap freqMap2 = new IntegerFreqMap();
        freqMap2.put("2", 4);
        freqMap2.put("3", 5);
        // When
        final IntegerFreqMap result = aggregator.apply(freqMap1, freqMap2);
        // Then
        Assert.assertEquals(((Integer) (2)), result.get("1"));
        Assert.assertEquals(((Integer) (7)), result.get("2"));
        Assert.assertEquals(((Integer) (5)), result.get("3"));
    }
}

