/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2018 by Hitachi Vantara : http://www.pentaho.com
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


import JmsProvider.ConnectionType.ACTIVEMQ;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import org.apache.activemq.artemis.junit.EmbeddedJMSResource;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.core.logging.LogChannelInterfaceFactory;
import org.pentaho.di.core.util.GenericStepData;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.step.jms.context.JmsProvider;


@RunWith(MockitoJUnitRunner.class)
public class JmsProducerTest {
    @Mock
    LogChannelInterfaceFactory logChannelFactory;

    @Mock
    LogChannelInterface logChannel;

    @Mock
    JMSContext jmsContext;

    @Mock
    JmsProvider jmsProvider;

    @Mock
    JMSProducer jmsProducer;

    @Rule
    public EmbeddedJMSResource resource = new EmbeddedJMSResource(0);

    static final String PROPERTY_NAME_ONE = "property1";

    static final String PROPERTY_NAME_TWO = "property2";

    private Trans trans;

    private JmsProducer step;

    private JmsProducerMeta meta;

    private GenericStepData data;

    @Test
    public void testProperties() throws InterruptedException, ExecutionException, TimeoutException, KettleException {
        Map<String, String> propertyValuesByName = new LinkedHashMap<>();
        propertyValuesByName.put(JmsProducerTest.PROPERTY_NAME_ONE, "property1Value");
        propertyValuesByName.put(JmsProducerTest.PROPERTY_NAME_TWO, "property2Value");
        meta.setPropertyValuesByName(propertyValuesByName);
        step.init(meta, data);
        trans.prepareExecution(new String[]{  });
        trans.startThreads();
        trans.waitUntilFinished();
        Runnable processRowRunnable = () -> {
            try {
                step.processRow(meta, data);
            } catch (KettleException e) {
                Assert.fail(e.getMessage());
            }
        };
        ExecutorService service = Executors.newSingleThreadExecutor();
        service.submit(processRowRunnable).get(5, TimeUnit.SECONDS);
        service.awaitTermination(5, TimeUnit.SECONDS);
        step.stopRunning(meta, data);
        service.shutdown();
        // Ensure the producer properties were set
        Assert.assertEquals(propertyValuesByName.get(JmsProducerTest.PROPERTY_NAME_ONE), step.producer.getStringProperty(JmsProducerTest.PROPERTY_NAME_ONE));
        Assert.assertEquals(propertyValuesByName.get(JmsProducerTest.PROPERTY_NAME_TWO), step.producer.getStringProperty(JmsProducerTest.PROPERTY_NAME_TWO));
    }

    @Test
    public void testSetOptions() throws KettleException {
        step.init(meta, data);
        // Defaults
        step.processRow(meta, data);
        Assert.assertEquals(false, step.producer.getDisableMessageID());
        Assert.assertEquals(false, step.producer.getDisableMessageTimestamp());
        Assert.assertEquals(2, step.producer.getDeliveryMode());
        Assert.assertEquals(4, step.producer.getPriority());
        Assert.assertEquals(0, step.producer.getTimeToLive());
        Assert.assertEquals(0, step.producer.getDeliveryDelay());
        Assert.assertNull(step.producer.getJMSCorrelationID());
        Assert.assertNull(step.producer.getJMSType());
    }

    @Test
    public void testUserDrivenSetOptions() throws KettleException {
        // User driven
        meta.setDisableMessageId("true");
        meta.setDisableMessageTimestamp("false");
        meta.setDeliveryMode("1");
        meta.setPriority("2");
        meta.setTimeToLive("3");
        meta.setDeliveryDelay("4");
        meta.setJmsCorrelationId("ASDF");
        meta.setJmsType("JMSType");
        step.first = true;
        step.init(meta, data);
        step.processRow(meta, data);
        Assert.assertEquals(true, step.producer.getDisableMessageID());
        Assert.assertEquals(false, step.producer.getDisableMessageTimestamp());
        Assert.assertEquals(1, step.producer.getDeliveryMode());
        Assert.assertEquals(2, step.producer.getPriority());
        Assert.assertEquals(3, step.producer.getTimeToLive());
        Assert.assertEquals(4, step.producer.getDeliveryDelay());
        Assert.assertEquals("ASDF", step.producer.getJMSCorrelationID());
        Assert.assertEquals("JMSType", step.producer.getJMSType());
    }

    @Test
    public void testInit() {
        meta.setDisableMessageId("true");
        meta.setDisableMessageTimestamp("false");
        meta.setDeliveryMode("1");
        meta.setPriority("2");
        meta.setTimeToLive("3");
        meta.setDeliveryDelay("4");
        meta.setJmsCorrelationId("ASDF");
        meta.setJmsType("JMSType");
        Assert.assertTrue(step.init(meta, data));
        meta.setDisableMessageTimestamp("asdf");
        meta.setDisableMessageId("asdf");
        meta.setDeliveryMode("asdf");
        meta.setPriority("asdf");
        meta.setPriority("asdf");
        meta.setTimeToLive("asdf");
        Assert.assertFalse(step.init(meta, data));
    }

    @Test
    public void jmsContextClosedOnStop() throws Exception {
        TransMeta transMeta = new TransMeta(getClass().getResource("/jms-generate-produce.ktr").getPath());
        Trans trans = new Trans(transMeta);
        trans.prepareExecution(new String[]{  });
        StepMetaDataCombi combi = trans.getSteps().get(1);
        JmsProducer step = ((JmsProducer) (combi.step));
        JmsProducerMeta jmsMeta = step.meta;
        jmsMeta.jmsDelegate.jmsProviders = Collections.singletonList(jmsProvider);
        Mockito.when(jmsProvider.supports(ACTIVEMQ)).thenReturn(true);
        Mockito.when(jmsProvider.getContext(jmsMeta.jmsDelegate)).thenReturn(jmsContext);
        Mockito.when(jmsContext.createProducer()).thenReturn(jmsProducer);
        Mockito.when(jmsProducer.send(jmsMeta.jmsDelegate.getDestination(), "ackbar")).then(( ignore) -> {
            trans.stopAll();
            return null;
        });
        trans.startThreads();
        trans.waitUntilFinished();
        Mockito.verify(jmsContext).close();
    }
}

