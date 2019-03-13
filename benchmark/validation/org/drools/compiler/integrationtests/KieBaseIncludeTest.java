/**
 * Copyright 2005 JBoss Inc
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


import KieServices.Factory;
import org.drools.compiler.kie.builder.impl.DrlProject;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.FileManager;
import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


@RunWith(Parameterized.class)
public class KieBaseIncludeTest {
    private final Class<? extends KieBuilder.ProjectType> projectType;

    public KieBaseIncludeTest(boolean useModel) {
        this.projectType = (useModel) ? ExecutableModelProject.class : DrlProject.class;
    }

    @Test
    public void testKJarIncludedDependency() throws Exception {
        String rule = "package org.test rule R when String() then end";
        KieServices ks = Factory.get();
        ReleaseId includedReleaseId = Factory.get().newReleaseId("org.kie", "test-project", "1.0.0-SNAPSHOT");
        FileManager fileManager = new FileManager();
        fileManager.setUp();
        InternalKieModule kJar1 = createKieJar(ks, includedReleaseId, rule);
        fileManager.tearDown();
        fileManager = new FileManager();
        fileManager.setUp();
        ReleaseId containerReleaseId = Factory.get().newReleaseId("org.kie", "test-container", "1.0.0-SNAPSHOT");
        KieFileSystem kfs = ks.newKieFileSystem();
        KieModuleModel kproj = ks.newKieModuleModel();
        kproj.newKieBaseModel("KBase2").addInclude("KBase1").newKieSessionModel("KSession2");
        kfs.writeKModuleXML(kproj.toXML());
        kfs.writePomXML(getPom(containerReleaseId, includedReleaseId));
        KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        Assert.assertTrue(kieBuilder.buildAll(projectType).getResults().getMessages().isEmpty());
        InternalKieModule containerKJar = ((InternalKieModule) (kieBuilder.getKieModule()));
        KieContainer kieContainer = ks.newKieContainer(containerReleaseId);
        KieSession ksession = kieContainer.newKieSession("KSession2");
        ksession.insert("test");
        Assert.assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
        fileManager.tearDown();
    }
}

