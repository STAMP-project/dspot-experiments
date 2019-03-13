package com.github.jknack.handlebars.maven;


import org.codehaus.plexus.util.FileUtils;
import org.junit.Assert;
import org.junit.Test;


public class Issue230 {
    @Test
    public void issue230() throws Exception {
        I18nJsPlugin plugin = new I18nJsPlugin();
        plugin.setBundle("i230");
        plugin.setProject(newProject("src/test/resources"));
        plugin.setOutput("target");
        plugin.execute();
        Assert.assertEquals(("(function() {\n" + ((((("  /* English (United States) */\n" + "  I18n.translations = I18n.translations || {};\n") + "  I18n.translations[\'en_US\'] = {\n") + "    \"pagination_top_of_page\": \"Inicio de la p\u00e1gina\"\n") + "  };\n") + "})();\n")), FileUtils.fileRead("target/i230.js"));
    }
}

