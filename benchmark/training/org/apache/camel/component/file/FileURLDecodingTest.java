/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.file;


import org.apache.camel.ContextTestSupport;
import org.junit.Test;


public class FileURLDecodingTest extends ContextTestSupport {
    static final String TARGET_DIR = "target/data/files";

    @Test
    public void testSimpleFile() throws Exception {
        assertTargetFile("data.txt", "data.txt");
    }

    @Test
    public void testFilePlus() throws Exception {
        assertTargetFile("data+.txt", "data .txt");
    }

    @Test
    public void testFileSpace() throws Exception {
        assertTargetFile("data%20.txt", "data .txt");
    }

    @Test
    public void testFile2B() throws Exception {
        assertTargetFile("data%2B.txt", "data .txt");
    }

    @Test
    public void testFileRaw2B() throws Exception {
        assertTargetFile("RAW(data%2B.txt)", "data%2B.txt");
    }

    @Test
    public void testFileRawPlus() throws Exception {
        assertTargetFile("RAW(data+.txt)", "data+.txt");
    }

    @Test
    public void testFileRawSpace() throws Exception {
        assertTargetFile("RAW(data%20.txt)", "data%20.txt");
    }

    @Test
    public void testFileRaw2520() throws Exception {
        assertTargetFile("RAW(data%2520.txt)", "data%2520.txt");
    }

    @Test
    public void testFileWithTwoHundredPercent() throws Exception {
        assertTargetFile("RAW(data%%.txt)", "data%%.txt");
    }
}

