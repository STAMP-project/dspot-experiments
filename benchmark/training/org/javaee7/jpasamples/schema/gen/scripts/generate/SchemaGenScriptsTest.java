package org.javaee7.jpasamples.schema.gen.scripts.generate;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Roberto Cortez
 */
@RunWith(Arquillian.class)
public class SchemaGenScriptsTest {
    @Test
    @RunAsClient
    public void testSchemaGenIndex() throws Exception {
        Path create = Paths.get("target", "create-script.sql");
        Path drop = Paths.get("target", "drop-script.sql");
        Assert.assertTrue(Files.exists(create));
        Assert.assertTrue(Files.exists(drop));
        Assert.assertTrue(new String(Files.readAllBytes(create), StandardCharsets.UTF_8).toLowerCase().contains("create table employee_schema_gen_scripts_generate"));
        Assert.assertTrue(new String(Files.readAllBytes(drop), StandardCharsets.UTF_8).toLowerCase().contains("drop table employee_schema_gen_scripts_generate"));
    }
}

