/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.bytecode.enhancement.lazy;


import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.bytecode.enhancement.BytecodeEnhancerRunner;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


@TestForIssue(jiraKey = "")
@RunWith(BytecodeEnhancerRunner.class)
public class LazyCollectionHandlingTest extends BaseCoreFunctionalTestCase {
    private Integer id;

    @Test
    public void test() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.bytecode.enhancement.lazy.JafSid sid = new org.hibernate.test.bytecode.enhancement.lazy.JafSid();
            s.save(sid);
            s.flush();
            s.clear();
            this.id = sid.getId();
        });
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            s.get(.class, this.id);
        });
    }

    @MappedSuperclass
    public abstract static class DatabaseEntity {
        private int id;

        @Id
        @GeneratedValue
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }

    @Entity(name = "JafSid")
    public static class JafSid extends LazyCollectionHandlingTest.DatabaseEntity {
        private Set<LazyCollectionHandlingTest.UserGroup> groups = new LinkedHashSet<>();

        @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
        public Set<LazyCollectionHandlingTest.UserGroup> getGroups() {
            return groups;
        }

        public void setGroups(Set<LazyCollectionHandlingTest.UserGroup> groups) {
            this.groups = groups;
        }
    }

    @Entity(name = "UserGroup")
    public static class UserGroup extends LazyCollectionHandlingTest.DatabaseEntity {
        private Set<LazyCollectionHandlingTest.JafSid> members = new LinkedHashSet<>();

        @ManyToMany
        public Set<LazyCollectionHandlingTest.JafSid> getMembers() {
            return members;
        }

        public void setMembers(Set<LazyCollectionHandlingTest.JafSid> members) {
            this.members = members;
        }
    }
}

