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


import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.nifi.processors.evtx.parser.bxml.BxmlNode;
import org.apache.nifi.processors.evtx.parser.bxml.EndOfStreamNode;
import org.apache.nifi.processors.evtx.parser.bxml.NameStringNode;
import org.apache.nifi.processors.evtx.parser.bxml.RootNode;
import org.apache.nifi.processors.evtx.parser.bxml.TemplateNode;
import org.junit.Assert;
import org.junit.Test;


public class ChunkHeaderTest {
    private final Random random = new Random();

    private int headerOffset = 101;

    private int chunkNumber = 102;

    private ChunkHeader chunkHeader;

    private int fileFirstRecordNumber = 103;

    private int fileLastRecordNumber = 105;

    private int logFirstRecordNumber = 106;

    private int logLastRecordNumber = 107;

    private int headerSize = 112;

    private int lastRecordOffset = 12380;

    private int nextRecordOffset = 2380;

    private int dataChecksum = 1111;

    private List<String> guids;

    @Test
    public void testInit() throws IOException {
        int count = 0;
        for (Map.Entry<Integer, NameStringNode> integerNameStringNodeEntry : new java.util.TreeMap(chunkHeader.getNameStrings()).entrySet()) {
            Assert.assertEquals(Integer.toString((count++)), integerNameStringNodeEntry.getValue().getString());
        }
        Iterator<String> iterator = guids.iterator();
        for (Map.Entry<Integer, TemplateNode> integerTemplateNodeEntry : new java.util.TreeMap(chunkHeader.getTemplateNodes()).entrySet()) {
            Assert.assertEquals(iterator.next(), integerTemplateNodeEntry.getValue().getGuid());
        }
        Assert.assertTrue(chunkHeader.hasNext());
        Record next = chunkHeader.next();
        Assert.assertEquals(fileLastRecordNumber, next.getRecordNum().intValue());
        RootNode rootNode = next.getRootNode();
        List<BxmlNode> children = rootNode.getChildren();
        Assert.assertEquals(1, children.size());
        Assert.assertTrue(((children.get(0)) instanceof EndOfStreamNode));
        Assert.assertEquals(0, rootNode.getSubstitutions().size());
        Assert.assertFalse(chunkHeader.hasNext());
    }
}

