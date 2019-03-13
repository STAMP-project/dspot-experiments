/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import java.io.File;
import java.nio.file.Files;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class SchemaMigrationTargetScriptCreationTest extends BaseCoreFunctionalTestCase {
    private File output;

    @Test
    @TestForIssue(jiraKey = "HHH-10684")
    public void testTargetScriptIsCreated() throws Exception {
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
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

