/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.persistence.monitoring;


import KieServices.Factory;
import KieSessionType.STATEFUL;
import java.lang.management.ManagementFactory;
import java.util.Map;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.persistence.util.DroolsPersistenceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.management.KieContainerMonitorMXBean;
import org.kie.api.management.KieSessionMonitoringMXBean;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieContainer;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MonitoringWithJPAKnowledgeServiceTest {
    private static Logger LOG = LoggerFactory.getLogger(MonitoringWithJPAKnowledgeServiceTest.class);

    private Map<String, Object> context;

    private Environment env;

    public MonitoringWithJPAKnowledgeServiceTest() {
    }

    private String mbeansprop;

    @Test
    public void testBasic() throws MalformedObjectNameException {
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        KieServices ks = Factory.get();
        String containerId = "testcontainer-" + (System.currentTimeMillis());
        KieContainer kc = ks.newKieClasspathContainer(containerId);
        KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy(containerId), KieContainerMonitorMXBean.class);
        KieBase kb = kc.getKieBase("org.kie.monitoring.kbase1");
        // Use JPAKnowledgeService to create the KieSession
        StatefulKnowledgeSession statefulKieSession = JPAKnowledgeService.newStatefulKnowledgeSession(kb, null, env);
        long sessionIdentifier = statefulKieSession.getIdentifier();
        statefulKieSession.insert("String1");
        statefulKieSession.fireAllRules();
        KieSessionMonitoringMXBean statefulKieSessionMonitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy(containerId, "org.kie.monitoring.kbase1", STATEFUL, "persistent"), KieSessionMonitoringMXBean.class);
        Assert.assertEquals(1, statefulKieSessionMonitor.getTotalMatchesFired());
        // There should be 3 mbeans for KieContainer, KieBase and KieSession.
        Assert.assertEquals(3, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote(containerId))) + ",*")), null).size());
        // needs to be done separately:
        statefulKieSession.dispose();
        StatefulKnowledgeSession deserialized = JPAKnowledgeService.loadStatefulKnowledgeSession(sessionIdentifier, kb, null, DroolsPersistenceUtil.createEnvironment(context));
        deserialized.insert("String2");
        deserialized.fireAllRules();
        // the mbean does not persist state, but in this case consolidate by grouping the fire of the former session and the deserialized one
        Assert.assertEquals(2, statefulKieSessionMonitor.getTotalMatchesFired());
        kc.dispose();
    }
}

