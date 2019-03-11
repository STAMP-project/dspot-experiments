/**
 * ========================================================================
 */
/**
 * Copyright 2007-2011 David Yu dyuproject@gmail.com
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


import EnumGroup.Value;
import java.io.File;
import junit.framework.TestCase;


/**
 * Test proto options.
 *
 * @author David Yu
 * @unknown Aug 11, 2011
 */
public class OptionTest extends TestCase {
    public void testIt() throws Exception {
        File f = ProtoParserTest.getFile("io/protostuff/parser/test_options.proto");
        TestCase.assertTrue(f.exists());
        Proto proto = new Proto(f);
        ProtoUtil.loadFrom(f, proto);
        Message aMessage = proto.getMessage("AMessage");
        TestCase.assertNotNull(aMessage);
        TestCase.assertEquals("something", aMessage.getField("anotherMessage").getOption("anOption"));
        TestCase.assertEquals(Boolean.TRUE, aMessage.getExtraOption("message_set_wire_format"));
        Message anotherMessage = proto.getMessage("AnotherMessage");
        TestCase.assertNotNull(anotherMessage);
        TestCase.assertEquals("bar", anotherMessage.getExtraOption("foo"));
        EnumGroup baz = proto.getEnumGroup("Baz");
        TestCase.assertNotNull(baz);
        TestCase.assertEquals(Float.valueOf(1.0F), baz.getExtraOption("random.enum.option"));
        EnumGroup.Value a = baz.getValue("A");
        TestCase.assertEquals(1, a.getOptions().get("some_int"));
        TestCase.assertEquals(Boolean.TRUE, a.getOptions().get("some_bool"));
        TestCase.assertEquals("foo", a.getOptions().get("some_string"));
        TestCase.assertEquals("none", a.getOptions().get("default"));
    }
}

