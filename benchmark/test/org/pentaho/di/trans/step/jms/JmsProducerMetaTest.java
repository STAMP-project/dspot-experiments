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


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.variables.Variables;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.step.StepOption;


public class JmsProducerMetaTest {
    @Test
    public void testRetriveOptions() {
        List<StepOption> compareStepOptions = Arrays.asList(new StepOption(JmsProducerMeta.DISABLE_MESSAGE_ID, getString(JmsProducerMeta.class, "JmsDialog.Options.DISABLE_MESSAGE_ID"), "false"), new StepOption(JmsProducerMeta.DISABLE_MESSAGE_TIMESTAMP, getString(JmsProducerMeta.class, "JmsDialog.Options.DISABLE_MESSAGE_TIMESTAMP"), "true"), new StepOption(JmsProducerMeta.DELIVERY_MODE, getString(JmsProducerMeta.class, "JmsDialog.Options.DELIVERY_MODE"), "2"), new StepOption(JmsProducerMeta.PRIORITY, getString(JmsProducerMeta.class, "JmsDialog.Options.PRIORITY"), "3"), new StepOption(JmsProducerMeta.TIME_TO_LIVE, getString(JmsProducerMeta.class, "JmsDialog.Options.TIME_TO_LIVE"), "100"), new StepOption(JmsProducerMeta.DELIVERY_DELAY, getString(JmsProducerMeta.class, "JmsDialog.Options.DELIVERY_DELAY"), "20"), new StepOption(JmsProducerMeta.JMS_CORRELATION_ID, getString(JmsProducerMeta.class, "JmsDialog.Options.JMS_CORRELATION_ID"), "asdf"), new StepOption(JmsProducerMeta.JMS_TYPE, getString(JmsProducerMeta.class, "JmsDialog.Options.JMS_TYPE"), "myType"));
        JmsProducerMeta jmsProducerMeta = new JmsProducerMeta();
        jmsProducerMeta.setDisableMessageId("false");
        jmsProducerMeta.setDisableMessageTimestamp("true");
        jmsProducerMeta.setDeliveryMode("2");
        jmsProducerMeta.setPriority("3");
        jmsProducerMeta.setTimeToLive("100");
        jmsProducerMeta.setDeliveryDelay("20");
        jmsProducerMeta.setJmsCorrelationId("asdf");
        jmsProducerMeta.setJmsType("myType");
        List<StepOption> stepOptions = jmsProducerMeta.retriveOptions();
        Assert.assertNotNull(stepOptions);
        Assert.assertEquals(8, stepOptions.size());
        assertOptions(compareStepOptions, stepOptions);
    }

    @Test
    public void testCheck() {
        List<CheckResultInterface> remarks = new ArrayList<>();
        JmsProducerMeta jmsProducerMeta = new JmsProducerMeta();
        jmsProducerMeta.setDisableMessageId("asdf");
        jmsProducerMeta.setDisableMessageTimestamp("asdf");
        jmsProducerMeta.setDeliveryMode("asdf");
        jmsProducerMeta.setPriority("asdf");
        jmsProducerMeta.setTimeToLive("asdf");
        jmsProducerMeta.setDeliveryDelay("asdf");
        jmsProducerMeta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(6, remarks.size());
        Assert.assertTrue(remarks.get(0).getText().contains(BaseMessages.getString(JmsProducerMeta.class, "JmsDialog.Options.DISABLE_MESSAGE_ID")));
        Assert.assertTrue(remarks.get(1).getText().contains(BaseMessages.getString(JmsProducerMeta.class, "JmsDialog.Options.DISABLE_MESSAGE_TIMESTAMP")));
        Assert.assertTrue(remarks.get(2).getText().contains(BaseMessages.getString(JmsProducerMeta.class, "JmsDialog.Options.DELIVERY_MODE")));
        Assert.assertTrue(remarks.get(3).getText().contains(BaseMessages.getString(JmsProducerMeta.class, "JmsDialog.Options.PRIORITY")));
        Assert.assertTrue(remarks.get(4).getText().contains(BaseMessages.getString(JmsProducerMeta.class, "JmsDialog.Options.TIME_TO_LIVE")));
        Assert.assertTrue(remarks.get(5).getText().contains(BaseMessages.getString(JmsProducerMeta.class, "JmsDialog.Options.DELIVERY_DELAY")));
        remarks = new ArrayList();
        jmsProducerMeta.setDisableMessageId("true");
        jmsProducerMeta.setDisableMessageTimestamp("false");
        jmsProducerMeta.setDeliveryMode("1");
        jmsProducerMeta.setPriority("2");
        jmsProducerMeta.setTimeToLive("3");
        jmsProducerMeta.setDeliveryDelay("4");
        jmsProducerMeta.check(remarks, null, null, null, null, null, null, new Variables(), null, null);
        Assert.assertEquals(0, remarks.size());
    }
}

