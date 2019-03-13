package org.apache.hadoop.hbase.filter;


import Durability.SKIP_WAL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.hbase.HBaseClassTestRule;
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.KeyValue;
import org.apache.hadoop.hbase.KeyValueTestUtil;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.testclassification.FilterTests;
import org.apache.hadoop.hbase.testclassification.MediumTests;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Category({ FilterTests.class, MediumTests.class })
public class TestColumnRangeFilter {
    @ClassRule
    public static final HBaseClassTestRule CLASS_RULE = HBaseClassTestRule.forClass(TestColumnRangeFilter.class);

    private static final HBaseTestingUtility TEST_UTIL = new HBaseTestingUtility();

    private static final Logger LOG = LoggerFactory.getLogger(TestColumnRangeFilter.class);

    @Rule
    public TestName name = new TestName();

    @Test
    public void TestColumnRangeFilterClient() throws Exception {
        String family = "Family";
        Table ht = TestColumnRangeFilter.TEST_UTIL.createTable(TableName.valueOf(name.getMethodName()), Bytes.toBytes(family), Integer.MAX_VALUE);
        List<String> rows = generateRandomWords(10, 8);
        long maxTimestamp = 2;
        List<String> columns = generateRandomWords(20000, 8);
        List<KeyValue> kvList = new ArrayList<>();
        Map<StringRange, List<KeyValue>> rangeMap = new HashMap<>();
        rangeMap.put(new StringRange(null, true, "b", false), new ArrayList());
        rangeMap.put(new StringRange("p", true, "q", false), new ArrayList());
        rangeMap.put(new StringRange("r", false, "s", true), new ArrayList());
        rangeMap.put(new StringRange("z", false, null, false), new ArrayList());
        String valueString = "ValueString";
        for (String row : rows) {
            Put p = new Put(Bytes.toBytes(row));
            p.setDurability(SKIP_WAL);
            for (String column : columns) {
                for (long timestamp = 1; timestamp <= maxTimestamp; timestamp++) {
                    KeyValue kv = KeyValueTestUtil.create(row, family, column, timestamp, valueString);
                    p.add(kv);
                    kvList.add(kv);
                    for (StringRange s : rangeMap.keySet()) {
                        if (s.inRange(column)) {
                            rangeMap.get(s).add(kv);
                        }
                    }
                }
            }
            ht.put(p);
        }
        TestColumnRangeFilter.TEST_UTIL.flush();
        ColumnRangeFilter filter;
        Scan scan = new Scan();
        scan.setMaxVersions();
        for (StringRange s : rangeMap.keySet()) {
            filter = new ColumnRangeFilter(((s.getStart()) == null ? null : Bytes.toBytes(s.getStart())), s.isStartInclusive(), ((s.getEnd()) == null ? null : Bytes.toBytes(s.getEnd())), s.isEndInclusive());
            Assert.assertEquals(rangeMap.get(s).size(), cellsCount(ht, filter));
        }
        ht.close();
    }

    @Test
    public void TestColumnRangeFilterWithColumnPaginationFilter() throws Exception {
        String family = "Family";
        String table = "TestColumnRangeFilterWithColumnPaginationFilter";
        try (Table ht = TestColumnRangeFilter.TEST_UTIL.createTable(TableName.valueOf(table), Bytes.toBytes(family), Integer.MAX_VALUE)) {
            // one row.
            String row = "row";
            // One version
            long timestamp = 100;
            // 10 columns
            int[] columns = new int[]{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
            String valueString = "ValueString";
            Put p = new Put(Bytes.toBytes(row));
            p.setDurability(SKIP_WAL);
            for (int column : columns) {
                KeyValue kv = KeyValueTestUtil.create(row, family, Integer.toString(column), timestamp, valueString);
                p.add(kv);
            }
            ht.put(p);
            TestColumnRangeFilter.TEST_UTIL.flush();
            // Column range from 1 to 9.
            StringRange stringRange = new StringRange("1", true, "9", false);
            ColumnRangeFilter filter1 = new ColumnRangeFilter(Bytes.toBytes(stringRange.getStart()), stringRange.isStartInclusive(), Bytes.toBytes(stringRange.getEnd()), stringRange.isEndInclusive());
            ColumnPaginationFilter filter2 = new ColumnPaginationFilter(5, 0);
            ColumnPaginationFilter filter3 = new ColumnPaginationFilter(5, 1);
            ColumnPaginationFilter filter4 = new ColumnPaginationFilter(5, 2);
            ColumnPaginationFilter filter5 = new ColumnPaginationFilter(5, 6);
            ColumnPaginationFilter filter6 = new ColumnPaginationFilter(5, 9);
            Assert.assertEquals(5, cellsCount(ht, new FilterList(Operator.MUST_PASS_ALL, filter1, filter2)));
            Assert.assertEquals(5, cellsCount(ht, new FilterList(Operator.MUST_PASS_ALL, filter1, filter3)));
            Assert.assertEquals(5, cellsCount(ht, new FilterList(Operator.MUST_PASS_ALL, filter1, filter4)));
            Assert.assertEquals(2, cellsCount(ht, new FilterList(Operator.MUST_PASS_ALL, filter1, filter5)));
            Assert.assertEquals(0, cellsCount(ht, new FilterList(Operator.MUST_PASS_ALL, filter1, filter6)));
        }
    }
}

