/**
 * Copyright 2015 The Bazel Authors. All rights reserved.
 */
/**
 *
 */
/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
/**
 * you may not use this file except in compliance with the License.
 */
/**
 * You may obtain a copy of the License at
 */
/**
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
 */
/**
 * Unless required by applicable law or agreed to in writing, software
 */
/**
 * distributed under the License is distributed on an "AS IS" BASIS,
 */
/**
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
/**
 * See the License for the specific language governing permissions and
 */
/**
 * limitations under the License.
 */
package com.google.devtools.build.android.ziputils;


import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipInputStream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static DirectoryEntry.SIGNATURE;


/**
 * Unit tests for {@link DirectoryEntry}.
 */
@RunWith(JUnit4.class)
public class DirectoryEntryTest {
    /**
     * Test of viewOf method, of class DirectoryEntry.
     */
    @Test
    public void testViewOf() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 100; i++) {
            buffer.put(((byte) (i)));
        }
        int offset = 20;
        int filenameLength = 10;
        int extraLength = 6;
        int commentLength = 8;
        int marker = SIGNATURE;
        buffer.putShort((offset + (ZipInputStream.CENNAM)), ((short) (filenameLength)));// filename length

        buffer.putShort((offset + (ZipInputStream.CENEXT)), ((short) (extraLength)));// extra data length

        buffer.putShort((offset + (ZipInputStream.CENCOM)), ((short) (commentLength)));// comment length

        buffer.putInt(20, marker);// any marker

        buffer.position(offset);
        DirectoryEntry view = DirectoryEntry.viewOf(buffer);
        int expMark = ((int) (ZipInputStream.CENSIG));
        int expSize = (((ZipInputStream.CENHDR) + filenameLength) + extraLength) + commentLength;
        int expPos = 0;
        assertWithMessage("not based at current position").that(view.get(DirectoryEntry.CENSIG)).isEqualTo(expMark);
        assertWithMessage("Not slice with position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized with comment").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
    }

    /**
     * Test of view method, of class DirectoryEntry.
     */
    @Test
    public void testView_3Args() {
        String filename = "pkg/foo.class";
        String comment = "got milk";
        byte[] extraData = new byte[]{ 1, 2, 3, 4, 5, 6, 7, 8 };
        int expSize = (((ZipInputStream.CENHDR) + (filename.getBytes(StandardCharsets.UTF_8).length)) + (extraData.length)) + (comment.getBytes(StandardCharsets.UTF_8).length);
        int expPos = 0;
        DirectoryEntry view = DirectoryEntry.allocate(filename, extraData, comment);
        assertWithMessage("Incorrect filename").that(view.getFilename()).isEqualTo(filename);
        assertWithMessage("Incorrect extra data").that(view.getExtraData()).isEqualTo(extraData);
        assertWithMessage("Incorrect comment").that(view.getComment()).isEqualTo(comment);
        assertWithMessage("Not at position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized correctly").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
    }

    /**
     * Test of view method, of class DirectoryEntry.
     */
    @Test
    public void testView_4Args() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 100; i++) {
            buffer.put(((byte) (i)));
        }
        int offset = 20;
        buffer.position(offset);
        String filename = "pkg/foo.class";
        byte[] extraData = new byte[]{ 1, 2, 3, 4, 5 };
        String comment = "c";
        int expMark = ((int) (ZipInputStream.CENSIG));
        int expSize = ((46 + (filename.getBytes(StandardCharsets.UTF_8).length)) + (extraData.length)) + (comment.getBytes(StandardCharsets.UTF_8).length);
        int expPos = 0;
        DirectoryEntry view = DirectoryEntry.view(buffer, filename, extraData, comment);
        assertWithMessage("not based at current position").that(view.get(DirectoryEntry.CENSIG)).isEqualTo(expMark);
        assertWithMessage("Not slice with position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized with filename").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
        assertWithMessage("Incorrect filename").that(view.getFilename()).isEqualTo(filename);
        assertWithMessage("Incorrect extra data").that(view.getExtraData()).isEqualTo(extraData);
        assertWithMessage("Incorrect comment").that(view.getComment()).isEqualTo(comment);
    }

    /**
     * Test of copy method, of class DirectoryEntry.
     */
    @Test
    public void testCopy() {
        String filename = "pkg/foo.class";
        byte[] extraData = new byte[]{  };
        String comment = "always comment!";
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        DirectoryEntry view = DirectoryEntry.allocate(filename, extraData, comment);
        view.copy(buffer);
        int expSize = view.getSize();
        assertWithMessage("buffer not advanced as expected").that(buffer.position()).isEqualTo(expSize);
        buffer.position(0);
        DirectoryEntry clone = DirectoryEntry.viewOf(buffer);
        assertWithMessage("Fail to copy mark").that(clone.get(DirectoryEntry.CENSIG)).isEqualTo(view.get(DirectoryEntry.CENSIG));
        assertWithMessage("Fail to copy comment").that(clone.getFilename()).isEqualTo(view.getFilename());
        assertWithMessage("Fail to copy comment").that(clone.getExtraData()).isEqualTo(view.getExtraData());
        assertWithMessage("Fail to copy comment").that(clone.getComment()).isEqualTo(view.getComment());
    }

    /**
     * Test of with and get methods.
     */
    @Test
    public void testWithAndGetMethods() {
        int crc = 305419896;
        int compressed = 56095189;
        int uncompressed = 1954623833;
        short flags = 31329;
        short method = 15145;
        int time = 305210181;
        short version = 4660;
        short versionMadeBy = 10145;
        short disk = 23160;
        int extAttr = 1941076501;
        short intAttr = 14284;
        int offset = 1959344833;
        DirectoryEntry view = DirectoryEntry.allocate("pkg/foo.class", null, "").set(DirectoryEntry.CENCRC, crc).set(DirectoryEntry.CENSIZ, compressed).set(DirectoryEntry.CENLEN, uncompressed).set(DirectoryEntry.CENFLG, flags).set(DirectoryEntry.CENHOW, method).set(DirectoryEntry.CENTIM, time).set(DirectoryEntry.CENVER, version).set(DirectoryEntry.CENVEM, versionMadeBy).set(DirectoryEntry.CENDSK, disk).set(DirectoryEntry.CENATX, extAttr).set(DirectoryEntry.CENATT, intAttr).set(DirectoryEntry.CENOFF, offset);
        assertWithMessage("CRC").that(view.get(DirectoryEntry.CENCRC)).isEqualTo(crc);
        assertWithMessage("Compressed size").that(view.get(DirectoryEntry.CENSIZ)).isEqualTo(compressed);
        assertWithMessage("Uncompressed size").that(view.get(DirectoryEntry.CENLEN)).isEqualTo(uncompressed);
        assertWithMessage("Flags").that(view.get(DirectoryEntry.CENFLG)).isEqualTo(flags);
        assertWithMessage("Method").that(view.get(DirectoryEntry.CENHOW)).isEqualTo(method);
        assertWithMessage("Modified time").that(view.get(DirectoryEntry.CENTIM)).isEqualTo(time);
        assertWithMessage("Version needed").that(view.get(DirectoryEntry.CENVER)).isEqualTo(version);
        assertWithMessage("Version made by").that(view.get(DirectoryEntry.CENVEM)).isEqualTo(versionMadeBy);
        assertWithMessage("Disk").that(view.get(DirectoryEntry.CENDSK)).isEqualTo(disk);
        assertWithMessage("External attributes").that(view.get(DirectoryEntry.CENATX)).isEqualTo(extAttr);
        assertWithMessage("Internal attributes").that(view.get(DirectoryEntry.CENATT)).isEqualTo(intAttr);
        assertWithMessage("Offset").that(view.get(DirectoryEntry.CENOFF)).isEqualTo(offset);
    }
}

