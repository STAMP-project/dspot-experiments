/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.dialect.HANAColumnStoreDialect;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 * Tests the correctness of the SAP HANA fulltext-search functions.
 *
 * @author Jonathan Bregler
 */
@RequiresDialect({ HANAColumnStoreDialect.class })
public class HANASearchTest extends BaseCoreFunctionalTestCase {
    private static final String ENTITY_NAME = "SearchEntity";

    @Test
    @TestForIssue(jiraKey = "HHH-13021")
    public void testTextType() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.dialect.functional.SearchEntity entity = new org.hibernate.test.dialect.functional.SearchEntity();
            entity.key = Integer.valueOf(1);
            entity.t = "TEST TEXT";
            entity.c = "TEST STRING";
            s.persist(entity);
            s.flush();
            Query<Object[]> legacyQuery = s.createQuery((("select b, snippets(t), highlighted(t), score() from " + (ENTITY_NAME)) + " b where contains(b.t, 'text') = contains_rhs()"), .class);
            Object[] result = legacyQuery.getSingleResult();
            org.hibernate.test.dialect.functional.SearchEntity retrievedEntity = ((org.hibernate.test.dialect.functional.SearchEntity) (result[0]));
            assertEquals(4, result.length);
            assertEquals(Integer.valueOf(1), retrievedEntity.key);
            assertEquals("TEST TEXT", retrievedEntity.t);
            assertEquals("TEST STRING", retrievedEntity.c);
            assertEquals("TEST <b>TEXT</b>", result[1]);
            assertEquals("TEST <b>TEXT</b>", result[2]);
            assertEquals(0.75, result[3]);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13021")
    public void testTextTypeFalse() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.dialect.functional.SearchEntity entity = new org.hibernate.test.dialect.functional.SearchEntity();
            entity.key = Integer.valueOf(1);
            entity.t = "TEST TEXT";
            entity.c = "TEST STRING";
            s.persist(entity);
            s.flush();
            Query<Object[]> legacyQuery = s.createQuery((("select b, snippets(t), highlighted(t), score() from " + (ENTITY_NAME)) + " b where not_contains(b.t, 'string') = contains_rhs()"), .class);
            Object[] result = legacyQuery.getSingleResult();
            org.hibernate.test.dialect.functional.SearchEntity retrievedEntity = ((org.hibernate.test.dialect.functional.SearchEntity) (result[0]));
            assertEquals(4, result.length);
            assertEquals(Integer.valueOf(1), retrievedEntity.key);
            assertEquals("TEST TEXT", retrievedEntity.t);
            assertEquals("TEST STRING", retrievedEntity.c);
            assertEquals("TEST TEXT", result[1]);
            assertEquals("TEST TEXT", result[2]);
            assertEquals(1.0, result[3]);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13021")
    public void testCharType() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.dialect.functional.SearchEntity entity = new org.hibernate.test.dialect.functional.SearchEntity();
            entity.key = Integer.valueOf(1);
            entity.t = "TEST TEXT";
            entity.c = "TEST STRING";
            s.persist(entity);
            s.getTransaction().commit();
            s.beginTransaction();
            Query<Object[]> legacyQuery = s.createQuery((("select b, snippets(c), highlighted(c), score() from " + (ENTITY_NAME)) + " b where contains(b.c, 'string') = contains_rhs()"), .class);
            Object[] result = legacyQuery.getSingleResult();
            org.hibernate.test.dialect.functional.SearchEntity retrievedEntity = ((org.hibernate.test.dialect.functional.SearchEntity) (result[0]));
            assertEquals(4, result.length);
            assertEquals(Integer.valueOf(1), retrievedEntity.key);
            assertEquals("TEST TEXT", retrievedEntity.t);
            assertEquals("TEST STRING", retrievedEntity.c);
            assertEquals("TEST <b>STRING</b>", result[1]);
            assertEquals("TEST <b>STRING</b>", result[2]);
            assertEquals(0.75, result[3]);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13021")
    public void testCharTypeComplexQuery() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.dialect.functional.SearchEntity entity = new org.hibernate.test.dialect.functional.SearchEntity();
            entity.key = Integer.valueOf(1);
            entity.t = "TEST TEXT";
            entity.c = "TEST STRING";
            s.persist(entity);
            s.flush();
            s.getTransaction().commit();
            s.beginTransaction();
            Query<Object[]> legacyQuery = s.createQuery((("select b, snippets(c), highlighted(c), score() from " + (ENTITY_NAME)) + " b where contains(b.c, 'string') = contains_rhs() and key=1 and score() > 0.5"), .class);
            Object[] result = legacyQuery.getSingleResult();
            org.hibernate.test.dialect.functional.SearchEntity retrievedEntity = ((org.hibernate.test.dialect.functional.SearchEntity) (result[0]));
            assertEquals(4, result.length);
            assertEquals(Integer.valueOf(1), retrievedEntity.key);
            assertEquals("TEST TEXT", retrievedEntity.t);
            assertEquals("TEST STRING", retrievedEntity.c);
            assertEquals("TEST <b>STRING</b>", result[1]);
            assertEquals("TEST <b>STRING</b>", result[2]);
            assertEquals(0.75, result[3]);
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13021")
    public void testFuzzy() throws Exception {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            org.hibernate.test.dialect.functional.SearchEntity entity = new org.hibernate.test.dialect.functional.SearchEntity();
            entity.key = Integer.valueOf(1);
            entity.t = "TEST TEXT";
            entity.c = "TEST STRING";
            s.persist(entity);
            s.flush();
            s.getTransaction().commit();
            s.beginTransaction();
            Query<Object[]> legacyQuery = s.createQuery((("select b, snippets(c), highlighted(c), score() from " + (ENTITY_NAME)) + " b where contains(b.c, 'string', FUZZY(0.7)) = contains_rhs()"), .class);
            Object[] result = legacyQuery.getSingleResult();
            org.hibernate.test.dialect.functional.SearchEntity retrievedEntity = ((org.hibernate.test.dialect.functional.SearchEntity) (result[0]));
            assertEquals(4, result.length);
            assertEquals(Integer.valueOf(1), retrievedEntity.key);
            assertEquals("TEST TEXT", retrievedEntity.t);
            assertEquals("TEST STRING", retrievedEntity.c);
            assertEquals("TEST <b>STRING</b>", result[1]);
            assertEquals("TEST <b>STRING</b>", result[2]);
            assertEquals(0.75, result[3]);
        });
    }

    @Entity(name = HANASearchTest.ENTITY_NAME)
    public static class SearchEntity {
        @Id
        public Integer key;

        public String t;

        public String c;
    }
}

