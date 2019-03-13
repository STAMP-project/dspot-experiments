package com.vip.saturn.job.sharding;


import Level.INFO;
import Level.OFF;
import ch.qos.logback.classic.Logger;
import com.vip.saturn.job.sharding.entity.Executor;
import com.vip.saturn.job.sharding.entity.Shard;
import com.vip.saturn.job.sharding.service.NamespaceShardingContentService;
import com.vip.saturn.job.utils.NestedZkUtils;
import java.util.ArrayList;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.junit.Test;
import org.slf4j.LoggerFactory;


/**
 * Created by xiaopeng.he on 2016/7/12.
 */
public class NamespaceShardingContentServiceTest {
    private NestedZkUtils nestedZkUtils;

    @Test
    public void testBigData() throws Exception {
        Logger logger = ((Logger) (LoggerFactory.getLogger(NamespaceShardingContentService.class)));
        logger.setLevel(OFF);
        try {
            CuratorFramework framework = nestedZkUtils.createClient("namespace");
            NamespaceShardingContentService namespaceShardingContentService = new NamespaceShardingContentService(framework);
            List<Executor> executorList = new ArrayList<>();
            for (int i = 1; i < 250; i++) {
                Executor executor = new Executor();
                executor.setExecutorName(("e" + i));
                executor.setIp(("ip" + i));
                List<Shard> shardList = new ArrayList<>();
                for (int j = 1; j < 100; j++) {
                    Shard shard = new Shard();
                    shard.setJobName((("job" + i) + j));
                    shard.setItem(j);
                    shard.setLoadLevel(j);
                    shardList.add(shard);
                    executor.setTotalLoadLevel(((executor.getTotalLoadLevel()) + ((shard.getLoadLevel()) * (shard.getItem()))));
                }
                executor.setShardList(shardList);
                executorList.add(executor);
            }
            namespaceShardingContentService.persistDirectly(executorList);
            List<Executor> executorList1 = namespaceShardingContentService.getExecutorList();
            assertThat(executorList1.size()).isEqualTo(executorList.size());
        } finally {
            logger.setLevel(INFO);
        }
    }
}

