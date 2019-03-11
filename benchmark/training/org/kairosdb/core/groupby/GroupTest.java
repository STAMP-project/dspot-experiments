package org.kairosdb.core.groupby;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.kairosdb.core.KairosDataPointFactory;
import org.kairosdb.core.TestDataPointFactory;
import org.kairosdb.core.datapoints.LongDataPoint;
import org.kairosdb.core.datastore.DataPointGroup;
import org.kairosdb.testing.ListDataPointGroup;


/**
 * Created by bhawkins on 12/14/13.
 */
public class GroupTest {
    @Test
    public void test() throws IOException {
        KairosDataPointFactory kairosDataPointFactory = new TestDataPointFactory();
        ListDataPointGroup dataPointGroup = new ListDataPointGroup("TestGroup");
        addTag("host", "server1");
        List<Integer> groupIds = new ArrayList<Integer>();
        groupIds.add(1);
        groupIds.add(2);
        groupIds.add(3);
        Group group = Group.createGroup(dataPointGroup, groupIds, Collections.<GroupByResult>emptyList(), kairosDataPointFactory);
        group.addDataPoint(new LongDataPoint(1, 1));
        group.addDataPoint(new LongDataPoint(2, 2));
        DataPointGroup cachedGroup = group.getDataPointGroup();
        GrouperTest.assertDataPoint(cachedGroup.next(), 1, 1);
        GrouperTest.assertDataPoint(cachedGroup.next(), 2, 2);
        cachedGroup.close();
    }
}

