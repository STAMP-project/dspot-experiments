/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2006,2008 University of Maryland
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
package edu.umd.cs.findbugs.util;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author pugh
 */
public class ClassNameTest {
    @Test
    public void testExtractPackagePrefix() {
        Assert.assertEquals("", ClassName.extractPackagePrefix("org.apache.ant.subpkg.sub2", 0));
        Assert.assertEquals("org", ClassName.extractPackagePrefix("org", 1));
        Assert.assertEquals("org.apache.ant", ClassName.extractPackagePrefix("org.apache.ant.subpkg.sub2", 3));
        Assert.assertEquals("org.apache.ant.subpkg.sub2", ClassName.extractPackagePrefix("org.apache.ant.subpkg.sub2", 5));
        Assert.assertEquals("org.apache.ant.subpkg.sub2", ClassName.extractPackagePrefix("org.apache.ant.subpkg.sub2", 6));
    }

    @Test
    public void testExtractClassName() {
        Assert.assertEquals("java/lang/Integer", ClassName.extractClassName("Ljava/lang/Integer;"));
        Assert.assertEquals("java/lang/Integer", ClassName.extractClassName("[Ljava/lang/Integer;"));
        Assert.assertEquals("java/lang/Integer", ClassName.extractClassName("[[Ljava/lang/Integer;"));
        Assert.assertEquals("java/lang/Integer", ClassName.extractClassName("[[[Ljava/lang/Integer;"));
        Assert.assertEquals("java/lang/Integer", ClassName.extractClassName("java/lang/Integer"));
    }

    @Test
    public void testGetPrimitiveType() {
        Assert.assertEquals("I", ClassName.getPrimitiveType("java/lang/Integer"));
        Assert.assertEquals("F", ClassName.getPrimitiveType("java/lang/Float"));
        Assert.assertEquals("D", ClassName.getPrimitiveType("java/lang/Double"));
        Assert.assertEquals("J", ClassName.getPrimitiveType("java/lang/Long"));
        Assert.assertEquals("B", ClassName.getPrimitiveType("java/lang/Byte"));
        Assert.assertEquals("C", ClassName.getPrimitiveType("java/lang/Character"));
        Assert.assertEquals("S", ClassName.getPrimitiveType("java/lang/Short"));
        Assert.assertEquals("Z", ClassName.getPrimitiveType("java/lang/Boolean"));
        Assert.assertNull(ClassName.getPrimitiveType("java/lang/String"));
        Assert.assertNull(ClassName.getPrimitiveType("java/util/HashMap"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtractClassNameBad() {
        ClassName.extractClassName("L[Ljava/lang/Integer;");
    }
}

