package com.vip.saturn.it.impl;


import JobType.JAVA_JOB;
import SaturnExecutorsNode.SHARDING_COUNT_PATH;
import com.vip.saturn.it.base.AbstractSaturnIT;
import com.vip.saturn.it.base.FinishCheck;
import com.vip.saturn.it.job.SimpleJavaJob;
import com.vip.saturn.job.console.domain.JobConfig;
import com.vip.saturn.job.executor.Main;
import com.vip.saturn.job.internal.sharding.ShardingNode;
import com.vip.saturn.job.internal.storage.JobNodePath;
import com.vip.saturn.job.sharding.node.SaturnExecutorsNode;
import com.vip.saturn.job.utils.ItemUtils;
import com.vip.saturn.job.utils.SystemEnvProperties;
import java.util.List;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShardingIT extends AbstractSaturnIT {
    @Test
    public void test_A_JAVA() throws Exception {
        int shardCount = 3;
        final String jobName = "test_A_JAVA";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        Thread.sleep(1000);
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???1?executor

        runAtOnce(jobName);
        Thread.sleep(1000);
        assertThat(AbstractSaturnIT.regCenter.getDirectly(SHARDING_COUNT_PATH)).isEqualTo("4");
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).contains(0, 1, 2);
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ???2?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).isNotEmpty();
        System.out.println(items);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isNotEmpty();
        Main executor3 = AbstractSaturnIT.startOneNewExecutorList();// ???3?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor3.getExecutorName()))));
        System.out.println(items);
        assertThat(items).hasSize(1);
        AbstractSaturnIT.stopExecutorGracefully(0);// ??1?executor

        Thread.sleep(1000);
        assertThat(AbstractSaturnIT.regCenter.getDirectly(SHARDING_COUNT_PATH)).isEqualTo("10");
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        AbstractSaturnIT.stopExecutorGracefully(1);// ??2?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        // ???????3?executor
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor3.getExecutorName()))));
        assertThat(items).contains(0, 1, 2);
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    @Test
    public void test_D_PreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???1?executor

        int shardCount = 3;
        final String jobName = "test_D_PreferList";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        jobConfig.setPreferList(executor1.getExecutorName());
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).contains(0, 1, 2);
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ???2?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).contains(0, 1, 2);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        AbstractSaturnIT.stopExecutorGracefully(0);// ??1?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        System.out.println(items);
        assertThat(items).contains(0, 1, 2);
        // ???????executor
        startExecutor(0);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        // ???????executor1?
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        AbstractSaturnIT.log.info("sharding at executor1 {}: ", items);
        assertThat(items).contains(0, 1, 2);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        AbstractSaturnIT.log.info("sharding at executor2 {}: ", items);
        assertThat(items).isEmpty();
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    @Test
    public void test_E_PreferListOnly() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???1?executor

        int shardCount = 3;
        final String jobName = "test_E_PreferListOnly";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("0=0,1=1,2=2");
        jobConfig.setPreferList(executor1.getExecutorName());
        jobConfig.setUseDispreferList(false);
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).contains(0, 1, 2);
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ???2?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).contains(0, 1, 2);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        AbstractSaturnIT.stopExecutorGracefully(0);// ??1?executor

        Thread.sleep(1000);
        runAtOnce(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                if (isNeedSharding(jobName)) {
                    return false;
                }
                return true;
            }
        }, 10);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        System.out.println(items);
        assertThat(items).isEmpty();
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * ??????????preferList???useDispreferList?false????preferList????????
     */
    @Test
    public void test_F_LocalModeWithPreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();
        int shardCount = 2;
        final String jobName = "test_F_LocalModeWithPreferList";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("*=0");
        jobConfig.setLocalMode(true);
        jobConfig.setPreferList(executor2.getExecutorName());// ??preferList?executor2

        jobConfig.setUseDispreferList(false);// ??useDispreferList?false

        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        runAtOnce(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        assertThat(items).contains(0);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).isEmpty();
        // wait running completed
        Thread.sleep(1000);
        // executor2??
        AbstractSaturnIT.stopExecutorGracefully(1);
        Thread.sleep(1000);
        // ??sharding????
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        runAtOnce(jobName);
        // ??????
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        // executor1????????
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).isEmpty();
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * ??????????preferList?????useDispreferList?true???????preferList??????????useDispreferList????????????
     */
    @Test
    public void test_F_LocalModeWithPreferListAndUseDispreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();
        int shardCount = 2;
        final String jobName = "test_F_LocalModeWithPreferListAndUseDispreferList";
        for (int i = 0; i < shardCount; i++) {
            String key = (jobName + "_") + i;
            SimpleJavaJob.statusMap.put(key, 0);
        }
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(shardCount);
        jobConfig.setShardingItemParameters("*=0");
        jobConfig.setLocalMode(true);
        jobConfig.setPreferList(executor2.getExecutorName());// ??preferList?executor2

        jobConfig.setUseDispreferList(true);// ??useDispreferList?true

        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        runAtOnce(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        // executor2???0???executor1??????
        List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
        assertThat(items).contains(0);
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).isEmpty();
        // wait running completed
        Thread.sleep(1000);
        // executor2??
        AbstractSaturnIT.stopExecutorGracefully(1);
        Thread.sleep(1000L);
        // ??sharding????
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        runAtOnce(jobName);
        // ??????
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        // executor1????????
        items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
        assertThat(items).isEmpty();
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * preferList??????????useDispreferList?true??????executor???????????????executor??????executor????
     */
    @Test
    public void test_G_ContainerWithUseDispreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???????executor

        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ??????executor

            final int shardCount = 2;
            final String jobName = "test_G_ContainerWithUseDispreferList";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            final JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("0=0,1=1");
            jobConfig.setLocalMode(false);
            jobConfig.setPreferList(("@" + taskId));// ??preferList?@taskId

            jobConfig.setUseDispreferList(true);// ??useDispreferList?true

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor2???0?1???executor1??????
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
            assertThat(items).contains(0, 1);
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return hasCompletedZnodeForAllShards(jobName, shardCount);
                }
            }, 10);
            // executor2??
            AbstractSaturnIT.stopExecutorGracefully(1);
            Thread.sleep(1000L);
            // ??sharding????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            // ??????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor1????0?1??
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).contains(0, 1);
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * preferList??????????useDispreferList?false??????executor???????????????executor??????executor???????
     */
    @Test
    public void test_H_ContainerWithOnlyPreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???????executor

        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ??????executor

            int shardCount = 2;
            final String jobName = "test_H_ContainerWithOnlyPreferList";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("0=0,1=1");
            jobConfig.setLocalMode(false);
            jobConfig.setPreferList(("@" + taskId));// ??preferList?@taskId

            jobConfig.setUseDispreferList(false);// ??useDispreferList?false

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor2???0?1???executor1??????
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
            assertThat(items).contains(0, 1);
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            // executor2??
            AbstractSaturnIT.stopExecutorGracefully(1);
            Thread.sleep(1000L);
            // ??sharding????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            // ??????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor1????????
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * preferList????????????????useDispreferList?true??????executor???????????????1???????executor??????executor?????????useDispreferList???????
     */
    @Test
    public void test_I_ContainerWithLocalModeAndUseDispreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???????executor

        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ??????executor

            int shardCount = 2;
            final String jobName = "test_I_ContainerWithLocalModeAndUseDispreferList";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("*=a");
            jobConfig.setLocalMode(true);// ??localMode?true

            jobConfig.setPreferList(("@" + taskId));// ??preferList?@taskId

            jobConfig.setUseDispreferList(true);// ??useDispreferList?true

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor2???0???executor1??????
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
            assertThat(items).hasSize(1).contains(0);
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            // executor2??
            AbstractSaturnIT.stopExecutorGracefully(1);
            Thread.sleep(1000L);
            // ??sharding????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            // ??????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor1???????
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * preferList????????????????useDispreferList?false??????executor???????????????1???????executor??????executor??????
     */
    @Test
    public void test_J_ContainerWithLocalModeAndOnlyPreferList() throws Exception {
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();// ???????executor

        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main executor2 = AbstractSaturnIT.startOneNewExecutorList();// ??????executor

            int shardCount = 2;
            final String jobName = "test_J_ContainerWithLocalModeAndOnlyPreferList";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("*=a");
            jobConfig.setLocalMode(true);// ??localMode?true

            jobConfig.setPreferList(("@" + taskId));// ??preferList?@taskId

            jobConfig.setUseDispreferList(false);// ??useDispreferList?false

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor2???0???executor1??????
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor2.getExecutorName()))));
            assertThat(items).hasSize(1).contains(0);
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            // executor2??
            AbstractSaturnIT.stopExecutorGracefully(1);
            Thread.sleep(1000L);
            // ??sharding????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            // ??????
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // executor1????????
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(executor1.getExecutorName()))));
            assertThat(items).isEmpty();
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * preferList????????????useDispreferList?true?????????????
     */
    @Test
    public void test_K_ContainerWithUseDispreferList_ButInvalidTaskId() throws Exception {
        Main logicExecutor = AbstractSaturnIT.startOneNewExecutorList();// ???????executor

        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main vdosExecutor = AbstractSaturnIT.startOneNewExecutorList();// ??????executor

            int shardCount = 2;
            final String jobName = "test_K_ContainerWithUseDispreferList_ButInvalidTaskId";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("0=0,1=1");
            jobConfig.setLocalMode(false);
            jobConfig.setPreferList(("@haha" + taskId));// ??preferList?@hahataskId

            jobConfig.setUseDispreferList(true);// ??useDispreferList?true

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // vdosExecutor???????logicExecutor???0?1??
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(vdosExecutor.getExecutorName()))));
            assertThat(items).isEmpty();
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(logicExecutor.getExecutorName()))));
            assertThat(items).contains(0, 1);
            // vdosExecutor??
            AbstractSaturnIT.stopExecutorGracefully(1);
            Thread.sleep(1000);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // logicExecutor????0?1??
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(logicExecutor.getExecutorName()))));
            assertThat(items).contains(0, 1);
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * preferList????????????useDispreferList?true?????????????????????????????????????
     */
    @Test
    public void test_L_ContainerWithUseDispreferList_ButInvalidTaskId_ContainerFirst() throws Exception {
        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            int shardCount = 2;
            final String jobName = "test_L_ContainerWithUseDispreferList_ButInvalidTaskId_ContainerFirst";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("0=0,1=1");
            jobConfig.setLocalMode(false);
            jobConfig.setPreferList("@haha");// ??preferList?@haha

            jobConfig.setUseDispreferList(true);// ??useDispreferList?true

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            // ??????executor
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main vdosExecutor = AbstractSaturnIT.startOneNewExecutorList();
            // ???????executor
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = false;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = null;
            Main logicExecutor = AbstractSaturnIT.startOneNewExecutorList();
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // vdosExecutor???????logicExecutor???0?1??
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(vdosExecutor.getExecutorName()))));
            assertThat(items).isEmpty();
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(logicExecutor.getExecutorName()))));
            assertThat(items).contains(0, 1);
            // vdosExecutor??
            AbstractSaturnIT.stopExecutorGracefully(0);
            Thread.sleep(1000L);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // logicExecutor????0?1??
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(logicExecutor.getExecutorName()))));
            assertThat(items).contains(0, 1);
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * preferList????????????useDispreferList?true????????????????????????????????????
     */
    @Test
    public void test_M_UseDispreferList_ButInvalidLogicPreferList() throws Exception {
        boolean cleanOld = SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN;
        String taskOld = SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID;
        try {
            int shardCount = 2;
            final String jobName = "test_M_UseDispreferList_ButInvalidLogicPreferList";
            for (int i = 0; i < shardCount; i++) {
                String key = (jobName + "_") + i;
                SimpleJavaJob.statusMap.put(key, 0);
            }
            JobConfig jobConfig = new JobConfig();
            jobConfig.setJobName(jobName);
            jobConfig.setCron("9 9 9 9 9 ? 2099");
            jobConfig.setJobType(JAVA_JOB.toString());
            jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
            jobConfig.setShardingTotalCount(shardCount);
            jobConfig.setShardingItemParameters("0=0,1=1");
            jobConfig.setLocalMode(false);
            jobConfig.setPreferList("haha");// ??preferList?@haha

            jobConfig.setUseDispreferList(true);// ??useDispreferList?true

            addJob(jobConfig);
            Thread.sleep(1000);
            enableJob(jobName);
            // ??????executor
            String taskId = "test1";
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = true;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskId;
            Main vdosExecutor = AbstractSaturnIT.startOneNewExecutorList();
            // ???????executor
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = false;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = null;
            Main logicExecutor = AbstractSaturnIT.startOneNewExecutorList();
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // vdosExecutor???????logicExecutor???0?1??
            List<Integer> items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(vdosExecutor.getExecutorName()))));
            assertThat(items).isEmpty();
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(logicExecutor.getExecutorName()))));
            assertThat(items).contains(0, 1);
            // vdosExecutor??
            AbstractSaturnIT.stopExecutorGracefully(0);
            Thread.sleep(1000L);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return isNeedSharding(jobName);
                }
            }, 10);
            runAtOnce(jobName);
            waitForFinish(new FinishCheck() {
                @Override
                public boolean isOk() {
                    return !(isNeedSharding(jobName));
                }
            }, 10);
            // logicExecutor????0?1??
            items = ItemUtils.toItemList(AbstractSaturnIT.regCenter.getDirectly(JobNodePath.getNodeFullPath(jobName, ShardingNode.getShardingNode(logicExecutor.getExecutorName()))));
            assertThat(items).contains(0, 1);
            disableJob(jobName);
            Thread.sleep(1000);
            removeJob(jobName);
            AbstractSaturnIT.stopExecutorListGracefully();
        } finally {
            SystemEnvProperties.VIP_SATURN_EXECUTOR_CLEAN = cleanOld;
            SystemEnvProperties.VIP_SATURN_CONTAINER_DEPLOYMENT_ID = taskOld;
        }
    }

    /**
     * sharding?????????????
     */
    @Test
    public void test_N_NotifyNecessaryJobs() throws Exception {
        // ??1?executor
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        // ???????
        Thread.sleep(1000);
        final String jobName1 = "test_N_NotifyNecessaryJobs1";
        JobConfig jobConfig1 = new JobConfig();
        jobConfig1.setJobName(jobName1);
        jobConfig1.setCron("9 9 9 9 9 ? 2099");
        jobConfig1.setJobType(JAVA_JOB.toString());
        jobConfig1.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig1.setShardingTotalCount(1);
        jobConfig1.setShardingItemParameters("0=0");
        addJob(jobConfig1);
        Thread.sleep(1000);
        enableJob(jobName1);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName1);
            }
        }, 10);
        runAtOnce(jobName1);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName1));
            }
        }, 10);
        // ???????
        Thread.sleep(1000);
        final String jobName2 = "test_N_NotifyNecessaryJobs2";
        JobConfig jobConfig2 = new JobConfig();
        jobConfig2.setJobName(jobName2);
        jobConfig2.setCron("9 9 9 9 9 ? 2099");
        jobConfig2.setJobType(JAVA_JOB.toString());
        jobConfig2.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig2.setShardingTotalCount(1);
        jobConfig2.setShardingItemParameters("0=0");
        addJob(jobConfig2);
        // job1?job2???re-sharding
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return (!(isNeedSharding(jobName1))) && (!(isNeedSharding(jobName2)));
            }
        }, 10);
        enableJob(jobName2);
        // job1??re-sharding
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName1));
            }
        }, 10);
        disableJob(jobName1);
        removeJob(jobName1);
        disableJob(jobName2);
        removeJob(jobName2);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * sharding????????????? test the fix:
     * https://github.com/vipshop/Saturn/commit/9b64dfe50c21c1b4f3e3f781d5281be06a0a8d08
     */
    @Test
    public void test_O_NotifyNecessaryJobsPrior() throws Exception {
        // ??1?executor
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        // ???????
        Thread.sleep(1000);
        final String jobName = "test_O_NotifyNecessaryJobsPrior";
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        runAtOnce(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        // ????
        Thread.sleep(1000);
        disableJob(jobName);
        // ??preferList??????executor?????useDispreferList?false
        zkUpdateJobNode(jobName, "config/preferList", "abc");
        zkUpdateJobNode(jobName, "config/useDispreferList", "false");
        // ????
        Thread.sleep(500);
        enableJob(jobName);
        // job1?re-sharding
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * NamespaceShardingService is not necessary to persist the sharding result content that is not changed<br/>
     * https://github.com/vipshop/Saturn/issues/88
     */
    @Test
    public void test_P_PersistShardingContentIfNecessary() throws Exception {
        // ??1?executor
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        // ???????
        Thread.sleep(1000);
        final String jobName = "test_P_PersistShardingContentIfNecessary";
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setPreferList("abc");
        jobConfig.setUseDispreferList(false);
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        runAtOnce(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        long mtime = checkExists().forPath(SaturnExecutorsNode.getShardingContentElementNodePath("0")).getMtime();
        // ????
        Thread.sleep(1000);
        disableJob(jobName);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        long mtime2 = checkExists().forPath(SaturnExecutorsNode.getShardingContentElementNodePath("0")).getMtime();
        assertThat(mtime).isEqualTo(mtime2);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }

    /**
     * https://github.com/vipshop/Saturn/issues/119
     */
    @Test
    public void test_Q_PersistNecessaryTheRightData() throws Exception {
        // ??1?executor
        Main executor1 = AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        // ???????
        Thread.sleep(1000);
        final String jobName = "test_Q_PersistNecessaryTheRightData";
        JobConfig jobConfig = new JobConfig();
        jobConfig.setJobName(jobName);
        jobConfig.setCron("9 9 9 9 9 ? 2099");
        jobConfig.setJobType(JAVA_JOB.toString());
        jobConfig.setJobClass(SimpleJavaJob.class.getCanonicalName());
        jobConfig.setShardingTotalCount(1);
        jobConfig.setShardingItemParameters("0=0");
        jobConfig.setUseDispreferList(false);
        addJob(jobConfig);
        Thread.sleep(1000);
        enableJob(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        String jobLeaderShardingNecessaryNodePath = SaturnExecutorsNode.getJobLeaderShardingNecessaryNodePath(jobName);
        String data1 = AbstractSaturnIT.regCenter.getDirectly(jobLeaderShardingNecessaryNodePath);
        System.out.println(("data1:" + data1));
        runAtOnce(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        // ???2?executor
        Main executor2 = AbstractSaturnIT.startOneNewExecutorList();
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        String data2 = AbstractSaturnIT.regCenter.getDirectly(jobLeaderShardingNecessaryNodePath);
        System.out.println(("data2:" + data2));
        assertThat(data2.contains(executor2.getExecutorName())).isTrue();
        runAtOnce(jobName);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return !(isNeedSharding(jobName));
            }
        }, 10);
        // offline executor2
        AbstractSaturnIT.stopExecutorGracefully(1);
        Thread.sleep(1000);
        waitForFinish(new FinishCheck() {
            @Override
            public boolean isOk() {
                return isNeedSharding(jobName);
            }
        }, 10);
        String data3 = AbstractSaturnIT.regCenter.getDirectly(jobLeaderShardingNecessaryNodePath);
        System.out.println(("data3:" + data3));
        assertThat(data3.contains(executor2.getExecutorName())).isFalse();
        disableJob(jobName);
        Thread.sleep(1000);
        removeJob(jobName);
        AbstractSaturnIT.stopExecutorListGracefully();
    }
}

