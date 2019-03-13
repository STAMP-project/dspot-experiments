/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.filter;


import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
public class ManyToManyWithDynamicFilterTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11410")
    public void testManyToManyCollectionWithActiveFilterOnJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            session.enableFilter("activeUserFilter");
            session.enableFilter("activeRoleFilter");
            final org.hibernate.test.filter.User user = session.get(.class, 1);
            assertNotNull(user);
            assertTrue(user.getRoles().isEmpty());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11410")
    public void testManyToManyCollectionWithNoFilterOnJoin() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( session) -> {
            final org.hibernate.test.filter.User user = session.get(.class, 1);
            assertNotNull(user);
            assertEquals(2, user.getRoles().size());
        });
    }

    @MappedSuperclass
    public abstract static class AbstractEntity implements Serializable {
        @Id
        private Integer id;

        AbstractEntity() {
        }

        AbstractEntity(Integer id) {
            this.id = id;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }
    }

    @Entity(name = "User")
    @Table(name = "`User`")
    @FilterDef(name = "activeUserFilter", defaultCondition = "active = true")
    @Filter(name = "activeUserFilter")
    public static class User extends ManyToManyWithDynamicFilterTest.AbstractEntity {
        private String name;

        private Boolean active;

        @ManyToMany
        @Fetch(FetchMode.JOIN)
        @Filter(name = "activeRoleFilter")
        private Set<ManyToManyWithDynamicFilterTest.Role> roles = new HashSet<>();

        public User() {
        }

        public User(Integer id, String name, Boolean active, ManyToManyWithDynamicFilterTest.Role... roles) {
            super(id);
            this.name = name;
            this.active = active;
            this.roles = new HashSet<>(Arrays.asList(roles));
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }

        public Set<ManyToManyWithDynamicFilterTest.Role> getRoles() {
            return roles;
        }

        public void setRoles(Set<ManyToManyWithDynamicFilterTest.Role> roles) {
            this.roles = roles;
        }
    }

    @Entity(name = "Role")
    @Table(name = "Roles")
    @FilterDef(name = "activeRoleFilter", defaultCondition = "active = true")
    @Filter(name = "activeRoleFilter")
    public static class Role extends ManyToManyWithDynamicFilterTest.AbstractEntity {
        private String name;

        private Boolean active;

        Role() {
        }

        public Role(Integer id, String name, Boolean active) {
            super(id);
            this.name = name;
            this.active = active;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }
}

