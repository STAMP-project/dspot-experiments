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
package org.apache.camel.jsonpath;


import org.junit.Assert;
import org.junit.Test;


public class JsonStreamTest {
    @Test
    public void utf8() throws Exception {
        test("json_stream/jsonUTF8.txt", "UTF-8");
    }

    @Test
    public void iSO88591() throws Exception {
        try {
            test("json_stream/jsonISO8859-1.txt", "ISO-8859-1");
            Assert.fail("Error exepcted");
        } catch (AssertionError e) {
            Assert.assertEquals("expected:<ISO-8859-1> but was:<UTF-8>", e.getMessage());
        }
    }

    @Test
    public void utf8WithoutBOM() throws Exception {
        test("json_stream/jsonUTF8WithoutBOM.txt", "UTF-8");
    }

    @Test
    public void utf16BEWithBom() throws Exception {
        test("json_stream/jsonUCS2BigEndianWithBOM.txt", "UTF-16BE");
    }

    @Test
    public void utf16BEWithoutBom() throws Exception {
        test("json_stream/jsonUCS2BigEndianWithoutBOM.txt", "UTF-16BE");
    }

    @Test
    public void utf16LEWithBom() throws Exception {
        test("json_stream/jsonUCS2LittleEndianWithBom.txt", "UTF-16LE");
    }

    @Test
    public void utf16LEWithoutBom() throws Exception {
        test("json_stream/jsonUCS2LittleEndianWithoutBOM.txt", "UTF-16LE");
    }

    @Test
    public void utf32BEWithBOM() throws Exception {
        test("json_stream/jsonUTF32BEWithBOM.txt", "UTF-32BE");
    }

    @Test
    public void utf32BEWithoutBOM() throws Exception {
        test("json_stream/jsonUTF32BEWithoutBOM.txt", "UTF-32BE");
    }

    @Test
    public void utf32LEWithBOM() throws Exception {
        test("json_stream/jsonUTF32LEWithBOM.txt", "UTF-32LE");
    }

    @Test
    public void utf32LEWithoutBOM() throws Exception {
        test("json_stream/jsonUTF32LEWithoutBOM.txt", "UTF-32LE");
    }

    @Test
    public void oneChar() throws Exception {
        test("json_stream/oneChar", "UTF-8", "1");
    }

    @Test
    public void fourChar() throws Exception {
        test("json_stream/fourChar", "UTF-8", "1234");
    }
}

