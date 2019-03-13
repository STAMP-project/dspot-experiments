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
package org.kie.scanner;


import KieServices.Factory;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import junit.framework.TestCase;
import org.appformer.maven.support.DependencyFilter;
import org.drools.compiler.kie.builder.impl.KieBuilderImpl;
import org.drools.core.rule.TypeMetaInfo;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieModule;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;


public class KieModuleMetaDataTest extends AbstractKieCiTest {
    @Test
    public void testKieModuleMetaData() throws Exception {
        ReleaseId releaseId = Factory.get().newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId);
        checkDroolsCoreDep(kieModuleMetaData);
        Assert.assertTrue(("" + (kieModuleMetaData.getPackages())).contains("junit"));
    }

    @Test
    public void testKieModuleMetaDataWithoutTestDependencies() throws Exception {
        ReleaseId releaseId = Factory.get().newReleaseId("org.drools", "drools-core", "5.5.0.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId, new DependencyFilter.ExcludeScopeFilter("test"));
        checkDroolsCoreDep(kieModuleMetaData);
        TestCase.assertFalse(("" + (kieModuleMetaData.getPackages())).contains("junit"));
    }

    @Test
    public void testKieModuleMetaDataForNonExistingGAV() throws Exception {
        // DROOLS-1562
        ReleaseId releaseId = Factory.get().newReleaseId("org.drools", "drools-core", "5.7.0.Final");
        KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(releaseId);
        Assert.assertEquals(0, kieModuleMetaData.getPackages().size());
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClass() throws Exception {
        testKieModuleMetaDataInMemory(false);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemory(true);
    }

    @Test
    public void testKieModuleMetaDataInMemoryUsingPOMWithTypeDeclaration() throws Exception {
        testKieModuleMetaDataInMemoryUsingPOM(true);
    }

    @Test
    public void testKieModuleMetaDataForDependenciesInMemory() throws Exception {
        testKieModuleMetaDataForDependenciesInMemory(false);
    }

    @Test
    public void testKieModuleMetaDataInMemoryWithJavaClassDefaultPackage() throws Exception {
        final KieServices ks = Factory.get();
        final ReleaseId releaseId = ks.newReleaseId("org.kie", "javaDefaultPackage", "1.0-SNAPSHOT");
        final KieModuleModel kproj = ks.newKieModuleModel();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(kproj.toXML()).writePomXML(KieBuilderImpl.generatePomXml(releaseId)).write("src/main/java/test/Bean.java", createJavaSource());
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        Assert.assertTrue(messages.isEmpty());
        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        // The call to kieModuleMetaData.getClass() assumes a Java file has an explicit package
        final Class<?> beanClass = kieModuleMetaData.getClass("", "test.Bean");
        Assert.assertNotNull(beanClass);
        final TypeMetaInfo beanMetaInfo = kieModuleMetaData.getTypeMetaInfo(beanClass);
        Assert.assertNotNull(beanMetaInfo);
    }

    @Test
    public void testGetPackageNames() {
        final KieServices ks = Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/test.drl", "package org.test declare Bean end");
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        Assert.assertTrue(messages.isEmpty());
        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        TestCase.assertFalse(kieModuleMetaData.getPackages().isEmpty());
        Assert.assertTrue(kieModuleMetaData.getPackages().contains("org.test"));
    }

    @Test
    public void testIncludeAllDeps() {
        final KieServices ks = Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML(getPomWithTestDependency());
        final KieModule kieModule = ks.newKieBuilder(kfs).getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        Assert.assertTrue(("" + (kieModuleMetaData.getPackages())).contains("junit"));
    }

    @Test
    public void testExcludeTestDeps() {
        final KieServices ks = Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writePomXML(getPomWithTestDependency());
        final KieModule kieModule = ks.newKieBuilder(kfs).getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule, new DependencyFilter.ExcludeScopeFilter("test"));
        TestCase.assertFalse(("" + (kieModuleMetaData.getPackages())).contains("junit"));
    }

    @Test
    public void testGetRuleNames() {
        final KieServices ks = Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("src/main/resources/test1.drl", ("package org.test\n" + ((((((("rule A\n" + " when\n") + "then\n") + "end\n") + "rule B\n") + " when\n") + "then\n") + "end\n")));
        kfs.write("src/main/resources/test2.drl", ("package org.test\n" + ((("rule C\n" + " when\n") + "then\n") + "end\n")));
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        Assert.assertTrue(messages.isEmpty());
        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        Collection<String> rules = kieModuleMetaData.getRuleNamesInPackage("org.test");
        Assert.assertEquals(3, rules.size());
        Assert.assertTrue(rules.containsAll(Arrays.asList("A", "B", "C")));
    }

    @Test
    public void testKieMavenPluginEmptyProject() {
        // According to https://bugzilla.redhat.com/show_bug.cgi?id=1049674#c2 the below is the minimal POM required to use KieMavenPlugin.
        final KieServices ks = Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.write("pom.xml", ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (((((((((((((((("<project xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd\" xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" + "  <modelVersion>4.0.0</modelVersion>") + "  <groupId>org.kie</groupId>") + "  <artifactId>plugin-test</artifactId>") + "  <version>1.0</version>") + "  <packaging>kjar</packaging>") + "  <build>") + "    <plugins>") + "      <plugin>") + "        <groupId>org.kie</groupId>") + "        <artifactId>kie-maven-plugin</artifactId>") + "        <version>the-test-does-not-need-proper-version-here</version>") + "        <extensions>true</extensions>") + "      </plugin>") + "    </plugins>") + "  </build>") + "</project>")));
        kfs.write("/src/main/resources/META-INF/kmodule.xml", "<kmodule xmlns=\"http://www.drools.org/xsd/kmodule\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/>");
        final KieBuilder kieBuilder = ks.newKieBuilder(kfs);
        final List<Message> messages = kieBuilder.buildAll().getResults().getMessages();
        Assert.assertTrue(messages.isEmpty());
        final KieModule kieModule = kieBuilder.getKieModule();
        final KieModuleMetaData kieModuleMetaData = KieModuleMetaData.Factory.newKieModuleMetaData(kieModule);
        boolean fail = false;
        for (final String packageName : kieModuleMetaData.getPackages()) {
            for (final String className : kieModuleMetaData.getClasses(packageName)) {
                try {
                    kieModuleMetaData.getClass(packageName, className);
                } catch (Throwable e) {
                    fail = true;
                    System.out.println(e);
                }
            }
        }
        if (fail) {
            Assert.fail("See console for details.");
        }
    }
}

