package org.opentripplanner.routing.core;


import junit.framework.TestCase;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Route;


public class TestRouteMatcher extends TestCase {
    public void testRouteMatcher() {
        Route r1 = new Route();
        r1.setId(new FeedScopedId("A1", "42"));
        r1.setShortName("R1");
        Route r2 = new Route();
        r2.setId(new FeedScopedId("A1", "43"));
        r2.setShortName("R2");
        Route r1b = new Route();
        r1b.setId(new FeedScopedId("A2", "42"));
        r1b.setShortName("R1");
        Route r3 = new Route();
        r3.setId(new FeedScopedId("A1", "44"));
        r3.setShortName("R3_b");
        RouteMatcher emptyMatcher = RouteMatcher.emptyMatcher();
        TestCase.assertFalse(emptyMatcher.matches(r1));
        TestCase.assertFalse(emptyMatcher.matches(r1b));
        TestCase.assertFalse(emptyMatcher.matches(r2));
        RouteMatcher matcherR1i = RouteMatcher.parse("A1__42");
        TestCase.assertTrue(matcherR1i.matches(r1));
        TestCase.assertFalse(matcherR1i.matches(r1b));
        TestCase.assertFalse(matcherR1i.matches(r2));
        RouteMatcher matcherR2n = RouteMatcher.parse("A1_R2");
        TestCase.assertFalse(matcherR2n.matches(r1));
        TestCase.assertFalse(matcherR2n.matches(r1b));
        TestCase.assertTrue(matcherR2n.matches(r2));
        RouteMatcher matcherR1R2 = RouteMatcher.parse("A1_R1,A1__43,A2__43");
        TestCase.assertTrue(matcherR1R2.matches(r1));
        TestCase.assertFalse(matcherR1R2.matches(r1b));
        TestCase.assertTrue(matcherR1R2.matches(r2));
        RouteMatcher matcherR1n = RouteMatcher.parse("_R1");
        TestCase.assertTrue(matcherR1n.matches(r1));
        TestCase.assertTrue(matcherR1n.matches(r1b));
        TestCase.assertFalse(matcherR1n.matches(r2));
        RouteMatcher matcherR1R1bR2 = RouteMatcher.parse("A1_R1,A2_R1,A1_R2");
        TestCase.assertTrue(matcherR1R1bR2.matches(r1));
        TestCase.assertTrue(matcherR1R1bR2.matches(r1b));
        TestCase.assertTrue(matcherR1R1bR2.matches(r2));
        RouteMatcher matcherR3e = RouteMatcher.parse("A1_R3 b");
        TestCase.assertFalse(matcherR3e.matches(r1));
        TestCase.assertFalse(matcherR3e.matches(r1b));
        TestCase.assertFalse(matcherR3e.matches(r2));
        TestCase.assertTrue(matcherR3e.matches(r3));
        RouteMatcher nullList = RouteMatcher.parse(null);
        TestCase.assertTrue((nullList == (RouteMatcher.emptyMatcher())));
        RouteMatcher emptyList = RouteMatcher.parse("");
        TestCase.assertTrue((emptyList == (RouteMatcher.emptyMatcher())));
        RouteMatcher degenerate = RouteMatcher.parse(",,,");
        TestCase.assertTrue((degenerate == (RouteMatcher.emptyMatcher())));
        boolean thrown = false;
        try {
            RouteMatcher badMatcher = RouteMatcher.parse("A1_R1_42");
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        TestCase.assertTrue(thrown);
        Route r1c = new Route();
        r1c.setId(new FeedScopedId("A_1", "R_42"));
        r1c.setShortName("R_42");
        RouteMatcher matcherR1c = RouteMatcher.parse("A\\_1_R 42");
        TestCase.assertTrue(matcherR1c.matches(r1c));
        TestCase.assertFalse(matcherR1c.matches(r1));
        TestCase.assertFalse(matcherR1c.matches(r1b));
        RouteMatcher matcherR1c2 = RouteMatcher.parse("A\\_1__R\\_42");
        TestCase.assertTrue(matcherR1c2.matches(r1c));
        TestCase.assertFalse(matcherR1c2.matches(r1));
        TestCase.assertFalse(matcherR1c2.matches(r1b));
    }
}

