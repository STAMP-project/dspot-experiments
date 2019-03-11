/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.schemagen;


import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.testing.TestForIssue;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class SchemaScriptFileGenerationTest {
    private File createSchema;

    private File dropSchema;

    private EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Test
    @TestForIssue(jiraKey = "10601")
    public void testGenerateSchemaDoesNotProduceTheSameStatementTwice() throws Exception {
        entityManagerFactoryBuilder.generateSchema();
        final String fileContent = new String(Files.readAllBytes(createSchema.toPath())).toLowerCase();
        Pattern createStatementPattern = Pattern.compile("create( (column|row))? table test_entity");
        Matcher createStatementMatcher = createStatementPattern.matcher(fileContent);
        Assert.assertThat(createStatementMatcher.find(), Is.is(true));
        Assert.assertThat("The statement 'create table test_entity' is generated twice", createStatementMatcher.find(), Is.is(false));
        final String dropFileContent = new String(Files.readAllBytes(dropSchema.toPath())).toLowerCase();
        Assert.assertThat(dropFileContent.contains("drop table "), Is.is(true));
        Assert.assertThat("The statement 'drop table ' is generated twice", dropFileContent.replaceFirst("drop table ", "").contains("drop table "), Is.is(false));
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

