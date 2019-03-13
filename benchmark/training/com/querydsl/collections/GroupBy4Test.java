package com.querydsl.collections;


import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.querydsl.core.annotations.QueryEntity;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;

import static QGroupBy4Test_Table.table;


public class GroupBy4Test {
    @QueryEntity
    public static class Table {
        String col1;

        String col2;

        String col3;

        public Table(String c1, String c2, String c3) {
            col1 = c1;
            col2 = c2;
            col3 = c3;
        }
    }

    @Test
    public void test() {
        List<GroupBy4Test.Table> data = Lists.newArrayList();
        data.add(new GroupBy4Test.Table("1", "abc", "111"));
        data.add(new GroupBy4Test.Table("1", "pqr", "222"));
        data.add(new GroupBy4Test.Table("2", "abc", "333"));
        data.add(new GroupBy4Test.Table("2", "pqr", "444"));
        data.add(new GroupBy4Test.Table("3", "abc", "555"));
        data.add(new GroupBy4Test.Table("3", "pqr", "666"));
        QGroupBy4Test_Table table = table;
        Map<String, Map<String, String>> grouped = CollQueryFactory.from(table, data).transform(groupBy(table.col1).as(map(table.col2, table.col3)));
        Assert.assertEquals(3, grouped.size());
        Assert.assertEquals(2, grouped.get("1").size());
        Assert.assertEquals(ImmutableSet.of("abc", "pqr"), grouped.get("1").keySet());
    }
}

