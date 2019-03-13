/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.query.Query;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the correctness of the parameter hibernate.dialect.hana.use_legacy_boolean_type which controls the mapping of
 * boolean types to be either TINYINT (parameter is set to true) or BOOLEAN (default behavior or parameter is set to
 * false)
 *
 * @author Jonathan Bregler
 */
@RequiresDialect({ AbstractHANADialect.class })
public class HANABooleanTest extends BaseCoreFunctionalTestCase {
    private static final String ENTITY_NAME = "BooleanEntity";

    private static final String LEGACY_ENTITY_NAME = "LegacyBooleanEntity";

    @Test
    @TestForIssue(jiraKey = "HHH-12132")
    public void testBooleanType() throws Exception {
        rebuildSessionFactory(( configuration) -> {
            configuration.setProperty("hibernate.dialect.hana.use_legacy_boolean_type", Boolean.FALSE.toString());
        });
        Session s = openSession();
        s.beginTransaction();
        HANABooleanTest.BooleanEntity entity = new HANABooleanTest.BooleanEntity();
        entity.key = Integer.valueOf(1);
        entity.bool = Boolean.TRUE;
        s.persist(entity);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANABooleanTest.BooleanEntity> legacyQuery = s.createQuery((("select b from " + (HANABooleanTest.ENTITY_NAME)) + " b where bool = true"), HANABooleanTest.BooleanEntity.class);
        HANABooleanTest.BooleanEntity retrievedEntity = legacyQuery.getSingleResult();
        Assert.assertEquals(Integer.valueOf(1), retrievedEntity.key);
        Assert.assertTrue(retrievedEntity.bool);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12132")
    public void testBooleanTypeDefaultBehavior() throws Exception {
        rebuildSessionFactory();
        Session s = openSession();
        s.beginTransaction();
        HANABooleanTest.BooleanEntity entity = new HANABooleanTest.BooleanEntity();
        entity.key = Integer.valueOf(1);
        entity.bool = Boolean.TRUE;
        s.persist(entity);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANABooleanTest.BooleanEntity> legacyQuery = s.createQuery((("select b from " + (HANABooleanTest.ENTITY_NAME)) + " b where bool = true"), HANABooleanTest.BooleanEntity.class);
        HANABooleanTest.BooleanEntity retrievedEntity = legacyQuery.getSingleResult();
        Assert.assertEquals(Integer.valueOf(1), retrievedEntity.key);
        Assert.assertTrue(retrievedEntity.bool);
    }

    @Test(expected = PersistenceException.class)
    @TestForIssue(jiraKey = "HHH-12132")
    public void testLegacyBooleanType() throws Exception {
        rebuildSessionFactory(( configuration) -> {
            configuration.setProperty("hibernate.dialect.hana.use_legacy_boolean_type", Boolean.FALSE.toString());
        });
        Session s = openSession();
        s.beginTransaction();
        HANABooleanTest.LegacyBooleanEntity legacyEntity = new HANABooleanTest.LegacyBooleanEntity();
        legacyEntity.key = Integer.valueOf(2);
        legacyEntity.bool = Boolean.FALSE;
        s.persist(legacyEntity);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANABooleanTest.LegacyBooleanEntity> query = s.createQuery((("select b from " + (HANABooleanTest.LEGACY_ENTITY_NAME)) + " b where bool = true"), HANABooleanTest.LegacyBooleanEntity.class);
        query.getSingleResult();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12132")
    public void testLegacyBooleanTypeLegacyBehavior() throws Exception {
        rebuildSessionFactory(( configuration) -> {
            configuration.setProperty("hibernate.dialect.hana.use_legacy_boolean_type", Boolean.TRUE.toString());
        });
        Session s = openSession();
        s.beginTransaction();
        HANABooleanTest.LegacyBooleanEntity legacyEntity = new HANABooleanTest.LegacyBooleanEntity();
        legacyEntity.key = Integer.valueOf(1);
        legacyEntity.bool = Boolean.TRUE;
        s.persist(legacyEntity);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANABooleanTest.LegacyBooleanEntity> legacyQuery = s.createQuery((("select b from " + (HANABooleanTest.LEGACY_ENTITY_NAME)) + " b where bool = true"), HANABooleanTest.LegacyBooleanEntity.class);
        HANABooleanTest.LegacyBooleanEntity retrievedEntity = legacyQuery.getSingleResult();
        Assert.assertEquals(Integer.valueOf(1), retrievedEntity.key);
        Assert.assertTrue(retrievedEntity.bool);
    }

    @Test(expected = PersistenceException.class)
    @TestForIssue(jiraKey = "HHH-12132")
    public void testBooleanTypeLegacyBehavior() throws Exception {
        rebuildSessionFactory(( configuration) -> {
            configuration.setProperty("hibernate.dialect.hana.use_legacy_boolean_type", Boolean.TRUE.toString());
        });
        Session s = openSession();
        s.beginTransaction();
        HANABooleanTest.BooleanEntity entity = new HANABooleanTest.BooleanEntity();
        entity.key = Integer.valueOf(2);
        entity.bool = Boolean.FALSE;
        s.persist(entity);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANABooleanTest.BooleanEntity> query = s.createQuery((("select b from " + (HANABooleanTest.ENTITY_NAME)) + " b where bool = true"), HANABooleanTest.BooleanEntity.class);
        query.getSingleResult();
    }

    @Entity(name = HANABooleanTest.LEGACY_ENTITY_NAME)
    public static class LegacyBooleanEntity {
        @Id
        public Integer key;

        public Boolean bool;
    }

    @Entity(name = HANABooleanTest.ENTITY_NAME)
    public static class BooleanEntity {
        @Id
        public Integer key;

        public Boolean bool;
    }
}

