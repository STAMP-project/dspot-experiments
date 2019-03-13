/**
 * Copyright (c) 2012-2018 Red Hat, Inc.
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.plugin.dynamodule;


import com.google.inject.Module;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.eclipse.che.inject.ModuleFinder;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runners.MethodSorters;


/**
 * Test the generation of the class and the serviceloader SPI.
 *
 * @author Florent Benoit
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class DynaModuleListGeneratorMojoTest {
    /**
     * Rule to manage the mojo (inject, get variables from mojo)
     */
    @Rule
    public MojoRule rule = new MojoRule();

    /**
     * Resources of each test mapped on the name of the method
     */
    @Rule
    public TestResources resources = new TestResources();

    /**
     * Check that the ModuleList class is generated and contains the expected modules.
     */
    @Test
    public void testModuleListGenerated() throws Exception {
        File projectCopy = this.resources.getBasedir("project");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertNotNull(pom);
        Assert.assertTrue(pom.exists());
        DynaModuleListGeneratorMojo mojo = ((DynaModuleListGeneratorMojo) (this.rule.lookupMojo("build", pom)));
        configure(mojo, projectCopy);
        mojo.execute();
        File generatedModuleFile = mojo.getGuiceGeneratedModuleFile();
        // Check file has been generated
        Assert.assertTrue(generatedModuleFile.exists());
        Class<ModuleFinder> moduleFinderClass = ((Class<ModuleFinder>) (new DynaModuleListGeneratorMojoTest.CustomClassLoader().defineClass("org.eclipse.che.dynamodule.MyDynamoduleTestModule", Files.readAllBytes(generatedModuleFile.toPath()))));
        ModuleFinder moduleFinder = moduleFinderClass.getDeclaredConstructor().newInstance();
        List<Module> moduleList = moduleFinder.getModules();
        org.testng.Assert.assertEquals(moduleList.size(), 2);
        Assert.assertTrue(moduleList.stream().anyMatch(( item) -> item.getClass().equals(.class)));
        Assert.assertTrue(moduleList.stream().anyMatch(( item) -> item.getClass().equals(.class)));
    }

    /**
     * Check that the ServiceLoader is generated and working
     */
    @Test
    public void testServiceLoaderGenerated() throws Exception {
        File projectCopy = this.resources.getBasedir("project");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertNotNull(pom);
        Assert.assertTrue(pom.exists());
        DynaModuleListGeneratorMojo mojo = ((DynaModuleListGeneratorMojo) (this.rule.lookupMojo("build", pom)));
        configure(mojo, projectCopy);
        mojo.execute();
        URL url = new File(((projectCopy + (File.separator)) + "classes")).toURI().toURL();
        URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{ url });
        ServiceLoader<ModuleFinder> moduleFinderServiceLoader = ServiceLoader.load(ModuleFinder.class, urlClassLoader);
        Iterator<ModuleFinder> iterator = moduleFinderServiceLoader.iterator();
        Assert.assertTrue(iterator.hasNext());
        ModuleFinder moduleFinder = iterator.next();
        List<Module> moduleList = moduleFinder.getModules();
        org.testng.Assert.assertEquals(moduleList.size(), 2);
        Assert.assertTrue(moduleList.stream().anyMatch(( item) -> item.getClass().equals(.class)));
        Assert.assertTrue(moduleList.stream().anyMatch(( item) -> item.getClass().equals(.class)));
        Assert.assertFalse(iterator.hasNext());
    }

    /**
     * Check that the plugin is able to scan war files
     */
    @Test
    public void testWarFiles() throws Exception {
        File projectCopy = this.resources.getBasedir("project");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertNotNull(pom);
        Assert.assertTrue(pom.exists());
        DynaModuleListGeneratorMojo mojo = ((DynaModuleListGeneratorMojo) (this.rule.lookupMojo("build", pom)));
        configure(mojo, projectCopy);
        this.rule.setVariableValueToObject(mojo, "scanWarDependencies", true);
        this.rule.setVariableValueToObject(mojo, "scanJarInWarDependencies", true);
        mojo.execute();
        Set<String> findClasses = mojo.getDynaModuleListGenerator().getDynaModuleScanner().getDynaModuleClasses();
        Assert.assertTrue(((findClasses.stream().filter(( className) -> className.contains("org.eclipse.che.wsagent")).collect(Collectors.toList()).size()) > 2));
        // this dependency is inside the wsagent-core file.
        Assert.assertTrue(((findClasses.stream().filter(( className) -> className.contains("org.eclipse.che.wsagent.server.")).collect(Collectors.toList()).size()) >= 2));
    }

    private static class CustomClassLoader extends ClassLoader {
        public Class<?> defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }
}

