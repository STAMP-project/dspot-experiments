/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers;


import java.util.regex.Pattern;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockitoutil.TestBase;


public class MatchersToStringTest extends TestBase {
    @Test
    public void sameToStringWithString() {
        Assert.assertEquals("same(\"X\")", new Same("X").toString());
    }

    @Test
    public void nullToString() {
        Assert.assertEquals("isNull()", Null.NULL.toString());
    }

    @Test
    public void notNullToString() {
        Assert.assertEquals("notNull()", NotNull.NOT_NULL.toString());
    }

    @Test
    public void anyToString() {
        Assert.assertEquals("<any>", Any.ANY.toString());
    }

    @Test
    public void sameToStringWithChar() {
        Assert.assertEquals("same('x')", new Same('x').toString());
    }

    @Test
    public void sameToStringWithObject() {
        Object o = new Object() {
            @Override
            public String toString() {
                return "X";
            }
        };
        Assert.assertEquals("same(X)", new Same(o).toString());
    }

    @Test
    public void equalsToStringWithString() {
        Assert.assertEquals("\"X\"", new Equals("X").toString());
    }

    @Test
    public void equalsToStringWithChar() {
        Assert.assertEquals("'x'", new Equals('x').toString());
    }

    @Test
    public void equalsToStringWithObject() {
        Object o = new Object() {
            @Override
            public String toString() {
                return "X";
            }
        };
        Assert.assertEquals("X", new Equals(o).toString());
    }

    @Test
    public void orToString() {
        ArgumentMatcher<?> m1 = new Equals(1);
        ArgumentMatcher<?> m2 = new Equals(2);
        Assert.assertEquals("or(1, 2)", new Or(m1, m2).toString());
    }

    @Test
    public void notToString() {
        Assert.assertEquals("not(1)", new Not(new Equals(1)).toString());
    }

    @Test
    public void andToString() {
        ArgumentMatcher<?> m1 = new Equals(1);
        ArgumentMatcher<?> m2 = new Equals(2);
        Assert.assertEquals("and(1, 2)", new And(m1, m2).toString());
    }

    @Test
    public void startsWithToString() {
        Assert.assertEquals("startsWith(\"AB\")", new StartsWith("AB").toString());
    }

    @Test
    public void endsWithToString() {
        Assert.assertEquals("endsWith(\"AB\")", new EndsWith("AB").toString());
    }

    @Test
    public void containsToString() {
        Assert.assertEquals("contains(\"AB\")", new Contains("AB").toString());
    }

    @Test
    public void findToString() {
        Assert.assertEquals("find(\"\\\\s+\")", new Find("\\s+").toString());
    }

    @Test
    public void matchesToString() {
        Assert.assertEquals("matches(\"\\\\s+\")", new Matches("\\s+").toString());
        Assert.assertEquals("matches(\"\\\\s+\")", new Matches(Pattern.compile("\\s+")).toString());
    }
}

