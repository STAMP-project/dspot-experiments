package org.kairosdb.core.aggregator;


import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


/**
 * Created with IntelliJ IDEA.
 * User: bhawkins
 * Date: 6/18/13
 * Time: 11:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class LeastSquaresAggregatorTest {
    LeastSquaresAggregator m_aggregator;

    @Test
    public void test_noDataPoint() {
        ListDataPointGroup group = new ListDataPointGroup("group");
        DataPointGroup result = m_aggregator.aggregate(group);
        MatcherAssert.assertThat(result.hasNext(), IsEqual.equalTo(false));
    }

    @Test
    public void test_singleDataPoint() {
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        DataPointGroup result = m_aggregator.aggregate(group);
        DataPoint dp = result.next();
        MatcherAssert.assertThat(dp.getTimestamp(), IsEqual.equalTo(1L));
        MatcherAssert.assertThat(dp.getDoubleValue(), IsEqual.equalTo(10.0));
        MatcherAssert.assertThat(result.hasNext(), IsEqual.equalTo(false));
    }

    @Test
    public void test_twoDataPoints() {
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        DataPointGroup result = m_aggregator.aggregate(group);
        DataPoint dp = result.next();
        MatcherAssert.assertThat(dp.getTimestamp(), IsEqual.equalTo(1L));
        MatcherAssert.assertThat(dp.getDoubleValue(), IsEqual.equalTo(10.0));
        dp = result.next();
        MatcherAssert.assertThat(dp.getTimestamp(), IsEqual.equalTo(2L));
        MatcherAssert.assertThat(dp.getDoubleValue(), IsEqual.equalTo(20.0));
        MatcherAssert.assertThat(result.hasNext(), IsEqual.equalTo(false));
    }
}

