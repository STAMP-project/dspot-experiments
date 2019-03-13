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
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.openmessaging.rocketmq.producer;


import SendStatus.FLUSH_DISK_TIMEOUT;
import SendStatus.SEND_OK;
import io.openmessaging.exception.OMSRuntimeException;
import io.openmessaging.producer.Producer;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ProducerImplTest {
    private Producer producer;

    @Mock
    private DefaultMQProducer rocketmqProducer;

    @Test
    public void testSend_OK() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        SendResult sendResult = new SendResult();
        sendResult.setMsgId("TestMsgID");
        sendResult.setSendStatus(SEND_OK);
        Mockito.when(rocketmqProducer.send(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyLong())).thenReturn(sendResult);
        io.openmessaging.producer.SendResult omsResult = producer.send(producer.createBytesMessage("HELLO_TOPIC", new byte[]{ 'a' }));
        assertThat(omsResult.messageId()).isEqualTo("TestMsgID");
    }

    @Test
    public void testSend_Not_OK() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        SendResult sendResult = new SendResult();
        sendResult.setSendStatus(FLUSH_DISK_TIMEOUT);
        Mockito.when(rocketmqProducer.send(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyLong())).thenReturn(sendResult);
        try {
            producer.send(producer.createBytesMessage("HELLO_TOPIC", new byte[]{ 'a' }));
            failBecauseExceptionWasNotThrown(OMSRuntimeException.class);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Send message to RocketMQ broker failed.");
        }
    }

    @Test
    public void testSend_WithException() throws InterruptedException, MQBrokerException, MQClientException, RemotingException {
        Mockito.when(rocketmqProducer.send(ArgumentMatchers.any(Message.class), ArgumentMatchers.anyLong())).thenThrow(MQClientException.class);
        try {
            producer.send(producer.createBytesMessage("HELLO_TOPIC", new byte[]{ 'a' }));
            failBecauseExceptionWasNotThrown(OMSRuntimeException.class);
        } catch (Exception e) {
            assertThat(e).hasMessageContaining("Send message to RocketMQ broker failed.");
        }
    }
}

