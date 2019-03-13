/**
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
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
package org.drools.compiler.integrationtests;


import EventProcessingOption.CLOUD;
import KieServices.Factory;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieSession;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.io.ResourceFactory;


public class QueryInRHSCepTest {
    private KieSession ksession;

    private SessionPseudoClock clock;

    private List<?> myGlobal;

    // empty pojo.
    public static class QueryItemPojo {}

    // empty pojo.
    public static class SolicitFirePojo {}

    @Test
    public void withResultOfSize1Test() {
        prepare1();
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new QueryInRHSCepTest.QueryItemPojo());
        ksession.insert(new QueryInRHSCepTest.SolicitFirePojo());
        int fired = ksession.fireAllRules();
        Assert.assertEquals(1, fired);
        Assert.assertEquals(1, myGlobal.size());
        Assert.assertEquals(1, size());
    }

    @Test
    public void withResultOfSize1AnotherTest() {
        prepare1();
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new QueryInRHSCepTest.SolicitFirePojo());
        ksession.insert(new QueryInRHSCepTest.QueryItemPojo());
        int fired = ksession.fireAllRules();
        Assert.assertEquals(1, fired);
        Assert.assertEquals(1, myGlobal.size());
        Assert.assertEquals(1, size());
    }

    @Test
    public void withResultOfSize0Test() {
        prepare1();
        clock.advanceTime(1, TimeUnit.SECONDS);
        ksession.insert(new QueryInRHSCepTest.SolicitFirePojo());
        int fired = ksession.fireAllRules();
        Assert.assertEquals(1, fired);
        Assert.assertEquals(1, myGlobal.size());
        Assert.assertEquals(0, size());
    }

    @Test
    public void withInsertBeforeQueryCloudTest() {
        String drl = ((((((((((((((((("package org.drools.compiler.integrationtests\n" + "import ") + (QueryInRHSCepTest.SolicitFirePojo.class.getCanonicalName())) + "\n") + "import ") + (QueryInRHSCepTest.QueryItemPojo.class.getCanonicalName())) + "\n") + "global java.util.List myGlobal \n") + "query \"myQuery\"\n") + "    $r : QueryItemPojo()\n") + "end\n") + "rule \"drools-usage/WLHxG8S\"\n") + " no-loop\n") + " when\n") + " SolicitFirePojo()\n") + " then\n") + " insert(new QueryItemPojo());\n") + " myGlobal.add(drools.getKieRuntime().getQueryResults(\"myQuery\"));\n") + " end\n";
        System.out.println(drl);
        final KieServices ks = Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kmodule = ks.newKieModuleModel();
        KieBaseModel baseModel = kmodule.newKieBaseModel("defaultKBase").setDefault(true).setEventProcessingMode(CLOUD);
        baseModel.newKieSessionModel("defaultKSession").setDefault(true);
        kfs.writeKModuleXML(kmodule.toXML());
        kfs.write(ResourceFactory.newByteArrayResource(drl.getBytes()).setTargetPath((("org/drools/compiler/integrationtests/" + (this.getClass().getName())) + ".drl")));
        Assert.assertTrue(ks.newKieBuilder(kfs).buildAll().getResults().getMessages().isEmpty());
        ksession = ks.newKieContainer(ks.getRepository().getDefaultReleaseId()).newKieSession();
        myGlobal = new ArrayList<>();
        ksession.setGlobal("myGlobal", myGlobal);
        ksession.insert(new QueryInRHSCepTest.QueryItemPojo());
        ksession.insert(new QueryInRHSCepTest.SolicitFirePojo());
        int fired = ksession.fireAllRules();
        Assert.assertEquals(1, fired);
        Assert.assertEquals(1, myGlobal.size());
        Assert.assertEquals(2, size());// notice 1 is manually inserted, 1 get inserted from rule's RHS, for a total of 2.

    }
}

