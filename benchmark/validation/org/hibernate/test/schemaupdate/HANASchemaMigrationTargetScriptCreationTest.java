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
import javax.persistence.Lob;
import javax.persistence.Table;
import org.hamcrest.core.Is;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Jonathan Bregler
 */
@RequiresDialect(AbstractHANADialect.class)
public class HANASchemaMigrationTargetScriptCreationTest extends BaseCoreFunctionalTestCase {
    private File output;

    @Test
    @TestForIssue(jiraKey = "HHH-12302")
    public void testTargetScriptIsCreatedStringTypeDefault() throws Exception {
        this.rebuildSessionFactory();
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity \\(field varchar.+, b boolean.+, c varchar.+, lob clob.+");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12302")
    public void testTargetScriptIsCreatedStringTypeNVarchar() throws Exception {
        rebuildSessionFactory(( config) -> {
            config.setProperty("hibernate.dialect.hana.use_unicode_string_types", "true");
        });
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity \\(field nvarchar.+, b boolean.+, c nvarchar.+, lob nclob");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12302")
    public void testTargetScriptIsCreatedStringTypeVarchar() throws Exception {
        rebuildSessionFactory(( config) -> {
            config.setProperty("hibernate.dialect.hana.use_nvarchar_string_type", "false");
        });
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity \\(field varchar.+, b boolean.+, c varchar.+, lob clob");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12132")
    public void testTargetScriptIsCreatedBooleanTypeDefault() throws Exception {
        this.rebuildSessionFactory();
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity \\(field varchar.+, b boolean.+, c varchar.+, lob clob");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12132")
    public void testTargetScriptIsCreatedBooleanTypeLegacy() throws Exception {
        rebuildSessionFactory(( config) -> {
            config.setProperty("hibernate.dialect.hana.use_legacy_boolean_type", "true");
        });
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity \\(field varchar.+, b tinyint.+, c varchar.+, lob clob");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12132")
    public void testTargetScriptIsCreatedBooleanType() throws Exception {
        rebuildSessionFactory(( config) -> {
            config.setProperty("hibernate.dialect.hana.use_legacy_boolean_type", "false");
        });
        String fileContent = new String(Files.readAllBytes(output.toPath()));
        Pattern fileContentPattern = Pattern.compile("create( (column|row))? table test_entity \\(field varchar.+, b boolean.+, c varchar.+, lob clob");
        Matcher fileContentMatcher = fileContentPattern.matcher(fileContent.toLowerCase());
        Assert.assertThat(("Script file : " + (fileContent.toLowerCase())), fileContentMatcher.find(), Is.is(true));
    }

    @Entity
    @Table(name = "test_entity")
    public static class TestEntity {
        @Id
        private String field;

        private char c;

        @Lob
        private String lob;

        private boolean b;
    }
}

