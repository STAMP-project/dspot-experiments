/**
 * Copyright 2017 LinkedIn Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package azkaban.flowtrigger;


import azkaban.executor.ExecutorLoader;
import azkaban.executor.ExecutorManager;
import azkaban.flowtrigger.database.FlowTriggerInstanceLoader;
import azkaban.utils.EmailMessage;
import azkaban.utils.EmailMessageCreator;
import azkaban.utils.Emailer;
import azkaban.utils.TestUtils;
import java.text.ParseException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class TriggerInstanceProcessorTest {
    private static final String EMAIL = "test@email.com";

    private FlowTriggerInstanceLoader triggerInstLoader;

    private ExecutorManager executorManager;

    private Emailer emailer;

    private EmailMessage message;

    private EmailMessageCreator messageCreator;

    private TriggerInstanceProcessor processor;

    private CountDownLatch sendEmailLatch;

    private CountDownLatch submitFlowLatch;

    private CountDownLatch updateExecIDLatch;

    private ExecutorLoader executorLoader;

    @Test
    public void testProcessSucceed() throws Exception {
        final TriggerInstance triggerInstance = TriggerInstanceProcessorTest.createTriggerInstance();
        this.processor.processSucceed(triggerInstance);
        this.submitFlowLatch.await(10L, TimeUnit.SECONDS);
        Mockito.verify(this.executorManager).submitExecutableFlow(ArgumentMatchers.any(), ArgumentMatchers.anyString());
        this.updateExecIDLatch.await(10L, TimeUnit.SECONDS);
        Mockito.verify(this.triggerInstLoader).updateAssociatedFlowExecId(triggerInstance);
    }

    @Test
    public void testProcessTermination() throws Exception {
        final TriggerInstance triggerInstance = TriggerInstanceProcessorTest.createTriggerInstance();
        this.processor.processTermination(triggerInstance);
        this.sendEmailLatch.await(10L, TimeUnit.SECONDS);
        Assert.assertEquals(0, sendEmailLatch.getCount());
        Mockito.verify(this.message).setSubject("flow trigger for flow 'flowId', project 'proj' has been cancelled on azkaban");
        assertThat(TestUtils.readResource("/emailTemplate/flowtriggerfailureemail.html", this)).isEqualToIgnoringWhitespace(this.message.getBody());
    }

    @Test
    public void testNewInstance() throws ParseException {
        final TriggerInstance triggerInstance = TriggerInstanceProcessorTest.createTriggerInstance();
        this.processor.processNewInstance(triggerInstance);
        Mockito.verify(this.triggerInstLoader).uploadTriggerInstance(triggerInstance);
    }
}

