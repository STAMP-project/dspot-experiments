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
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hamcrest.core.IsNot;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.dialect.MySQLDialect;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.CustomRunner;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 *
 *
 * @author Andrea Boriero
 */
@TestForIssue(jiraKey = "HHH-9866")
@RunWith(CustomRunner.class)
@RequiresDialect(MySQLDialect.class)
public class SchemaExportWithGlobalQuotingEnabledTest {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void testSchemaExport() throws Exception {
        SchemaExport schemaExport = new SchemaExport();
        schemaExport.create(EnumSet.of(STDOUT, DATABASE), metadata);
        List<SQLException> exceptions = schemaExport.getExceptions();
        for (SQLException exception : exceptions) {
            Assert.assertThat(exception.getMessage(), exception.getSQLState(), IsNot.not("42000"));
        }
    }

    @Entity
    @Table(name = "MyEntity")
    public static class MyEntity {
        private int id;

        private Set<SchemaExportWithGlobalQuotingEnabledTest.Role> roles;

        @Id
        public int getId() {
            return this.id;
        }

        public void setId(final int id) {
            this.id = id;
        }

        @ManyToMany
        public Set<SchemaExportWithGlobalQuotingEnabledTest.Role> getRoles() {
            return roles;
        }

        public void setRoles(Set<SchemaExportWithGlobalQuotingEnabledTest.Role> roles) {
            this.roles = roles;
        }
    }

    @Entity
    public static class Role {
        private Integer id;

        @Id
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

