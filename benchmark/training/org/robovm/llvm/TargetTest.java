/**
 * Copyright (C) 2013 RoboVM AB
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/gpl-2.0.html>.
 */
package org.robovm.llvm;


import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Tests {@link Target}.
 */
public class TargetTest {
    @Test
    public void testGetTargets() throws Exception {
        List<Target> all = Target.getTargets();
        Assert.assertFalse(all.isEmpty());
    }

    @Test
    public void testGetTargetsMap() throws Exception {
        Map<String, Target> map = Target.getTargetsMap();
        Assert.assertFalse(map.isEmpty());
        Target arm = map.get("arm");
        Assert.assertNotNull(arm);
        Target thumb = map.get("thumb");
        Assert.assertNotNull(thumb);
        Target x86 = map.get("x86");
        Assert.assertNotNull(x86);
        Target x86_64 = map.get("x86-64");
        Assert.assertNotNull(x86_64);
    }

    @Test
    public void testGetTarget() throws Exception {
        Target arm = Target.getTarget("arm");
        Assert.assertNotNull(arm);
        Assert.assertEquals("arm", arm.getName());
        Assert.assertEquals("ARM", arm.getDescription());
        Target x86 = Target.getTarget("x86");
        Assert.assertNotNull(x86);
        Assert.assertEquals("x86", x86.getName());
        Assert.assertEquals("32-bit X86: Pentium-Pro and above", x86.getDescription());
        try {
            Target.getTarget("foobar");
            Assert.fail("LlvmException expected");
        } catch (LlvmException e) {
        }
    }

    @Test
    public void testLookupTarget() throws Exception {
        Target t = Target.lookupTarget("thumbv7-unknown-ios");
        Assert.assertNotNull(t);
        Assert.assertEquals("thumb", t.getName());
        Assert.assertEquals("Thumb", t.getDescription());
        try {
            Target.lookupTarget("foobar");
            Assert.fail("LlvmException expected");
        } catch (LlvmException e) {
        }
    }

    @Test
    public void testCreateTargetMachine() throws Exception {
        Target t = Target.getTarget("thumb");
        try (TargetMachine tm = t.createTargetMachine("thumbv7-unknown-ios")) {
            Assert.assertNotNull(tm);
            Assert.assertEquals("thumbv7-unknown-ios", tm.getTriple());
            Assert.assertEquals(t, tm.getTarget());
        }
    }
}

