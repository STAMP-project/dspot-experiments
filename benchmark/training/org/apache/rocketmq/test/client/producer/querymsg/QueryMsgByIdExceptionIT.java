/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.apache.rocketmq.test.client.producer.querymsg;


import org.apache.log4j.Logger;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.test.base.BaseConf;
import org.apache.rocketmq.test.client.rmq.RMQNormalProducer;
import org.junit.Assert;
import org.junit.Test;


public class QueryMsgByIdExceptionIT extends BaseConf {
    private static Logger logger = Logger.getLogger(QueryMsgByKeyIT.class);

    private static RMQNormalProducer producer = null;

    private static String topic = null;

    @Test
    public void testQueryMsgByErrorMsgId() {
        QueryMsgByIdExceptionIT.producer.clearMsg();
        int msgSize = 20;
        String errorMsgId = "errorMsgId";
        QueryMsgByIdExceptionIT.producer.send(msgSize);
        Assert.assertEquals("Not all are sent", msgSize, QueryMsgByIdExceptionIT.producer.getAllUndupMsgBody().size());
        MessageExt queryMsg = null;
        try {
            queryMsg = QueryMsgByIdExceptionIT.producer.getProducer().viewMessage(errorMsgId);
        } catch (Exception e) {
        }
        assertThat(queryMsg).isNull();
    }

    @Test
    public void testQueryMsgByNullMsgId() {
        QueryMsgByIdExceptionIT.producer.clearMsg();
        int msgSize = 20;
        String errorMsgId = null;
        QueryMsgByIdExceptionIT.producer.send(msgSize);
        Assert.assertEquals("Not all are sent", msgSize, QueryMsgByIdExceptionIT.producer.getAllUndupMsgBody().size());
        MessageExt queryMsg = null;
        try {
            queryMsg = QueryMsgByIdExceptionIT.producer.getProducer().viewMessage(errorMsgId);
        } catch (Exception e) {
        }
        assertThat(queryMsg).isNull();
    }
}

