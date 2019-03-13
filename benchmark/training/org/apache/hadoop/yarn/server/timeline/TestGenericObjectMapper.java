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
package org.apache.hadoop.yarn.server.timeline;


import InterfaceAudience.Private;
import InterfaceStability.Unstable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.junit.Assert;
import org.junit.Test;


@InterfaceAudience.Private
@InterfaceStability.Unstable
public class TestGenericObjectMapper {
    @Test
    public void testEncoding() {
        TestGenericObjectMapper.testEncoding(Long.MAX_VALUE);
        TestGenericObjectMapper.testEncoding(Long.MIN_VALUE);
        TestGenericObjectMapper.testEncoding(0L);
        TestGenericObjectMapper.testEncoding(128L);
        TestGenericObjectMapper.testEncoding(256L);
        TestGenericObjectMapper.testEncoding(512L);
        TestGenericObjectMapper.testEncoding((-256L));
    }

    @Test
    public void testValueTypes() throws IOException {
        TestGenericObjectMapper.verify(Integer.MAX_VALUE);
        TestGenericObjectMapper.verify(Integer.MIN_VALUE);
        Assert.assertEquals(Integer.MAX_VALUE, GenericObjectMapper.read(GenericObjectMapper.write(((long) (Integer.MAX_VALUE)))));
        Assert.assertEquals(Integer.MIN_VALUE, GenericObjectMapper.read(GenericObjectMapper.write(((long) (Integer.MIN_VALUE)))));
        TestGenericObjectMapper.verify((((long) (Integer.MAX_VALUE)) + 1L));
        TestGenericObjectMapper.verify((((long) (Integer.MIN_VALUE)) - 1L));
        TestGenericObjectMapper.verify(Long.MAX_VALUE);
        TestGenericObjectMapper.verify(Long.MIN_VALUE);
        Assert.assertEquals(42, GenericObjectMapper.read(GenericObjectMapper.write(42L)));
        TestGenericObjectMapper.verify(42);
        TestGenericObjectMapper.verify(1.23);
        TestGenericObjectMapper.verify("abc");
        TestGenericObjectMapper.verify(true);
        List<String> list = new ArrayList<String>();
        list.add("123");
        list.add("abc");
        TestGenericObjectMapper.verify(list);
        Map<String, String> map = new HashMap<String, String>();
        map.put("k1", "v1");
        map.put("k2", "v2");
        TestGenericObjectMapper.verify(map);
    }
}

