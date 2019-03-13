package org.javaee7.jpa.index;


import java.io.BufferedReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
public class SchemaGenIndexTest {
    @Test
    public void testSchemaGenIndex() throws Exception {
        Path create = Paths.get("/tmp/index-create.sql");
        Assert.assertTrue(Files.exists(create));
        Path drop = Paths.get("/tmp/index-drop.sql");
        Assert.assertTrue(Files.exists(create));
        String line;
        BufferedReader reader = Files.newBufferedReader(create, Charset.defaultCharset());
        boolean createGenerated = false;
        boolean createIndex = false;
        while ((line = reader.readLine()) != null) {
            if (line.toLowerCase().contains("create table employee_schema_gen_index")) {
                createGenerated = true;
            }
            if (line.toLowerCase().contains("create index")) {
                createIndex = true;
            }
        } 
        reader = Files.newBufferedReader(drop, Charset.defaultCharset());
        boolean dropGenerated = false;
        while ((line = reader.readLine()) != null) {
            if (line.toLowerCase().contains("drop table employee_schema_gen_index")) {
                dropGenerated = true;
                break;
            }
        } 
        Assert.assertTrue(createGenerated);
        Assert.assertTrue(createIndex);
        Assert.assertTrue(dropGenerated);
    }
}

