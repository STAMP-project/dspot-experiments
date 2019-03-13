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

import static EndOfCentralDirectory.SIGNATURE;


/**
 * Unit tests for {@link EndOfCentralDirectory}.
 */
@RunWith(JUnit4.class)
public class EndOfCentralDirectoryTest {
    @Test
    public void testViewOf() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 100; i++) {
            buffer.put(((byte) (i)));
        }
        int offset = 50;
        int marker = SIGNATURE;
        int comLength = 8;
        buffer.putInt(offset, marker);
        buffer.putShort((offset + (ZipInputStream.ENDCOM)), ((short) (comLength)));
        buffer.position(offset);
        EndOfCentralDirectory view = EndOfCentralDirectory.viewOf(buffer);
        int expMark = ((int) (ZipInputStream.ENDSIG));
        int expSize = (ZipInputStream.ENDHDR) + comLength;// fixed + comment

        int expPos = 0;
        assertWithMessage("not based at current position").that(view.get(EndOfCentralDirectory.ENDSIG)).isEqualTo(expMark);
        assertWithMessage("Not slice with position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized with comment").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
    }

    @Test
    public void testView_String() {
        String[] comments = new String[]{ "hello world", "", null };
        for (String comment : comments) {
            String expComment = (comment != null) ? comment : "";
            EndOfCentralDirectory view = EndOfCentralDirectory.allocate(comment);
            String commentResult = view.getComment();
            assertWithMessage("Incorrect comment").that(commentResult).isEqualTo(expComment);
            int expSize = (ZipInputStream.ENDHDR) + (comment != null ? comment.getBytes(StandardCharsets.UTF_8).length : 0);
            int expPos = 0;
            assertWithMessage("Not at position 0").that(view.buffer.position()).isEqualTo(expPos);
            assertWithMessage("Not sized correctly").that(view.getSize()).isEqualTo(expSize);
            assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
        }
    }

    @Test
    public void testView_ByteBuffer_String() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 100; i++) {
            buffer.put(((byte) (i)));
        }
        int offset = 50;
        buffer.position(offset);
        String comment = "this is a comment";
        EndOfCentralDirectory view = EndOfCentralDirectory.view(buffer, comment);
        int expMark = ((int) (ZipInputStream.ENDSIG));
        int expSize = (ZipInputStream.ENDHDR) + (comment.length());
        int expPos = 0;
        assertWithMessage("not based at current position").that(view.get(EndOfCentralDirectory.ENDSIG)).isEqualTo(expMark);
        assertWithMessage("Not slice with position 0").that(view.buffer.position()).isEqualTo(expPos);
        assertWithMessage("Not sized with comment").that(view.getSize()).isEqualTo(expSize);
        assertWithMessage("Not limited to size").that(view.buffer.limit()).isEqualTo(expSize);
        assertWithMessage("Incorrect comment").that(view.getComment()).isEqualTo(comment);
    }

    @Test
    public void testCopy() {
        ByteBuffer buffer = ByteBuffer.allocate(100).order(ByteOrder.LITTLE_ENDIAN);
        EndOfCentralDirectory view = EndOfCentralDirectory.allocate("comment");
        view.copy(buffer);
        int expSize = view.getSize();
        assertWithMessage("buffer not advanced as expected").that(buffer.position()).isEqualTo(expSize);
        buffer.position(0);
        EndOfCentralDirectory clone = EndOfCentralDirectory.viewOf(buffer);
        assertWithMessage("Fail to copy mark").that(clone.get(EndOfCentralDirectory.ENDSIG)).isEqualTo(view.get(EndOfCentralDirectory.ENDSIG));
        assertWithMessage("Fail to copy comment").that(clone.getComment()).isEqualTo(view.getComment());
    }

    @Test
    public void testWithAndGetMethods() {
        short cdDisk = ((short) (14018));
        int cdOffset = -1840594347;
        int cdSize = 327983668;
        short disk = ((short) (23570));
        short local = ((short) (19169));
        short total = ((short) (25534));
        EndOfCentralDirectory view = EndOfCentralDirectory.allocate("Hello World!").set(EndOfCentralDirectory.ENDDCD, cdDisk).set(EndOfCentralDirectory.ENDOFF, cdOffset).set(EndOfCentralDirectory.ENDSIZ, cdSize).set(EndOfCentralDirectory.ENDDSK, disk).set(EndOfCentralDirectory.ENDSUB, local).set(EndOfCentralDirectory.ENDTOT, total);
        assertWithMessage("Central directory start disk").that(view.get(EndOfCentralDirectory.ENDDCD)).isEqualTo(cdDisk);
        assertWithMessage("Central directory file offset").that(view.get(EndOfCentralDirectory.ENDOFF)).isEqualTo(cdOffset);
        assertWithMessage("Central directory size").that(view.get(EndOfCentralDirectory.ENDSIZ)).isEqualTo(cdSize);
        assertWithMessage("This disk number").that(view.get(EndOfCentralDirectory.ENDDSK)).isEqualTo(disk);
        assertWithMessage("Number of records on this disk").that(view.get(EndOfCentralDirectory.ENDSUB)).isEqualTo(local);
        assertWithMessage("Total number of central directory records").that(view.get(EndOfCentralDirectory.ENDTOT)).isEqualTo(total);
    }
}

