/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.elasticsearch.trident;


import java.util.List;
import java.util.UUID;
import org.apache.storm.testing.IntegrationTest;
import org.apache.storm.trident.tuple.TridentTuple;
import org.elasticsearch.node.Node;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;


@IntegrationTest
@ExtendWith(MockitoExtension.class)
public class EsStateTest {
    private static Node node;

    private final String[] documentId = new String[]{ UUID.randomUUID().toString(), UUID.randomUUID().toString() };

    private final String index = "index";

    private final String type = "type";

    private final String[] source = new String[]{ "{\"user\":\"user1\"}", "{\"user\":\"user1\"}" };

    private EsState state = createEsState();

    @Test
    public void updateState() throws Exception {
        List<TridentTuple> tuples = tuples(index, type, documentId, source);
        state.updateState(tuples);
    }

    @Test
    public void indexMissing() throws Exception {
        List<TridentTuple> tuples = tuples("missing", type, documentId, source);
        state.updateState(tuples);
    }
}

