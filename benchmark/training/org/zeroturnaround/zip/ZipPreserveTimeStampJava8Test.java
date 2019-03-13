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
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;


public class ZipPreserveTimeStampJava8Test {
    private File srcZipFile;

    private File destZipFile;

    private ZipFile zf;

    @ClassRule
    public static final SkipIfZipEntryFileTimeNotAvailableRule skipRule = new SkipIfZipEntryFileTimeNotAvailableRule();

    @Test
    public void testDontPreserveTime() {
        // this construct doesn't add any entries but will trigger a re-pack with
        // the same files and preserve the time stamps
        Zips.get(srcZipFile).addEntries(new ZipEntrySource[0]).destination(destZipFile).process();
        Zips.get(destZipFile).iterate(new ZipEntryCallback() {
            public void process(InputStream in, ZipEntry zipEntry) throws IOException {
                String name = zipEntry.getName();
                Assert.assertNotEquals(zf.getEntry(name).getLastModifiedTime(), zipEntry.getLastModifiedTime());
            }
        });
    }

    @Test
    public void testPreserveTime() {
        // this construct doesn't add any entries but will trigger a re-pack with
        // the same files and preserve the time stamps
        Zips.get(srcZipFile).addEntries(new ZipEntrySource[0]).preserveTimestamps().destination(destZipFile).process();
        validateTimeStampEquality();
    }

    @Test
    public void testPreserveTimeWithSetter() {
        // this construct doesn't add any entries but will trigger a re-pack with
        // the same files and preserve the time stamps
        Zips.get(srcZipFile).addEntries(new ZipEntrySource[0]).setPreserveTimestamps(true).destination(destZipFile).process();
        validateTimeStampEquality();
    }
}

