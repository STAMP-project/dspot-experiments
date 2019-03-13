/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import junit.framework.TestCase;
import org.hamcrest.core.Is;
import org.hibernate.annotations.Table;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class TableCommentTest extends BaseNonConfigCoreFunctionalTestCase {
    private File output;

    @Test
    @TestForIssue(jiraKey = "HHH-10451")
    public void testCommentOnTableStatementIsGenerated() throws IOException {
        createSchema(new Class[]{ TableCommentTest.TableWithComment.class });
        final List<String> sqlLines = Files.readAllLines(output.toPath(), Charset.defaultCharset());
        boolean found = false;
        final String tableName = getTableName();
        for (String sqlStatement : sqlLines) {
            if (sqlStatement.toLowerCase().equals((("comment on table " + (tableName.toLowerCase())) + " is 'comment snippet'"))) {
                if (getDialect().supportsCommentOn()) {
                    found = true;
                } else {
                    TestCase.fail((("Generated " + sqlStatement) + "  statement, but Dialect does not support it"));
                }
            }
            if (containsCommentInCreateTableStatement(sqlStatement)) {
                if ((getDialect().supportsCommentOn()) && (!(getDialect().getTableComment("comment snippet").equals("")))) {
                    TestCase.fail("Added comment on create table statement when Dialect support create comment on table statement");
                } else {
                    found = true;
                }
            }
        }
        Assert.assertThat("Table Comment Statement not correctly generated", found, Is.is(true));
    }

    @Entity(name = "TableWithComment")
    @Table(name = "TABLE_WITH_COMMENT")
    @Table(appliesTo = "TABLE_WITH_COMMENT", comment = "comment snippet")
    public static class TableWithComment {
        @Id
        private int id;
    }
}

