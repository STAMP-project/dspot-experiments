/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.envers.test.integration.collection;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.PostgreSQL81Dialect;
import org.hibernate.envers.Audited;
import org.hibernate.envers.test.BaseEnversJPAFunctionalTestCase;
import org.hibernate.envers.test.Priority;
import org.hibernate.envers.test.tools.TestTools;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Chris Cranford
 */
@TestForIssue(jiraKey = "HHH-9834")
@SkipForDialect(Oracle8iDialect.class)
@SkipForDialect(value = PostgreSQL81Dialect.class, jiraKey = "HHH-11477", comment = "@Lob field in HQL predicate fails with error about text = bigint")
@SkipForDialect(value = AbstractHANADialect.class, comment = "HANA doesn't support comparing LOBs with the = operator")
public class StringMapLobTest extends BaseEnversJPAFunctionalTestCase {
    @Test
    @Priority(10)
    public void initData() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.Simple simple = new org.hibernate.envers.test.integration.collection.Simple(1, "Simple");
            simple.getEmbeddedMap().put("1", "One");
            simple.getEmbeddedMap().put("2", "Two");
            entityManager.persist(simple);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.Simple simple = entityManager.find(.class, 1);
            simple.getEmbeddedMap().put("3", "Three");
            entityManager.merge(simple);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.Simple simple = entityManager.find(.class, 1);
            simple.getEmbeddedMap().remove("1");
            simple.getEmbeddedMap().remove("2");
            entityManager.merge(simple);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.Simple simple = entityManager.find(.class, 1);
            simple.getEmbeddedMap().remove("3");
            simple.getEmbeddedMap().put("3", "Three-New");
            entityManager.merge(simple);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            final org.hibernate.envers.test.integration.collection.Simple simple = entityManager.find(.class, 1);
            simple.getEmbeddedMap().clear();
            entityManager.merge(simple);
        });
    }

    @Test
    public void testRevisionCounts() {
        Assert.assertEquals(Arrays.asList(1, 2, 3, 4, 5), getAuditReader().getRevisions(StringMapLobTest.Simple.class, 1));
    }

    @Test
    public void testRevisionHistory() {
        final StringMapLobTest.Simple rev1 = getAuditReader().find(StringMapLobTest.Simple.class, 1, 1);
        Assert.assertEquals(2, rev1.getEmbeddedMap().entrySet().size());
        TestTools.assertCollectionsEqual(TestTools.<String, String>mapBuilder().add("1", "One").add("2", "Two").entries(), rev1.getEmbeddedMap().entrySet());
        final StringMapLobTest.Simple rev2 = getAuditReader().find(StringMapLobTest.Simple.class, 1, 2);
        Assert.assertEquals(3, rev2.getEmbeddedMap().entrySet().size());
        TestTools.assertCollectionsEqual(TestTools.<String, String>mapBuilder().add("1", "One").add("2", "Two").add("3", "Three").entries(), rev2.getEmbeddedMap().entrySet());
        final StringMapLobTest.Simple rev3 = getAuditReader().find(StringMapLobTest.Simple.class, 1, 3);
        Assert.assertEquals(1, rev3.getEmbeddedMap().entrySet().size());
        TestTools.assertCollectionsEqual(TestTools.<String, String>mapBuilder().add("3", "Three").entries(), rev3.getEmbeddedMap().entrySet());
        final StringMapLobTest.Simple rev4 = getAuditReader().find(StringMapLobTest.Simple.class, 1, 4);
        TestTools.assertCollectionsEqual(TestTools.<String, String>mapBuilder().add("3", "Three-New").entries(), rev4.getEmbeddedMap().entrySet());
        final StringMapLobTest.Simple rev5 = getAuditReader().find(StringMapLobTest.Simple.class, 1, 5);
        Assert.assertEquals(0, rev5.getEmbeddedMap().size());
    }

    @Entity(name = "Simple")
    @Audited
    public static class Simple {
        @Id
        private Integer id;

        private String name;

        @ElementCollection
        @Lob
        private Map<String, String> embeddedMap = new HashMap<String, String>();

        Simple() {
        }

        Simple(Integer id, String name) {
            this.id = id;
            this.name = name;
        }

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Map<String, String> getEmbeddedMap() {
            return embeddedMap;
        }

        public void setEmbeddedMap(Map<String, String> embeddedMap) {
            this.embeddedMap = embeddedMap;
        }
    }
}

