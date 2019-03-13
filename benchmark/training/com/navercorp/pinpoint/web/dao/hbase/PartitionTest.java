package com.navercorp.pinpoint.web.dao.hbase;


import com.google.common.collect.Lists;
import java.util.List;
import org.junit.Test;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
public class PartitionTest {
    private List<Integer> original = Lists.newArrayList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

    @Test
    public void splitTransactionIdList() throws Exception {
        assertPartition(1);
        assertPartition(2);
        assertPartition(3);
        assertPartition(5);
        assertPartition(10);
        assertPartition(11);
    }
}

