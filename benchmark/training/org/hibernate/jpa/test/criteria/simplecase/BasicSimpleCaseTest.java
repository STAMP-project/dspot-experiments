/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.jpa.test.criteria.simplecase;


import java.util.List;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Tuple;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaBuilder.SimpleCase;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.FailureExpected;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Assert;
import org.junit.Test;


/**
 * Mote that these are simply performing syntax checking (can the criteria query
 * be properly compiled and executed)
 *
 * @author Steve Ebersole
 */
public class BasicSimpleCaseTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    @TestForIssue(jiraKey = "HHH-9343")
    public void testCaseStringResult() {
        EntityManager em = getOrCreateEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<BasicSimpleCaseTest.Customer> root = query.from(BasicSimpleCaseTest.Customer.class);
        Path<String> emailPath = root.get("email");
        CriteriaBuilder.Case<String> selectCase = builder.selectCase();
        selectCase.when(builder.greaterThan(builder.length(emailPath), 13), "Long");
        selectCase.when(builder.greaterThan(builder.length(emailPath), 12), "Normal");
        Expression<String> emailType = selectCase.otherwise("Unknown");
        query.multiselect(emailPath, emailType);
        em.createQuery(query).getResultList();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9343")
    public void testCaseIntegerResult() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<BasicSimpleCaseTest.Customer> root = query.from(BasicSimpleCaseTest.Customer.class);
        Path<String> emailPath = root.get("email");
        CriteriaBuilder.Case<Integer> selectCase = builder.selectCase();
        selectCase.when(builder.greaterThan(builder.length(emailPath), 13), 2);
        selectCase.when(builder.greaterThan(builder.length(emailPath), 12), 1);
        Expression<Integer> emailType = selectCase.otherwise(0);
        query.multiselect(emailPath, emailType);
        em.createQuery(query).getResultList();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9343")
    public void testCaseLiteralResult() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
        Root<BasicSimpleCaseTest.Customer> expense_ = cq.from(BasicSimpleCaseTest.Customer.class);
        em.createQuery(cq.distinct(true).where(cb.equal(expense_.get("email"), "@hibernate.com")).multiselect(cb.selectCase().when(cb.gt(cb.count(expense_), cb.literal(0L)), cb.literal(true)).otherwise(cb.literal(false)))).getSingleResult();
    }

    @Test
    @TestForIssue(jiraKey = "HHH-9343")
    public void testCaseLiteralResult2() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Boolean> cq = cb.createQuery(Boolean.class);
        Root<BasicSimpleCaseTest.Customer> expense_ = cq.from(BasicSimpleCaseTest.Customer.class);
        em.createQuery(cq.distinct(true).where(cb.equal(expense_.get("email"), "@hibernate.com")).multiselect(cb.selectCase().when(cb.gt(cb.count(expense_), cb.literal(0L)), true).otherwise(false))).getSingleResult();
    }

    @Test
    public void testCaseInOrderBy() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BasicSimpleCaseTest.Customer> query = builder.createQuery(BasicSimpleCaseTest.Customer.class);
        Root<BasicSimpleCaseTest.Customer> root = query.from(BasicSimpleCaseTest.Customer.class);
        query.select(root);
        Path<String> emailPath = root.get("email");
        SimpleCase<String, Integer> orderCase = builder.selectCase(emailPath);
        orderCase = orderCase.when("test@test.com", 1);
        orderCase = orderCase.when("test2@test.com", 2);
        query.orderBy(builder.asc(orderCase.otherwise(0)));
        em.createQuery(query);
    }

    @Test
    public void testCaseInOrderBy2() {
        EntityManager em = getOrCreateEntityManager();
        em.getTransaction().begin();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<BasicSimpleCaseTest.Customer> query = builder.createQuery(BasicSimpleCaseTest.Customer.class);
        Root<BasicSimpleCaseTest.Customer> root = query.from(BasicSimpleCaseTest.Customer.class);
        query.select(root);
        Path<String> emailPath = root.get("email");
        SimpleCase<String, String> orderCase = builder.selectCase(emailPath);
        orderCase = orderCase.when("test@test.com", "a");
        orderCase = orderCase.when("test2@test.com", "b");
        query.orderBy(builder.asc(orderCase.otherwise("c")));
        em.createQuery(query);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13016")
    @FailureExpected(jiraKey = "HHH-13016")
    public void testCaseEnumResult() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            // create entities
            org.hibernate.jpa.test.criteria.simplecase.Customer customer1 = new org.hibernate.jpa.test.criteria.simplecase.Customer();
            customer1.setEmail("LONG5678901234");
            em.persist(customer1);
            org.hibernate.jpa.test.criteria.simplecase.Customer customer2 = new org.hibernate.jpa.test.criteria.simplecase.Customer();
            customer2.setEmail("NORMAL7890123");
            em.persist(customer2);
            org.hibernate.jpa.test.criteria.simplecase.Customer customer3 = new org.hibernate.jpa.test.criteria.simplecase.Customer();
            customer3.setEmail("UNKNOWN");
            em.persist(customer3);
        });
        EntityManager em = getOrCreateEntityManager();
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = builder.createTupleQuery();
        Root<BasicSimpleCaseTest.Customer> root = query.from(BasicSimpleCaseTest.Customer.class);
        Path<String> emailPath = root.get("email");
        CriteriaBuilder.Case<BasicSimpleCaseTest.EmailType> selectCase = builder.selectCase();
        selectCase.when(builder.greaterThan(builder.length(emailPath), 13), BasicSimpleCaseTest.EmailType.LONG);
        selectCase.when(builder.greaterThan(builder.length(emailPath), 12), BasicSimpleCaseTest.EmailType.NORMAL);
        Expression<BasicSimpleCaseTest.EmailType> emailType = selectCase.otherwise(BasicSimpleCaseTest.EmailType.UNKNOWN);
        query.multiselect(emailPath, emailType);
        query.orderBy(builder.asc(emailPath));
        List<Tuple> results = em.createQuery(query).getResultList();
        Assert.assertEquals(3, results.size());
        Assert.assertEquals("LONG5678901234", results.get(0).get(0));
        Assert.assertEquals(BasicSimpleCaseTest.EmailType.LONG, results.get(0).get(1));
        Assert.assertEquals("NORMAL7890123", results.get(1).get(0));
        Assert.assertEquals(BasicSimpleCaseTest.EmailType.NORMAL, results.get(1).get(1));
        Assert.assertEquals("UNKNOWN", results.get(2).get(0));
        Assert.assertEquals(BasicSimpleCaseTest.EmailType.UNKNOWN, results.get(2).get(1));
    }

    @Test
    @TestForIssue(jiraKey = "HHH-13199")
    public void testCaseEnumInSum() throws Exception {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            // create entities
            org.hibernate.jpa.test.criteria.simplecase.TestEntity e1 = new org.hibernate.jpa.test.criteria.simplecase.TestEntity();
            e1.setEnumField(TestEnum.VAL_1);
            e1.setValue(20L);
            em.persist(e1);
            org.hibernate.jpa.test.criteria.simplecase.TestEntity e2 = new org.hibernate.jpa.test.criteria.simplecase.TestEntity();
            e2.setEnumField(TestEnum.VAL_2);
            e2.setValue(10L);
            em.persist(e2);
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( em) -> {
            // Works in previous version (e.g. Hibernate 5.3.7.Final)
            // Fails in Hibernate 5.4.0.Final
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Tuple> query = cb.createTupleQuery();
            Root<org.hibernate.jpa.test.criteria.simplecase.TestEntity> r = query.from(.class);
            CriteriaBuilder.Case<Long> case1 = cb.selectCase();
            case1.when(cb.equal(r.<org.hibernate.jpa.test.criteria.simplecase.TestEnum>get("enumField"), cb.literal(TestEnum.VAL_1)), r.<Long>get("value"));
            case1.otherwise(cb.nullLiteral(.class));
            CriteriaBuilder.Case<Long> case2 = cb.selectCase();
            case2.when(cb.equal(r.<org.hibernate.jpa.test.criteria.simplecase.TestEnum>get("enumField"), cb.literal(TestEnum.VAL_2)), r.<Long>get("value"));
            case2.otherwise(cb.nullLiteral(.class));
            /* Forces enums to be bound as parameters, so SQL is something like
            "SELECT enumfield AS enumField, SUM(CASE WHEN enumfield = ? THEN value
            ELSE NULL END) AS VAL_1, SUM(CASE WHEN enumfield =? THEN value ELSE NULL END) AS VAL_1 FROM TestEntity
            GROUP BY enumfield"
             */
            query.select(cb.tuple(r.<org.hibernate.jpa.test.criteria.simplecase.TestEnum>get("enumField").alias("enumField"), cb.sum(case1).alias("VAL_1"), cb.sum(case2).alias("VAL_2"))).groupBy(r.<org.hibernate.jpa.test.criteria.simplecase.TestEnum>get("enumField"));
            List<Tuple> list = em.createQuery(query).getResultList();
            assertEquals(2, list.size());
            for (Tuple tuple : list) {
                org.hibernate.jpa.test.criteria.simplecase.TestEnum enumVal = tuple.get("enumField", .class);
                if (enumVal == TestEnum.VAL_1) {
                    assertEquals(20L, tuple.get("VAL_1", .class).longValue());
                    assertNull(tuple.get("VAL_2", .class));
                } else
                    if (enumVal == TestEnum.VAL_2) {
                        assertNull(tuple.get("VAL_1", .class));
                        assertEquals(10L, tuple.get("VAL_2", .class).longValue());
                    }

            }
        });
    }

    @Entity(name = "Customer")
    @Table(name = "customer")
    public static class Customer {
        private Integer id;

        private String email;

        @Id
        @GeneratedValue
        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    public enum EmailType {

        LONG,
        NORMAL,
        UNKNOWN;}

    @Entity(name = "TestEntity")
    public static class TestEntity {
        @Id
        @GeneratedValue
        private long id;

        @Convert(converter = BasicSimpleCaseTest.TestEnumConverter.class)
        private BasicSimpleCaseTest.TestEnum enumField;

        private Long value;

        public long getId() {
            return id;
        }

        public BasicSimpleCaseTest.TestEnum getEnumField() {
            return enumField;
        }

        public void setEnumField(BasicSimpleCaseTest.TestEnum enumField) {
            this.enumField = enumField;
        }

        public Long getValue() {
            return value;
        }

        public void setValue(Long value) {
            this.value = value;
        }
    }

    public static enum TestEnum {

        VAL_1("1"),
        VAL_2("2");
        private final String value;

        private TestEnum(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static BasicSimpleCaseTest.TestEnum fromValue(String value) {
            if (value.equals(BasicSimpleCaseTest.TestEnum.VAL_1.value)) {
                return BasicSimpleCaseTest.TestEnum.VAL_1;
            } else
                if (value.equals(BasicSimpleCaseTest.TestEnum.VAL_2.value)) {
                    return BasicSimpleCaseTest.TestEnum.VAL_2;
                }

            return null;
        }
    }

    public static class TestEnumConverter implements AttributeConverter<BasicSimpleCaseTest.TestEnum, String> {
        @Override
        public String convertToDatabaseColumn(BasicSimpleCaseTest.TestEnum attribute) {
            return attribute == null ? null : attribute.getValue();
        }

        @Override
        public BasicSimpleCaseTest.TestEnum convertToEntityAttribute(String dbData) {
            return dbData == null ? null : BasicSimpleCaseTest.TestEnum.fromValue(dbData);
        }
    }
}

