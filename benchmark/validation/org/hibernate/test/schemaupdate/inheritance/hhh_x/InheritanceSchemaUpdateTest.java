/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.inheritance.hhh_x;


import DialectChecks.SupportsIdentityColumns;
import TargetType.DATABASE;
import java.util.EnumSet;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@RequiresDialectFeature(SupportsIdentityColumns.class)
public class InheritanceSchemaUpdateTest extends BaseUnitTestCase {
    @Test
    public void testBidirectionalOneToManyReferencingRootEntity() throws Exception {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(GroupStep.class).buildMetadata()));
        metadata.validate();
        try {
            try {
                new SchemaUpdate().execute(EnumSet.of(DATABASE), metadata);
            } finally {
                new SchemaExport().drop(EnumSet.of(DATABASE), metadata);
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }
}

