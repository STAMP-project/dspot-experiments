package org.roaringbitmap.realdata;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.RealDataset;


public class RealDataBenchmarkAndTest extends RealDataBenchmarkSanityTest {
    private static final Map<String, Integer> EXPECTED_RESULTS = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, 1245448).put(RealDataset.CENSUS1881, 23).put(RealDataset.DIMENSION_008, 112317).put(RealDataset.DIMENSION_003, 0).put(RealDataset.DIMENSION_033, 0).put(RealDataset.USCENSUS2000, 0).put(RealDataset.WEATHER_SEPT_85, 642019).put(RealDataset.WIKILEAKS_NOQUOTES, 3327).put(RealDataset.CENSUS_INCOME_SRT, 927715).put(RealDataset.CENSUS1881_SRT, 206).put(RealDataset.WEATHER_SEPT_85_SRT, 1062989).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 152).build();

    @Test
    public void test() throws Exception {
        int expected = RealDataBenchmarkAndTest.EXPECTED_RESULTS.get(dataset);
        RealDataBenchmarkAnd bench = new RealDataBenchmarkAnd();
        Assert.assertEquals(expected, bench.pairwiseAnd(bs));
    }
}

