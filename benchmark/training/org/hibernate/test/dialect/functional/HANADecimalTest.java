/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.dialect.functional;


import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import org.hibernate.Session;
import org.hibernate.dialect.AbstractHANADialect;
import org.hibernate.query.Query;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests the correctness of the parameter hibernate.dialect.hana.treat_double_typed_fields_as_decimal which controls the
 * handling of double types as either {@link BigDecimal} (parameter is set to true) or {@link Double} (default behavior
 * or parameter is set to false)
 *
 * @author Jonathan Bregler
 */
@RequiresDialect({ AbstractHANADialect.class })
public class HANADecimalTest extends BaseCoreFunctionalTestCase {
    private static final String ENTITY_NAME = "DecimalEntity";

    @Test
    @TestForIssue(jiraKey = "HHH-12995")
    public void testDecimalTypeFalse() throws Exception {
        rebuildSessionFactory(( configuration) -> {
            configuration.setProperty("hibernate.dialect.hana.treat_double_typed_fields_as_decimal", Boolean.FALSE.toString());
        });
        Session s = openSession();
        s.beginTransaction();
        HANADecimalTest.DecimalEntity entity = new HANADecimalTest.DecimalEntity();
        entity.key = Integer.valueOf(1);
        entity.doubleDouble = 1.19;
        entity.decimalDecimal = BigDecimal.valueOf(1.19);
        entity.doubleDecimal = 1.19;
        entity.decimalDouble = BigDecimal.valueOf(1.19);
        s.persist(entity);
        HANADecimalTest.DecimalEntity entity2 = new HANADecimalTest.DecimalEntity();
        entity2.key = Integer.valueOf(2);
        entity2.doubleDouble = 0.3;
        entity2.decimalDecimal = BigDecimal.valueOf(0.3);
        entity2.doubleDecimal = 0.3;
        entity2.decimalDouble = BigDecimal.valueOf(0.3);
        s.persist(entity2);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANADecimalTest.DecimalEntity> legacyQuery = s.createQuery((("select b from " + (HANADecimalTest.ENTITY_NAME)) + " b order by key asc"), HANADecimalTest.DecimalEntity.class);
        List<HANADecimalTest.DecimalEntity> retrievedEntities = legacyQuery.getResultList();
        Assert.assertEquals(2, retrievedEntities.size());
        HANADecimalTest.DecimalEntity retrievedEntity = retrievedEntities.get(0);
        Assert.assertEquals(Integer.valueOf(1), retrievedEntity.key);
        Assert.assertEquals(1.19, retrievedEntity.doubleDouble, 0);
        Assert.assertEquals(new BigDecimal("1.190000000000000"), retrievedEntity.decimalDecimal);
        Assert.assertEquals(1.189999999999999, retrievedEntity.doubleDecimal, 0);
        Assert.assertEquals(new BigDecimal("1.19"), retrievedEntity.decimalDouble);
        retrievedEntity = retrievedEntities.get(1);
        Assert.assertEquals(Integer.valueOf(2), retrievedEntity.key);
        Assert.assertEquals(0.3, retrievedEntity.doubleDouble, 0);
        Assert.assertEquals(new BigDecimal("0.300000000000000"), retrievedEntity.decimalDecimal);
        Assert.assertEquals(0.299999999999999, retrievedEntity.doubleDecimal, 0);
        Assert.assertEquals(new BigDecimal("0.3"), retrievedEntity.decimalDouble);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12995")
    public void testDecimalTypeDefault() throws Exception {
        rebuildSessionFactory();
        Session s = openSession();
        s.beginTransaction();
        HANADecimalTest.DecimalEntity entity = new HANADecimalTest.DecimalEntity();
        entity.key = Integer.valueOf(1);
        entity.doubleDouble = 1.19;
        entity.decimalDecimal = BigDecimal.valueOf(1.19);
        entity.doubleDecimal = 1.19;
        entity.decimalDouble = BigDecimal.valueOf(1.19);
        s.persist(entity);
        HANADecimalTest.DecimalEntity entity2 = new HANADecimalTest.DecimalEntity();
        entity2.key = Integer.valueOf(2);
        entity2.doubleDouble = 0.3;
        entity2.decimalDecimal = BigDecimal.valueOf(0.3);
        entity2.doubleDecimal = 0.3;
        entity2.decimalDouble = BigDecimal.valueOf(0.3);
        s.persist(entity2);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANADecimalTest.DecimalEntity> legacyQuery = s.createQuery((("select b from " + (HANADecimalTest.ENTITY_NAME)) + " b order by key asc"), HANADecimalTest.DecimalEntity.class);
        List<HANADecimalTest.DecimalEntity> retrievedEntities = legacyQuery.getResultList();
        Assert.assertEquals(2, retrievedEntities.size());
        HANADecimalTest.DecimalEntity retrievedEntity = retrievedEntities.get(0);
        Assert.assertEquals(Integer.valueOf(1), retrievedEntity.key);
        Assert.assertEquals(1.19, retrievedEntity.doubleDouble, 0);
        Assert.assertEquals(new BigDecimal("1.190000000000000"), retrievedEntity.decimalDecimal);
        Assert.assertEquals(1.189999999999999, retrievedEntity.doubleDecimal, 0);
        Assert.assertEquals(new BigDecimal("1.19"), retrievedEntity.decimalDouble);
        retrievedEntity = retrievedEntities.get(1);
        Assert.assertEquals(Integer.valueOf(2), retrievedEntity.key);
        Assert.assertEquals(0.3, retrievedEntity.doubleDouble, 0);
        Assert.assertEquals(new BigDecimal("0.300000000000000"), retrievedEntity.decimalDecimal);
        Assert.assertEquals(0.299999999999999, retrievedEntity.doubleDecimal, 0);
        Assert.assertEquals(new BigDecimal("0.3"), retrievedEntity.decimalDouble);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-12995")
    public void testDecimalTypeTrue() throws Exception {
        rebuildSessionFactory(( configuration) -> {
            configuration.setProperty("hibernate.dialect.hana.treat_double_typed_fields_as_decimal", Boolean.TRUE.toString());
        });
        Session s = openSession();
        s.beginTransaction();
        HANADecimalTest.DecimalEntity entity = new HANADecimalTest.DecimalEntity();
        entity.key = Integer.valueOf(1);
        entity.doubleDouble = 1.19;
        entity.decimalDecimal = BigDecimal.valueOf(1.19);
        entity.doubleDecimal = 1.19;
        entity.decimalDouble = BigDecimal.valueOf(1.19);
        s.persist(entity);
        HANADecimalTest.DecimalEntity entity2 = new HANADecimalTest.DecimalEntity();
        entity2.key = Integer.valueOf(2);
        entity2.doubleDouble = 0.3;
        entity2.decimalDecimal = BigDecimal.valueOf(0.3);
        entity2.doubleDecimal = 0.3;
        entity2.decimalDouble = BigDecimal.valueOf(0.3);
        s.persist(entity2);
        s.flush();
        s.getTransaction().commit();
        s.clear();
        Query<HANADecimalTest.DecimalEntity> legacyQuery = s.createQuery((("select b from " + (HANADecimalTest.ENTITY_NAME)) + " b order by key asc"), HANADecimalTest.DecimalEntity.class);
        List<HANADecimalTest.DecimalEntity> retrievedEntities = legacyQuery.getResultList();
        Assert.assertEquals(2, retrievedEntities.size());
        HANADecimalTest.DecimalEntity retrievedEntity = retrievedEntities.get(0);
        Assert.assertEquals(Integer.valueOf(1), retrievedEntity.key);
        Assert.assertEquals(1.19, retrievedEntity.doubleDouble, 0);
        Assert.assertEquals(new BigDecimal("1.190000000000000"), retrievedEntity.decimalDecimal);
        Assert.assertEquals(1.19, retrievedEntity.doubleDecimal, 0);
        Assert.assertEquals(new BigDecimal("1.19"), retrievedEntity.decimalDouble);
        retrievedEntity = retrievedEntities.get(1);
        Assert.assertEquals(Integer.valueOf(2), retrievedEntity.key);
        Assert.assertEquals(0.3, retrievedEntity.doubleDouble, 0);
        Assert.assertEquals(new BigDecimal("0.300000000000000"), retrievedEntity.decimalDecimal);
        Assert.assertEquals(0.3, retrievedEntity.doubleDecimal, 0);
        Assert.assertEquals(new BigDecimal("0.3"), retrievedEntity.decimalDouble);
    }

    @Entity(name = HANADecimalTest.ENTITY_NAME)
    public static class DecimalEntity {
        @Id
        public Integer key;

        public double doubleDouble;

        public BigDecimal decimalDecimal;

        public double doubleDecimal;

        public BigDecimal decimalDouble;
    }
}

