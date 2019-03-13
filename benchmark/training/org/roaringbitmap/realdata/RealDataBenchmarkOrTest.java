package org.roaringbitmap.realdata;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.RealDataset;


public class RealDataBenchmarkOrTest extends RealDataBenchmarkSanityTest {
    private static final Map<String, Integer> EXPECTED_RESULTS = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, 12487395).put(RealDataset.CENSUS1881, 2007691).put(RealDataset.DIMENSION_008, 5555233).put(RealDataset.DIMENSION_003, 7733676).put(RealDataset.DIMENSION_033, 7579526).put(RealDataset.USCENSUS2000, 11954).put(RealDataset.WEATHER_SEPT_85, 24729002).put(RealDataset.WIKILEAKS_NOQUOTES, 541893).put(RealDataset.CENSUS_INCOME_SRT, 11257282).put(RealDataset.CENSUS1881_SRT, 1360167).put(RealDataset.WEATHER_SEPT_85_SRT, 30863347).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 574463).build();

    private static final Map<String, Integer> EXPECTED_RESULTS_NO_CARDINALITY = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, 20377).put(RealDataset.CENSUS1881, 192344014).put(RealDataset.DIMENSION_008, 60471647).put(RealDataset.DIMENSION_003, (-2080279977)).put(RealDataset.DIMENSION_033, 48993139).put(RealDataset.USCENSUS2000, 1085687199).put(RealDataset.WEATHER_SEPT_85, 164092).put(RealDataset.WIKILEAKS_NOQUOTES, 43309741).put(RealDataset.CENSUS_INCOME_SRT, 325103).put(RealDataset.CENSUS1881_SRT, 113309680).put(RealDataset.WEATHER_SEPT_85_SRT, 11922488).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 28702307).build();

    @Test
    public void test() throws Exception {
        int expected = RealDataBenchmarkOrTest.EXPECTED_RESULTS.get(dataset);
        RealDataBenchmarkOr bench = new RealDataBenchmarkOr();
        Assert.assertEquals(expected, bench.pairwiseOr(bs));
    }

    @Test
    public void test_NoCardinality() throws Exception {
        int expected = RealDataBenchmarkOrTest.EXPECTED_RESULTS_NO_CARDINALITY.get(dataset);
        RealDataBenchmarkOr bench = new RealDataBenchmarkOr();
        Assert.assertEquals(expected, bench.pairwiseOr_NoCardinality(bs));
    }
}

