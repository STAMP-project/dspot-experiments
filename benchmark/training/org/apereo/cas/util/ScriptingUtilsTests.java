package org.apereo.cas.util;


import java.io.File;
import java.nio.charset.StandardCharsets;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.util.scripting.ScriptingUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ScriptingUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
public class ScriptingUtilsTests {
    @Test
    public void verifyInlineGroovyScript() {
        Assertions.assertTrue(ScriptingUtils.isInlineGroovyScript("groovy {return 0}"));
    }

    @Test
    public void verifyExternalGroovyScript() {
        Assertions.assertTrue(ScriptingUtils.isExternalGroovyScript("file:/tmp/sample.groovy"));
    }

    @Test
    public void verifyGroovyScriptShellExecution() {
        val result = ScriptingUtils.executeGroovyShellScript("return name", CollectionUtils.wrap("name", "casuser"), String.class);
        Assertions.assertEquals("casuser", result);
    }

    @Test
    @SneakyThrows
    public void verifyGroovyResourceExecution() {
        val file = File.createTempFile("test", ".groovy");
        FileUtils.write(file, "def process(String name) { return name }", StandardCharsets.UTF_8);
        val resource = new org.springframework.core.io.FileSystemResource(file);
        val result = ScriptingUtils.executeGroovyScript(resource, "process", String.class, "casuser");
        Assertions.assertEquals("casuser", result);
    }

    @Test
    public void verifyGroovyResourceEngineExecution() {
        val result = ScriptingUtils.executeGroovyScriptEngine("return name", CollectionUtils.wrap("name", "casuser"), String.class);
        Assertions.assertEquals("casuser", result);
    }

    @Test
    @SneakyThrows
    public void verifyResourceScriptEngineExecution() {
        val file = File.createTempFile("test", ".groovy");
        FileUtils.write(file, "def run(String name) { return name }", StandardCharsets.UTF_8);
        val result = ScriptingUtils.executeScriptEngine(file.getCanonicalPath(), new Object[]{ "casuser" }, String.class);
        Assertions.assertEquals("casuser", result);
    }
}

