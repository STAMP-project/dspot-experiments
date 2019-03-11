/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2008 University of Maryland
 *
 *  Author: Graham Allan
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package edu.umd.cs.findbugs.filter;


import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.xml.XMLOutput;
import java.io.IOException;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public class NotMatcherTest {
    private final BugInstance bug = new BugInstance("UUF_UNUSED_FIELD", 0);

    @Test
    public void invertsResultsFromWrappedMatcher_doesntMatchWhenWrappedDoesMatch() throws Exception {
        Matcher wrappedMatcher = new NotMatcherTest.TestMatcher(true);
        NotMatcher notMatcher = new NotMatcher();
        notMatcher.addChild(wrappedMatcher);
        Assert.assertFalse(notMatcher.match(bug));
    }

    @Test
    public void invertsResultsFromWrappedMatcher_doesMatchWhenWrappedDoesnt() throws Exception {
        Matcher wrappedMatcher = new NotMatcherTest.TestMatcher(false);
        NotMatcher notMatcher = new NotMatcher();
        notMatcher.addChild(wrappedMatcher);
        Assert.assertTrue(notMatcher.match(bug));
    }

    @Test
    public void writeXMLOutputAddsNotTagsAroundWrappedMatchersOutput() throws Exception {
        Matcher wrappedMatcher = new NotMatcherTest.TestMatcher(true);
        NotMatcher notMatcher = new NotMatcher();
        notMatcher.addChild(wrappedMatcher);
        String xmlOutputCreated = writeXMLAndGetStringOutput(notMatcher);
        Assert.assertTrue(CoreMatchers.containsString("<Not>").matches(xmlOutputCreated));
        Assert.assertTrue(CoreMatchers.containsString("<TestMatch>").matches(xmlOutputCreated));
        Assert.assertTrue(CoreMatchers.containsString("</TestMatch>").matches(xmlOutputCreated));
        Assert.assertTrue(CoreMatchers.containsString("</Not>").matches(xmlOutputCreated));
    }

    @Test
    public void canReturnChildMatcher() {
        Matcher wrappedMatcher = new NotMatcherTest.TestMatcher(true);
        NotMatcher notMatcher = new NotMatcher();
        notMatcher.addChild(wrappedMatcher);
        Assert.assertSame("Should return child matcher.", wrappedMatcher, notMatcher.originalMatcher());
    }

    @Test(expected = IllegalStateException.class)
    public void throwsExceptionWhenTryingToGetNonExistentChildMatcher() {
        new NotMatcher().originalMatcher();
    }

    private static class TestMatcher implements Matcher {
        private final boolean alwaysMatches;

        public TestMatcher(boolean alwaysMatches) {
            this.alwaysMatches = alwaysMatches;
        }

        @Override
        public boolean match(BugInstance bugInstance) {
            return alwaysMatches;
        }

        @Override
        public void writeXML(XMLOutput xmlOutput, boolean disabled) throws IOException {
            xmlOutput.openTag("TestMatch");
            xmlOutput.closeTag("TestMatch");
        }
    }
}

