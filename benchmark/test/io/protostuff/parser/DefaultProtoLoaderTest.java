/**
 * ========================================================================
 */
/**
 * Copyright 2007-2010 David Yu dyuproject@gmail.com
 */
/**
 * ------------------------------------------------------------------------
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
 * http://www.apache.org/licenses/LICENSE-2.0
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
/**
 * ========================================================================
 */
package io.protostuff.parser;


import java.io.File;
import junit.framework.TestCase;


/**
 * Test for the default proto loader.
 *
 * @author David Yu
 * @unknown May 16, 2010
 */
public class DefaultProtoLoaderTest extends TestCase {
    public void testPackageBaseDir() throws Exception {
        File f = ProtoParserTest.getFile("io/protostuff/parser/test_default_proto_loader.proto");
        TestCase.assertTrue(f.exists());
        Proto p = ProtoUtil.parseProto(f);
        TestCase.assertEquals("io.protostuff.parser", p.getPackageName());
    }

    public void testLoadProtoFromClasspath() throws Exception {
        Proto proto = DefaultProtoLoader.loadFromClasspath("google/protobuf/unittest_import.proto", null);
        TestCase.assertNotNull(proto);
        TestCase.assertEquals("protobuf_unittest_import", proto.getPackageName());
    }

    public void testImportFromClasspath() throws Exception {
        File f = new File("src/main/etc/test_default_proto_loader.proto");
        TestCase.assertTrue(f.exists());
        Proto p = ProtoUtil.parseProto(f);
        TestCase.assertEquals("io.protostuff.parser", p.getPackageName());
        Message testMessage = p.getMessage("TestMessage");
        TestCase.assertNotNull(testMessage);
        Field<?> f1 = testMessage.getField("imported_message1");
        Field<?> f2 = testMessage.getField("imported_message2");
        TestCase.assertNotNull(f1);
        TestCase.assertNotNull(f2);
        TestCase.assertTrue((f1 instanceof MessageField));
        TestCase.assertTrue((f2 instanceof MessageField));
        Message importedMessage1 = getMessage();
        Message importedMessage2 = getMessage();
        TestCase.assertNotNull(importedMessage1);
        TestCase.assertNotNull(importedMessage2);
        TestCase.assertTrue((importedMessage1 == importedMessage2));
    }
}

