/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.query.Query;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.BaseCoreFunctionalTestCase;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


/**
 *
 *
 * @author Andrea Boriero
 * @author Christian Beikov
 */
public class CollectionMapWithComponentValueTest extends BaseCoreFunctionalTestCase {
    private final CollectionMapWithComponentValueTest.KeyValue keyValue = new CollectionMapWithComponentValueTest.KeyValue("key1");

    private final CollectionMapWithComponentValueTest.EmbeddableValue embeddableValue = new CollectionMapWithComponentValueTest.EmbeddableValue(3);

    @Test
    public void testMapKeyExpressionInWhere() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // JPA form
            Query query = s.createQuery("select te from TestEntity te join te.values v where ?1 in (key(v)) ");
            query.setParameter(1, keyValue);
            assertThat(query.list().size(), is(1));
            // Hibernate additional form
            query = s.createQuery("select te from TestEntity te where ?1 in (key(te.values))");
            query.setParameter(1, keyValue);
            assertThat(query.list().size(), is(1));
            // Test key property dereference
            query = s.createQuery("select te from TestEntity te join te.values v where key(v).name in :names");
            query.setParameterList("names", Arrays.asList(keyValue.name));
            assertThat(query.list().size(), is(1));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10577")
    public void testMapValueExpressionInWhere() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // JPA form
            try {
                Query query = s.createQuery("select te from TestEntity te join te.values v where ? in (value(v))");
                query.setParameter(0, new org.hibernate.test.hql.EmbeddableValue(3));
                assertThat(query.list().size(), is(2));
                fail("HibernateException expected - Could not determine type for EmbeddableValue");
            } catch ( e) {
                assertTyping(.class, e);
            }
            // Hibernate additional form
            try {
                Query query = s.createQuery("select te from TestEntity te where ? in (value(te.values))");
                query.setParameter(0, new org.hibernate.test.hql.EmbeddableValue(3));
                assertThat(query.list().size(), is(2));
                fail("HibernateException expected - Could not determine type for EmbeddableValue");
            } catch ( e) {
                assertTyping(.class, e);
            }
            // Test value property dereference
            Query query = s.createQuery("select te from TestEntity te join te.values v where value(v).value in :values");
            query.setParameterList("values", Arrays.asList(3));
            assertThat(query.list().size(), is(2));
        });
    }

    @Test
    public void testMapKeyExpressionInSelect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // JPA form
            List results = s.createQuery("select key(v) from TestEntity te join te.values v").list();
            assertEquals(2, results.size());
            assertTyping(.class, results.get(0));
            // Hibernate additional form
            results = s.createQuery("select key(te.values) from TestEntity te").list();
            assertEquals(2, results.size());
            assertTyping(.class, results.get(0));
        });
    }

    @Test
    public void testMapValueExpressionInSelect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            List addresses = s.createQuery("select value(v) from TestEntity te join te.values v").list();
            assertEquals(2, addresses.size());
            assertTyping(.class, addresses.get(0));
            addresses = s.createQuery("select value(te.values) from TestEntity te").list();
            assertEquals(2, addresses.size());
            assertTyping(.class, addresses.get(0));
        });
    }

    @Test
    public void testMapEntryExpressionInSelect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            List addresses = s.createQuery("select entry(v) from TestEntity te join te.values v").list();
            assertEquals(2, addresses.size());
            assertTyping(.class, addresses.get(0));
            addresses = s.createQuery("select entry(te.values) from TestEntity te").list();
            assertEquals(2, addresses.size());
            assertTyping(.class, addresses.get(0));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10577")
    public void testMapKeyExpressionDereferenceInSelect() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            List<String> keyValueNames = s.createQuery("select key(v).name as name from TestEntity te join te.values v order by name", .class).list();
            assertEquals(2, keyValueNames.size());
            assertEquals("key1", keyValueNames.get(0));
            assertEquals("key2", keyValueNames.get(1));
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-10537")
    public void testLeftJoinMapAndUseKeyExpression() {
        TransactionUtil.doInHibernate(this::sessionFactory, ( s) -> {
            // Assert that a left join is used for joining the map key entity table
            List keyValues = s.createQuery("select key(v) from BaseTestEntity bte left join bte.entities te left join te.values v").list();
            System.out.println(keyValues);
            assertEquals(2, keyValues.size());
        });
    }

    @Entity(name = "BaseTestEntity")
    @Table(name = "BASE_TEST_ENTITY")
    public static class BaseTestEntity {
        @Id
        @GeneratedValue
        Long id;

        @OneToMany
        Set<CollectionMapWithComponentValueTest.TestEntity> entities;
    }

    @Entity(name = "TestEntity")
    @Table(name = "TEST_ENTITY")
    public static class TestEntity {
        @Id
        @GeneratedValue
        Long id;

        @ElementCollection
        Map<CollectionMapWithComponentValueTest.KeyValue, CollectionMapWithComponentValueTest.EmbeddableValue> values;
    }

    @Entity(name = "KeyValue")
    @Table(name = "KEY_VALUE")
    public static class KeyValue {
        @Id
        @GeneratedValue
        Long id;

        String name;

        public KeyValue() {
        }

        public KeyValue(String name) {
            this.name = name;
        }
    }

    @Embeddable
    public static class EmbeddableValue {
        Integer value;

        EmbeddableValue() {
        }

        EmbeddableValue(Integer value) {
            this.value = value;
        }
    }
}

