/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * Copyright (c) {DATE}, Red Hat Inc. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.  All third-party contributions are
 * distributed under license by Red Hat Inc.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this distribution; if not, write to:
 * Free Software Foundation, Inc.
 * 51 Franklin Street, Fifth Floor
 * Boston, MA  02110-1301  USA
 */
package org.hibernate.test.schemaupdate;


import TargetType.DATABASE;
import TargetType.STDOUT;
import java.util.EnumSet;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.CustomRunner;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-9849")
@RunWith(CustomRunner.class)
@RequiresDialect(MySQLDialect.class)
public class MixedFieldPropertyAnnotationTest {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void testUpdateSchema() throws Exception {
        new SchemaUpdate().execute(EnumSet.of(STDOUT, DATABASE), metadata);
    }

    @Entity
    @Table(name = "MyEntity")
    class MyEntity {
        @Id
        public int getId() {
            return 0;
        }

        @Column(name = "Ul")
        public int getValue() {
            return 0;
        }

        public void setId(final int _id) {
        }

        public void setValue(int value) {
        }
    }
}

