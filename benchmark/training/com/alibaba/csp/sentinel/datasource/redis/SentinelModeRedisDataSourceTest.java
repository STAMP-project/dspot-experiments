/**
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.datasource.redis;


import RedisURI.Builder;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.sync.RedisCommands;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


/**
 * Redis redisSentinel mode test cases for {@link RedisDataSource}.
 *
 * @author tiger
 */
@Ignore("Before run this test, you need to set up your Redis Sentinel.")
public class SentinelModeRedisDataSourceTest {
    private String host = "localhost";

    private int redisSentinelPort = 5000;

    private String redisSentinelMasterId = "myMaster";

    private String ruleKey = "sentinel.rules.flow.ruleKey";

    private String channel = "sentinel.rules.flow.channel";

    private final RedisClient client = RedisClient.create(Builder.sentinel(host, redisSentinelPort).withSentinelMasterId(redisSentinelMasterId).build());

    @Test
    public void testConnectToSentinelAndPubMsgSuccess() {
        int maxQueueingTimeMs = new Random().nextInt();
        String flowRulesJson = (("[{\"resource\":\"test\", \"limitApp\":\"default\", \"grade\":1, \"count\":\"0.0\", \"strategy\":0, " + ("\"refResource\":null, " + "\"controlBehavior\":0, \"warmUpPeriodSec\":10, \"maxQueueingTimeMs\":")) + maxQueueingTimeMs) + ", \"controller\":null}]";
        RedisCommands<String, String> subCommands = client.connect().sync();
        subCommands.multi();
        subCommands.set(ruleKey, flowRulesJson);
        subCommands.publish(channel, flowRulesJson);
        subCommands.exec();
        await().timeout(2, TimeUnit.SECONDS).until(new Callable<List<FlowRule>>() {
            @Override
            public List<FlowRule> call() throws Exception {
                return FlowRuleManager.getRules();
            }
        }, Matchers.hasSize(1));
        List<FlowRule> rules = FlowRuleManager.getRules();
        Assert.assertEquals(rules.get(0).getMaxQueueingTimeMs(), maxQueueingTimeMs);
        String value = subCommands.get(ruleKey);
        List<FlowRule> flowRulesValuesInRedis = buildFlowConfigParser().convert(value);
        Assert.assertEquals(flowRulesValuesInRedis.size(), 1);
        Assert.assertEquals(flowRulesValuesInRedis.get(0).getMaxQueueingTimeMs(), maxQueueingTimeMs);
    }
}

