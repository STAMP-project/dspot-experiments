/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.uniqueconstraint;


import TargetType.SCRIPT;
import java.io.File;
import java.nio.charset.Charset;
import java.util.EnumSet;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.DefaultSchemaFilter;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.internal.exec.ScriptTargetOutputToFile;
import org.hibernate.tool.schema.spi.ExecutionOptions;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class UniqueConstraintDropTest {
    private File output;

    private MetadataImplementor metadata;

    private StandardServiceRegistry ssr;

    private HibernateSchemaManagementTool tool;

    private ExecutionOptions options;

    @Test
    @TestForIssue(jiraKey = "HHH-11236")
    public void testUniqueConstraintIsGenerated() throws Exception {
        new org.hibernate.tool.schema.internal.IndividuallySchemaMigratorImpl(tool, DefaultSchemaFilter.INSTANCE).doMigration(metadata, options, new UniqueConstraintDropTest.TargetDescriptorImpl());
        if ((getDialect()) instanceof MySQLDialect) {
            Assert.assertThat("The test_entity_item table unique constraint has not been dropped", checkDropIndex("test_entity_item", "item"), Is.is(true));
        } else
            if ((getDialect()) instanceof DB2Dialect) {
                checkDB2DropIndex("test_entity_item", "item");
            } else {
                Assert.assertThat("The test_entity_item table unique constraint has not been dropped", checkDropConstraint("test_entity_item", "item"), Is.is(true));
            }

    }

    private class TargetDescriptorImpl implements TargetDescriptor {
        public EnumSet<TargetType> getTargetTypes() {
            return EnumSet.of(SCRIPT);
        }

        @Override
        public ScriptTargetOutput getScriptTargetOutput() {
            return new ScriptTargetOutputToFile(output, Charset.defaultCharset().name());
        }
    }
}

