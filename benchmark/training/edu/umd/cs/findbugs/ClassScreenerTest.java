/**
 * FindBugs - Find bugs in Java programs
 * Copyright (C) 2004, University of Maryland
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
package edu.umd.cs.findbugs;


import org.junit.Assert;
import org.junit.Test;


public class ClassScreenerTest {
    private IClassScreener emptyScreener;

    private ClassScreener particularClassScreener;

    private ClassScreener particularPackageScreener;

    private ClassScreener particularPackageScreener2;

    private static final String FOOBAR_PACKAGE = "com.foobar";

    private static final String FOOBAR_PACKAGE_WITH_TRAILING_DOT = "com.foobar.";

    private static final String FURRYLEMUR_PACKAGE = "org.furrylemur";

    private static final String SOME_CLASS = (ClassScreenerTest.FOOBAR_PACKAGE) + ".SomeClass";

    private static final String SOME_OTHER_CLASS = (ClassScreenerTest.FOOBAR_PACKAGE) + ".SomeOtherClass";

    private static final String UNRELATED_THING_CLASS = (ClassScreenerTest.FURRYLEMUR_PACKAGE) + ".UnrelatedThing";

    private static final String SOME_CLASS_FILENAME = ClassScreenerTest.makeFileName(ClassScreenerTest.SOME_CLASS);

    private static final String SOME_OTHER_CLASS_FILENAME = ClassScreenerTest.makeFileName(ClassScreenerTest.SOME_OTHER_CLASS);

    private static final String UNRELATED_THING_CLASS_FILENAME = ClassScreenerTest.makeFileName(ClassScreenerTest.UNRELATED_THING_CLASS);

    private static final String SOME_CLASS_JARFILENAME = ClassScreenerTest.makeJarURL(ClassScreenerTest.SOME_CLASS_FILENAME);

    private static final String SOME_OTHER_CLASS_JARFILENAME = ClassScreenerTest.makeJarURL(ClassScreenerTest.SOME_OTHER_CLASS_FILENAME);

    private static final String UNRELATED_THING_CLASS_JARFILENAME = ClassScreenerTest.makeJarURL(ClassScreenerTest.UNRELATED_THING_CLASS_FILENAME);

    @Test
    public void testEmptyClassScreener() {
        Assert.assertTrue(emptyScreener.matches(ClassScreenerTest.SOME_CLASS_FILENAME));
        Assert.assertTrue(emptyScreener.matches(ClassScreenerTest.SOME_OTHER_CLASS_FILENAME));
        Assert.assertTrue(emptyScreener.matches(ClassScreenerTest.UNRELATED_THING_CLASS_FILENAME));
        Assert.assertTrue(emptyScreener.matches(ClassScreenerTest.SOME_CLASS_JARFILENAME));
        Assert.assertTrue(emptyScreener.matches(ClassScreenerTest.SOME_OTHER_CLASS_JARFILENAME));
        Assert.assertTrue(emptyScreener.matches(ClassScreenerTest.UNRELATED_THING_CLASS_JARFILENAME));
    }

    @Test
    public void testParticularClassScreener() {
        Assert.assertTrue(particularClassScreener.matches(ClassScreenerTest.SOME_CLASS_FILENAME));
        Assert.assertFalse(particularClassScreener.matches(ClassScreenerTest.SOME_OTHER_CLASS_FILENAME));
        Assert.assertFalse(particularClassScreener.matches(ClassScreenerTest.UNRELATED_THING_CLASS_FILENAME));
        Assert.assertTrue(particularClassScreener.matches(ClassScreenerTest.SOME_CLASS_JARFILENAME));
        Assert.assertFalse(particularClassScreener.matches(ClassScreenerTest.SOME_OTHER_CLASS_JARFILENAME));
        Assert.assertFalse(particularClassScreener.matches(ClassScreenerTest.UNRELATED_THING_CLASS_JARFILENAME));
    }

    @Test
    public void testParticularPackageScreener() {
        testPackageScreener(particularPackageScreener);
        testPackageScreener(particularPackageScreener2);
    }
}

