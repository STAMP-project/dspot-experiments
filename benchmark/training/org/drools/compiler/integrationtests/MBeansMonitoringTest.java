/**
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;


import ClockType.PSEUDO_CLOCK;
import EventProcessingOption.STREAM;
import ForceEagerActivationOption.YES;
import KieContainerMonitorMXBean.CLASSPATH_KIECONTAINER_RELEASEID;
import KieServices.Factory;
import KieSessionModel.KieSessionType.STATELESS;
import KieSessionType.STATEFUL;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.net.URLClassLoader;
import javax.management.JMX;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.management.DroolsManagementAgent;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.management.KieContainerMonitorMXBean;
import org.kie.api.management.KieSessionMonitoringMXBean;
import org.kie.api.management.StatelessKieSessionMonitoringMXBean;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MBeansMonitoringTest extends CommonTestMethodBase {
    public static final Logger LOG = LoggerFactory.getLogger(MBeansMonitoringTest.class);

    public static final String KSESSION1 = "KSession1";

    public static final String KBASE1 = "KBase1";

    private static final String KBASE2 = "KBase2";

    private static final String KSESSION2 = "KSession2";

    private String mbeansprop;

    @Test
    public void testKieClasspathMBeans() throws Exception {
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        KieServices ks = Factory.get();
        KieContainer kc = ks.getKieClasspathContainer("myID");
        KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID"), KieContainerMonitorMXBean.class);
        KieBase kb = kc.getKieBase("org.kie.monitoring.kbase1");
        KieSession statefulKieSession = kc.newKieSession("org.kie.monitoring.kbase1.ksession1");
        StatelessKieSession statelessKieSession = kc.newStatelessKieSession("org.kie.monitoring.kbase1.ksession2");
        KieSessionMonitoringMXBean statefulKieSessionMonitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID", "org.kie.monitoring.kbase1", STATEFUL, "org.kie.monitoring.kbase1.ksession1"), KieSessionMonitoringMXBean.class);
        StatelessKieSessionMonitoringMXBean statelessKieSessionMonitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID", "org.kie.monitoring.kbase1", STATEFUL, "org.kie.monitoring.kbase1.ksession1"), StatelessKieSessionMonitoringMXBean.class);
        Assert.assertEquals("myID", c1Monitor.getContainerId());
        Assert.assertTrue(c1Monitor.getConfiguredReleaseId().sameGAVof(CLASSPATH_KIECONTAINER_RELEASEID));
        Assert.assertTrue(c1Monitor.getResolvedReleaseId().sameGAVof(CLASSPATH_KIECONTAINER_RELEASEID));
        Assert.assertEquals("org.kie.monitoring.kbase1.ksession1", statefulKieSessionMonitor.getKieSessionName());
        Assert.assertEquals("org.kie.monitoring.kbase1", statefulKieSessionMonitor.getKieBaseId());
        Assert.assertEquals("org.kie.monitoring.kbase1.ksession1", statelessKieSessionMonitor.getKieSessionName());
        Assert.assertEquals("org.kie.monitoring.kbase1", statelessKieSessionMonitor.getKieBaseId());
        KieContainer kc2 = ks.newKieClasspathContainer("myID2");
        KieContainerMonitorMXBean c2Monitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID2"), KieContainerMonitorMXBean.class);
        KieBase kb2 = kc2.getKieBase("org.kie.monitoring.kbase1");
        KieSession statefulKieSession2 = kc2.newKieSession("org.kie.monitoring.kbase1.ksession1");
        StatelessKieSession statelessKieSession2 = kc2.newStatelessKieSession("org.kie.monitoring.kbase1.ksession2");
        KieSessionMonitoringMXBean statefulKieSessionMonitor2 = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID2", "org.kie.monitoring.kbase1", STATEFUL, "org.kie.monitoring.kbase1.ksession1"), KieSessionMonitoringMXBean.class);
        StatelessKieSessionMonitoringMXBean statelessKieSessionMonitor2 = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID2", "org.kie.monitoring.kbase1", STATEFUL, "org.kie.monitoring.kbase1.ksession1"), StatelessKieSessionMonitoringMXBean.class);
        Assert.assertEquals("myID2", c2Monitor.getContainerId());
        Assert.assertTrue(c2Monitor.getConfiguredReleaseId().sameGAVof(CLASSPATH_KIECONTAINER_RELEASEID));
        Assert.assertTrue(c2Monitor.getResolvedReleaseId().sameGAVof(CLASSPATH_KIECONTAINER_RELEASEID));
        Assert.assertEquals("org.kie.monitoring.kbase1.ksession1", statefulKieSessionMonitor2.getKieSessionName());
        Assert.assertEquals("org.kie.monitoring.kbase1", statefulKieSessionMonitor2.getKieBaseId());
        Assert.assertEquals("org.kie.monitoring.kbase1.ksession1", statelessKieSessionMonitor2.getKieSessionName());
        Assert.assertEquals("org.kie.monitoring.kbase1", statelessKieSessionMonitor2.getKieBaseId());
        kc.dispose();
        kc2.dispose();
    }

    @Test
    public void testEventOffset() throws Exception {
        String drl = "package org.drools.compiler.test\n" + ((((((((("import org.drools.compiler.StockTick\n" + "declare StockTick\n") + "    @role(event)\n") + "    @expires(10s)\n") + "end\n") + "rule X\n") + "when\n") + "    StockTick()\n") + "then\n") + "end");
        KieServices ks = Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel(MBeansMonitoringTest.KBASE1).setDefault(true).setEventProcessingMode(STREAM);
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel(MBeansMonitoringTest.KSESSION1).setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get(PSEUDO_CLOCK.getId()));
        ReleaseId releaseId1 = ks.newReleaseId("org.kie.test", "mbeans", "1.0.0");
        CommonTestMethodBase.createKJar(ks, kproj, releaseId1, null, drl);
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieBase kiebase = kc.getKieBase(MBeansMonitoringTest.KBASE1);
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        ObjectName kbOn = DroolsManagementAgent.createObjectNameFor(((InternalKnowledgeBase) (kiebase)));
        mbserver.invoke(kbOn, "startInternalMBeans", new Object[0], new String[0]);
        Object expOffset = mbserver.getAttribute(new ObjectName((kbOn + ",group=EntryPoints,EntryPoint=DEFAULT,ObjectType=org.drools.compiler.StockTick")), "ExpirationOffset");
        Assert.assertEquals(10001, ((Number) (expOffset)).longValue());
    }

    @Test
    public void testContainerMBeans() throws Exception {
        String drl = "package org.drools.compiler.test\n" + ((((((((("import org.drools.compiler.StockTick\n" + "declare StockTick\n") + "    @role(event)\n") + "    @expires(10s)\n") + "end\n") + "rule X\n") + "when\n") + "    StockTick()\n") + "then\n") + "end");
        KieServices ks = Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel(MBeansMonitoringTest.KBASE1).setDefault(true).setEventProcessingMode(STREAM);
        KieSessionModel ksessionModel1 = kieBaseModel1.newKieSessionModel(MBeansMonitoringTest.KSESSION1).setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get(PSEUDO_CLOCK.getId()));
        ReleaseId releaseId1 = ks.newReleaseId("org.kie.test", "mbeans", "1.0.0");
        CommonTestMethodBase.createKJar(ks, kproj, releaseId1, null, drl);
        KieContainer kc = ks.newKieContainer(releaseId1);
        KieBase kiebase = kc.getKieBase(MBeansMonitoringTest.KBASE1);
        kc.newKieSession(MBeansMonitoringTest.KSESSION1);
        kiebase.newKieSession();
        String kc1ID = getContainerId();
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        MBeansMonitoringTest.LOG.debug("{}", mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote(kc1ID))) + ",*")), null));
        ReleaseId verRelease = ks.newReleaseId("org.kie.test", "mbeans", "RELEASE");
        KieContainer kc2 = ks.newKieContainer("Matteo", verRelease);
        kc2.newKieSession(MBeansMonitoringTest.KSESSION1);
        KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy(kc1ID), KieContainerMonitorMXBean.class);
        Assert.assertEquals(releaseId1.toExternalForm(), c1Monitor.getConfiguredReleaseIdStr());
        Assert.assertEquals(releaseId1.toExternalForm(), c1Monitor.getResolvedReleaseIdStr());
        Assert.assertTrue(c1Monitor.getConfiguredReleaseId().sameGAVof(releaseId1));
        Assert.assertTrue(c1Monitor.getResolvedReleaseId().sameGAVof(releaseId1));
        Assert.assertEquals(releaseId1.getVersion(), c1Monitor.getConfiguredReleaseId().getVersion());
        Assert.assertEquals(releaseId1.getVersion(), c1Monitor.getResolvedReleaseId().getVersion());
        KieContainerMonitorMXBean c2Monitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("Matteo"), KieContainerMonitorMXBean.class);
        Assert.assertEquals(verRelease.toExternalForm(), c2Monitor.getConfiguredReleaseIdStr());
        Assert.assertEquals(releaseId1.toExternalForm(), c2Monitor.getResolvedReleaseIdStr());
        Assert.assertTrue(c2Monitor.getConfiguredReleaseId().sameGAVof(verRelease));
        Assert.assertTrue(c2Monitor.getResolvedReleaseId().sameGAVof(releaseId1));
        Assert.assertEquals(verRelease.getVersion(), c2Monitor.getConfiguredReleaseId().getVersion());
        Assert.assertEquals(releaseId1.getVersion(), c2Monitor.getResolvedReleaseId().getVersion());
        // MBean are supported only via KieContainer public API.
        Assert.assertEquals(3, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote(kc1ID))) + ",*")), null).size());
        kc.dispose();
        Assert.assertEquals(0, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote(kc1ID))) + ",*")), null).size());
        Assert.assertEquals(3, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote("Matteo"))) + ",*")), null).size());
    }

    @Test
    public void testAggregatedAndDispose() throws Exception {
        String drl = "package org.drools.compiler.integrationtests\n" + (((((((((((((((("rule ND\n" + "when\n") + "    String() \n") + "    not ( Double() ) \n") + "then\n") + "    // do nothing. \n") + "end\n") + "rule ND2\n") + "salience 1\n") + "when\n") + "    $s : String() \n") + "    not ( Double() ) \n") + "then\n") + "    try { Thread.sleep(10); } catch (Exception e) { }\n") + "    insert( new Double(0) );\n") + "    retract( $s );\n") + "end\n");
        KieServices ks = Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel();
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel(MBeansMonitoringTest.KBASE1).setDefault(true).setEventProcessingMode(STREAM);
        KieSessionModel ksessionModel1 = kieBaseModel1.newKieSessionModel(MBeansMonitoringTest.KSESSION1).setDefault(true).setType(KieSessionModel.KieSessionType.STATEFUL).setClockType(ClockTypeOption.get(PSEUDO_CLOCK.getId()));
        KieBaseModel kieBaseModel2 = kproj.newKieBaseModel(MBeansMonitoringTest.KBASE2).setDefault(false);
        KieSessionModel ksessionModel2 = kieBaseModel2.newKieSessionModel(MBeansMonitoringTest.KSESSION2).setDefault(true).setType(STATELESS);
        ReleaseId releaseId1 = ks.newReleaseId("org.kie.test", "mbeans", "1.0.0");
        CommonTestMethodBase.createKJar(ks, kproj, releaseId1, null, drl);
        String containerId = "myContainerId";
        KieContainer kc = ks.newKieContainer(containerId, releaseId1);
        KieBase kiebase = kc.getKieBase(MBeansMonitoringTest.KBASE1);
        KieSessionConfiguration sessionConf = ks.newKieSessionConfiguration();
        sessionConf.setOption(YES);
        KieSession ksession = kc.newKieSession(MBeansMonitoringTest.KSESSION1, sessionConf);
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        KieSessionMonitoringMXBean aggrMonitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy(containerId, MBeansMonitoringTest.KBASE1, STATEFUL, MBeansMonitoringTest.KSESSION1), KieSessionMonitoringMXBean.class);
        long tft = 0;
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 0, 0, 0);
        checkTotalFactCount(aggrMonitor, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft);
        checkTotalSessions(aggrMonitor, 1);
        Assert.assertNull(aggrMonitor.getStatsForRule("ND"));
        Assert.assertNull(aggrMonitor.getStatsForRule("ND2"));
        Assert.assertFalse(aggrMonitor.getStatsByRule().containsKey("ND"));
        Assert.assertFalse(aggrMonitor.getStatsByRule().containsKey("ND2"));
        ksession.insert("Ciao");
        ksession.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 2, 1, 1);
        checkTotalFactCount(aggrMonitor, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, (tft + 1));
        checkTotalSessions(aggrMonitor, 1);
        checkStatsForRule(aggrMonitor, "ND", 1, 1, 0);
        checkStatsForRule(aggrMonitor, "ND2", 1, 0, 1);
        checkStatsByRule(aggrMonitor, "ND", 1, 1, 0);
        checkStatsByRule(aggrMonitor, "ND2", 1, 0, 1);
        ksession.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 2, 1, 1);
        checkTotalFactCount(aggrMonitor, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, tft);
        checkTotalSessions(aggrMonitor, 1);
        checkStatsForRule(aggrMonitor, "ND", 1, 1, 0);
        checkStatsForRule(aggrMonitor, "ND2", 1, 0, 1);
        checkStatsByRule(aggrMonitor, "ND", 1, 1, 0);
        checkStatsByRule(aggrMonitor, "ND2", 1, 0, 1);
        MBeansMonitoringTest.LOG.debug("---");
        KieSession ksession2 = kc.newKieSession(MBeansMonitoringTest.KSESSION1, sessionConf);
        ksession2.insert("Ciao");
        ksession2.fireAllRules();
        print(aggrMonitor);
        checkAgendaTotals(aggrMonitor, 4, 2, 2);
        checkTotalFactCount(aggrMonitor, 2);
        tft = checkTotalFiringTimeGEQ(aggrMonitor, (tft + 1));
        checkTotalSessions(aggrMonitor, 2);
        checkStatsForRule(aggrMonitor, "ND", 2, 2, 0);
        checkStatsForRule(aggrMonitor, "ND2", 2, 0, 2);
        checkStatsByRule(aggrMonitor, "ND", 2, 2, 0);
        checkStatsByRule(aggrMonitor, "ND2", 2, 0, 2);
        ksession.dispose();
        checkTotalSessions(aggrMonitor, 1);
        checkTotalFactCount(aggrMonitor, 1);
        ksession2.dispose();
        checkTotalSessions(aggrMonitor, 0);
        checkTotalFactCount(aggrMonitor, 0);
        MBeansMonitoringTest.LOG.debug("--- NOW for the STATELESS ---");
        tft = 0;
        StatelessKieSession stateless = kc.newStatelessKieSession(MBeansMonitoringTest.KSESSION2, sessionConf);
        StatelessKieSessionMonitoringMXBean aggrMonitor2 = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy(containerId, MBeansMonitoringTest.KBASE2, KieSessionType.STATELESS, MBeansMonitoringTest.KSESSION2), StatelessKieSessionMonitoringMXBean.class);
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 0, 0, 0);
        checkRuleRuntimeTotals(aggrMonitor2, 0, 0);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, tft);
        checkTotalSessions(aggrMonitor2, 0);
        Assert.assertNull(aggrMonitor2.getStatsForRule("ND"));
        Assert.assertNull(aggrMonitor2.getStatsForRule("ND2"));
        Assert.assertFalse(aggrMonitor2.getStatsByRule().containsKey("ND"));
        Assert.assertFalse(aggrMonitor2.getStatsByRule().containsKey("ND2"));
        stateless.execute("Ciao");
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 2, 1, 1);
        checkRuleRuntimeTotals(aggrMonitor2, 2, 1);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, (tft + 1));
        checkTotalSessions(aggrMonitor2, 1);
        checkStatsForRule(aggrMonitor2, "ND", 1, 1, 0);
        checkStatsForRule(aggrMonitor2, "ND2", 1, 0, 1);
        checkStatsByRule(aggrMonitor2, "ND", 1, 1, 0);
        checkStatsByRule(aggrMonitor2, "ND2", 1, 0, 1);
        stateless.execute("Ciao");
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 4, 2, 2);
        checkRuleRuntimeTotals(aggrMonitor2, 4, 2);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, (tft + 1));
        checkTotalSessions(aggrMonitor2, 2);
        checkStatsForRule(aggrMonitor2, "ND", 2, 2, 0);
        checkStatsForRule(aggrMonitor2, "ND2", 2, 0, 2);
        checkStatsByRule(aggrMonitor2, "ND", 2, 2, 0);
        checkStatsByRule(aggrMonitor2, "ND2", 2, 0, 2);
        StatelessKieSession stateless2 = kc.newStatelessKieSession(MBeansMonitoringTest.KSESSION2, sessionConf);
        MBeansMonitoringTest.LOG.debug(((stateless + " ") + stateless2));
        checkTotalSessions(aggrMonitor2, 2);
        stateless2.execute("Ciao");
        print(aggrMonitor2);
        checkAgendaTotals(aggrMonitor2, 6, 3, 3);
        checkRuleRuntimeTotals(aggrMonitor2, 6, 3);
        tft = checkTotalFiringTimeGEQ(aggrMonitor2, (tft + 1));
        checkTotalSessions(aggrMonitor2, 3);
        checkStatsForRule(aggrMonitor2, "ND", 3, 3, 0);
        checkStatsForRule(aggrMonitor2, "ND2", 3, 0, 3);
        checkStatsByRule(aggrMonitor2, "ND", 3, 3, 0);
        checkStatsByRule(aggrMonitor2, "ND2", 3, 0, 3);
        KieContainer kc2 = ks.newKieContainer("kc2", releaseId1);
        Assert.assertEquals(5, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote(containerId))) + ",*")), null).size());
        kc.dispose();
        Assert.assertEquals(0, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote(containerId))) + ",*")), null).size());
        Assert.assertEquals(1, mbserver.queryNames(new ObjectName((("org.kie:kcontainerId=" + (ObjectName.quote("kc2"))) + ",*")), null).size());
    }

    /**
     * Copied from KieRepositoryTest to test JMX monitoring
     */
    @Test
    public void testLoadKjarFromClasspath() {
        // DROOLS-1335
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ this.getClass().getResource("/kie-project-simple-1.0.0.jar") });
        Thread.currentThread().setContextClassLoader(urlClassLoader);
        MBeanServer mbserver = ManagementFactory.getPlatformMBeanServer();
        try {
            KieServices ks = Factory.get();
            KieRepository kieRepository = ks.getRepository();
            ReleaseId releaseId = ks.newReleaseId("org.test", "kie-project-simple", "1.0.0");
            KieModule kieModule = kieRepository.getKieModule(releaseId);
            Assert.assertNotNull(kieModule);
            Assert.assertEquals(releaseId, kieModule.getReleaseId());
            ks.newKieContainer("myID", releaseId);
            KieContainerMonitorMXBean c1Monitor = JMX.newMXBeanProxy(mbserver, DroolsManagementAgent.createObjectNameBy("myID"), KieContainerMonitorMXBean.class);
            Assert.assertTrue(c1Monitor.getConfiguredReleaseId().sameGAVof(releaseId));
            Assert.assertTrue(c1Monitor.getResolvedReleaseId().sameGAVof(releaseId));
        } finally {
            Thread.currentThread().setContextClassLoader(cl);
        }
    }
}

