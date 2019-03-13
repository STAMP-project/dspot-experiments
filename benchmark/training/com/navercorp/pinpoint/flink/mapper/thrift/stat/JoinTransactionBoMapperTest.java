/**
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.flink.mapper.thrift.stat;


import JoinTransactionBo.EMPTY_JOIN_TRANSACTION_BO;
import com.navercorp.pinpoint.common.server.bo.stat.join.JoinTransactionBo;
import com.navercorp.pinpoint.thrift.dto.flink.TFAgentStat;
import com.navercorp.pinpoint.thrift.dto.flink.TFTransaction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class JoinTransactionBoMapperTest {
    @Test
    public void mapTest() throws Exception {
        final TFAgentStat tFAgentStat = new TFAgentStat();
        final String id = "testAgent";
        tFAgentStat.setAgentId(id);
        tFAgentStat.setTimestamp(1491274138454L);
        tFAgentStat.setCollectInterval(5000);
        final TFTransaction tFTransaction = new TFTransaction();
        tFTransaction.setSampledNewCount(10);
        tFTransaction.setSampledContinuationCount(20);
        tFTransaction.setUnsampledNewCount(40);
        tFTransaction.setUnsampledContinuationCount(50);
        tFAgentStat.setTransaction(tFTransaction);
        final JoinTransactionBoMapper joinTransactionBoMapper = new JoinTransactionBoMapper();
        final JoinTransactionBo joinTransactionBo = joinTransactionBoMapper.map(tFAgentStat);
        Assert.assertNotNull(joinTransactionBo);
        Assert.assertEquals(joinTransactionBo.getId(), id);
        Assert.assertEquals(joinTransactionBo.getTimestamp(), 1491274138454L);
        Assert.assertEquals(joinTransactionBo.getCollectInterval(), 5000);
        Assert.assertEquals(joinTransactionBo.getTotalCount(), 120);
        Assert.assertEquals(joinTransactionBo.getMaxTotalCount(), 120);
        Assert.assertEquals(joinTransactionBo.getMaxTotalCountAgentId(), id);
        Assert.assertEquals(joinTransactionBo.getMinTotalCount(), 120);
        Assert.assertEquals(joinTransactionBo.getMinTotalCountAgentId(), id);
    }

    @Test
    public void map2Test() {
        final TFAgentStat tFAgentStat = new TFAgentStat();
        final String id = "testAgent";
        tFAgentStat.setAgentId(id);
        tFAgentStat.setTimestamp(1491274138454L);
        tFAgentStat.setCollectInterval(5000);
        final JoinTransactionBoMapper joinTransactionBoMapper = new JoinTransactionBoMapper();
        final JoinTransactionBo joinTransactionBo = joinTransactionBoMapper.map(tFAgentStat);
        Assert.assertEquals(joinTransactionBo, EMPTY_JOIN_TRANSACTION_BO);
    }
}

