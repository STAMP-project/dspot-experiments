package com.vip.saturn.it.impl;


import com.vip.saturn.it.base.AbstractSaturnIT;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Created by Ivy01.li on 2016/9/12.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShardingWithLoadIT extends AbstractSaturnIT {
    @Test
    public void A_JavaMultiJobWithLoad() throws Exception {
        String preferList = null;
        multiJobSharding(preferList);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    // ????3??????1,2
    @Test
    public void B_JavaMultiJobWithLoadWithPreferList() throws Exception {
        String preferList = ("executorName" + ("0," + "executorName")) + 1;
        multiJobSharding(preferList);
        AbstractSaturnIT.stopExecutorListGracefully();
    }
}

