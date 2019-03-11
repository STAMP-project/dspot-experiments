/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.processors.evtx.parser;


import FileHeader.ELF_FILE;
import java.util.Random;
import org.junit.Assert;
import org.junit.Test;


public class FileHeaderTest {
    private final Random random = new Random();

    private FileHeader fileHeader;

    private int oldestChunk = 111;

    private int currentChunkNumber = 112;

    private int nextRecordNumber = 113;

    private int headerSize = 114;

    private int minorVersion = 1;

    private int majorVersion = 3;

    private int headerChunkSize = 4096;

    private int chunkCount = 0;

    private int flags = 340942;

    @Test
    public void testInit() {
        Assert.assertEquals(ELF_FILE, fileHeader.getMagicString());
        Assert.assertEquals(oldestChunk, fileHeader.getOldestChunk().intValue());
        Assert.assertEquals(currentChunkNumber, fileHeader.getCurrentChunkNumber().intValue());
        Assert.assertEquals(nextRecordNumber, fileHeader.getNextRecordNumber().intValue());
        Assert.assertEquals(headerSize, fileHeader.getHeaderSize().intValue());
        Assert.assertEquals(minorVersion, fileHeader.getMinorVersion());
        Assert.assertEquals(majorVersion, fileHeader.getMajorVersion());
        Assert.assertEquals(headerChunkSize, fileHeader.getHeaderChunkSize());
        Assert.assertEquals(chunkCount, fileHeader.getChunkCount());
        Assert.assertEquals(flags, fileHeader.getFlags().intValue());
    }
}

