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
package org.eclipse.che.plugin.gwt;


import java.io.File;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.codehaus.plexus.util.FileUtils;
import org.codehaus.plexus.util.StringUtils;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


public class ProcessExcludesMojoTest {
    /**
     * Rule to manage the mojo (set/get values of variables of mojo).
     */
    @Rule
    public MojoRule rule = new MojoRule();

    /**
     * Resources of each test mapped on the name of the method.
     */
    @Rule
    public TestResources resources = new TestResources();

    /**
     * Tests the ability of the plugin to process IDE GWT module (IDE.gwt.xml) to prevent inheriting
     * the GWT modules of the excluded IDE plugins.
     */
    @Test
    public void testProcessingExcludes() throws Exception {
        File projectCopy = resources.getBasedir("project");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertTrue(pom.exists());
        ProcessExcludesMojo mojo = ((ProcessExcludesMojo) (rule.lookupMojo("process-excludes", pom)));
        Assert.assertNotNull(mojo);
        configureMojo(mojo, projectCopy);
        mojo.execute();
        File fullGwtXml = new File(projectCopy, (("classes/org/eclipse/che/ide/Full" + (ProcessExcludesMojo.FULL_IDE_GWT_MODULE_SUFFIX)) + ".gwt.xml"));
        Assert.assertTrue(fullGwtXml.exists());
        String fullGwtXmlContent = FileUtils.fileRead(fullGwtXml);
        Assert.assertFalse(fullGwtXmlContent.contains("org.eclipse.che.ide.ext.help.HelpAboutExtension"));
        File ideGwtXml = new File(projectCopy, "classes/org/eclipse/che/ide/IDE.gwt.xml");
        Assert.assertTrue(ideGwtXml.exists());
        String ideGwtXmlContent = FileUtils.fileRead(ideGwtXml);
        Assert.assertFalse(ideGwtXmlContent.contains("<inherits name=\"org.eclipse.che.ide.Full\"/>"));
        Assert.assertTrue(ideGwtXmlContent.contains((("<inherits name=\"org.eclipse.che.ide.Full" + (ProcessExcludesMojo.FULL_IDE_GWT_MODULE_SUFFIX)) + "\"/>")));
    }

    /**
     * Tests that plugins doesn't modify the IDE GWT module (IDE.gwt.xml) if there are no excluded
     * plugins.
     */
    @Test
    public void testWithoutExcludes() throws Exception {
        File projectCopy = resources.getBasedir("project-without-exclusions");
        File pom = new File(projectCopy, "pom.xml");
        Assert.assertTrue(pom.exists());
        ProcessExcludesMojo mojo = ((ProcessExcludesMojo) (rule.lookupMojo("process-excludes", pom)));
        Assert.assertNotNull(mojo);
        configureMojo(mojo, projectCopy);
        mojo.execute();
        File outputDirectory = ((File) (rule.getVariableValueFromObject(mojo, "outputDirectory")));
        File expected = new File("src/test/projects/project-without-exclusions/classes/org/eclipse/che/ide/IDE.gwt.xml");
        File actual = new File(outputDirectory, "org/eclipse/che/ide/IDE.gwt.xml");
        Assert.assertEquals("IDE.gwt.xml is changed but it shouldn't.", StringUtils.getNestedString(FileUtils.fileRead(expected), "<module", "</module>"), StringUtils.getNestedString(FileUtils.fileRead(actual), "<module", "</module>"));
    }
}

