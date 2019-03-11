/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.kafka.streams.kstream.internals;


import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.ForeachAction;
import org.apache.kafka.streams.kstream.GlobalKTable;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KeyValueMapper;
import org.apache.kafka.test.MockValueJoiner;
import org.junit.Test;


public class GlobalKTableJoinsTest {
    private final StreamsBuilder builder = new StreamsBuilder();

    private final Map<String, String> results = new HashMap<>();

    private final String streamTopic = "stream";

    private final String globalTopic = "global";

    private GlobalKTable<String, String> global;

    private KStream<String, String> stream;

    private KeyValueMapper<String, String, String> keyValueMapper;

    private ForeachAction<String, String> action;

    @Test
    public void shouldLeftJoinWithStream() {
        stream.leftJoin(global, keyValueMapper, MockValueJoiner.TOSTRING_JOINER).foreach(action);
        final Map<String, String> expected = new HashMap<>();
        expected.put("1", "a+A");
        expected.put("2", "b+B");
        expected.put("3", "c+null");
        verifyJoin(expected);
    }

    @Test
    public void shouldInnerJoinWithStream() {
        stream.join(global, keyValueMapper, MockValueJoiner.TOSTRING_JOINER).foreach(action);
        final Map<String, String> expected = new HashMap<>();
        expected.put("1", "a+A");
        expected.put("2", "b+B");
        verifyJoin(expected);
    }
}

