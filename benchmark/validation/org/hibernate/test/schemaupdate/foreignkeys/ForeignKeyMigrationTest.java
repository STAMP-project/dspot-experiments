/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.schemaupdate.foreignkeys;


import DialectChecks.SupportCatalogCreation;
import TargetType.DATABASE;
import java.util.EnumSet;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.testing.RequiresDialectFeature;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
@RequiresDialectFeature({ SupportCatalogCreation.class })
public class ForeignKeyMigrationTest extends BaseUnitTestCase {
    // @FailureExpected( jiraKey = "HHH-9716" )
    @Test
    @TestForIssue(jiraKey = "HHH-9716")
    public void testMigrationOfForeignKeys() {
        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().build();
        try {
            final MetadataImplementor metadata = ((MetadataImplementor) (addAnnotatedClass(ForeignKeyMigrationTest.Thing.class).buildMetadata()));
            metadata.validate();
            // first create the schema...
            new SchemaExport().create(EnumSet.of(DATABASE), metadata);
            try {
                // try to update the just created schema
                new SchemaUpdate().execute(EnumSet.of(DATABASE), metadata);
            } finally {
                // clean up
                new SchemaExport().drop(EnumSet.of(DATABASE), metadata);
            }
        } finally {
            StandardServiceRegistryBuilder.destroy(ssr);
        }
    }

    @Entity(name = "Box")
    @Table(name = "Box", schema = "PUBLIC", catalog = "DB1")
    public static class Box {
        @Id
        public Integer id;

        @ManyToOne
        @JoinColumn
        public ForeignKeyMigrationTest.Thing thing1;
    }

    @Entity(name = "Thing")
    @Table(name = "Thing", schema = "PUBLIC", catalog = "DB1")
    public static class Thing {
        @Id
        public Integer id;
    }
}

