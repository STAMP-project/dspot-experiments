package org.hibernate.jpa.test.query;


import java.math.BigInteger;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.test.BaseEntityManagerFunctionalTestCase;
import org.hibernate.testing.RequiresDialect;
import org.hibernate.testing.TestForIssue;
import org.hibernate.testing.transaction.TransactionUtil;
import org.junit.Test;


@RequiresDialect(H2Dialect.class)
public class TupleNativeQueryTest extends BaseEntityManagerFunctionalTestCase {
    @Test
    public void testPositionalGetterShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0));
            assertEquals("Arnold", tuple.get(1));
        });
    }

    @Test
    public void testPositionalGetterWithClassShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0, .class));
            assertEquals("Arnold", tuple.get(1, .class));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithClassShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithClassShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithClassShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test
    public void testAliasGetterWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID"));
            assertEquals("Arnold", tuple.get("FIRSTNAME"));
        });
    }

    @Test
    public void testAliasGetterShouldWorkWithoutExplicitAliasWhenLowerCaseAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get("id");
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAliasGetterShouldThrowExceptionWithoutExplicitAliasWhenWrongAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get("e");
        });
    }

    @Test
    public void testAliasGetterWithClassWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID", .class));
            assertEquals("Arnold", tuple.get("FIRSTNAME", .class));
        });
    }

    @Test
    public void testAliasGetterWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleAliasedResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1"));
            assertEquals("Arnold", tuple.get("ALIAS2"));
        });
    }

    @Test
    public void testAliasGetterWithClassWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleAliasedResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1", .class));
            assertEquals("Arnold", tuple.get("ALIAS2", .class));
        });
    }

    @Test
    public void testToArrayShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getTupleResult(entityManager);
            Object[] result = tuples.get(0).toArray();
            assertArrayEquals(new Object[]{ BigInteger.ONE, "Arnold" }, result);
        });
    }

    @Test
    public void testGetElementsShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getTupleResult(entityManager);
            List<TupleElement<?>> result = tuples.get(0).getElements();
            assertEquals(2, result.size());
            assertEquals(.class, result.get(0).getJavaType());
            assertEquals("ID", result.get(0).getAlias());
            assertEquals(.class, result.get(1).getJavaType());
            assertEquals("FIRSTNAME", result.get(1).getAlias());
        });
    }

    @Test
    public void testPositionalGetterWithNamedNativeQueryShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0));
            assertEquals("Arnold", tuple.get(1));
        });
    }

    @Test
    public void testPositionalGetterWithNamedNativeQueryWithClassShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0, .class));
            assertEquals("Arnold", tuple.get(1, .class));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithNamedNativeQueryShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithNamedNativeQueryWithClassShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithNamedNativeQueryShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithNamedNativeQueryWithClassShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithNamedNativeQueryShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPositionalGetterWithNamedNativeQueryWithClassShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test
    public void testAliasGetterWithNamedNativeQueryWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID"));
            assertEquals("Arnold", tuple.get("FIRSTNAME"));
        });
    }

    @Test
    public void testAliasGetterWithNamedNativeQueryShouldWorkWithoutExplicitAliasWhenLowerCaseAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get("id");
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAliasGetterWithNamedNativeQueryShouldThrowExceptionWithoutExplicitAliasWhenWrongAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get("e");
        });
    }

    @Test
    public void testAliasGetterWithNamedNativeQueryWithClassWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID", .class));
            assertEquals("Arnold", tuple.get("FIRSTNAME", .class));
        });
    }

    @Test
    public void testAliasGetterWithNamedNativeQueryWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard_with_alias");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1"));
            assertEquals("Arnold", tuple.get("ALIAS2"));
        });
    }

    @Test
    public void testAliasGetterWithNamedNativeQueryWithClassWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleNamedResult(entityManager, "standard_with_alias");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1", .class));
            assertEquals("Arnold", tuple.get("ALIAS2", .class));
        });
    }

    @Test
    public void testToArrayShouldWithNamedNativeQueryWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getTupleNamedResult(entityManager, "standard");
            Object[] result = tuples.get(0).toArray();
            assertArrayEquals(new Object[]{ BigInteger.ONE, "Arnold" }, result);
        });
    }

    @Test
    public void testGetElementsWithNamedNativeQueryShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getTupleNamedResult(entityManager, "standard");
            List<TupleElement<?>> result = tuples.get(0).getElements();
            assertEquals(2, result.size());
            assertEquals(.class, result.get(0).getJavaType());
            assertEquals("ID", result.get(0).getAlias());
            assertEquals(.class, result.get(1).getJavaType());
            assertEquals("FIRSTNAME", result.get(1).getAlias());
        });
    }

    @Test
    public void testStreamedPositionalGetterShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0));
            assertEquals("Arnold", tuple.get(1));
        });
    }

    @Test
    public void testStreamedPositionalGetterWithClassShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0, .class));
            assertEquals("Arnold", tuple.get(1, .class));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithClassShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithClassShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithClassShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test
    public void testStreamedAliasGetterWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID"));
            assertEquals("Arnold", tuple.get("FIRSTNAME"));
        });
    }

    @Test
    public void testStreamedAliasGetterShouldWorkWithoutExplicitAliasWhenLowerCaseAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get("id");
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedAliasGetterShouldThrowExceptionWithoutExplicitAliasWhenWrongAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            tuple.get("e");
        });
    }

    @Test
    public void testStreamedAliasGetterWithClassWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedTupleResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID", .class));
            assertEquals("Arnold", tuple.get("FIRSTNAME", .class));
        });
    }

    @Test
    public void testStreamedAliasGetterWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleAliasedResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1"));
            assertEquals("Arnold", tuple.get("ALIAS2"));
        });
    }

    @Test
    public void testStreamedAliasGetterWithClassWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getTupleAliasedResult(entityManager);
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1", .class));
            assertEquals("Arnold", tuple.get("ALIAS2", .class));
        });
    }

    @Test
    public void testStreamedToArrayShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getStreamedTupleResult(entityManager);
            Object[] result = tuples.get(0).toArray();
            assertArrayEquals(new Object[]{ BigInteger.ONE, "Arnold" }, result);
        });
    }

    @Test
    public void testStreamedGetElementsShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getStreamedTupleResult(entityManager);
            List<TupleElement<?>> result = tuples.get(0).getElements();
            assertEquals(2, result.size());
            assertEquals(.class, result.get(0).getJavaType());
            assertEquals("ID", result.get(0).getAlias());
            assertEquals(.class, result.get(1).getJavaType());
            assertEquals("FIRSTNAME", result.get(1).getAlias());
        });
    }

    @Test
    public void testStreamedPositionalGetterWithNamedNativeQueryShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0));
            assertEquals("Arnold", tuple.get(1));
        });
    }

    @Test
    public void testStreamedPositionalGetterWithNamedNativeQueryWithClassShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get(0, .class));
            assertEquals("Arnold", tuple.get(1, .class));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithNamedNativeQueryShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithNamedNativeQueryWithClassShouldThrowExceptionWhenLessThanZeroGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get((-1));
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithNamedNativeQueryShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithNamedNativeQueryWithClassShouldThrowExceptionWhenTupleSizePositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(2);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithNamedNativeQueryShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedPositionalGetterWithNamedNativeQueryWithClassShouldThrowExceptionWhenExceedingPositionGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get(3);
        });
    }

    @Test
    public void testStreamedAliasGetterWithNamedNativeQueryWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID"));
            assertEquals("Arnold", tuple.get("FIRSTNAME"));
        });
    }

    @Test
    public void testStreamedAliasGetterWithNamedNativeQueryShouldWorkWithoutExplicitAliasWhenLowerCaseAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get("id");
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void testStreamedAliasGetterWithNamedNativeQueryShouldThrowExceptionWithoutExplicitAliasWhenWrongAliasGiven() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            tuple.get("e");
        });
    }

    @Test
    public void testStreamedAliasGetterWithNamedNativeQueryWithClassWithoutExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ID", .class));
            assertEquals("Arnold", tuple.get("FIRSTNAME", .class));
        });
    }

    @Test
    public void testStreamedAliasGetterWithNamedNativeQueryWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard_with_alias");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1"));
            assertEquals("Arnold", tuple.get("ALIAS2"));
        });
    }

    @Test
    public void testStreamedAliasGetterWithNamedNativeQueryWithClassWithExplicitAliasShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> result = getStreamedNamedTupleResult(entityManager, "standard_with_alias");
            Tuple tuple = result.get(0);
            assertEquals(BigInteger.ONE, tuple.get("ALIAS1", .class));
            assertEquals("Arnold", tuple.get("ALIAS2", .class));
        });
    }

    @Test
    public void testStreamedToArrayShouldWithNamedNativeQueryWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getStreamedNamedTupleResult(entityManager, "standard");
            Object[] result = tuples.get(0).toArray();
            assertArrayEquals(new Object[]{ BigInteger.ONE, "Arnold" }, result);
        });
    }

    @Test
    public void testStreamedGetElementsWithNamedNativeQueryShouldWorkProperly() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getStreamedNamedTupleResult(entityManager, "standard");
            List<TupleElement<?>> result = tuples.get(0).getElements();
            assertEquals(2, result.size());
            assertEquals(.class, result.get(0).getJavaType());
            assertEquals("ID", result.get(0).getAlias());
            assertEquals(.class, result.get(1).getJavaType());
            assertEquals("FIRSTNAME", result.get(1).getAlias());
        });
    }

    @Test
    @TestForIssue(jiraKey = "HHH-11897")
    public void testGetElementsShouldNotThrowExceptionWhenResultContainsNullValue() {
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            org.hibernate.jpa.test.query.User user = entityManager.find(.class, 1L);
            user.firstName = null;
        });
        TransactionUtil.doInJPA(this::entityManagerFactory, ( entityManager) -> {
            List<Tuple> tuples = getTupleResult(entityManager);
            final Tuple tuple = tuples.get(0);
            List<TupleElement<?>> result = tuple.getElements();
            assertEquals(2, result.size());
            final TupleElement<?> firstTupleElement = result.get(0);
            assertEquals(.class, firstTupleElement.getJavaType());
            assertEquals("ID", firstTupleElement.getAlias());
            assertEquals(BigInteger.valueOf(1L), tuple.get(firstTupleElement.getAlias()));
            final TupleElement<?> secondTupleElement = result.get(1);
            assertEquals(.class, secondTupleElement.getJavaType());
            assertEquals("FIRSTNAME", secondTupleElement.getAlias());
            assertNull(tuple.get(secondTupleElement.getAlias()));
        });
    }

    @Entity
    @Table(name = "users")
    @NamedNativeQueries({ @NamedNativeQuery(name = "standard", query = "SELECT id, firstname FROM users"), @NamedNativeQuery(name = "standard_with_alias", query = "SELECT id AS alias1, firstname AS alias2 FROM users") })
    public static class User {
        @Id
        private long id;

        private String firstName;

        public User() {
        }

        public User(String firstName) {
            this.id = 1L;
            this.firstName = firstName;
        }
    }
}

