package org.opentripplanner.routing.core;


import junit.framework.TestCase;
import org.opentripplanner.model.FeedScopedId;
import org.opentripplanner.model.Stop;


public class TestStopMatcher extends TestCase {
    /**
     * Test different stop matchers
     */
    public void testStopMatcher() {
        Stop s1 = new Stop();
        s1.setId(new FeedScopedId("A1", "42"));
        Stop s2 = new Stop();
        s2.setId(new FeedScopedId("A1", "43"));
        StopMatcher emptyMatcher = StopMatcher.emptyMatcher();
        TestCase.assertFalse(emptyMatcher.matches(s1));
        TestCase.assertFalse(emptyMatcher.matches(s2));
        StopMatcher matcherS1 = StopMatcher.parse("A1:42");
        TestCase.assertTrue(matcherS1.matches(s1));
        TestCase.assertFalse(matcherS1.matches(s2));
        StopMatcher matcherS2 = StopMatcher.parse("A1:43");
        TestCase.assertFalse(matcherS2.matches(s1));
        TestCase.assertTrue(matcherS2.matches(s2));
        StopMatcher matcherS1S2 = StopMatcher.parse("A1:42,A1:43");
        TestCase.assertTrue(matcherS1S2.matches(s1));
        TestCase.assertTrue(matcherS1S2.matches(s2));
        StopMatcher nullList = StopMatcher.parse(null);
        TestCase.assertTrue(nullList.isEmpty());
        StopMatcher emptyList = StopMatcher.parse("");
        TestCase.assertTrue(emptyList.isEmpty());
        StopMatcher degenerate = StopMatcher.parse(",,,");
        TestCase.assertTrue(degenerate.isEmpty());
        boolean thrown = false;
        try {
            @SuppressWarnings("unused")
            StopMatcher badMatcher = StopMatcher.parse("A1");
        } catch (IllegalArgumentException e) {
            thrown = true;
        }
        TestCase.assertTrue(thrown);
    }

    /**
     * Test different stop matchers including stops with parents
     */
    public void testStopMatcherParents() {
        Stop parent = new Stop();
        parent.setId(new FeedScopedId("A1", "10"));
        Stop s1 = new Stop();
        s1.setId(new FeedScopedId("A1", "42"));
        s1.setParentStation("10");
        Stop s2 = new Stop();
        s2.setId(new FeedScopedId("A1", "43"));
        s2.setParentStation("10");
        StopMatcher matcherParent = StopMatcher.parse("A1:10");
        TestCase.assertTrue(matcherParent.matches(parent));
        TestCase.assertTrue(matcherParent.matches(s1));
        TestCase.assertTrue(matcherParent.matches(s2));
        StopMatcher matcherS1 = StopMatcher.parse("A1:42");
        TestCase.assertFalse(matcherS1.matches(parent));
        TestCase.assertTrue(matcherS1.matches(s1));
        TestCase.assertFalse(matcherS1.matches(s2));
        StopMatcher matcherS2 = StopMatcher.parse("A1:43");
        TestCase.assertFalse(matcherS2.matches(parent));
        TestCase.assertFalse(matcherS2.matches(s1));
        TestCase.assertTrue(matcherS2.matches(s2));
        StopMatcher matcherS1S2 = StopMatcher.parse("A1:42,A1:43");
        TestCase.assertFalse(matcherS1S2.matches(parent));
        TestCase.assertTrue(matcherS1S2.matches(s1));
        TestCase.assertTrue(matcherS1S2.matches(s2));
    }
}

