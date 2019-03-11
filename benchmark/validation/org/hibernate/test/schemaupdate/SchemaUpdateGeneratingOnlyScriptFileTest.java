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
import java.nio.file.Files;
import java.util.EnumSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
@TestForIssue(jiraKey = "10180")
public class SchemaUpdateGeneratingOnlyScriptFileTest {
    @Test
    public void testSchemaUpdateScriptGeneration() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().applySetting(HBM2DDL_AUTO, "none").build();
        try {
            File output = File.createTempFile("update_script", ".sql");
            output.deleteOnExit();
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(SchemaUpdateGeneratingOnlyScriptFileTest.TestEntity.class).buildMetadata()));
            metadata.validate();
            new SchemaUpdate().setHaltOnError(true).setOutputFile(output.getAbsolutePath()).setDelimiter(";").setFormat(true).execute(EnumSet.of(SCRIPT), metadata);
            String fileContent = new String(Files.readAllBytes(output.toPath()));
            Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity");
            Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
            Assert.assertThat(fileContentMatcher.find(), Is.is(true));
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

