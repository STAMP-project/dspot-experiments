/**
 * *****************************************************************************
 * Copyright (c) 2015 EclipseSource and others.
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


import Platform.ANDROID;
import Platform.LINUX;
import Platform.MACOSX;
import Platform.WINDOWS;
import PlatformDetector.OS;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import org.junit.Assume;
import org.junit.Test;


public class LibraryLoaderTest {
    private String osName;

    private String vendor;

    private String arch;

    private Field releaseFilesField;

    private String[] releaseFiles;

    private static final String skipMessage = "Skipped test (Cannot detect other platforms when running on Android)";

    @Test
    public void testAndroidLibNameStructure() throws Exception {
        Assume.assumeFalse(LibraryLoaderTest.skipMessage, LibraryLoaderTest.skipTest());// conditional skip

        System.setProperty("os.name", "Android");
        System.setProperty("java.specification.vendor", "...");
        System.setProperty("os.arch", "x64");
        performTests(ANDROID, null, ".so");
        System.setProperty("os.name", "...");
        System.setProperty("java.specification.vendor", "Android");
        System.setProperty("os.arch", "x64");
        performTests(ANDROID, null, ".so");
    }

    @Test
    public void testLinuxLibNameStructure() throws Exception {
        // skip this test on android
        if (OS.isAndroid()) {
            return;
        }
        System.setProperty("os.name", "Linux");
        System.setProperty("java.specification.vendor", "OSS");
        System.setProperty("os.arch", "x64");
        final String os_release_test_path = "./test-mockup-os-release";
        final String test_vendor = "linux_vendor";
        // mock /etc/os-release file
        releaseFilesField.set(null, new String[]{ os_release_test_path });
        PrintWriter out = new PrintWriter(os_release_test_path);
        out.println((((("NAME=The-Linux-Vendor\n" + ("VERSION=\"towel_42\"\n" + "ID=")) + test_vendor) + "\n") + "VERSION_ID=42\n"));
        out.close();
        performTests(LINUX, test_vendor, ".so");
    }

    @Test
    public void testMacOSXLibNameStructure() throws Exception {
        Assume.assumeFalse(LibraryLoaderTest.skipMessage, LibraryLoaderTest.skipTest());// conditional skip

        System.setProperty("os.name", "MacOSX");
        System.setProperty("java.specification.vendor", "Apple");
        System.setProperty("os.arch", "x64");
        performTests(MACOSX, null, ".dylib");
    }

    @Test
    public void testWindowsLibNameStructure() throws Exception {
        Assume.assumeFalse(LibraryLoaderTest.skipMessage, LibraryLoaderTest.skipTest());// conditional skip

        System.setProperty("os.name", "Windows");
        System.setProperty("java.specification.vendor", "Microsoft");
        System.setProperty("os.arch", "x64");
        performTests(WINDOWS, null, ".dll");
    }
}

