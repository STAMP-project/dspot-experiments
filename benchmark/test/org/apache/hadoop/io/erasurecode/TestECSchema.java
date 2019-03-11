/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.io.erasurecode;


import ECSchema.CODEC_NAME_KEY;
import ECSchema.NUM_DATA_UNITS_KEY;
import ECSchema.NUM_PARITY_UNITS_KEY;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;


public class TestECSchema {
    @Rule
    public Timeout globalTimeout = new Timeout(300000);

    @Test
    public void testGoodSchema() {
        int numDataUnits = 6;
        int numParityUnits = 3;
        String codec = "rs";
        String extraOption = "extraOption";
        String extraOptionValue = "extraOptionValue";
        Map<String, String> options = new HashMap<String, String>();
        options.put(NUM_DATA_UNITS_KEY, String.valueOf(numDataUnits));
        options.put(NUM_PARITY_UNITS_KEY, String.valueOf(numParityUnits));
        options.put(CODEC_NAME_KEY, codec);
        options.put(extraOption, extraOptionValue);
        ECSchema schema = new ECSchema(options);
        System.out.println(schema.toString());
        Assert.assertEquals(numDataUnits, schema.getNumDataUnits());
        Assert.assertEquals(numParityUnits, schema.getNumParityUnits());
        Assert.assertEquals(codec, schema.getCodecName());
        Assert.assertEquals(extraOptionValue, schema.getExtraOptions().get(extraOption));
        Map<String, String> extraMap = new TreeMap<>();
        extraMap.put(extraOption, extraOptionValue);
        ECSchema sameSchema = new ECSchema(codec, numDataUnits, numParityUnits, extraMap);
        Assert.assertEquals("Different constructors not equal", sameSchema, schema);
    }

    @Test
    public void testEqualsAndHashCode() {
        Map<String, String> extraMap = new TreeMap<>();
        extraMap.put("key", "value");
        ECSchema[] schemas = new ECSchema[]{ new ECSchema("one", 1, 2, null), new ECSchema("two", 1, 2, null), new ECSchema("one", 2, 2, null), new ECSchema("one", 1, 1, null), new ECSchema("one", 1, 2, extraMap) };
        for (int i = 0; i < (schemas.length); i++) {
            final ECSchema ei = schemas[i];
            // Check identity
            ECSchema temp = new ECSchema(ei.getCodecName(), ei.getNumDataUnits(), ei.getNumParityUnits(), ei.getExtraOptions());
            Assert.assertEquals(ei, temp);
            Assert.assertEquals(ei.hashCode(), temp.hashCode());
            // Check against other schemas
            for (int j = 0; j < (schemas.length); j++) {
                final ECSchema ej = schemas[j];
                if (i == j) {
                    Assert.assertEquals(ei, ej);
                    Assert.assertEquals(ei.hashCode(), ej.hashCode());
                } else {
                    Assert.assertNotEquals(ei, ej);
                    Assert.assertNotEquals(ei, ej.hashCode());
                }
            }
        }
    }
}

