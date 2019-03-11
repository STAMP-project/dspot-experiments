/**
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
package org.flowable.engine.test.api.nonpublic;


import java.util.List;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.EventSubscription;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Daniel Meyer
 */
public class EventSubscriptionQueryTest extends PluggableFlowableTestCase {
    @Test
    public void testQueryByEventName() {
        processEngineConfiguration.getCommandExecutor().execute(new org.flowable.common.engine.impl.interceptor.Command<Void>() {
            @Override
            public Void execute(org.flowable.common.engine.impl.interceptor.CommandContext commandContext) {
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity1 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity1.setEventName("messageName");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity1);
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity2 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity2.setEventName("messageName");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity2);
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity3 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity3.setEventName("messageName2");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity3);
                return null;
            }
        });
        List<EventSubscription> list = newEventSubscriptionQuery().eventName("messageName").list();
        assertEquals(2, list.size());
        list = newEventSubscriptionQuery().eventName("messageName2").list();
        assertEquals(1, list.size());
        cleanDb();
    }

    @Test
    public void testQueryByEventType() {
        processEngineConfiguration.getCommandExecutor().execute(new org.flowable.common.engine.impl.interceptor.Command<Void>() {
            @Override
            public Void execute(org.flowable.common.engine.impl.interceptor.CommandContext commandContext) {
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity1 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity1.setEventName("messageName");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity1);
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity2 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity2.setEventName("messageName");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity2);
                org.flowable.engine.impl.persistence.entity.SignalEventSubscriptionEntity signalEventSubscriptionEntity3 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createSignalEventSubscription();
                signalEventSubscriptionEntity3.setEventName("messageName2");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(signalEventSubscriptionEntity3);
                return null;
            }
        });
        List<EventSubscription> list = newEventSubscriptionQuery().eventType("signal").list();
        assertEquals(1, list.size());
        list = newEventSubscriptionQuery().eventType("message").list();
        assertEquals(2, list.size());
        cleanDb();
    }

    @Test
    public void testQueryByActivityId() {
        processEngineConfiguration.getCommandExecutor().execute(new org.flowable.common.engine.impl.interceptor.Command<Void>() {
            @Override
            public Void execute(org.flowable.common.engine.impl.interceptor.CommandContext commandContext) {
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity1 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity1.setEventName("messageName");
                messageEventSubscriptionEntity1.setActivityId("someActivity");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity1);
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity2 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity2.setEventName("messageName");
                messageEventSubscriptionEntity2.setActivityId("someActivity");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity2);
                org.flowable.engine.impl.persistence.entity.SignalEventSubscriptionEntity signalEventSubscriptionEntity3 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createSignalEventSubscription();
                signalEventSubscriptionEntity3.setEventName("messageName2");
                signalEventSubscriptionEntity3.setActivityId("someOtherActivity");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(signalEventSubscriptionEntity3);
                return null;
            }
        });
        List<EventSubscription> list = newEventSubscriptionQuery().activityId("someOtherActivity").list();
        assertEquals(1, list.size());
        list = newEventSubscriptionQuery().activityId("someActivity").eventType("message").list();
        assertEquals(2, list.size());
        cleanDb();
    }

    @Test
    public void testQueryByEventSubscriptionId() {
        processEngineConfiguration.getCommandExecutor().execute(new org.flowable.common.engine.impl.interceptor.Command<Void>() {
            @Override
            public Void execute(org.flowable.common.engine.impl.interceptor.CommandContext commandContext) {
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity1 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity1.setEventName("messageName");
                messageEventSubscriptionEntity1.setActivityId("someActivity");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity1);
                org.flowable.engine.impl.persistence.entity.MessageEventSubscriptionEntity messageEventSubscriptionEntity2 = org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).createMessageEventSubscription();
                messageEventSubscriptionEntity2.setEventName("messageName");
                messageEventSubscriptionEntity2.setActivityId("someOtherActivity");
                org.flowable.engine.impl.util.CommandContextUtil.getEventSubscriptionEntityManager(commandContext).insert(messageEventSubscriptionEntity2);
                return null;
            }
        });
        List<EventSubscription> list = newEventSubscriptionQuery().activityId("someOtherActivity").list();
        assertEquals(1, list.size());
        final EventSubscription entity = list.get(0);
        list = newEventSubscriptionQuery().id(entity.getId()).list();
        assertEquals(1, list.size());
        cleanDb();
    }

    @Test
    @Deployment
    public void testQueryByExecutionId() {
        // starting two instances:
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("catchSignal");
        runtimeService.startProcessInstanceByKey("catchSignal");
        // test query by process instance id
        EventSubscription subscription = newEventSubscriptionQuery().processInstanceId(processInstance.getId()).singleResult();
        assertNotNull(subscription);
        Execution executionWaitingForSignal = runtimeService.createExecutionQuery().activityId("signalEvent").processInstanceId(processInstance.getId()).singleResult();
        // test query by execution id
        EventSubscription signalSubscription = newEventSubscriptionQuery().executionId(executionWaitingForSignal.getId()).singleResult();
        assertNotNull(signalSubscription);
        assertEquals(signalSubscription, subscription);
        cleanDb();
    }
}

