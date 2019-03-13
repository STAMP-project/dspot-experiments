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


import KieServices.Factory;
import ResourceType.DRL;
import ResourceType.JAVA;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Message;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;


public class DynamicRuleLoadTest extends CommonTestMethodBase {
    private final String drl1 = "package org.drools.compiler\n" + (((("rule R1 when\n" + "   Message( $m : message )\n") + "then\n") + "    System.out.println($m);\n") + "end\n");

    private final String drl2_1 = ((((((("package org.drools.compiler\n" + "global ") + (DynamicRuleLoadTest.class.getCanonicalName())) + " test;\n") + "rule R2_1 when\n") + "   $m : Message( message == \"Hi Universe\" )\n") + "then\n") + "    test.updateToVersion();") + "end\n";

    private final String drl2_2 = ((((((("package org.drools.compiler\n" + "global ") + (DynamicRuleLoadTest.class.getCanonicalName())) + " test;\n") + "rule R2_2 when\n") + "   $m : Message( message == \"Hello World\" )\n") + "then\n") + "    test.done();") + "end\n";

    private final String javaSrc = "package org.drools.compiler.test;\n" + ((((((((((("public class PersonObject {\n" + "    private String id;\n") + "    public String getId() {\n") + "        return id;\n") + "    }\n") + "    public void setId(String id) {\n") + "        this.id = id;\n") + "    }\n") + "    public void updateId() {\n") + "        this.id = \"Person from version 1\";\n") + "    }\n") + "}");

    private final String javaSrc_2 = "package org.drools.compiler.test;\n" + ((((((((((("public class PersonObject {\n" + "    private String id;\n") + "    public String getId() {\n") + "        return id;\n") + "    }\n") + "    public void setId(String id) {\n") + "        this.id = id;\n") + "    }\n") + "    public void updateId() {\n") + "        this.id = \"Person from version 2\";\n") + "    }\n") + "}");

    private final String person_drl = "package org.drools.compiler.test\n" + (((((((("import org.drools.compiler.test.PersonObject;\n" + "\n") + "rule \"Update person\'s id\"\n") + "when\n") + "    $person : PersonObject()\n") + "then\n") + "    $person.updateId();\n") + "    delete($person);\n") + "end");

    private KieContainer kieContainer;

    private KieSession ksession;

    private boolean done = false;

    @Test
    public void testKJarUpgrade() throws Exception {
        // DROOLS-919
        KieServices ks = Factory.get();
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade", "1.0.0");
        KieModule km = CommonTestMethodBase.createAndDeployJar(ks, releaseId1, drl1, drl2_1);
        // Create a session and fire rules
        kieContainer = ks.newKieContainer(km.getReleaseId());
        ksession = kieContainer.newKieSession();
        ksession.setGlobal("test", this);
        ksession.insert(new Message("Hi Universe"));
        ksession.fireAllRules();
        Assert.assertTrue(done);
    }

    @Test
    public void testKJarUpgradeWithJavaClass() throws Exception {
        KieServices ks = Factory.get();
        String kmodule = "<kmodule xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" \n" + (((("         xmlns=\"http://www.drools.org/xsd/kmodule\">\n" + "  <kbase name=\"kbase1\">\n") + "    <ksession name=\"ksession1\" default=\"true\"/>\n") + "  </kbase>\n") + "</kmodule>");
        // Create an in-memory jar for version 1.0.0
        ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-upgrade-java", "1.0.0");
        Resource javaResource = ResourceFactory.newByteArrayResource(javaSrc.getBytes()).setResourceType(JAVA).setSourcePath("org/drools/compiler/test/PersonObject.java");
        Resource drlResource = ResourceFactory.newByteArrayResource(person_drl.getBytes()).setResourceType(DRL).setSourcePath("kbase1/person.drl");
        KieModule km = CommonTestMethodBase.createAndDeployJar(ks, kmodule, releaseId1, javaResource, drlResource);
        // Create a session and fire rules
        kieContainer = ks.newKieContainer(km.getReleaseId());
        ksession = kieContainer.newKieSession();
        Class<?> clazz = kieContainer.getClassLoader().loadClass("org.drools.compiler.test.PersonObject");
        Object person = clazz.newInstance();
        ksession.insert(person);
        ksession.fireAllRules();
        Assert.assertNotNull(person);
        Object personId = valueOf(person, "id");
        Assert.assertNotNull(personId);
        Assert.assertEquals("Person from version 1", personId);
        ReleaseId releaseId2 = ks.newReleaseId("org.kie", "test-upgrade-java", "1.1.0");
        Resource javaResource2 = ResourceFactory.newByteArrayResource(javaSrc_2.getBytes()).setResourceType(JAVA).setSourcePath("org/drools/compiler/test/PersonObject.java");
        Resource drlResource2 = ResourceFactory.newByteArrayResource(person_drl.getBytes()).setResourceType(DRL).setSourcePath("kbase1/person.drl");
        CommonTestMethodBase.createAndDeployJar(ks, kmodule, releaseId2, javaResource2, drlResource2);
        // update container
        kieContainer.updateToVersion(releaseId2);
        Assert.assertEquals(releaseId2, kieContainer.getReleaseId());
        // now let's run the rules
        ksession = kieContainer.newKieSession();
        person = kieContainer.getClassLoader().loadClass("org.drools.compiler.test.PersonObject").newInstance();
        ksession.insert(person);
        ksession.fireAllRules();
        Assert.assertNotNull(person);
        personId = valueOf(person, "id");
        Assert.assertNotNull(personId);
        Assert.assertEquals("Person from version 2", personId);
    }
}

