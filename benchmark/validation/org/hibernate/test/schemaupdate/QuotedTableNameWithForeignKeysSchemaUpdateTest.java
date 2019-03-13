/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate;


import TargetType.DATABASE;
import TargetType.STDOUT;
import java.util.EnumSet;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10197")
public class QuotedTableNameWithForeignKeysSchemaUpdateTest extends BaseUnitTestCase {
    @Test
    public void testUpdateExistingSchema() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataImplementor metadata = ((MetadataImplementor) (addResource("org/hibernate/test/schemaupdate/UserGroup.hbm.xml").buildMetadata()));
            new SchemaUpdate().execute(EnumSet.of(DATABASE), metadata);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Test
    public void testGeneratingUpdateScript() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataImplementor metadata = ((MetadataImplementor) (addResource("org/hibernate/test/schemaupdate/UserGroup.hbm.xml").buildMetadata()));
            new SchemaUpdate().execute(EnumSet.of(STDOUT), metadata);
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

