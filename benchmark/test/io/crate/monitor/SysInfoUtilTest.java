/**
 * Licensed to Crate under one or more contributor license agreements.
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.  Crate licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * However, if you have executed another commercial license agreement
 * with Crate these terms will supersede the license and you may use the
 * software solely pursuant to the terms of the relevant commercial
 * agreement.
 */
package io.crate.monitor;


import SysInfo.Builder;
import io.crate.test.integration.CrateUnitTest;
import java.util.List;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.Is;
import org.junit.Test;


public class SysInfoUtilTest extends CrateUnitTest {
    private static final Builder SYSINFO_BUILDER = new SysInfo.Builder();

    private static final AnyOf<String> X86_64 = AnyOf.anyOf(Is.is("x86_64"), Is.is("amd64"), Is.is("x64"));

    @Test
    public void testParseKeyValue() {
        assertThat(Builder.parseKeyValuePair("KEY=\"val\"ue\""), Is.is(new String[]{ "KEY", "val\"ue" }));
        assertThat(Builder.parseKeyValuePair("KEY=\"value\""), Is.is(new String[]{ "KEY", "value" }));
        assertThat(Builder.parseKeyValuePair("KEY=value"), Is.is(new String[]{ "KEY", "value" }));
        assertThat(Builder.parseKeyValuePair("KEY="), Is.is(new String[]{ "KEY", "" }));
        assertThat(Builder.parseKeyValuePair("KEY"), Is.is(new String[]{ "KEY", "" }));
        assertThat(Builder.parseKeyValuePair(""), Is.is(new String[]{ "", "" }));
    }

    @Test
    public void testWindows() {
        SysInfo sysInfo = new SysInfo.Builder().withName("Windows 10").withVersion("10.0").withArch("x86_64").gather();
        assertThat(sysInfo.arch(), Is.is("x86_64"));
        assertThat(sysInfo.description(), Is.is("Microsoft Windows 10"));
        assertThat(sysInfo.machine(), SysInfoUtilTest.X86_64);
        assertThat(sysInfo.name(), Is.is("Win32"));
        assertThat(sysInfo.patchLevel(), Is.is(""));
        assertThat(sysInfo.vendor(), Is.is("Microsoft"));
        assertThat(sysInfo.vendorCodeName(), Is.is(""));
        assertThat(sysInfo.vendorName(), Is.is("Windows 10"));
        assertThat(sysInfo.vendorVersion(), Is.is("10"));
        assertThat(sysInfo.version(), Is.is("10.0"));
    }

    @Test
    public void testMacOS() {
        SysInfo sysInfo = new SysInfo.Builder().withName("Mac OS").withVersion("10.12.6").withArch("x86_64").gather();
        assertThat(sysInfo.arch(), Is.is("x86_64"));
        assertThat(sysInfo.description(), Is.is("Mac OS X (Sierra)"));
        assertThat(sysInfo.machine(), SysInfoUtilTest.X86_64);
        assertThat(sysInfo.name(), Is.is("MacOSX"));
        assertThat(sysInfo.patchLevel(), Is.is(""));
        assertThat(sysInfo.vendor(), Is.is("Apple"));
        assertThat(sysInfo.vendorCodeName(), Is.is("Sierra"));
        assertThat(sysInfo.vendorName(), Is.is("Mac OS X"));
        assertThat(sysInfo.vendorVersion(), Is.is("10.12"));
        assertThat(sysInfo.version(), Is.is("10.12.6"));
    }

    @Test
    public void testDarwin() {
        SysInfo sysInfo = new SysInfo.Builder().withName("Darwin").withVersion("16.6.0").withArch("x86_64").gather();
        assertThat(sysInfo.arch(), Is.is("x86_64"));
        assertThat(sysInfo.description(), Is.is("Mac OS X (Sierra)"));
        assertThat(sysInfo.machine(), SysInfoUtilTest.X86_64);
        assertThat(sysInfo.name(), Is.is("MacOSX"));
        assertThat(sysInfo.patchLevel(), Is.is(""));
        assertThat(sysInfo.vendor(), Is.is("Apple"));
        assertThat(sysInfo.vendorCodeName(), Is.is("Sierra"));
        assertThat(sysInfo.vendorName(), Is.is("Mac OS X"));
        assertThat(sysInfo.vendorVersion(), Is.is("16.6"));
        assertThat(sysInfo.version(), Is.is("16.6.0"));
    }

    @Test
    public void testParseRedHatVendorCentOs() {
        SysInfo sysInfo = new SysInfo();
        String release = "CentOS Linux release 7.4.1708 (Core)";
        SysInfoUtilTest.SYSINFO_BUILDER.parseRedHatVendorLine(sysInfo, release);
        assertThat(sysInfo.vendor(), Is.is("CentOS"));
        assertThat(sysInfo.vendorCodeName(), Is.is("Core"));
    }

    @Test
    public void testParseRedHatVendorRhel() {
        SysInfo sysInfo = new SysInfo();
        String release = "Red Hat Enterprise Linux Server release 6.7 (Santiago)";
        SysInfoUtilTest.SYSINFO_BUILDER.parseGenericVendorLine(sysInfo, release);
        assertThat(sysInfo.vendorVersion(), Is.is("6.7"));// required for parseRedHatVendorLine()

        SysInfoUtilTest.SYSINFO_BUILDER.parseRedHatVendorLine(sysInfo, release);
        assertThat(sysInfo.vendorVersion(), Is.is("Enterprise Linux 6"));
        assertThat(sysInfo.vendorCodeName(), Is.is("Santiago"));
    }

    @Test
    public void testGenericVendor() {
        assertVendorVersionFromGenericLine("", "");
        assertVendorVersionFromGenericLine("8", "8");
        assertVendorVersionFromGenericLine("8.10", "8.10");
        assertVendorVersionFromGenericLine("8.10.1", "8.10.1");
        assertVendorVersionFromGenericLine("buster/sid", "");
        assertVendorVersionFromGenericLine("jessie", "");
        assertVendorVersionFromGenericLine("jessie 8.10", "8.10");
        assertVendorVersionFromGenericLine("9.1 stretch", "9.1");
        assertVendorVersionFromGenericLine("9.1.x", "9.1.");
    }

    @Test
    public void testFailedSysCall() {
        // by default sys call filter is enabled, so any Runtime.getRuntime().exec(...) will fail
        List<String> result = SysInfo.sysCall(new String[]{ "undefined" }, "default");
        assertThat(result.get(0), Is.is("default"));
    }
}

