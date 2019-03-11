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
package org.apache.rocketmq.remoting.protocol;


import java.util.Arrays;
import java.util.List;
import org.junit.Test;


public class RemotingSerializableTest {
    @Test
    public void testEncodeAndDecode_HeterogeneousClass() {
        Sample sample = new Sample();
        byte[] bytes = RemotingSerializable.encode(sample);
        Sample decodedSample = RemotingSerializable.decode(bytes, Sample.class);
        assertThat(decodedSample).isEqualTo(sample);
    }

    @Test
    public void testToJson_normalString() {
        RemotingSerializable serializable = new RemotingSerializable() {
            private List<String> stringList = Arrays.asList("a", "o", "e", "i", "u", "v");

            public List<String> getStringList() {
                return stringList;
            }

            public void setStringList(List<String> stringList) {
                this.stringList = stringList;
            }
        };
        String string = serializable.toJson();
        assertThat(string).isEqualTo("{\"stringList\":[\"a\",\"o\",\"e\",\"i\",\"u\",\"v\"]}");
    }

    @Test
    public void testToJson_prettyString() {
        RemotingSerializable serializable = new RemotingSerializable() {
            private List<String> stringList = Arrays.asList("a", "o", "e", "i", "u", "v");

            public List<String> getStringList() {
                return stringList;
            }

            public void setStringList(List<String> stringList) {
                this.stringList = stringList;
            }
        };
        String prettyString = serializable.toJson(true);
        assertThat(prettyString).isEqualTo(("{\n" + (((((((("\t\"stringList\":[\n" + "\t\t\"a\",\n") + "\t\t\"o\",\n") + "\t\t\"e\",\n") + "\t\t\"i\",\n") + "\t\t\"u\",\n") + "\t\t\"v\"\n") + "\t]\n") + "}")));
    }
}

