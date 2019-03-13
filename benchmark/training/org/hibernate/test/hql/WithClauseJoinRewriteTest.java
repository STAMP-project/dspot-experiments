/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.MappedSuperclass;
import javax.persistence.Table;
import javax.persistence.Version;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Test;


/**
 * Implementation of WithClauseTest.
 *
 * @author Steve Ebersole
 */
public class WithClauseJoinRewriteTest extends BaseCoreFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-11230")
    public void testInheritanceReAliasing() {
        Session s = openSession();
        Transaction tx = s.beginTransaction();
        // Just assert that the query is successful
        List<Object[]> results = s.createQuery(((((((("SELECT usedBy.id, usedBy.name, COUNT(inverse.id) " + "FROM ") + (WithClauseJoinRewriteTest.AbstractConfigurationObject.class.getName())) + " config ") + "INNER JOIN config.usedBy usedBy ") + "LEFT JOIN usedBy.uses inverse ON inverse.id = config.id ") + "WHERE config.id = 0 ") + "GROUP BY usedBy.id, usedBy.name"), Object[].class).getResultList();
        tx.commit();
        s.close();
    }

    @Entity
    @Table(name = "config")
    @Inheritance(strategy = InheritanceType.JOINED)
    public abstract static class AbstractConfigurationObject<T extends WithClauseJoinRewriteTest.ConfigurationObject> extends WithClauseJoinRewriteTest.AbstractObject {
        private String name;

        private Set<WithClauseJoinRewriteTest.ConfigurationObject> uses = new HashSet<>();

        private Set<WithClauseJoinRewriteTest.ConfigurationObject> usedBy = new HashSet<>();

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @ManyToMany(targetEntity = WithClauseJoinRewriteTest.AbstractConfigurationObject.class, fetch = FetchType.LAZY, cascade = {  })
        public Set<WithClauseJoinRewriteTest.ConfigurationObject> getUses() {
            return uses;
        }

        public void setUses(Set<WithClauseJoinRewriteTest.ConfigurationObject> uses) {
            this.uses = uses;
        }

        @ManyToMany(targetEntity = WithClauseJoinRewriteTest.AbstractConfigurationObject.class, fetch = FetchType.LAZY, mappedBy = "uses", cascade = {  })
        public Set<WithClauseJoinRewriteTest.ConfigurationObject> getUsedBy() {
            return usedBy;
        }

        public void setUsedBy(Set<WithClauseJoinRewriteTest.ConfigurationObject> usedBy) {
            this.usedBy = usedBy;
        }
    }

    @Entity
    @Table(name = "config_config")
    public static class ConfigurationObject extends WithClauseJoinRewriteTest.AbstractConfigurationObject<WithClauseJoinRewriteTest.ConfigurationObject> {}

    @MappedSuperclass
    public static class AbstractObject {
        private Long id;

        private Long version;

        @Id
        @GeneratedValue
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        @Version
        @Column(nullable = false)
        public Long getVersion() {
            return version;
        }

        public void setVersion(Long version) {
            this.version = version;
        }
    }
}

