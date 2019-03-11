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
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseNonConfigCoreFunctionalTestCase;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-1872")
@RequiresDialect(PostgreSQL81Dialect.class)
public class SchemaUpdateWithViewsTest extends BaseNonConfigCoreFunctionalTestCase {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void testUpdateSchema() {
        new SchemaUpdate().execute(EnumSet.of(DATABASE, STDOUT), metadata);
    }

    @Entity
    @Table(name = "MyEntity", indexes = { @Index(columnList = "id", name = "user_id_hidx") })
    public static class MyEntity {
        private int id;

        @Id
        public int getId() {
            return this.id;
        }

        public void setId(final int id) {
            this.id = id;
        }
    }
}

