package com.vip.saturn.it.impl;


import com.vip.saturn.it.base.AbstractSaturnIT;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FailoverIT extends AbstractSaturnIT {
    /**
     * ??1???????Executor?failover???????????????sharding?? Executor?? > ???????
     */
    @Test
    public void test_A_JavaJob() throws Exception {
        AbstractSaturnIT.startExecutorList(3);
        final int shardCount = 2;
        final String jobName = "failoverITJobJava1";
        failover(shardCount, jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * ??2????failover?? Executor?? = ???????
     */
    @Test
    public void test_B_JavaJob() throws Exception {
        AbstractSaturnIT.startExecutorList(2);
        final int shardCount = 2;
        final String jobName = "failoverITJobJava2";
        failover(shardCount, jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * ??3??failover??????????????????????failover??
     */
    @Test
    public void test_C_JavaJob() throws Exception {
        AbstractSaturnIT.startExecutorList(2);
        final int shardCount = 2;
        final String jobName = "failoverITJobJava3";
        failoverWithDisabled(shardCount, jobName, 2);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * ???????executor????????java???????
     */
    @Test
    public void test_D_disabledJavaJobStillBeAborted() throws Exception {
        AbstractSaturnIT.startExecutorList(2);
        final int shardCount = 2;
        final String jobName = "failoverITJobJava4";
        failoverWithDisabled(shardCount, jobName, 1);
        AbstractSaturnIT.stopExecutorListGracefully();
    }
}

