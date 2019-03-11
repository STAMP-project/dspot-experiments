/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2008 University of Maryland
 *
 *  Author: Andrey Loskutov
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
import edu.umd.cs.findbugs.ClassAnnotation;
import edu.umd.cs.findbugs.SourceLineAnnotation;
import org.apache.tools.ant.filters.StringInputStream;
import org.junit.Assert;
import org.junit.Test;


public class SourceMatcherTest {
    private BugInstance bug;

    private String fileName;

    @Test
    public void writeXML() throws Exception {
        SourceMatcher sm = new SourceMatcher(fileName);
        String xmlOutput = writeXMLAndGetStringOutput(sm, false);
        Assert.assertEquals((("<Source name=\"" + (fileName)) + "\"/>"), xmlOutput);
        sm = new SourceMatcher(fileName);
        xmlOutput = writeXMLAndGetStringOutput(sm, true);
        Assert.assertEquals((("<Source name=\"" + (fileName)) + "\" disabled=\"true\"/>"), xmlOutput);
    }

    @Test
    public void readXML() throws Exception {
        SourceMatcher sm = new SourceMatcher(fileName);
        String xml = writeXMLAndGetStringOutput(sm, false);
        xml = ((("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + (("\n<FindBugsFilter>" + "\n<Match>") + "\n")) + xml) + "\n</Match>") + "\n</FindBugsFilter>\n";
        Filter filter = new Filter(new StringInputStream(xml));
        Assert.assertFalse(filter.match(bug));
        bug.addClass("bla", fileName);
        Assert.assertTrue(filter.match(bug));
    }

    @Test
    public void match() throws Exception {
        SourceMatcher sm = new SourceMatcher(fileName);
        // no source set: test incomplete data
        Assert.assertFalse(sm.match(bug));
        bug.addClass("bla", null);
        Assert.assertFalse(sm.match(bug));
        ClassAnnotation primaryClass = bug.getPrimaryClass();
        primaryClass.setSourceLines(SourceLineAnnotation.createUnknown("bla", ""));
        Assert.assertFalse(sm.match(bug));
        // set right source file
        primaryClass.setSourceLines(SourceLineAnnotation.createUnknown("bla", fileName));
        // exact match
        Assert.assertTrue(sm.match(bug));
        // regexp first part
        sm = new SourceMatcher("~bla.*");
        Assert.assertTrue(sm.match(bug));
        sm = new SourceMatcher("~blup.*");
        Assert.assertFalse(sm.match(bug));
        // regexp second part
        sm = new SourceMatcher("~.*\\.groovy");
        Assert.assertTrue(sm.match(bug));
        sm = new SourceMatcher("~.*\\.java");
        Assert.assertFalse(sm.match(bug));
    }
}

