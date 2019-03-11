/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import Environment.HBM2DDL_AUTO;
import TargetType.SCRIPT;
import java.io.File;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-1122")
public class SchemaUpdateDelimiterTest {
    public static final String EXPECTED_DELIMITER = ";";

    @Test
    public void testSchemaUpdateApplyDelimiterToGeneratedSQL() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, "none").build();
        try {
            File output = File.createTempFile("update_script", ".sql");
            output.deleteOnExit();
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(SchemaUpdateDelimiterTest.TestEntity.class).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(SchemaUpdateDelimiterTest.EXPECTED_DELIMITER).setFormat(false).execute(EnumSet.of(SCRIPT), metadata);
            List<String> sqlLines = Files.readAllLines(output.toPath(), Charset.defaultCharset());
            for (String line : sqlLines) {
                Assert.assertThat(("The expected delimiter is not applied " + line), line.endsWith(SchemaUpdateDelimiterTest.EXPECTED_DELIMITER), Is.is(true));
            }
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

