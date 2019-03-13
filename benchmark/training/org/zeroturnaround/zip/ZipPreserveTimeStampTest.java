/**
 * Copyright (C) 2012 ZeroTurnaround LLC <support@zeroturnaround.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.zeroturnaround.zip;


import java.io.File;
import java.util.zip.ZipFile;
import org.junit.ClassRule;
import org.junit.Test;


/**
 * These tests shouldn't be run with JDK8 as we preserve
 * lastModified, create, lastAccessed times for JDK8 and
 * the tests here that target the older API would fail.
 */
public class ZipPreserveTimeStampTest {
    private File srcZipFile;

    private File destZipFile;

    private ZipFile zf;

    @ClassRule
    public static final SkipIfZipEntryFileTimeNotAvailableRule skipRule = new SkipIfZipEntryFileTimeNotAvailableRule();

    @Test
    public void testPreservingTimestamps() {
        // this construct doesn't add any entries but will trigger a re-pack with
        // the same files and preserve the time stamps
        Zips.get(srcZipFile).addEntries(new ZipEntrySource[0]).preserveTimestamps().destination(destZipFile).process();
        validateTimeStamps();
    }

    @Test
    public void testPreservingTimestampsSetter() {
        // this construct doesn't add any entries but will trigger a re-pack with
        // the same files and preserve the time stamps
        Zips.get(srcZipFile).addEntries(new ZipEntrySource[0]).setPreserveTimestamps(true).destination(destZipFile).process();
        validateTimeStamps();
    }
}

