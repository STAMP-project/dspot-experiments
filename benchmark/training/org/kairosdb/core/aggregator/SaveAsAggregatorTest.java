package org.kairosdb.core.aggregator;


import com.google.common.collect.ImmutableSortedMap;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.kairosdb.core.DataPoint;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.core.exception.DatastoreException;
import org.kairosdb.core.groupby.TagGroupBy;
import org.kairosdb.eventbus.FilterEventBus;
import org.kairosdb.eventbus.Publisher;
import org.kairosdb.events.DataPointEvent;
import org.kairosdb.plugin.GroupBy;
import org.kairosdb.testing.ListDataPointGroup;
import org.kairosdb.util.DataPointEventUtil;
import org.mockito.ArgumentCaptor;


/**
 * Created by bhawkins on 2/9/16.
 */
public class SaveAsAggregatorTest {
    private SaveAsAggregator m_aggregator;

    private FilterEventBus m_mockEventBus;

    private Publisher<DataPointEvent> m_publisher;

    ArgumentCaptor<DataPointEvent> m_event;

    @Test
    @SuppressWarnings("unchecked")
    public void testTtl() throws DatastoreException {
        m_aggregator.setMetricName("testTtl");
        m_aggregator.setTtl(42);
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        DataPointGroup results = m_aggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(10L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", dataPoint, 42);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", dataPoint, 42);
        results.close();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNoTtl() throws DatastoreException {
        m_aggregator.setMetricName("testTtl");
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        DataPointGroup results = m_aggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(10L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", dataPoint, 0);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", dataPoint, 0);
        results.close();
    }

    @Test
    public void testNotAddingSavedFrom() throws DatastoreException {
        m_aggregator.setMetricName("testTtl");
        m_aggregator.setTags(ImmutableSortedMap.of("sweet_tag", "value"));
        m_aggregator.setAddSavedFrom(false);
        ImmutableSortedMap<String, String> verifyMap = ImmutableSortedMap.<String, String>naturalOrder().put("sweet_tag", "value").build();
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        addTag("host", "tag_should_not_be_there");
        DataPointGroup results = m_aggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(10L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", verifyMap, dataPoint, 0);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", verifyMap, dataPoint, 0);
        results.close();
    }

    @Test
    public void testAddedTags() throws DatastoreException {
        m_aggregator.setMetricName("testTtl");
        m_aggregator.setTags(ImmutableSortedMap.of("sweet_tag", "value"));
        ImmutableSortedMap<String, String> verifyMap = ImmutableSortedMap.<String, String>naturalOrder().put("saved_from", "group").put("sweet_tag", "value").build();
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        addTag("host", "tag_should_not_be_there");
        DataPointGroup results = m_aggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(10L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", verifyMap, dataPoint, 0);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", verifyMap, dataPoint, 0);
        results.close();
    }

    @Test
    public void testGroupByTagFilter() throws DatastoreException {
        m_aggregator.setMetricName("testTtl");
        m_aggregator.setTtl(42);
        GroupBy groupBy = new TagGroupBy("host", "host2");
        m_aggregator.setGroupBys(Collections.singletonList(groupBy));
        ImmutableSortedMap<String, String> verifyMap = ImmutableSortedMap.<String, String>naturalOrder().put("saved_from", "group").put("host", "bob").build();
        ListDataPointGroup group = new ListDataPointGroup("group");
        group.addDataPoint(new LongDataPoint(1, 10));
        group.addDataPoint(new LongDataPoint(2, 20));
        addTag("host", "bob");
        addTag("some_tag", "tag_should_not_be_there");
        addTag("host2", "host2_tag");
        addTag("host2", "wont show up because there are two");
        DataPointGroup results = m_aggregator.aggregate(group);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        DataPoint dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(1L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(10L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", verifyMap, dataPoint, 42);
        Assert.assertThat(results.hasNext(), CoreMatchers.equalTo(true));
        dataPoint = results.next();
        Assert.assertThat(dataPoint.getTimestamp(), CoreMatchers.equalTo(2L));
        Assert.assertThat(dataPoint.getLongValue(), CoreMatchers.equalTo(20L));
        DataPointEventUtil.verifyEvent(m_publisher, "testTtl", verifyMap, dataPoint, 42);
        results.close();
    }
}

