/**
 * Hibernate, Relational Persistence for Idiomatic Java
 *
 * License: GNU Lesser General Public License (LGPL), version 2.1 or later.
 * See the lgpl.txt file in the root directory or <http://www.gnu.org/licenses/lgpl-2.1.html>.
 */
package org.hibernate.test.hql;


import LongType.INSTANCE;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import javax.persistence.PersistenceException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.dialect.Oracle8iDialect;
import org.hibernate.dialect.SQLServerDialect;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.hql.internal.ast.QueryTranslatorImpl;
import org.hibernate.hql.internal.classic.ClassicQueryTranslatorFactory;
import org.hibernate.hql.spi.QueryTranslator;
import org.hibernate.hql.spi.QueryTranslatorFactory;
import org.hibernate.testing.SkipForDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.junit4.ExtraAssertions;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests cases for ensuring alignment between HQL and Criteria behavior.
 *
 * @author Max Rydahl Andersen
 */
public class CriteriaHQLAlignmentTest extends QueryTranslatorTestCase {
    private boolean initialVersion2SqlFlagValue;

    @Test
    public void testHQLAggregationReturnType() {
        // EJB3: COUNT returns Long
        QueryTranslatorImpl translator = createNewQueryTranslator("select count(*) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", INSTANCE, translator.getReturnTypes()[0]);
        translator = createNewQueryTranslator("select count(h.heightInches) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", INSTANCE, translator.getReturnTypes()[0]);
        // MAX, MIN return the type of the state-field to which they are applied.
        translator = createNewQueryTranslator("select max(h.heightInches) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0]);
        translator = createNewQueryTranslator("select max(h.id) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", INSTANCE, translator.getReturnTypes()[0]);
        // AVG returns Double.
        translator = createNewQueryTranslator("select avg(h.heightInches) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0]);
        translator = createNewQueryTranslator("select avg(h.id) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0]);
        translator = createNewQueryTranslator("select avg(h.bigIntegerValue) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0]);
        // SUM returns Long when applied to state-fields of integral types (other than BigInteger);
        translator = createNewQueryTranslator("select sum(h.id) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", INSTANCE, translator.getReturnTypes()[0]);
        translator = createNewQueryTranslator("select sum(h.intValue) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", INSTANCE, translator.getReturnTypes()[0]);
        // SUM returns Double when applied to state-fields of floating point types;
        translator = createNewQueryTranslator("select sum(h.heightInches) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0]);
        translator = createNewQueryTranslator("select sum(h.floatValue) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", DoubleType.INSTANCE, translator.getReturnTypes()[0]);
        // SUM returns BigInteger when applied to state-fields of type BigInteger
        translator = createNewQueryTranslator("select sum(h.bigIntegerValue) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", BigIntegerType.INSTANCE, translator.getReturnTypes()[0]);
        // SUM and BigDecimal when applied to state-fields of type BigDecimal.
        translator = createNewQueryTranslator("select sum(h.bigDecimalValue) from Human h");
        Assert.assertEquals("incorrect return type count", 1, translator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", BigDecimalType.INSTANCE, translator.getReturnTypes()[0]);
        // special case to test classicquery special case handling of count(*)
        String hql = "select count(*) from Human h";
        QueryTranslatorFactory classic = new ClassicQueryTranslatorFactory();
        QueryTranslator oldQueryTranslator = classic.createQueryTranslator(hql, hql, Collections.EMPTY_MAP, sessionFactory(), null);
        oldQueryTranslator.compile(Collections.EMPTY_MAP, true);
        Assert.assertEquals("incorrect return type count", 1, oldQueryTranslator.getReturnTypes().length);
        Assert.assertEquals("incorrect return type", INSTANCE, oldQueryTranslator.getReturnTypes()[0]);
    }

    @Test
    @TestForIssue(jiraKey = "HHH-1724")
    public void testCriteriaAggregationReturnType() {
        Session s = openSession();
        s.beginTransaction();
        Human human = new Human();
        human.setBigIntegerValue(new BigInteger("42"));
        human.setBigDecimalValue(new BigDecimal(45));
        s.save(human);
        s.flush();
        s.clear();
        // EJB3: COUNT returns Long
        Long longValue = ((Long) (s.createCriteria(Human.class).setProjection(Projections.rowCount()).uniqueResult()));
        Assert.assertEquals(longValue, new Long(1));
        longValue = ((Long) (s.createCriteria(Human.class).setProjection(Projections.count("heightInches")).uniqueResult()));
        Assert.assertEquals(longValue, new Long(1));
        // MAX, MIN return the type of the state-field to which they are applied.
        Double dblValue = ((Double) (s.createCriteria(Human.class).setProjection(Projections.max("heightInches")).uniqueResult()));
        Assert.assertNotNull(dblValue);
        longValue = ((Long) (s.createCriteria(Human.class).setProjection(Projections.max("id")).uniqueResult()));
        Assert.assertNotNull(longValue);
        // AVG returns Double.
        dblValue = ((Double) (s.createCriteria(Human.class).setProjection(Projections.avg("heightInches")).uniqueResult()));
        Assert.assertNotNull(dblValue);
        dblValue = ((Double) (s.createCriteria(Human.class).setProjection(Projections.avg("id")).uniqueResult()));
        Assert.assertNotNull(dblValue);
        dblValue = ((Double) (s.createCriteria(Human.class).setProjection(Projections.avg("bigIntegerValue")).uniqueResult()));
        Assert.assertNotNull(dblValue);
        // SUM returns Long when applied to state-fields of integral types (other than BigInteger);
        longValue = ((Long) (s.createCriteria(Human.class).setProjection(Projections.sum("id")).uniqueResult()));
        Assert.assertNotNull(longValue);
        longValue = ((Long) (s.createCriteria(Human.class).setProjection(Projections.sum("intValue")).uniqueResult()));
        Assert.assertNotNull(longValue);
        // SUM returns Double when applied to state-fields of floating point types;
        dblValue = ((Double) (s.createCriteria(Human.class).setProjection(Projections.sum("heightInches")).uniqueResult()));
        Assert.assertNotNull(dblValue);
        dblValue = ((Double) (s.createCriteria(Human.class).setProjection(Projections.sum("floatValue")).uniqueResult()));
        Assert.assertNotNull(dblValue);
        // SUM returns BigInteger when applied to state-fields of type BigInteger
        BigInteger bigIValue = ((BigInteger) (s.createCriteria(Human.class).setProjection(Projections.sum("bigIntegerValue")).uniqueResult()));
        Assert.assertNotNull(bigIValue);
        // SUM and BigDecimal when applied to state-fields of type BigDecimal.
        BigDecimal bigDValue = ((BigDecimal) (s.createCriteria(Human.class).setProjection(Projections.sum("bigDecimalValue")).uniqueResult()));
        Assert.assertNotNull(bigDValue);
        s.delete(human);
        s.flush();
        s.getTransaction().commit();
        s.close();
    }

    @Test
    @SkipForDialect(value = Oracle8iDialect.class, comment = "Cannot count distinct over multiple columns in Oracle")
    @SkipForDialect(value = SQLServerDialect.class, comment = "Cannot count distinct over multiple columns in SQL Server")
    public void testCountReturnValues() {
        Session s = openSession();
        Transaction t = s.beginTransaction();
        Human human1 = new Human();
        human1.setName(new Name("John", 'Q', "Public"));
        human1.setNickName("Johnny");
        s.save(human1);
        Human human2 = new Human();
        human2.setName(new Name("John", 'A', "Doe"));
        human2.setNickName("Johnny");
        s.save(human2);
        Human human3 = new Human();
        human3.setName(new Name("John", 'A', "Doe"));
        human3.setNickName("Jack");
        s.save(human3);
        Human human4 = new Human();
        human4.setName(new Name("John", 'A', "Doe"));
        s.save(human4);
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        Long count = ((Long) (s.createQuery("select count( * ) from Human").uniqueResult()));
        Assert.assertEquals(4, count.longValue());
        s.clear();
        count = ((Long) (s.createCriteria(Human.class).setProjection(Projections.rowCount()).uniqueResult()));
        Assert.assertEquals(4, count.longValue());
        s.clear();
        count = ((Long) (s.createQuery("select count( nickName ) from Human").uniqueResult()));
        Assert.assertEquals(3, count.longValue());
        s.clear();
        count = ((Long) (s.createCriteria(Human.class).setProjection(Projections.count("nickName")).uniqueResult()));
        Assert.assertEquals(3, count.longValue());
        s.clear();
        count = ((Long) (s.createQuery("select count( distinct nickName ) from Human").uniqueResult()));
        Assert.assertEquals(2, count.longValue());
        s.clear();
        count = ((Long) (s.createCriteria(Human.class).setProjection(Projections.count("nickName").setDistinct()).uniqueResult()));
        Assert.assertEquals(2, count.longValue());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        try {
            count = ((Long) (s.createQuery("select count( distinct name ) from Human").uniqueResult()));
            if (!(getDialect().supportsTupleDistinctCounts())) {
                Assert.fail("expected SQLGrammarException");
            }
            Assert.assertEquals(2, count.longValue());
        } catch (SQLGrammarException ex) {
            if (!(getDialect().supportsTupleDistinctCounts())) {
                // expected
            } else {
                throw ex;
            }
        } catch (PersistenceException e) {
            SQLGrammarException cause = ExtraAssertions.assertTyping(SQLGrammarException.class, e.getCause());
            if (!(getDialect().supportsTupleCounts())) {
                // expected
            } else {
                throw e;
            }
        } finally {
            t.rollback();
            s.close();
        }
        s = openSession();
        t = s.beginTransaction();
        try {
            count = ((Long) (s.createCriteria(Human.class).setProjection(Projections.count("name").setDistinct()).uniqueResult()));
            if (!(getDialect().supportsTupleDistinctCounts())) {
                Assert.fail("expected SQLGrammarException");
            }
            Assert.assertEquals(2, count.longValue());
        } catch (SQLGrammarException ex) {
            if (!(getDialect().supportsTupleDistinctCounts())) {
                // expected
            } else {
                throw ex;
            }
        } finally {
            t.rollback();
            s.close();
        }
        s = openSession();
        t = s.beginTransaction();
        count = ((Long) (s.createQuery("select count( distinct name.first ) from Human").uniqueResult()));
        Assert.assertEquals(1, count.longValue());
        s.clear();
        count = ((Long) (s.createCriteria(Human.class).setProjection(Projections.count("name.first").setDistinct()).uniqueResult()));
        Assert.assertEquals(1, count.longValue());
        t.commit();
        s.close();
        s = openSession();
        t = s.beginTransaction();
        try {
            count = ((Long) (s.createQuery("select count( name ) from Human").uniqueResult()));
            if (!(getDialect().supportsTupleCounts())) {
                Assert.fail("expected SQLGrammarException");
            }
            Assert.assertEquals(1, count.longValue());
        } catch (SQLGrammarException ex) {
            if (!(getDialect().supportsTupleCounts())) {
                // expected
            } else {
                throw ex;
            }
        } catch (PersistenceException e) {
            SQLGrammarException cause = ExtraAssertions.assertTyping(SQLGrammarException.class, e.getCause());
            if (!(getDialect().supportsTupleCounts())) {
                // expected
            } else {
                throw e;
            }
        } finally {
            t.rollback();
            s.close();
        }
        s = openSession();
        t = s.beginTransaction();
        try {
            count = ((Long) (s.createCriteria(Human.class).setProjection(Projections.count("name")).uniqueResult()));
            if (!(getDialect().supportsTupleCounts())) {
                Assert.fail("expected SQLGrammarException");
            }
            Assert.assertEquals(1, count.longValue());
        } catch (SQLGrammarException ex) {
            if (!(getDialect().supportsTupleCounts())) {
                // expected
            } else {
                throw ex;
            }
        } finally {
            t.rollback();
            s.close();
        }
        s = openSession();
        t = s.beginTransaction();
        s.createQuery("delete from Human").executeUpdate();
        t.commit();
        s.close();
    }
}

