/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.schemagen;


import java.io.IOException;
import java.io.Writer;
import java.util.regex.Pattern;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import javax.persistence.Table;
import org.hibernate.jpa.boot.spi.EntityManagerFactoryBuilder;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.schema.spi.CommandAcceptanceException;
import org.hibernate.tool.schema.spi.SchemaManagementException;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Vlad Mihalcea
 */
public class SchemaScriptFileGenerationFailureTest {
    private Writer writer;

    private EntityManagerFactoryBuilder entityManagerFactoryBuilder;

    @Test
    @TestForIssue(jiraKey = "HHH-12192")
    public void testErrorMessageContainsTheFailingDDLCommand() {
        try {
            entityManagerFactoryBuilder.generateSchema();
            Assert.fail("Should haave thrown IOException");
        } catch (Exception e) {
            Assert.assertTrue((e instanceof PersistenceException));
            Assert.assertTrue(((e.getCause()) instanceof SchemaManagementException));
            Assert.assertTrue(((e.getCause().getCause()) instanceof CommandAcceptanceException));
            CommandAcceptanceException commandAcceptanceException = ((CommandAcceptanceException) (e.getCause().getCause()));
            boolean ddlCommandFound = Pattern.compile("drop table( if exists)? test_entity( if exists)?").matcher(commandAcceptanceException.getMessage().toLowerCase()).find();
            Assert.assertTrue("The Exception Message does not contain the DDL command causing the failure", ddlCommandFound);
            Assert.assertTrue(((commandAcceptanceException.getCause()) instanceof IOException));
            IOException root = ((IOException) (e.getCause().getCause().getCause()));
            Assert.assertEquals("Expected", root.getMessage());
        }
    }

    @Entity
    @Table(name = "test_entity")
    public static class TestEntity {
        @Id
        private String field;

        private String table;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }

    public class FailingWriter extends Writer {
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }

        @Override
        public void flush() throws IOException {
            throw new IOException("Expected");
        }

        @Override
        public void close() throws IOException {
        }
    }
}

