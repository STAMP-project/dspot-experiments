package com.orientechnologies.orient.server.distributed.scenariotest;


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
        init(2);
        prepare(false);
        execute();
    }
}

