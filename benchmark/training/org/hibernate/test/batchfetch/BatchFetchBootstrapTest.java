package org.hibernate.test.batchfetch;


import java.util.LinkedHashSet;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


public class BatchFetchBootstrapTest extends BaseCoreFunctionalTestCase {
    @Test
    public void test() {
        buildSessionFactory();
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
    public static class JafSid extends BatchFetchBootstrapTest.DatabaseEntity {
        private Set<BatchFetchBootstrapTest.UserGroup> groups = new LinkedHashSet<>();

        @ManyToMany(mappedBy = "members", fetch = FetchType.EAGER)
        public Set<BatchFetchBootstrapTest.UserGroup> getGroups() {
            return groups;
        }

        public void setGroups(Set<BatchFetchBootstrapTest.UserGroup> groups) {
            this.groups = groups;
        }
    }

    @Entity(name = "UserGroup")
    public static class UserGroup extends BatchFetchBootstrapTest.DatabaseEntity {
        private Set<BatchFetchBootstrapTest.JafSid> members = new LinkedHashSet<>();

        @ManyToMany
        public Set<BatchFetchBootstrapTest.JafSid> getMembers() {
            return members;
        }

        public void setMembers(Set<BatchFetchBootstrapTest.JafSid> members) {
            this.members = members;
        }
    }
}

