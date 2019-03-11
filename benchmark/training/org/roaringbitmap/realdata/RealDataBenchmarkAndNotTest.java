package org.roaringbitmap.realdata;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.RealDataset;


public class RealDataBenchmarkAndNotTest extends RealDataBenchmarkSanityTest {
    private static final Map<String, Integer> EXPECTED_RESULTS = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, 5666586).put(RealDataset.CENSUS1881, 1003836).put(RealDataset.DIMENSION_008, 2721459).put(RealDataset.DIMENSION_003, 3866831).put(RealDataset.DIMENSION_033, 3866842).put(RealDataset.USCENSUS2000, 5970).put(RealDataset.WEATHER_SEPT_85, 11960876).put(RealDataset.WIKILEAKS_NOQUOTES, 271605).put(RealDataset.CENSUS_INCOME_SRT, 5164671).put(RealDataset.CENSUS1881_SRT, 679375).put(RealDataset.WEATHER_SEPT_85_SRT, 14935706).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 286904).build();

    @Test
    public void test() throws Exception {
        int expected = RealDataBenchmarkAndNotTest.EXPECTED_RESULTS.get(dataset);
        RealDataBenchmarkAndNot bench = new RealDataBenchmarkAndNot();
        Assert.assertEquals(expected, bench.pairwiseAndNot(bs));
    }
}

