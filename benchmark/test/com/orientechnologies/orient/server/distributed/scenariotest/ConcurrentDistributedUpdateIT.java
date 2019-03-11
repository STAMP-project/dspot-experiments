package com.orientechnologies.orient.server.distributed.scenariotest;


import OGlobalConfiguration.DISTRIBUTED_CONCURRENT_TX_MAX_AUTORETRY;
import com.orientechnologies.orient.server.distributed.AbstractDistributedWriteTest;
import com.orientechnologies.orient.server.distributed.AbstractServerClusterInsertTest;
import org.junit.Test;


public class ConcurrentDistributedUpdateIT extends AbstractScenarioTest {
    @Test
    public void test() throws Exception {
        count = 1;
        AbstractServerClusterInsertTest.writerCount = 1;
        delayWriter = 500;
        className = "Test";
        indexName = null;
        DISTRIBUTED_CONCURRENT_TX_MAX_AUTORETRY.setValue(0);
        init(2);
        prepare(false);
        execute();
    }
}

