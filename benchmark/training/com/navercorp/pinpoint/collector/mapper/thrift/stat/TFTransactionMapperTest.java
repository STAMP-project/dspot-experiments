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
package com.navercorp.pinpoint.collector.mapper.thrift.stat;


import com.navercorp.pinpoint.common.server.bo.stat.TransactionBo;
import com.navercorp.pinpoint.thrift.dto.flink.TFTransaction;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author minwoo.jung
 */
public class TFTransactionMapperTest {
    @Test
    public void mapTest() throws Exception {
        TFTransactionMapper tFTransactionMapper = new TFTransactionMapper();
        TransactionBo transactionBo = new TransactionBo();
        transactionBo.setSampledContinuationCount(5);
        transactionBo.setUnsampledContinuationCount(6);
        transactionBo.setSampledNewCount(11);
        transactionBo.setUnsampledNewCount(10);
        TFTransaction tFtransaction = tFTransactionMapper.map(transactionBo);
        Assert.assertEquals(tFtransaction.getSampledContinuationCount(), 5);
        Assert.assertEquals(tFtransaction.getUnsampledContinuationCount(), 6);
        Assert.assertEquals(tFtransaction.getSampledNewCount(), 11);
        Assert.assertEquals(tFtransaction.getUnsampledNewCount(), 10);
    }
}

