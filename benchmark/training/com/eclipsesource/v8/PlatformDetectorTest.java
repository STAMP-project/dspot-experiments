/**
 * *****************************************************************************
 * Copyright (c) 2017 EclipseSource and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    EclipseSource - initial API and implementation
 *    Wolfgang Steiner - code separation PlatformDetector/LibraryLoader
 * ****************************************************************************
 */
package com.eclipsesource.v8;


import PlatformDetector.Arch;
import PlatformDetector.OS;
import PlatformDetector.Vendor;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;


public class PlatformDetectorTest {
    private String osName;

    private String vendor;

    private String arch;

    private static final String skipMessage = "Skipped test (Cannot detect other platforms when running on Android)";

    @Test
    public void testGetOSUnknown() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.name", "???");
        System.setProperty("java.specification.vendor", "???");
        try {
            OS.getName();
        } catch (Error e) {
            Assert.assertTrue("Expected UnsatisfiedLinkError", (e instanceof UnsatisfiedLinkError));
            Assert.assertTrue(e.getMessage().startsWith("Unsupported platform/vendor"));
            return;
        }
        Assert.fail("Expected exception");
    }

    @Test
    public void testGetOSMac() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.name", "Mac OS X");
        System.setProperty("java.specification.vendor", "Apple");
        Assert.assertEquals("macosx", OS.getName());
    }

    @Test
    public void testGetOSLinux() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.name", "Linux");
        System.setProperty("java.specification.vendor", "OSS");
        Assert.assertEquals("linux", OS.getName());
    }

    @Test
    public void testGetOSWindows() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.name", "Windows");
        System.setProperty("java.specification.vendor", "Microsoft");
        Assert.assertEquals("windows", OS.getName());
    }

    @Test
    public void testGetOSAndroid() {
        System.setProperty("os.name", "Linux");
        System.setProperty("java.specification.vendor", "The Android Project");
        Assert.assertEquals("android", OS.getName());
    }

    @Test
    public void testGetOSFileExtensionAndroid() {
        System.setProperty("os.name", "naclthe android project");
        System.setProperty("java.specification.vendor", "The Android Project");
        Assert.assertEquals("so", OS.getLibFileExtension());
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void testGetArchxNaCl() {
        System.setProperty("os.arch", "nacl");
        Arch.getName();
    }

    @Test
    public void testGetArchaarch64() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.arch", "aarch64");
        Assert.assertEquals("aarch_64", Arch.getName());
    }

    @Test
    public void testGetArchx86() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.arch", "x86");
        Assert.assertEquals("x86_32", Arch.getName());
    }

    @Test
    public void testGetArchx86_64() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.arch", "x86_64");
        Assert.assertEquals("x86_64", Arch.getName());
    }

    @Test
    public void testGetArchx64FromAmd64() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.arch", "amd64");
        Assert.assertEquals("x86_64", Arch.getName());
    }

    @Test(expected = UnsatisfiedLinkError.class)
    public void testGetArcharmv7l() {
        System.setProperty("os.arch", "armv7l");
        Arch.getName();
    }

    @Test
    public void test686isX86() {
        Assume.assumeFalse(PlatformDetectorTest.skipMessage, PlatformDetectorTest.skipTest());// conditional skip

        System.setProperty("os.arch", "i686");
        Assert.assertEquals("x86_32", Arch.getName());
    }

    @Test
    public void testVendor_Alpine() {
        if (!(isAlpineLinux())) {
            return;
        }
        Assert.assertEquals("alpine", Vendor.getName());
    }
}

