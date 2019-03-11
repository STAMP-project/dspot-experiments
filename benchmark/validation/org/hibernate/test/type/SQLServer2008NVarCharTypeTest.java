/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.type;


import TargetType.DATABASE;
import java.util.EnumSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Nationalized;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.SQLServer2008Dialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseUnitTestCase;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-10529")
@RequiresDialect(SQLServer2008Dialect.class)
public class SQLServer2008NVarCharTypeTest extends BaseUnitTestCase {
    private StandardServiceRegistry ssr;

    private MetadataImplementor metadata;

    private SchemaExport schemaExport;

    @Test
    public void testSchemaIsCreatedWithoutExceptions() {
        schemaExport.createOnly(EnumSet.of(DATABASE), metadata);
    }

    @Entity(name = "MyEntity")
    @Table(name = "MY_ENTITY")
    public static class MyEntity {
        @Id
        long id;

        @Nationalized
        @Column(length = 4001)
        String name;
    }
}

