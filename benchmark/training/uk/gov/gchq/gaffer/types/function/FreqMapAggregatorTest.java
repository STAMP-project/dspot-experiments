package uk.gov.gchq.gaffer.types.function;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.types.FreqMap;
import uk.gov.gchq.koryphe.binaryoperator.BinaryOperatorTest;


public class FreqMapAggregatorTest extends BinaryOperatorTest {
    @Test
    public void shouldMergeFreqMaps() {
        // Given
        final FreqMapAggregator aggregator = new FreqMapAggregator();
        final FreqMap freqMap1 = new FreqMap();
        freqMap1.put("1", 2L);
        freqMap1.put("2", 3L);
        final FreqMap freqMap2 = new FreqMap();
        freqMap2.put("2", 4L);
        freqMap2.put("3", 5L);
        // When
        final FreqMap result = aggregator.apply(freqMap1, freqMap2);
        // Then
        Assert.assertEquals(((Long) (2L)), result.get("1"));
        Assert.assertEquals(((Long) (7L)), result.get("2"));
        Assert.assertEquals(((Long) (5L)), result.get("3"));
    }
}

