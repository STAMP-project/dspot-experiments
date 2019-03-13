package org.roaringbitmap.realdata;


import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.roaringbitmap.RealDataset;


public class RealDataBenchmarkIterateTest extends RealDataBenchmarkSanityTest {
    private static final Map<String, Integer> EXPECTED_RESULTS = ImmutableMap.<String, Integer>builder().put(RealDataset.CENSUS_INCOME, (-942184551)).put(RealDataset.CENSUS1881, 246451066).put(RealDataset.DIMENSION_008, (-423436314)).put(RealDataset.DIMENSION_003, (-1287135055)).put(RealDataset.DIMENSION_033, (-1287135055)).put(RealDataset.USCENSUS2000, (-1260727955)).put(RealDataset.WEATHER_SEPT_85, 644036874).put(RealDataset.WIKILEAKS_NOQUOTES, 413846869).put(RealDataset.CENSUS_INCOME_SRT, (-679313956)).put(RealDataset.CENSUS1881_SRT, 445584405).put(RealDataset.WEATHER_SEPT_85_SRT, 1132748056).put(RealDataset.WIKILEAKS_NOQUOTES_SRT, 1921022163).build();

    @Test
    public void test() throws Exception {
        int expected = RealDataBenchmarkIterateTest.EXPECTED_RESULTS.get(dataset);
        RealDataBenchmarkIterate bench = new RealDataBenchmarkIterate();
        Assert.assertEquals(expected, bench.iterate(bs));
    }
}

