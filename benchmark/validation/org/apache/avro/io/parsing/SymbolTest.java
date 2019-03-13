/**
 * Copyright 2016 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.avro.io.parsing;


import java.io.IOException;
import java.util.HashSet;
import org.apache.avro.Schema;
import org.junit.Test;


/**
 * Unit test to verify that recursive schemas are flattened correctly.
 * See AVRO-1667.
 */
public class SymbolTest {
    private static final String SCHEMA = "{\"type\":\"record\",\"name\":\"SampleNode\"," + ((((((((((((("\"namespace\":\"org.spf4j.ssdump2.avro\",\n" + " \"fields\":[\n") + "    {\"name\":\"count\",\"type\":\"int\",\"default\":0},\n") + "    {\"name\":\"subNodes\",\"type\":\n") + "       {\"type\":\"array\",\"items\":{\n") + "           \"type\":\"record\",\"name\":\"SamplePair\",\n") + "           \"fields\":[\n") + "              {\"name\":\"method\",\"type\":\n") + "                  {\"type\":\"record\",\"name\":\"Method\",\n") + "                  \"fields\":[\n") + "                     {\"name\":\"declaringClass\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},\n") + "                     {\"name\":\"methodName\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}\n") + "                  ]}},\n") + "              {\"name\":\"node\",\"type\":\"SampleNode\"}]}}}]}");

    @Test
    public void testSomeMethod() throws IOException {
        Schema schema = new Schema.Parser().parse(SymbolTest.SCHEMA);
        Symbol root = new ResolvingGrammarGenerator().generate(schema, schema);
        SymbolTest.validateNonNull(root, new HashSet());
    }
}

