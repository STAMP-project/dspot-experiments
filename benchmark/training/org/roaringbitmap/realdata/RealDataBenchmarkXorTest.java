package org.roaringbitmap.realdata;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.RealDataset;


public class RealDataBenchmarkXorTest extends RealDataBenchmarkSanityTest {
    private static final Map<String, Integer> EXPECTED_RESULTS = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, 11241947).put(RealDataset.CENSUS1881, 2007668).put(RealDataset.DIMENSION_008, 5442916).put(RealDataset.DIMENSION_003, 7733676).put(RealDataset.DIMENSION_033, 7579526).put(RealDataset.USCENSUS2000, 11954).put(RealDataset.WEATHER_SEPT_85, 24086983).put(RealDataset.WIKILEAKS_NOQUOTES, 538566).put(RealDataset.CENSUS_INCOME_SRT, 10329567).put(RealDataset.CENSUS1881_SRT, 1359961).put(RealDataset.WEATHER_SEPT_85_SRT, 29800358).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 574311).build();

    @Test
    public void test() throws Exception {
        int expected = RealDataBenchmarkXorTest.EXPECTED_RESULTS.get(dataset);
        RealDataBenchmarkXor bench = new RealDataBenchmarkXor();
        Assert.assertEquals(expected, bench.pairwiseXor(bs));
    }
}

