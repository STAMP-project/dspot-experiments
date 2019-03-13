/**
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.cdi.kproject;


import DeclarativeAgendaOption.ENABLED;
import EqualityBehaviorOption.EQUALITY;
import EventProcessingOption.STREAM;
import KieServices.Factory;
import KieSessionType.STATEFUL;
import LanguageLevelOption.DRL6_STRICT;
import LanguageLevelOption.PROPERTY_NAME;
import ListenerModel.Kind.AGENDA_EVENT_LISTENER;
import ListenerModel.Kind.PROCESS_EVENT_LISTENER;
import ListenerModel.Kind.RULE_RUNTIME_EVENT_LISTENER;
import PropertySpecificOption.ALWAYS;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.FileLoggerModel;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.builder.model.KieSessionModel;
import org.kie.api.builder.model.ListenerModel;
import org.kie.api.builder.model.WorkItemHandlerModel;
import org.kie.api.runtime.conf.BeliefSystemTypeOption;
import org.kie.api.runtime.conf.ClockTypeOption;


public class KieModuleModelTest {
    @Test
    public void testMarshallingUnmarshalling() {
        KieServices ks = Factory.get();
        KieModuleModel kproj = ks.newKieModuleModel().setConfigurationProperty(PROPERTY_NAME, DRL6_STRICT.toString()).setConfigurationProperty(PropertySpecificOption.PROPERTY_NAME, ALWAYS.toString());
        KieBaseModel kieBaseModel1 = kproj.newKieBaseModel("KBase1").setEqualsBehavior(EQUALITY).setEventProcessingMode(STREAM).setDeclarativeAgenda(ENABLED).addInclude("OtherKBase").addPackage("org.kie.pkg1").addPackage("org.kie.pkg2");
        KieSessionModel ksession1 = kieBaseModel1.newKieSessionModel("KSession1").setType(STATEFUL).setClockType(ClockTypeOption.get("realtime")).setBeliefSystem(BeliefSystemTypeOption.get("jtms")).setFileLogger("drools.log", 10, true).addCalendar("monday", "org.domain.Monday").setDefault(true);
        ksession1.newListenerModel("org.domain.FirstInterface", AGENDA_EVENT_LISTENER);
        ksession1.newListenerModel("org.domain.SecondInterface", RULE_RUNTIME_EVENT_LISTENER).newQualifierModel("MyQualfier2");
        ksession1.newListenerModel("org.domain.ThirdInterface", PROCESS_EVENT_LISTENER).newQualifierModel("MyQualfier3").setValue("v1");
        ksession1.newListenerModel("org.domain.FourthInterface", AGENDA_EVENT_LISTENER).newQualifierModel("MyQualfier4").addArgument("name1", "xxxx").addArgument("name2", "yyyy");
        ksession1.newWorkItemHandlerModel("name", "org.domain.FifthInterface").newQualifierModel("MyQualfier5").addArgument("name1", "aaa").addArgument("name2", "bbb");
        // KieBaseModel kieBaseModel2 = kproj.newKieBaseModel("KBase2")
        // .setEqualsBehavior( EqualityBehaviorOption.EQUALITY )
        // .setEventProcessingMode( EventProcessingOption.STREAM );
        String xml = kproj.toXML();
        // System.out.println( xml );
        KieModuleModel kprojXml = fromXML(xml);
        Assert.assertEquals(DRL6_STRICT.toString(), kprojXml.getConfigurationProperty(PROPERTY_NAME));
        Assert.assertEquals(ALWAYS.toString(), kprojXml.getConfigurationProperty(PropertySpecificOption.PROPERTY_NAME));
        KieBaseModel kieBaseModelXML = kprojXml.getKieBaseModels().get("KBase1");
        Assert.assertSame(kprojXml, getKModule());
        Assert.assertEquals(EQUALITY, kieBaseModelXML.getEqualsBehavior());
        Assert.assertEquals(STREAM, kieBaseModelXML.getEventProcessingMode());
        Assert.assertEquals(ENABLED, kieBaseModelXML.getDeclarativeAgenda());
        Assert.assertFalse(kieBaseModelXML.isDefault());
        Assert.assertEquals("org.kie.pkg1", kieBaseModelXML.getPackages().get(0));
        Assert.assertEquals("org.kie.pkg2", kieBaseModelXML.getPackages().get(1));
        Assert.assertEquals("OtherKBase", getIncludes().iterator().next());
        KieSessionModel kieSessionModelXML = kieBaseModelXML.getKieSessionModels().get("KSession1");
        Assert.assertSame(kieBaseModelXML, getKieBaseModel());
        Assert.assertEquals(STATEFUL, kieSessionModelXML.getType());
        Assert.assertEquals(ClockTypeOption.get("realtime"), kieSessionModelXML.getClockType());
        Assert.assertEquals(BeliefSystemTypeOption.get("jtms"), kieSessionModelXML.getBeliefSystem());
        Assert.assertEquals("org.domain.Monday", kieSessionModelXML.getCalendars().get("monday"));
        FileLoggerModel fileLogger = kieSessionModelXML.getFileLogger();
        Assert.assertEquals("drools.log", fileLogger.getFile());
        Assert.assertEquals(10, fileLogger.getInterval());
        Assert.assertEquals(true, fileLogger.isThreaded());
        Assert.assertTrue(kieSessionModelXML.isDefault());
        List<ListenerModel> listeners = kieSessionModelXML.getListenerModels();
        ListenerModel listener2 = listeners.get(0);
        Assert.assertEquals("org.domain.SecondInterface", listener2.getType());
        Assert.assertEquals(RULE_RUNTIME_EVENT_LISTENER, listener2.getKind());
        // QualifierModel qualifier2 = listener2.getQualifierModel();
        // assertEquals("MyQualfier2", qualifier2.getType());
        ListenerModel listener1 = listeners.get(1);
        Assert.assertEquals("org.domain.FirstInterface", listener1.getType());
        Assert.assertEquals(AGENDA_EVENT_LISTENER, listener1.getKind());
        // assertNull(listener1.getQualifierModel());
        ListenerModel listener4 = listeners.get(2);
        Assert.assertEquals("org.domain.FourthInterface", listener4.getType());
        Assert.assertEquals(AGENDA_EVENT_LISTENER, listener4.getKind());
        // QualifierModel qualifier4 = listener4.getQualifierModel();
        // assertEquals("MyQualfier4", qualifier4.getType());
        // assertEquals("xxxx", qualifier4.getArguments().get("name1"));
        // assertEquals("yyyy", qualifier4.getArguments().get("name2"));
        ListenerModel listener3 = listeners.get(3);
        Assert.assertEquals("org.domain.ThirdInterface", listener3.getType());
        Assert.assertEquals(PROCESS_EVENT_LISTENER, listener3.getKind());
        // QualifierModel qualifier3 = listener3.getQualifierModel();
        // assertEquals("MyQualfier3", qualifier3.getType());
        // assertEquals("v1", qualifier3.getValue());
        WorkItemHandlerModel wihm = kieSessionModelXML.getWorkItemHandlerModels().get(0);
        Assert.assertEquals("org.domain.FifthInterface", wihm.getType());
        // QualifierModel qualifier5 = wihm.getQualifierModel();
        // assertEquals("MyQualfier5", qualifier5.getType());
        // assertEquals("aaa", qualifier5.getArguments().get("name1"));
        // assertEquals("bbb", qualifier5.getArguments().get("name2"));
    }
}

