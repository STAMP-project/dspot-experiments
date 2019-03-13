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
package org.drools.compiler.builder;


import KieServices.Factory;
import Level.ERROR;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.appformer.maven.support.PomModel;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.compiler.io.memory.MemoryFileSystem;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.kproject.models.KieBaseModelImpl;
import org.drools.core.util.FileManager;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.KieRepository;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieBaseModel;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;


public class KieBuilderTest extends CommonTestMethodBase {
    protected FileManager fileManager;

    @Test
    public void testInMemory() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "memory", "1.0");
        KieModuleModel kProj = createKieProject(namespace);
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, releaseId, kProj);
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace);
    }

    @Test
    public void testOnDisc() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        KieModuleModel kProj = createKieProject(namespace);
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "memory", "1.0");
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generateAll(kfs, namespace, releaseId, kProj);
        MemoryFileSystem mfs = asMemoryFileSystem();
        File file = fileManager.getRootDirectory();
        mfs.writeAsFs(file);
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace);
    }

    @Test
    public void testKieModuleDependencies() throws IOException, ClassNotFoundException, InterruptedException {
        KieServices ks = Factory.get();
        String namespace1 = "org.kie.test1";
        ReleaseId releaseId1 = Factory.get().newReleaseId(namespace1, "memory", "1.0");
        KieModuleModel kProj1 = createKieProject(namespace1);
        KieFileSystem kfs1 = Factory.get().newKieFileSystem();
        generateAll(kfs1, namespace1, releaseId1, kProj1);
        KieBuilder kb1 = createKieBuilder(kfs1);
        kb1.buildAll();
        if (kb1.getResults().hasMessages(ERROR)) {
            Assert.fail(("Unable to build KieJar\n" + (kb1.getResults().toString())));
        }
        KieRepository kr = ks.getRepository();
        KieModule kModule1 = kr.getKieModule(releaseId1);
        Assert.assertNotNull(kModule1);
        String namespace2 = "org.kie.test2";
        ReleaseId releaseId2 = Factory.get().newReleaseId(namespace2, "memory", "1.0");
        KieModuleModel kProj2 = createKieProject(namespace2);
        KieBaseModelImpl kieBase2 = ((KieBaseModelImpl) (kProj2.getKieBaseModels().get(namespace2)));
        kieBase2.addInclude(namespace1);
        KieFileSystem kfs2 = Factory.get().newKieFileSystem();
        generateAll(kfs2, namespace2, releaseId2, kProj2);
        KieBuilder kb2 = createKieBuilder(kfs2);
        kb2.setDependencies(kModule1);
        kb2.buildAll();
        if (kb2.getResults().hasMessages(ERROR)) {
            Assert.fail(("Unable to build KieJar\n" + (kb2.getResults().toString())));
        }
        KieModule kModule2 = kr.getKieModule(releaseId2);
        Assert.assertNotNull(kModule2);
        KieContainer kContainer = ks.newKieContainer(releaseId2);
        KieBase kBase = kContainer.getKieBase(namespace2);
        KieSession kSession = kBase.newKieSession();
        List list = new ArrayList();
        kSession.setGlobal("list", list);
        kSession.fireAllRules();
        Assert.assertEquals(2, list.size());
        if ("org.kie.test1.Message".equals(list.get(0).getClass().getName())) {
            Assert.assertEquals("org.kie.test2.Message", list.get(1).getClass().getName());
        } else {
            Assert.assertEquals("org.kie.test2.Message", list.get(0).getClass().getName());
            Assert.assertEquals("org.kie.test1.Message", list.get(1).getClass().getName());
        }
    }

    @Test
    public void testNotExistingInclude() throws Exception {
        String drl = "package org.drools.compiler.integrationtests\n" + (((((("declare CancelFact\n" + " cancel : boolean = true\n") + "end\n") + "rule R1 when\n") + " $m : CancelFact( cancel == true )\n") + "then\n") + "end\n");
        KieServices ks = Factory.get();
        KieFileSystem kfs = ks.newKieFileSystem().write("src/main/resources/r1.drl", drl);
        KieModuleModel module = ks.newKieModuleModel();
        final String defaultBaseName = "defaultKBase";
        KieBaseModel defaultBase = module.newKieBaseModel(defaultBaseName).addInclude("notExistingKB1").addInclude("notExistingKB2");
        defaultBase.setDefault(true);
        defaultBase.addPackage("*");
        defaultBase.newKieSessionModel("defaultKSession").setDefault(true);
        kfs.writeKModuleXML(module.toXML());
        KieBuilder kb = ks.newKieBuilder(kfs).buildAll();
        Assert.assertEquals(2, kb.getResults().getMessages().size());
    }

    @Test
    public void testNoPomXml() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        KieModuleModel kProj = createKieProject(namespace);
        ReleaseId releaseId = Factory.get().getRepository().getDefaultReleaseId();
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generateKProjectXML(kfs, namespace, kProj);
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        MemoryFileSystem mfs = asMemoryFileSystem();
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), namespace);
    }

    @Test
    public void testNoProjectXml() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "memory", "1.0");
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generatePomXML(kfs, releaseId);
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        MemoryFileSystem mfs = asMemoryFileSystem();
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null);
    }

    @Test
    public void testEmptyProjectXml() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        KieServices ks = Factory.get();
        KieModuleModel kProj = ks.newKieModuleModel();
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "memory", "1.0");
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generateKProjectXML(kfs, namespace, kProj);
        generatePomXML(kfs, releaseId);
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        MemoryFileSystem mfs = asMemoryFileSystem();
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null);
    }

    @Test
    public void testNoPomAndProjectXml() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        ReleaseId releaseId = Factory.get().getRepository().getDefaultReleaseId();
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        MemoryFileSystem mfs = asMemoryFileSystem();
        createAndTestKieContainer(releaseId, createKieBuilder(kfs), null);
    }

    @Test
    public void testInvalidPomXmlGAV() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        KieModuleModel kProj = createKieProject(namespace);
        ReleaseId releaseId = new ReleaseIdImpl("", "", "");
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generatePomXML(kfs, releaseId);
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        MemoryFileSystem mfs = asMemoryFileSystem();
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        Assert.assertTrue(kieBuilder.getResults().hasMessages(ERROR));
    }

    @Test
    public void testInvalidPomXmlContent() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        KieModuleModel kProj = createKieProject(namespace);
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "memory", "1.0");
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        kfs.write("pom.xml", "xxxx");
        generateKProjectXML(kfs, namespace, kProj);
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        Assert.assertTrue(kieBuilder.getResults().hasMessages(ERROR));
    }

    @Test
    public void testInvalidProjectXml() throws IOException, ClassNotFoundException, InterruptedException {
        String namespace = "org.kie.test";
        KieModuleModel kProj = createKieProject(namespace);
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "memory", "1.0");
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        generatePomXML(kfs, releaseId);
        kfs.writeKModuleXML("xxxx");
        generateMessageClass(kfs, namespace);
        generateRule(kfs, namespace);
        KieBuilder kieBuilder = createKieBuilder(kfs);
        kieBuilder.buildAll();
        Assert.assertTrue(kieBuilder.getResults().hasMessages(ERROR));
    }

    @Test
    public void testSetPomModelReuse() throws IOException {
        String namespace = "org.kie.test";
        ReleaseId releaseId = Factory.get().newReleaseId(namespace, "pomModelReuse", "1.0");
        String pom = KieBuilderImpl.generatePomXml(releaseId);
        KieFileSystem kfs = Factory.get().newKieFileSystem();
        kfs.writePomXML(pom);
        // Create a KieBuilder instance
        KieBuilder kieBuilder1 = createKieBuilder(kfs);
        kieBuilder1.buildAll();
        // Get PomModel to re-use in second KieBuilder instance
        PomModel pomModel = getPomModel();
        kfs.writePomXML(pom);
        // Create another KieBuilder instance with the same KieFileSystem, setting PomModel
        KieBuilder kieBuilder2 = createKieBuilder(kfs);
        ((KieBuilderImpl) (kieBuilder2)).setPomModel(pomModel);
        kieBuilder2.buildAll();
        // Read pom.xml from first KieBuilder's KieModule
        InternalKieModule kieModule1 = ((InternalKieModule) (getKieModuleIgnoringErrors()));
        final Reader reader1 = kieModule1.getResource("META-INF/maven/org.kie.test/pomModelReuse/pom.xml").getReader();
        int charCode;
        String readPom1 = "";
        while ((charCode = reader1.read()) != (-1)) {
            readPom1 = readPom1 + ((char) (charCode));
        } 
        reader1.close();
        Assert.assertEquals(pom, readPom1);
        // Read pom.xml from second KieBuilder's KieModule
        InternalKieModule kieModule2 = ((InternalKieModule) (getKieModuleIgnoringErrors()));
        final Reader reader2 = kieModule2.getResource("META-INF/maven/org.kie.test/pomModelReuse/pom.xml").getReader();
        String readPom2 = "";
        while ((charCode = reader2.read()) != (-1)) {
            readPom2 = readPom2 + ((char) (charCode));
        } 
        reader1.close();
        Assert.assertEquals(pom, readPom2);
    }
}

