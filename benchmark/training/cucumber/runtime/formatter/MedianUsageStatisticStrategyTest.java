package cucumber.runtime.formatter;


import UsageFormatter.MedianUsageStatisticStrategy;
import java.util.Arrays;
import java.util.Collections;
import org.junit.Assert;
import org.junit.Test;


public class MedianUsageStatisticStrategyTest {
    @Test
    public void calculateOddEntries() throws Exception {
        UsageFormatter.MedianUsageStatisticStrategy medianUsageStatisticStrategy = new UsageFormatter.MedianUsageStatisticStrategy();
        Long result = medianUsageStatisticStrategy.calculate(Arrays.asList(1L, 2L, 3L));
        Assert.assertEquals(result, Long.valueOf(2));
    }

    @Test
    public void calculateEvenEntries() throws Exception {
        UsageFormatter.MedianUsageStatisticStrategy medianUsageStatisticStrategy = new UsageFormatter.MedianUsageStatisticStrategy();
        Long result = medianUsageStatisticStrategy.calculate(Arrays.asList(1L, 3L, 10L, 5L));
        Assert.assertEquals(result, Long.valueOf(4));
    }

    @Test
    public void calculateNull() throws Exception {
        UsageFormatter.MedianUsageStatisticStrategy medianUsageStatisticStrategy = new UsageFormatter.MedianUsageStatisticStrategy();
        Long result = medianUsageStatisticStrategy.calculate(null);
        Assert.assertEquals(result, Long.valueOf(0));
    }

    @Test
    public void calculateEmptylist() throws Exception {
        UsageFormatter.MedianUsageStatisticStrategy medianUsageStatisticStrategy = new UsageFormatter.MedianUsageStatisticStrategy();
        Long result = medianUsageStatisticStrategy.calculate(Collections.<Long>emptyList());
        Assert.assertEquals(result, Long.valueOf(0));
    }

    @Test
    public void calculateListWithNulls() throws Exception {
        UsageFormatter.MedianUsageStatisticStrategy medianUsageStatisticStrategy = new UsageFormatter.MedianUsageStatisticStrategy();
        Long result = medianUsageStatisticStrategy.calculate(Arrays.<Long>asList(1L, null, 3L));
        Assert.assertEquals(result, Long.valueOf(0));
    }
}

