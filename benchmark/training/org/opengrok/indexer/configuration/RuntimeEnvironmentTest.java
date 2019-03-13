/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License (the "License").
 * You may not use this file except in compliance with the License.
 *
 * See LICENSE.txt included in this distribution for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at LICENSE.txt.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 */
/**
 * Copyright (c) 2008, 2018, Oracle and/or its affiliates. All rights reserved.
 * Portions Copyright (c) 2017-2018, Chris Fraire <cfraire@me.com>.
 */
package org.opengrok.indexer.configuration;


import Configuration.RemoteSCM.DIRBASED;
import Configuration.RemoteSCM.OFF;
import Configuration.RemoteSCM.ON;
import Configuration.RemoteSCM.UIONLY;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.opengrok.indexer.authorization.AuthorizationPlugin;
import org.opengrok.indexer.authorization.AuthorizationStack;
import org.opengrok.indexer.history.RepositoryInfo;
import org.opengrok.indexer.util.ForbiddenSymlinkException;
import org.opengrok.indexer.util.IOUtils;


/**
 * Test the RuntimeEnvironment class
 *
 * @author Trond Norbye
 */
public class RuntimeEnvironmentTest {
    private static File originalConfig;

    public RuntimeEnvironmentTest() {
    }

    @Test
    public void testDataRoot() throws IOException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertNull(instance.getDataRootFile());
        Assert.assertNull(instance.getDataRootPath());
        File f = File.createTempFile("dataroot", null);
        String path = f.getCanonicalPath();
        Assert.assertTrue(f.delete());
        Assert.assertFalse(f.exists());
        instance.setDataRoot(path);
        // setDataRoot() used to create path if it didn't exist, but that
        // logic has been moved. Verify that it is so.
        Assert.assertFalse(f.exists());
        Assert.assertTrue(f.mkdirs());
        Assert.assertEquals(path, instance.getDataRootPath());
        Assert.assertEquals(path, instance.getDataRootFile().getCanonicalPath());
    }

    @Test
    public void testIncludeRoot() throws IOException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertNull(instance.getIncludeRootPath());
        // set data root
        File f = File.createTempFile("dataroot", null);
        String path = f.getCanonicalPath();
        instance.setDataRoot(path);
        // verify they are the same
        Assert.assertEquals(instance.getDataRootPath(), instance.getIncludeRootPath());
        // set include root
        f = File.createTempFile("includeroot", null);
        path = f.getCanonicalPath();
        instance.setIncludeRoot(path);
        Assert.assertEquals(path, instance.getIncludeRootPath());
    }

    @Test
    public void testSourceRoot() throws IOException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertNull(instance.getSourceRootFile());
        Assert.assertNull(instance.getSourceRootPath());
        File f = File.createTempFile("sourceroot", null);
        String path = f.getCanonicalPath();
        Assert.assertTrue(f.delete());
        instance.setSourceRoot(path);
        Assert.assertEquals(path, instance.getSourceRootPath());
        Assert.assertEquals(path, instance.getSourceRootFile().getCanonicalPath());
    }

    @Test
    public void testProjects() throws IOException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        instance.setProjectsEnabled(true);
        Assert.assertFalse(instance.hasProjects());
        Assert.assertNotNull(instance.getProjects());
        Assert.assertEquals(0, instance.getProjects().size());
        Assert.assertNull(instance.getDefaultProjects());
        File file = new File("/opengrok_automatic_test/foo/bar");
        File folder = new File("/opengrok_automatic_test/foo");
        instance.setSourceRoot(folder.getCanonicalPath());
        Project p = new Project("bar");
        p.setPath("/bar");
        Assert.assertEquals("/bar", p.getId());
        instance.getProjects().put(p.getName(), p);
        Assert.assertEquals(p, Project.getProject(file));
        instance.setProjects(null);
        Assert.assertNull(instance.getProjects());
    }

    @Test
    public void testGroups() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertFalse(instance.hasGroups());
        Assert.assertNotNull(instance.getGroups());
        Assert.assertEquals(0, instance.getGroups().size());
        Group g = new Group("Random", "xyz.*");
        instance.getGroups().add(g);
        Assert.assertEquals(1, instance.getGroups().size());
        Assert.assertEquals(g, instance.getGroups().iterator().next());
        Assert.assertEquals("Random", instance.getGroups().iterator().next().getName());
        instance.setGroups(null);
        Assert.assertNull(instance.getGroups());
    }

    @Test
    public void testPerThreadConsistency() throws InterruptedException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String path = "/tmp/dataroot1";
        instance.setDataRoot(path);
        Thread t = new Thread(() -> {
            Configuration c = new Configuration();
            c.setDataRoot("/tmp/dataroot2");
            RuntimeEnvironment.getInstance().setConfiguration(c);
        });
        t.start();
        t.join();
        Assert.assertEquals("/tmp/dataroot2", instance.getDataRootPath());
    }

    @Test
    public void testUrlPrefix() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertEquals("/source/s?", instance.getUrlPrefix());
    }

    @Test
    public void testCtags() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String instanceCtags = instance.getCtags();
        Assert.assertNotNull(instanceCtags);
        Assert.assertTrue("instance ctags should equals 'ctags' or the sys property", ((instanceCtags.equals("ctags")) || (instanceCtags.equals(System.getProperty("org.opengrok.indexer.analysis.Ctags")))));
        String path = "/usr/bin/ctags";
        instance.setCtags(path);
        Assert.assertEquals(path, instance.getCtags());
        instance.setCtags(null);
        instanceCtags = instance.getCtags();
        Assert.assertTrue("instance ctags should equals 'ctags' or the sys property", ((instanceCtags.equals("ctags")) || (instanceCtags.equals(System.getProperty("org.opengrok.indexer.analysis.Ctags")))));
    }

    @Test
    public void testHistoryReaderTimeLimit() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertEquals(30, instance.getHistoryReaderTimeLimit());
        instance.setHistoryReaderTimeLimit(50);
        Assert.assertEquals(50, instance.getHistoryReaderTimeLimit());
    }

    @Test
    public void testFetchHistoryWhenNotInCache() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertTrue(instance.isFetchHistoryWhenNotInCache());
        instance.setFetchHistoryWhenNotInCache(false);
        Assert.assertFalse(instance.isFetchHistoryWhenNotInCache());
    }

    @Test
    public void testUseHistoryCache() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertTrue(instance.useHistoryCache());
        instance.setUseHistoryCache(false);
        Assert.assertFalse(instance.useHistoryCache());
    }

    @Test
    public void testGenerateHtml() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertTrue(instance.isGenerateHtml());
        instance.setGenerateHtml(false);
        Assert.assertFalse(instance.isGenerateHtml());
    }

    @Test
    public void testCompressXref() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertTrue(instance.isCompressXref());
        instance.setCompressXref(false);
        Assert.assertFalse(instance.isCompressXref());
    }

    @Test
    public void testQuickContextScan() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertTrue(instance.isQuickContextScan());
        instance.setQuickContextScan(false);
        Assert.assertFalse(instance.isQuickContextScan());
    }

    @Test
    public void testRepositories() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertNotNull(instance.getRepositories());
        instance.removeRepositories();
        Assert.assertNull(instance.getRepositories());
        List<RepositoryInfo> reps = new ArrayList<>();
        instance.setRepositories(reps);
        Assert.assertSame(reps, instance.getRepositories());
    }

    @Test
    public void testRamBufferSize() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertEquals(16, instance.getRamBufferSize(), 0);// default is 16

        instance.setRamBufferSize(256);
        Assert.assertEquals(256, instance.getRamBufferSize(), 0);
    }

    @Test
    public void testAllowLeadingWildcard() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertFalse(instance.isAllowLeadingWildcard());
        instance.setAllowLeadingWildcard(true);
        Assert.assertTrue(instance.isAllowLeadingWildcard());
    }

    @Test
    public void testIgnoredNames() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertNotNull(instance.getIgnoredNames());
        instance.setIgnoredNames(null);
        Assert.assertNull(instance.getIgnoredNames());
    }

    @Test
    public void testUserPage() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String page = "http://www.myserver.org/viewProfile.jspa?username=";
        Assert.assertNull(instance.getUserPage());// default value is null

        instance.setUserPage(page);
        Assert.assertEquals(page, instance.getUserPage());
    }

    @Test
    public void testBugPage() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String page = "http://bugs.myserver.org/bugdatabase/view_bug.do?bug_id=";
        Assert.assertNull(instance.getBugPage());// default value is null

        instance.setBugPage(page);
        Assert.assertEquals(page, instance.getBugPage());
    }

    @Test
    public void testBugPattern() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String[] tests = new String[]{ "\\b([12456789][0-9]{6})\\b", "\\b(#\\d+)\\b", "(BUG123)", "\\sbug=(\\d+[a-t])*(\\W*)" };
        for (String test : tests) {
            try {
                instance.setBugPattern(test);
                Assert.assertEquals(test, instance.getBugPattern());
            } catch (IOException ex) {
                Assert.fail((("The pattern '" + test) + "' should not throw an exception"));
            }
        }
    }

    @Test
    public void testInvalidBugPattern() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String[] tests = new String[]{ "\\b([", "\\b({,6})\\b", "\\b6)\\b", "*buggy", "BUG123"// does not contain a group
        , "\\b[a-z]+\\b"// does not contain a group
         };
        for (String test : tests) {
            try {
                instance.setBugPattern(test);
                Assert.fail((("The pattern '" + test) + "' should throw an exception"));
            } catch (IOException ex) {
            }
        }
    }

    @Test
    public void testReviewPage() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String page = "http://arc.myserver.org/caselog/PSARC/";
        Assert.assertNull(instance.getReviewPage());// default value is null

        instance.setReviewPage(page);
        Assert.assertEquals(page, instance.getReviewPage());
    }

    @Test
    public void testReviewPattern() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String[] tests = new String[]{ "\\b(\\d{4}/\\d{3})\\b", "\\b(#PSARC\\d+)\\b", "(REVIEW 123)", "\\sreview=(\\d+[a-t])*(\\W*)" };
        for (String test : tests) {
            try {
                instance.setReviewPattern(test);
                Assert.assertEquals(test, instance.getReviewPattern());
            } catch (IOException ex) {
                Assert.fail((("The pattern '" + test) + "' should not throw an exception"));
            }
        }
    }

    @Test
    public void testInvalidReviewPattern() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        String[] tests = new String[]{ "\\b([", "\\b({,6})\\b", "\\b6)\\b", "*reviewy", "REVIEW 123"// does not contain a group
        , "\\b[a-z]+\\b"// does not contain a group
         };
        for (String test : tests) {
            try {
                instance.setReviewPattern(test);
                Assert.fail((("The pattern '" + test) + "' should throw an exception"));
            } catch (IOException ex) {
            }
        }
    }

    @Test
    public void testWebappLAF() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertEquals("default", instance.getWebappLAF());
        instance.setWebappLAF("foo");
        Assert.assertEquals("foo", instance.getWebappLAF());
    }

    @Test
    public void testRemoteScmSupported() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertEquals(OFF, instance.getRemoteScmSupported());
        instance.setRemoteScmSupported(ON);
        Assert.assertEquals(ON, instance.getRemoteScmSupported());
        instance.setRemoteScmSupported(DIRBASED);
        Assert.assertEquals(DIRBASED, instance.getRemoteScmSupported());
        instance.setRemoteScmSupported(UIONLY);
        Assert.assertEquals(UIONLY, instance.getRemoteScmSupported());
    }

    @Test
    public void testOptimizeDatabase() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertTrue(instance.isOptimizeDatabase());
        instance.setOptimizeDatabase(false);
        Assert.assertFalse(instance.isOptimizeDatabase());
    }

    @Test
    public void testUsingLuceneLocking() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertEquals(LuceneLockName.OFF, instance.getLuceneLocking());
    }

    @Test
    public void testIndexVersionedFilesOnly() {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        Assert.assertFalse(instance.isIndexVersionedFilesOnly());
        instance.setIndexVersionedFilesOnly(true);
        Assert.assertTrue(instance.isIndexVersionedFilesOnly());
    }

    @Test
    public void testXMLencdec() throws IOException {
        Configuration c = new Configuration();
        String m = c.getXMLRepresentationAsString();
        Configuration o = Configuration.makeXMLStringAsConfiguration(m);
        Assert.assertNotNull(o);
        m = m.replace('a', 'm');
        try {
            o = Configuration.makeXMLStringAsConfiguration(m);
            Assert.fail("makeXmlStringsAsConfiguration should throw exception");
        } catch (Throwable t) {
        }
    }

    @Test
    public void testAuthorizationFlagDecode() throws IOException {
        String confString = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n" + ((((((((((((((((((((((((((((((((((((((((((((("<java class=\"java.beans.XMLDecoder\" version=\"1.8.0_121\">\n" + " <object class=\"org.opengrok.indexer.configuration.Configuration\">\n") + "\t<void property=\"pluginStack\">\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>sufficient</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>Plugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>required</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>OtherPlugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>REQUISITE</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>AnotherPlugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>reQuIrEd</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>DifferentPlugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t</void>\n") + " </object>\n") + "</java>");
        Configuration conf = Configuration.makeXMLStringAsConfiguration(confString);
        Assert.assertNotNull(conf.getPluginStack());
        AuthorizationStack pluginConfiguration = conf.getPluginStack();
        Assert.assertEquals(4, pluginConfiguration.getStack().size());
        Assert.assertTrue(pluginConfiguration.getStack().get(0).getFlag().isSufficient());
        Assert.assertEquals("Plugin", pluginConfiguration.getStack().get(0).getName());
        Assert.assertTrue(pluginConfiguration.getStack().get(1).getFlag().isRequired());
        Assert.assertEquals("OtherPlugin", pluginConfiguration.getStack().get(1).getName());
        Assert.assertTrue(pluginConfiguration.getStack().get(2).getFlag().isRequisite());
        Assert.assertEquals("AnotherPlugin", pluginConfiguration.getStack().get(2).getName());
        Assert.assertTrue(pluginConfiguration.getStack().get(3).getFlag().isRequired());
        Assert.assertEquals("DifferentPlugin", pluginConfiguration.getStack().get(3).getName());
    }

    @Test
    public void testAuthorizationStackDecode() throws IOException {
        String confString = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n" + (((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((((("<java class=\"java.beans.XMLDecoder\" version=\"1.8.0_121\">\n" + " <object class=\"org.opengrok.indexer.configuration.Configuration\">\n") + "\t<void property=\"pluginStack\">\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object id=\"first_plugin\" class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>sufficient</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>Plugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object id=\"first_stack\" class=\"org.opengrok.indexer.authorization.AuthorizationStack\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>required</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>basic stack</string>\n") + "\t\t\t\t</void>\n") + "                             <void property=\"stack\">") + "                                 <void method=\"add\">") + "\t                 \t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t                 \t\t\t<void property=\"flag\">\n") + "\t                 \t\t\t\t<string>required</string>\n") + "\t                 \t\t\t</void>\n") + "\t                 \t\t\t<void property=\"name\">\n") + "\t                 \t\t\t\t<string>NestedPlugin</string>\n") + "\t                 \t\t\t</void>\n") + "\t\t                 \t</object>\n") + "                                 </void>") + "                                 <void method=\"add\">") + "\t                 \t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t                 \t\t\t<void property=\"flag\">\n") + "\t                 \t\t\t\t<string>requisite</string>\n") + "\t                 \t\t\t</void>\n") + "\t                 \t\t\t<void property=\"name\">\n") + "\t                 \t\t\t\t<string>NestedPlugin</string>\n") + "\t                 \t\t\t</void>\n") + "                                             <void property=\"setup\">") + "                                                 <void method=\"put\">") + "                                                     <string>key</string>") + "                                                     <string>value</string>") + "                                                 </void>") + "                                                 <void method=\"put\">") + "                                                     <string>plugin</string>") + "                                                     <object idref=\"first_plugin\" />") + "                                                 </void>") + "                                             </void>") + "\t\t                 \t</object>\n") + "                                 </void>") + "                             </void>") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>requisite</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>Requisite</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationStack\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>required</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>advanced stack</string>\n") + "\t\t\t\t</void>\n") + "                             <void property=\"stack\">") + "                                 <void method=\"add\">") + "\t                 \t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t                 \t\t\t<void property=\"flag\">\n") + "\t                 \t\t\t\t<string>required</string>\n") + "\t                 \t\t\t</void>\n") + "\t                 \t\t\t<void property=\"name\">\n") + "\t                 \t\t\t\t<string>NestedPlugin</string>\n") + "\t                 \t\t\t</void>\n") + "\t\t                 \t</object>\n") + "                                 </void>") + "                                 <void method=\"add\">") + "\t                 \t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t                 \t\t\t<void property=\"flag\">\n") + "\t                 \t\t\t\t<string>requisite</string>\n") + "\t                 \t\t\t</void>\n") + "\t                 \t\t\t<void property=\"name\">\n") + "\t                 \t\t\t\t<string>NestedPlugin</string>\n") + "\t                 \t\t\t</void>\n") + "                                             <void property=\"setup\">") + "                                                 <void method=\"put\">") + "                                                     <string>key</string>") + "                                                     <string>other value</string>") + "                                                 </void>") + "                                                 <void method=\"put\">") + "                                                     <string>plugin</string>") + "                                                     <object idref=\"first_plugin\" />") + "                                                 </void>") + "                                             </void>") + "\t\t                 \t</object>\n") + "                                 </void>") + "                             </void>") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object idref=\"first_stack\" />") + "\t\t</void>\n") + "\t</void>\n") + " </object>\n") + "</java>");
        Configuration conf = Configuration.makeXMLStringAsConfiguration(confString);
        Assert.assertNotNull(conf.getPluginStack());
        AuthorizationStack pluginConfiguration = conf.getPluginStack();
        Assert.assertEquals(5, pluginConfiguration.getStack().size());
        // single plugins
        Assert.assertTrue(pluginConfiguration.getStack().get(0).getFlag().isSufficient());
        Assert.assertEquals("Plugin", pluginConfiguration.getStack().get(0).getName());
        Assert.assertTrue(pluginConfiguration.getStack().get(2).getFlag().isRequisite());
        Assert.assertEquals("Requisite", pluginConfiguration.getStack().get(2).getName());
        /**
         * Third element is a stack which defines two nested plugins.
         */
        Assert.assertTrue(((pluginConfiguration.getStack().get(1)) instanceof AuthorizationStack));
        AuthorizationStack stack = ((AuthorizationStack) (pluginConfiguration.getStack().get(1)));
        Assert.assertTrue(stack.getFlag().isRequired());
        Assert.assertEquals("basic stack", stack.getName());
        Assert.assertEquals(2, stack.getStack().size());
        Assert.assertTrue(((stack.getStack().get(0)) instanceof AuthorizationPlugin));
        Assert.assertEquals("NestedPlugin", stack.getStack().get(0).getName());
        Assert.assertTrue(stack.getStack().get(0).isRequired());
        Assert.assertTrue(((stack.getStack().get(1)) instanceof AuthorizationPlugin));
        Assert.assertEquals("NestedPlugin", stack.getStack().get(1).getName());
        Assert.assertTrue(stack.getStack().get(1).isRequisite());
        AuthorizationPlugin plugin = ((AuthorizationPlugin) (stack.getStack().get(1)));
        Assert.assertTrue(plugin.getSetup().containsKey("key"));
        Assert.assertEquals("value", plugin.getSetup().get("key"));
        Assert.assertTrue(plugin.getSetup().containsKey("plugin"));
        Assert.assertTrue(((plugin.getSetup().get("plugin")) instanceof AuthorizationPlugin));
        Assert.assertEquals(pluginConfiguration.getStack().get(0), plugin.getSetup().get("plugin"));
        /**
         * Fourth element is a stack slightly changed from the previous stack.
         * Only the setup for the particular plugin is changed.
         */
        Assert.assertTrue(((pluginConfiguration.getStack().get(3)) instanceof AuthorizationStack));
        stack = ((AuthorizationStack) (pluginConfiguration.getStack().get(3)));
        Assert.assertTrue(stack.getFlag().isRequired());
        Assert.assertEquals("advanced stack", stack.getName());
        Assert.assertEquals(2, stack.getStack().size());
        Assert.assertTrue(((stack.getStack().get(0)) instanceof AuthorizationPlugin));
        Assert.assertEquals("NestedPlugin", stack.getStack().get(0).getName());
        Assert.assertTrue(stack.getStack().get(0).isRequired());
        Assert.assertTrue(((stack.getStack().get(1)) instanceof AuthorizationPlugin));
        Assert.assertEquals("NestedPlugin", stack.getStack().get(1).getName());
        Assert.assertTrue(stack.getStack().get(1).isRequisite());
        plugin = ((AuthorizationPlugin) (stack.getStack().get(1)));
        Assert.assertTrue(plugin.getSetup().containsKey("key"));
        Assert.assertEquals("other value", plugin.getSetup().get("key"));
        Assert.assertTrue(plugin.getSetup().containsKey("plugin"));
        Assert.assertTrue(((plugin.getSetup().get("plugin")) instanceof AuthorizationPlugin));
        Assert.assertEquals(pluginConfiguration.getStack().get(0), plugin.getSetup().get("plugin"));
        /**
         * Fifth element is a direct copy of the first stack.
         */
        Assert.assertTrue(((pluginConfiguration.getStack().get(4)) instanceof AuthorizationStack));
        stack = ((AuthorizationStack) (pluginConfiguration.getStack().get(4)));
        Assert.assertTrue(stack.getFlag().isRequired());
        Assert.assertEquals("basic stack", stack.getName());
        Assert.assertEquals(2, stack.getStack().size());
        Assert.assertTrue(((stack.getStack().get(0)) instanceof AuthorizationPlugin));
        Assert.assertEquals("NestedPlugin", stack.getStack().get(0).getName());
        Assert.assertTrue(stack.getStack().get(0).isRequired());
        Assert.assertTrue(((stack.getStack().get(1)) instanceof AuthorizationPlugin));
        Assert.assertEquals("NestedPlugin", stack.getStack().get(1).getName());
        Assert.assertTrue(stack.getStack().get(1).isRequisite());
        plugin = ((AuthorizationPlugin) (stack.getStack().get(1)));
        Assert.assertTrue(plugin.getSetup().containsKey("key"));
        Assert.assertEquals("value", plugin.getSetup().get("key"));
        Assert.assertTrue(plugin.getSetup().containsKey("plugin"));
        Assert.assertTrue(((plugin.getSetup().get("plugin")) instanceof AuthorizationPlugin));
        Assert.assertEquals(pluginConfiguration.getStack().get(0), plugin.getSetup().get("plugin"));
    }

    /**
     * Testing invalid flag property.
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test(expected = IOException.class)
    public void testAuthorizationFlagDecodeInvalid() throws IOException {
        String confString = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n" + ((((((((((((((("<java class=\"java.beans.XMLDecoder\" version=\"1.8.0_121\">\n" + " <object class=\"org.opengrok.indexer.configuration.Configuration\">\n") + "\t<void property=\"pluginStack\">\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.opengrok.indexer.authorization.AuthorizationPlugin\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>noflag</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>Plugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t</void>\n") + " </object>\n") + "</java>");
        Configuration.makeXMLStringAsConfiguration(confString);
    }

    /**
     * Testing invalid class names for authorization checks.
     *
     * @throws IOException
     * 		I/O exception
     */
    @Test(expected = IOException.class)
    public void testAuthorizationDecodeInvalid() throws IOException {
        String confString = "<?xml version=\'1.0\' encoding=\'UTF-8\'?>\n" + ((((((((((((((("<java class=\"java.beans.XMLDecoder\" version=\"1.8.0_121\">\n" + " <object class=\"org.opengrok.indexer.configuration.Configuration\">\n") + "\t<void property=\"pluginStack\">\n") + "\t\t<void method=\"add\">\n") + "\t\t\t<object class=\"org.bad.package.authorization.NoCheck\">\n") + "\t\t\t\t<void property=\"flag\">\n") + "\t\t\t\t\t<string>sufficient</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t\t<void property=\"name\">\n") + "\t\t\t\t\t<string>Plugin</string>\n") + "\t\t\t\t</void>\n") + "\t\t\t</object>\n") + "\t\t</void>\n") + "\t</void>\n") + " </object>\n") + "</java>");
        Configuration.makeXMLStringAsConfiguration(confString);
    }

    @Test
    public void testBug3095() throws IOException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        File file = new File("foobar");
        Assert.assertTrue(file.createNewFile());
        Assert.assertFalse(file.isAbsolute());
        instance.setDataRoot(file.getName());
        File f = instance.getDataRootFile();
        Assert.assertNotNull(f);
        Assert.assertEquals("foobar", f.getName());
        Assert.assertTrue(f.isAbsolute());
        Assert.assertTrue(file.delete());
    }

    @Test
    public void testBug3154() throws IOException {
        RuntimeEnvironment instance = RuntimeEnvironment.getInstance();
        File file = File.createTempFile("dataroot", null);
        Assert.assertTrue(file.delete());
        Assert.assertFalse(file.exists());
        instance.setDataRoot(file.getAbsolutePath());
        // The point of this test was to verify that setDataRoot() created
        // the directory, but that logic has been moved as of bug 16986, so
        // expect that the file does not exist.
        Assert.assertFalse(file.exists());
    }

    @Test
    public void testObfuscateEMail() throws IOException {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        // By default, don't obfuscate.
        assertObfuscated(false, env);
        env.setObfuscatingEMailAddresses(true);
        assertObfuscated(true, env);
        env.setObfuscatingEMailAddresses(false);
        assertObfuscated(false, env);
    }

    @Test
    public void isChattyStatusPage() {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        // By default, status page should not be chatty.
        Assert.assertFalse(env.isChattyStatusPage());
        env.setChattyStatusPage(true);
        Assert.assertTrue(env.isChattyStatusPage());
        env.setChattyStatusPage(false);
        Assert.assertFalse(env.isChattyStatusPage());
    }

    /**
     * Verify that getPathRelativeToSourceRoot() returns path relative to
     * source root for both directories and symbolic links.
     *
     * @throws java.io.IOException
     * 		I/O exception
     * @throws ForbiddenSymlinkException
     * 		forbidden symlink exception
     */
    @Test
    public void testGetPathRelativeToSourceRoot() throws IOException, ForbiddenSymlinkException {
        RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        // Create and set source root.
        File sourceRoot = Files.createTempDirectory("src").toFile();
        Assert.assertTrue(sourceRoot.exists());
        Assert.assertTrue(sourceRoot.isDirectory());
        env.setSourceRoot(sourceRoot.getPath());
        // Create directory underneath source root and check.
        String filename = "foo";
        File file = new File(env.getSourceRootFile(), filename);
        file.createNewFile();
        Assert.assertTrue(file.exists());
        Assert.assertEquals(((File.separator) + filename), env.getPathRelativeToSourceRoot(file));
        // Create symlink underneath source root.
        String symlinkName = "symlink";
        Path realDir = Files.createTempDirectory("realdir");
        File symlink = new File(sourceRoot, symlinkName);
        Files.createSymbolicLink(symlink.toPath(), realDir);
        Assert.assertTrue(symlink.exists());
        env.setAllowedSymlinks(new HashSet());
        ForbiddenSymlinkException expex = null;
        try {
            env.getPathRelativeToSourceRoot(symlink);
        } catch (ForbiddenSymlinkException e) {
            expex = e;
        }
        Assert.assertNotNull(("getPathRelativeToSourceRoot() should have thrown " + "IOexception for symlink that is not allowed"), expex);
        // Allow the symlink and retest.
        env.setAllowedSymlinks(new HashSet(Arrays.asList(symlink.getPath())));
        Assert.assertEquals(((File.separator) + symlinkName), env.getPathRelativeToSourceRoot(symlink));
        // cleanup
        IOUtils.removeRecursive(sourceRoot.toPath());
        IOUtils.removeRecursive(realDir);
    }

    @Test
    public void testPopulateGroupsMultipleTimes() {
        // create a structure with two repositories
        final RuntimeEnvironment env = RuntimeEnvironment.getInstance();
        Project project1 = new Project("bar", "/bar");
        env.getProjects().put(project1.getName(), project1);
        Project project2 = new Project("barfoo", "/barfoo");
        env.getProjects().put(project2.getName(), project2);
        final Group group1 = new Group("group1", "bar");
        env.getGroups().add(group1);
        final Group group2 = new Group("group2", "bar.*");
        env.getGroups().add(group2);
        final RepositoryInfo repository1 = new RepositoryInfo();
        repository1.setDirectoryNameRelative("/bar");
        env.getRepositories().add(repository1);
        final RepositoryInfo repo2 = new RepositoryInfo();
        repository1.setDirectoryNameRelative("/barfoo");
        env.getRepositories().add(repo2);
        env.getProjectRepositoriesMap().put(project1, Arrays.asList(repository1));
        env.getProjectRepositoriesMap().put(project2, Arrays.asList(repo2));
        Assert.assertEquals(2, env.getProjects().size());
        Assert.assertEquals(2, env.getRepositories().size());
        Assert.assertEquals(2, env.getProjectRepositoriesMap().size());
        Assert.assertEquals(2, env.getGroups().size());
        // populate groups for the first time
        env.populateGroups(env.getGroups(), new java.util.TreeSet(env.getProjects().values()));
        Assert.assertEquals(2, env.getProjects().size());
        Assert.assertEquals(2, env.getRepositories().size());
        Assert.assertEquals(2, env.getProjectRepositoriesMap().size());
        Assert.assertEquals(2, env.getGroups().size());
        Assert.assertEquals(0, group1.getProjects().size());
        Assert.assertEquals(1, group1.getRepositories().size());
        Assert.assertEquals(0, group2.getProjects().size());
        Assert.assertEquals(2, group2.getRepositories().size());
        // remove a single repository object => project1 will become a simple project
        env.getProjectRepositoriesMap().remove(project1);
        env.getRepositories().remove(repository1);
        // populate groups for the second time
        env.populateGroups(env.getGroups(), new java.util.TreeSet(env.getProjects().values()));
        Assert.assertEquals(2, env.getProjects().size());
        Assert.assertEquals(1, env.getRepositories().size());
        Assert.assertEquals(1, env.getProjectRepositoriesMap().size());
        Assert.assertEquals(2, env.getGroups().size());
        Assert.assertEquals(1, group1.getProjects().size());
        Assert.assertEquals(0, group1.getRepositories().size());
        Assert.assertEquals(1, group2.getProjects().size());
        Assert.assertEquals(1, group2.getRepositories().size());
    }
}

