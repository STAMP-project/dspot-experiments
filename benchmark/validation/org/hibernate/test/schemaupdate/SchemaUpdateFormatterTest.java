package org.hibernate.test.schemaupdate;


import Environment.HBM2DDL_AUTO;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Koen Aers
 */
@RequiresDialect(H2Dialect.class)
@TestForIssue(jiraKey = "HHH-10158")
public class SchemaUpdateFormatterTest extends BaseUnitTestCase {
    private static final String AFTER_FORMAT = "\n\\s+create table test_entity \\(\n" + (("\\s+field varchar\\(255\\) not null,\n" + "\\s+primary key \\(field\\)\n") + "\\s+\\).*?;\n");

    private static final String DELIMITER = ";";

    @Test
    public void testSetFormat() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, "none").build();
        try {
            File output = File.createTempFile("update_script", ".sql");
            output.deleteOnExit();
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(SchemaUpdateFormatterTest.TestEntity.class).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(SchemaUpdateFormatterTest.DELIMITER).setFormat(true).execute(EnumSet.of(SCRIPT), metadata);
            String outputContent = new String(Files.readAllBytes(output.toPath()));
            // Old Macs use \r as a new line delimiter
            outputContent = outputContent.replaceAll("\r", "\n");
            // On Windows, \r\n would become \n\n, so we eliminate duplicates
            outputContent = outputContent.replaceAll("\n\n", "\n");
            Assert.assertTrue(Pattern.compile(SchemaUpdateFormatterTest.AFTER_FORMAT).matcher(outputContent).matches());
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity
    @Table(name = "test_entity")
    public static class TestEntity {
        @Id
        private String field;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}

