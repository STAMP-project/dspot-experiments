package org.roaringbitmap.realdata;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.RealDataset;


public class RealDataBenchmarkWideOrPqTest extends RealDataBenchmarkSanityTest {
    private static final Map<String, Integer> EXPECTED_RESULTS = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, 199523).put(RealDataset.CENSUS1881, 988653).put(RealDataset.DIMENSION_008, 148278).put(RealDataset.DIMENSION_003, 3866847).put(RealDataset.DIMENSION_033, 3866847).put(RealDataset.USCENSUS2000, 5985).put(RealDataset.WEATHER_SEPT_85, 1015367).put(RealDataset.WIKILEAKS_NOQUOTES, 242540).put(RealDataset.CENSUS_INCOME_SRT, 199523).put(RealDataset.CENSUS1881_SRT, 656346).put(RealDataset.WEATHER_SEPT_85_SRT, 1015367).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 236436).build();

    @Test
    public void test() throws Exception {
        int expected = RealDataBenchmarkWideOrPqTest.EXPECTED_RESULTS.get(dataset);
        RealDataBenchmarkWideOrPq bench = new RealDataBenchmarkWideOrPq();
        Assert.assertEquals(expected, bench.wideOr_pq(bs));
    }
}

