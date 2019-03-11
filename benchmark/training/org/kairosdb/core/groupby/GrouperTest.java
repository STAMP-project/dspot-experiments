/**
 *
 */
/**
 * GrouperTest.java
 */
/**
 *
 */
/**
 * Copyright 2016, KairosDB Authors
 */
/**
 *
 */
package org.kairosdb.core.groupby;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.kairosdb.core.TestDataPointFactory;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.core.formatter.FormatterException;
import org.kairosdb.plugin.GroupBy;
import org.kairosdb.testing.ListDataPointGroup;


public class GrouperTest {
    @Test
    public void test() throws IOException, FormatterException {
        Grouper grouper = new Grouper(new TestDataPointFactory());
        List<GroupBy> groupBys = new ArrayList<GroupBy>();
        groupBys.add(new ValueGroupBy(3));
        groupBys.add(new SimpleTimeGroupBy(2));
        ListDataPointGroup dataPointGroup1 = new ListDataPointGroup("dataPointGroup1");
        addTag("host", "server1");
        addTag("customer", "acme");
        dataPointGroup1.addDataPoint(new LongDataPoint(1, 0));
        dataPointGroup1.addDataPoint(new LongDataPoint(1, 1));
        dataPointGroup1.addDataPoint(new LongDataPoint(2, 2));
        dataPointGroup1.addDataPoint(new LongDataPoint(2, 3));
        dataPointGroup1.addDataPoint(new LongDataPoint(2, 4));
        ListDataPointGroup dataPointGroup2 = new ListDataPointGroup("dataPointGroup2");
        addTag("host", "server2");
        addTag("customer", "foobar");
        dataPointGroup2.addDataPoint(new LongDataPoint(2, 5));
        dataPointGroup2.addDataPoint(new LongDataPoint(2, 6));
        dataPointGroup2.addDataPoint(new LongDataPoint(2, 7));
        dataPointGroup2.addDataPoint(new LongDataPoint(2, 8));
        List<DataPointGroup> dataPointGroups = new ArrayList<DataPointGroup>();
        dataPointGroups.add(dataPointGroup1);
        dataPointGroups.add(dataPointGroup2);
        List<DataPointGroup> groups = grouper.group(groupBys, dataPointGroups);
        MatcherAssert.assertThat(groups.size(), CoreMatchers.equalTo(5));
        // Group 1
        DataPointGroup group1 = groups.get(0);
        MatcherAssert.assertThat(group1.getTagValues("host"), CoreMatchers.hasItems("server1"));
        MatcherAssert.assertThat(group1.getTagValues("customer"), CoreMatchers.hasItems("acme"));
        GrouperTest.assertDataPoint(group1.next(), 1, 0);
        GrouperTest.assertDataPoint(group1.next(), 1, 1);
        MatcherAssert.assertThat(group1.next(), CoreMatchers.equalTo(null));
        group1.close();// cleans up temp files

        // Group 2
        DataPointGroup group2 = groups.get(1);
        MatcherAssert.assertThat(group2.getTagValues("host"), CoreMatchers.hasItems("server1"));
        MatcherAssert.assertThat(group2.getTagValues("customer"), CoreMatchers.hasItems("acme"));
        GrouperTest.assertDataPoint(group2.next(), 2, 2);
        MatcherAssert.assertThat(group2.next(), CoreMatchers.equalTo(null));
        group2.close();// cleans up temp files

        // Group 3
        DataPointGroup group3 = groups.get(2);
        MatcherAssert.assertThat(group3.getTagValues("host"), CoreMatchers.hasItems("server1"));
        MatcherAssert.assertThat(group3.getTagValues("customer"), CoreMatchers.hasItems("acme"));
        GrouperTest.assertDataPoint(group3.next(), 2, 3);
        GrouperTest.assertDataPoint(group3.next(), 2, 4);
        MatcherAssert.assertThat(group3.next(), CoreMatchers.equalTo(null));
        group3.close();// cleans up temp files

        // Group 4
        DataPointGroup group4 = groups.get(3);
        MatcherAssert.assertThat(group4.getTagValues("host"), CoreMatchers.hasItems("server2"));
        MatcherAssert.assertThat(group4.getTagValues("customer"), CoreMatchers.hasItems("foobar"));
        GrouperTest.assertDataPoint(group4.next(), 2, 5);
        MatcherAssert.assertThat(group4.next(), CoreMatchers.equalTo(null));
        group4.close();// cleans up temp files

        // Group 5
        DataPointGroup group5 = groups.get(4);
        MatcherAssert.assertThat(group5.getTagValues("host"), CoreMatchers.hasItems("server2"));
        MatcherAssert.assertThat(group5.getTagValues("customer"), CoreMatchers.hasItems("foobar"));
        GrouperTest.assertDataPoint(group5.next(), 2, 6);
        GrouperTest.assertDataPoint(group5.next(), 2, 7);
        GrouperTest.assertDataPoint(group5.next(), 2, 8);
        MatcherAssert.assertThat(group5.next(), CoreMatchers.equalTo(null));
        group5.close();// cleans up temp files

    }
}

