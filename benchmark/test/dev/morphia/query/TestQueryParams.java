package dev.morphia.query;


import Assert.AssertionFailedException;
import dev.morphia.TestBase;
import dev.morphia.TestMapping;
import dev.morphia.annotations.Entity;
import java.util.Collections;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class TestQueryParams extends TestBase {
    private FieldEnd<?> e;

    @Test(expected = AssertionFailedException.class)
    public void testAnyOfNull() {
        e.hasAnyOf(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testGreaterThanNull() {
        e.greaterThan(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testGreaterThanOrEqualNull() {
        e.greaterThanOrEq(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testHasAllOfEmptyList() {
        final Query<TestQueryParams.E> q = getDs().find(TestQueryParams.E.class);
        q.field("_id").hasAllOf(Collections.emptyList());
    }

    @Test(expected = AssertionFailedException.class)
    public void testHasAllOfNull() {
        e.hasAllOf(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testHasNoneOfNull() {
        e.hasNoneOf(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testHasThisNullElement() {
        e.hasThisElement(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testLessThanNull() {
        e.lessThan(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testLessThanOrEqualNull() {
        e.lessThanOrEq(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testNoneOfEmptyList() {
        final Query<TestQueryParams.E> q = getDs().find(TestQueryParams.E.class);
        q.field("_id").hasNoneOf(Collections.emptyList());
    }

    @Test
    public void testNullAcceptance() {
        // have to succeed:
        e.equal(null);
        e.notEqual(null);
        e.hasThisOne(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testStartsWithIgnoreCaseNull() {
        e.startsWithIgnoreCase(null);
    }

    @Test(expected = AssertionFailedException.class)
    public void testStartsWithNull() {
        e.startsWith(null);
    }

    @Entity
    static class E extends TestMapping.BaseEntity {}
}

