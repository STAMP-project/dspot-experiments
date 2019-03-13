/**
 * Copyright (c) 2017 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockito.internal.matchers.text;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockitoutil.TestBase;


public class MatcherToStringTest extends TestBase {
    static class MatcherWithoutDescription implements ArgumentMatcher<Object> {
        public boolean matches(Object argument) {
            return false;
        }
    }

    static class MatcherWithDescription implements ArgumentMatcher<Object> {
        public boolean matches(Object argument) {
            return false;
        }

        public String toString() {
            return "*my custom description*";
        }
    }

    static class MatcherWithInheritedDescription extends MatcherToStringTest.MatcherWithDescription {
        public boolean matches(Object argument) {
            return false;
        }
    }

    @Test
    public void better_toString_for_matchers() {
        Assert.assertEquals("<Matcher without description>", MatcherToString.toString(new MatcherToStringTest.MatcherWithoutDescription()));
        Assert.assertEquals("*my custom description*", MatcherToString.toString(new MatcherToStringTest.MatcherWithDescription()));
        Assert.assertEquals("*my custom description*", MatcherToString.toString(new MatcherToStringTest.MatcherWithInheritedDescription()));
    }
}

