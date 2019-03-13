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
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.junit.Test;


/**
 *
 *
 * @author Steve Ebersole
 */
public class TestFkUpdating {
    protected ServiceRegistry serviceRegistry;

    protected MetadataImplementor metadata;

    @Test
    public void testUpdate() {
        System.out.println("********* Starting SchemaUpdate for TEST *************************");
        new SchemaUpdate().execute(EnumSet.of(DATABASE, STDOUT), metadata);
        System.out.println("********* Completed SchemaUpdate for TEST *************************");
    }

    @Entity(name = "User")
    @Table(name = "my_user")
    public static class User {
        private Integer id;

        private Set<TestFkUpdating.Role> roles;

        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        @ManyToMany
        public Set<TestFkUpdating.Role> getRoles() {
            return roles;
        }

        public void setRoles(Set<TestFkUpdating.Role> roles) {
            this.roles = roles;
        }
    }

    @Entity(name = "Role")
    @Table(name = "`Role`")
    public class Role {
        private Integer id;

        @Id
        @GeneratedValue(generator = "increment")
        @GenericGenerator(name = "increment", strategy = "increment")
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }
}

