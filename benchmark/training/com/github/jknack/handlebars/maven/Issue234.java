package com.github.jknack.handlebars.maven;


import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class Issue234 {
    @Test
    public void withAmdOutput() throws Exception {
        PrecompilePlugin plugin = new PrecompilePlugin();
        plugin.setPrefix("src/test/resources/templates");
        plugin.setSuffix(".hbs");
        plugin.setOutput("target/issue234.js");
        plugin.addTemplate("a");
        plugin.addTemplate("c");
        plugin.setAmd(true);
        plugin.setProject(newProject());
        plugin.setHandlebarsJsFile("/handlebars-v4.0.4.js");
        plugin.execute();
        Assert.assertEquals(FileUtils.fileRead("src/test/resources/issue234.expected.js"), FileUtils.fileRead("target/issue234.js"));
    }
}

