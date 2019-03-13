/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.uniqueconstraint;


import TargetType.SCRIPT;
import java.io.File;
import java.util.EnumSet;
import org.hamcrest.core.Is;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.DB2Dialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
public class UniqueConstraintGenerationTest {
    private File output;

    private MetadataImplementor metadata;

    StandardServiceRegistry ssr;

    @Test
    @TestForIssue(jiraKey = "HHH-11101")
    public void testUniqueConstraintIsGenerated() throws Exception {
        new SchemaExport().setOutputFile(output.getAbsolutePath()).create(EnumSet.of(SCRIPT), metadata);
        if ((getDialect()) instanceof DB2Dialect) {
            Assert.assertThat("The test_entity_item table unique constraint has not been generated", isCreateUniqueIndexGenerated("test_entity_item", "item"), Is.is(true));
        } else {
            Assert.assertThat("The test_entity_item table unique constraint has not been generated", isUniqueConstraintGenerated("test_entity_item", "item"), Is.is(true));
        }
        Assert.assertThat("The test_entity_children table unique constraint has not been generated", isUniqueConstraintGenerated("test_entity_children", "child"), Is.is(true));
    }
}

