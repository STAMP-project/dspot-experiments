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


import KieServices.Factory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.drools.compiler.kie.builder.impl.FileKieModule;
import org.drools.compiler.kie.builder.impl.KieModuleKieProject;
import org.drools.compiler.kie.builder.impl.ZipKieModule;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.runtime.KieContainer;


public class KieProjectRuntimeModulesTest extends AbstractKnowledgeTest {
    @Test
    public void createMultpleJarAndFileResources() throws IOException, ClassNotFoundException, InterruptedException {
        KieModuleModel kProjModel1 = createKieModule("jar1", true);
        KieModuleModel kProjModel2 = createKieModule("jar2", true);
        KieModuleModel kProjModel3 = createKieModule("jar3", true);
        KieModuleModel kProjModel4 = createKieModule("fol4", false);
        ReleaseId releaseId1 = Factory.get().newReleaseId("jar1", "art1", "1.0-SNAPSHOT");
        ReleaseId releaseId2 = Factory.get().newReleaseId("jar2", "art1", "1.0-SNAPSHOT");
        ReleaseId releaseId3 = Factory.get().newReleaseId("jar3", "art1", "1.0-SNAPSHOT");
        ReleaseId releaseId4 = Factory.get().newReleaseId("fol4", "art1", "1.0-SNAPSHOT");
        File file1 = fileManager.newFile("jar1-1.0-SNAPSHOT.jar");
        File file2 = fileManager.newFile("jar2-1.0-SNAPSHOT.jar");
        File file3 = fileManager.newFile("jar3-1.0-SNAPSHOT.jar");
        File fol4 = fileManager.newFile("fol4-1.0-SNAPSHOT");
        ZipKieModule mod1 = new ZipKieModule(releaseId1, kProjModel1, file1);
        ZipKieModule mod2 = new ZipKieModule(releaseId2, kProjModel2, file2);
        ZipKieModule mod3 = new ZipKieModule(releaseId3, kProjModel3, file3);
        FileKieModule mod4 = new FileKieModule(releaseId4, kProjModel4, fol4);
        mod1.addKieDependency(mod2);
        mod1.addKieDependency(mod3);
        mod1.addKieDependency(mod4);
        KieModuleKieProject kProject = new KieModuleKieProject(mod1);
        KieContainer kContainer = new org.drools.compiler.kie.builder.impl.KieContainerImpl(kProject, null);
        KieBase kBase = kContainer.getKieBase("jar1.KBase1");
        ClassLoader cl = getRootClassLoader();
        Class cls = cl.loadClass("org.drools.compiler.cdi.test.KProjectTestClassjar1");
        Assert.assertNotNull(cls);
        cls = cl.loadClass("org.drools.compiler.cdi.test.KProjectTestClassjar2");
        Assert.assertNotNull(cls);
        cls = cl.loadClass("org.drools.compiler.cdi.test.KProjectTestClassjar3");
        Assert.assertNotNull(cls);
        testEntry(new KProjectTestClassImpl("jar1", kContainer), "jar1");
        testEntry(new KProjectTestClassImpl("jar2", kContainer), "jar2");
        testEntry(new KProjectTestClassImpl("jar3", kContainer), "jar3");
        testEntry(new KProjectTestClassImpl("fol4", kContainer), "fol4");
    }

    @Test
    public void createModuleAndFindResources() throws IOException, ClassNotFoundException, InterruptedException {
        createKieModule("fol4", false);
        ReleaseId releaseId = Factory.get().newReleaseId("fol4", "art1", "1.0-SNAPSHOT");
        KieContainer kieContainer = Factory.get().newKieContainer(releaseId);
        Assert.assertNotNull(kieContainer);
        InputStream is = kieContainer.getClassLoader().getResourceAsStream("/META-INF/beans.xml");
        Assert.assertNotNull(is);
        byte[] bytesFromStream = readBytesFromInputStream(is);
        Enumeration<URL> foundResources = kieContainer.getClassLoader().getResources("/META-INF/beans.xml");
        Assert.assertNotNull(foundResources);
        List<URL> resourcesAsList = Collections.list(foundResources);
        Assert.assertNotNull(resourcesAsList);
        Assert.assertEquals(1, resourcesAsList.size());
        URL resourceUrl = resourcesAsList.get(0);
        byte[] bytesFromURL = readBytesFromInputStream(resourceUrl.openStream());
        Assert.assertTrue(Arrays.equals(bytesFromStream, bytesFromURL));
        String url = resourceUrl.toString();
        Assert.assertNotNull(url);
        Assert.assertEquals("mfs:/META-INF/beans.xml", url);
        String file = resourceUrl.getFile();
        Assert.assertNotNull(file);
        Assert.assertEquals("/META-INF/beans.xml", file);
    }

    @Test
    public void createModuleAndFindResourcesVerifyURL() throws IOException, ClassNotFoundException, InterruptedException {
        createKieModule("fol4", false);
        ReleaseId releaseId = Factory.get().newReleaseId("fol4", "art1", "1.0-SNAPSHOT");
        KieContainer kieContainer = Factory.get().newKieContainer(releaseId);
        Assert.assertNotNull(kieContainer);
        InputStream is = kieContainer.getClassLoader().getResourceAsStream("META-INF/beans.xml");
        Assert.assertNotNull(is);
        byte[] bytesFromStream = readBytesFromInputStream(is);
        Enumeration<URL> foundResources = kieContainer.getClassLoader().getResources("META-INF/beans.xml");
        Assert.assertNotNull(foundResources);
        List<URL> resourcesAsList = /* This module and uberfire-commons depenency have beans.xml files
        which are found when calling `getResources` with a relative path.
         */
        Collections.list(foundResources).stream().filter(( url) -> (!(url.toString().contains("uberfire-commons"))) && (!(url.toString().contains("drools-cdi")))).collect(Collectors.toList());
        Assert.assertNotNull(resourcesAsList);
        Assert.assertEquals(1, resourcesAsList.size());
        URL resourceUrl = resourcesAsList.get(0);
        byte[] bytesFromURL = readBytesFromInputStream(resourceUrl.openStream());
        Assert.assertTrue(Arrays.equals(bytesFromStream, bytesFromURL));
        String url = resourceUrl.toString();
        Assert.assertNotNull(url);
        Assert.assertEquals("mfs:/META-INF/beans.xml", url);
        String file = resourceUrl.getFile();
        Assert.assertNotNull(file);
        Assert.assertEquals("/META-INF/beans.xml", file);
    }
}

