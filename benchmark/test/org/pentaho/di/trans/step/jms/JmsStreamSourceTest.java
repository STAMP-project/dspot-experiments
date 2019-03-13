/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.step.jms;


import com.ibm.msg.client.jms.JmsContext;
import java.util.Collections;
import java.util.List;
import javax.jms.Destination;
import javax.jms.JMSConsumer;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class JmsStreamSourceTest {
    @Mock
    private JmsContext context;

    @Mock
    private JmsDelegate delegate;

    @Mock
    private JMSConsumer consumer;

    @Mock
    private JmsConsumer consumerStep;

    @Mock
    private Destination destination;

    @Mock
    private Message message;

    private JmsStreamSource source;

    @Test(timeout = 5000)
    public void testReceiveMessage() {
        source.open();
        Mockito.verify(delegate, Mockito.atLeastOnce()).getJmsContext();
        Mockito.verify(delegate, Mockito.atLeastOnce()).getDestination();
        List<Object> sentMessage = source.flowable().firstElement().blockingGet(Collections.emptyList());
        Assert.assertThat(sentMessage.size(), CoreMatchers.equalTo(2));
        Assert.assertThat(sentMessage.get(0), CoreMatchers.equalTo("message"));
        Assert.assertThat(sentMessage.get(1), CoreMatchers.equalTo("dest"));
        Mockito.verify(consumer).close();
        Mockito.verify(context).close();
    }

    @Test(timeout = 5000)
    public void handlesJmsRuntimeException() {
        Mockito.when(consumer.receive(0)).thenThrow(new JMSRuntimeException("exception"));
        source.open();
        Mockito.verify(delegate).getJmsContext();
        Mockito.verify(delegate).getDestination();
        try {
            source.flowable().firstElement().blockingGet(Collections.emptyList());
            Assert.fail("Expected exception ");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof JMSRuntimeException));
        }
    }
}

