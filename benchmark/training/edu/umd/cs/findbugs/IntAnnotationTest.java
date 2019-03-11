/**
 * FindBugs - Find Bugs in Java programs
 * Copyright (C) 2003-2008 University of Maryland
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


/**
 *
 *
 * @author pugh
 */
public class IntAnnotationTest {
    @Test
    public void test() {
        Assert.assertEquals("0xffff", IntAnnotation.getShortInteger(65535));
        Assert.assertEquals("0x1ffff", IntAnnotation.getShortInteger(131071));
        Assert.assertEquals("0x1fffffff", IntAnnotation.getShortInteger(536870911));
        Assert.assertEquals("0x7fffffff", IntAnnotation.getShortInteger(2147483647));
        Assert.assertEquals("15", IntAnnotation.getShortInteger(15));
        Assert.assertEquals("255", IntAnnotation.getShortInteger(255));
        Assert.assertEquals("-1", IntAnnotation.getShortInteger((-1)));
        Assert.assertEquals("-2", IntAnnotation.getShortInteger((-2)));
        Assert.assertEquals("0xffffffff", IntAnnotation.getShortInteger(4294967295L));
        Assert.assertEquals("0x1ffffffff", IntAnnotation.getShortInteger(8589934591L));
        Assert.assertEquals("0xfffffffff", IntAnnotation.getShortInteger(68719476735L));
    }
}

