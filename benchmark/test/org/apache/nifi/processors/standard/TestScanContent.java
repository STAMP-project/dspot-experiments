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
package org.apache.nifi.processors.standard;


import ScanContent.DICTIONARY;
import ScanContent.DICTIONARY_ENCODING;
import ScanContent.REL_NO_MATCH;
import ScanContent.TEXT_ENCODING;
import ScanContent.UTF8;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import org.apache.nifi.util.TestRunner;
import org.apache.nifi.util.TestRunners;
import org.junit.Test;


public class TestScanContent {
    @Test
    public void testBlankLineInDictionaryTextEncoding() throws IOException {
        final String dictionaryWithBlankLine = "Line1\n\nLine3";
        final byte[] dictionaryBytes = dictionaryWithBlankLine.getBytes(UTF8);
        final Path dictionaryPath = Paths.get("target/dictionary");
        Files.write(dictionaryPath, dictionaryBytes, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
        final TestRunner runner = TestRunners.newTestRunner(new ScanContent());
        runner.setThreadCount(1);
        runner.setProperty(DICTIONARY, dictionaryPath.toString());
        runner.setProperty(DICTIONARY_ENCODING, TEXT_ENCODING);
        runner.enqueue(new byte[0]);
        runner.run();
        runner.assertTransferCount(REL_NO_MATCH, 1);
    }
}

