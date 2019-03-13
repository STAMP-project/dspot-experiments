/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006, University of Maryland
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
package edu.umd.cs.findbugs.ba.generic;


import org.apache.bcel.generic.Type;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pugh
 */
public class GenericUtilitiesTest {
    private static final String SAMPLE_SIGNATURE = "Lcom/sleepycat/persist/EntityJoin<TPK;TE;>.JoinForwardCursor<TV;>;";

    @Test
    public void testUnmatchedRightAngleBracket() {
        Assert.assertEquals(3, GenericUtilities.nextUnmatchedRightAngleBracket("<I>>", 0));
        Assert.assertEquals(1, GenericUtilities.nextUnmatchedRightAngleBracket("I><I>", 0));
    }

    @Test
    public void testMapSignature() {
        GenericObjectType t = ((GenericObjectType) (GenericUtilities.getType("Ljava/util/Map<Ljava/lang/String;Ljava/lang/Object;>;")));
        Assert.assertEquals(2, t.getNumParameters());
    }

    @Test
    public void testNestedSignatureParser() {
        GenericSignatureParser parser = new GenericSignatureParser((("(" + (GenericUtilitiesTest.SAMPLE_SIGNATURE)) + ")V"));
        Assert.assertEquals(1, parser.getNumParameters());
    }

    @Test
    public void testOKSignaturesThatHaveCausedProblems() {
        GenericUtilities.getType("[Ljava/util/Map$Entry<Ljava/lang/String;[B>;");
        GenericUtilities.getType("[Ljava/util/Map<Ljava/lang/String;[Ljava/lang/String;>;");
        GenericUtilities.getType("Lcom/palantir/finance/commons/service/calculator/Call<-Ljava/util/List<!*>;+Ljava/util/List<Ljava/lang/String;>;>;");
    }

    @Test
    public void testEclipseJDTInvalidUpperBoundSignature() {
        final Type type = GenericUtilities.getType("!+LHasUniqueKey<Ljava/lang/Integer;>;");
        Assert.assertThat(type, CoreMatchers.instanceOf(GenericObjectType.class));
        Assert.assertEquals("+", getVariable());
        Assert.assertEquals("HasUniqueKey<java.lang.Integer>", getExtension().toString());
    }

    @Test
    public void testEclipseJDTInvalidLowerBoundSignature() {
        final Type type = GenericUtilities.getType("!-LHasUniqueKey<Ljava/lang/Integer;>;");
        Assert.assertThat(type, CoreMatchers.instanceOf(GenericObjectType.class));
        Assert.assertEquals("-", getVariable());
        Assert.assertEquals("HasUniqueKey<java.lang.Integer>", getExtension().toString());
    }

    @Test
    public void testEclipseJDTInvalidWildcardSignature() {
        final Type type = GenericUtilities.getType("!*");
        Assert.assertThat(type, CoreMatchers.instanceOf(GenericObjectType.class));
        Assert.assertEquals("*", getVariable());
        Assert.assertNull(getExtension());
    }
}

