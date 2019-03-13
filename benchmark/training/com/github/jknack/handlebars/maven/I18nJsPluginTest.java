package com.github.jknack.handlebars.maven;


import java.io.File;
import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class I18nJsPluginTest {
    @Test
    public void i18nJsNoMerge() throws Exception {
        I18nJsPlugin plugin = new I18nJsPlugin();
        plugin.setProject(newProject("src/test/resources"));
        plugin.setOutput("target");
        plugin.execute();
        Assert.assertEquals(tokens("src/test/resources/messages.expected.js"), tokens("target/messages.js"));
        Assert.assertEquals(tokens("src/test/resources/messages_es_AR.expected.js"), tokens("target/messages_es_AR.js"));
        FileUtils.copyFile(new File("target/messages.js"), new File("target/messages-tests.js"));
    }

    @Test
    public void i18nJsNoMergeDeepPath() throws Exception {
        I18nJsPlugin plugin = new I18nJsPlugin();
        plugin.setProject(newProject("src/test/resources/"));
        plugin.setBundle("deep.path.deep");
        plugin.setOutput("target");
        plugin.execute();
        Assert.assertEquals(FileUtils.fileRead("src/test/resources/deep.expected.js"), FileUtils.fileRead("target/deep.js"));
    }

    @Test
    public void i18nJsNoMergeAmd() throws Exception {
        I18nJsPlugin plugin = new I18nJsPlugin();
        plugin.setProject(newProject("src/test/resources"));
        plugin.setOutput("target");
        plugin.setAmd(true);
        plugin.execute();
        Assert.assertEquals(tokens("src/test/resources/messages-amd.expected.js"), tokens("target/messages.js"));
        Assert.assertEquals(tokens("src/test/resources/messages_es_AR-amd.expected.js"), tokens("target/messages_es_AR.js"));
    }

    @Test
    public void i18nJsWithMerge() throws Exception {
        I18nJsPlugin plugin = new I18nJsPlugin();
        plugin.setProject(newProject("src/test/resources"));
        plugin.setOutput("target");
        plugin.setMerge(true);
        plugin.execute();
        Assert.assertEquals(tokens("src/test/resources/messages-merged.js"), tokens("target/messages.js"));
    }

    @Test
    public void i18nJsWithMergeAmd() throws Exception {
        I18nJsPlugin plugin = new I18nJsPlugin();
        plugin.setProject(newProject("src/test/resources"));
        plugin.setOutput("target");
        plugin.setMerge(true);
        plugin.setAmd(true);
        plugin.execute();
        Assert.assertEquals(tokens("src/test/resources/messages-merged-amd.js"), tokens("target/messages.js"));
    }
}

