/**
 * Copyright 2017 JanusGraph Authors
 */
/**
 *
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
 *
 */
/**
 * http://www.apache.org/licenses/LICENSE-2.0
 */
/**
 *
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
package org.janusgraph.diskstorage.lucene;


import org.apache.commons.codec.digest.DigestUtils;
import org.janusgraph.example.GraphOfTheGodsFactory;
import org.janusgraph.graphdb.JanusGraphIndexTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class BerkeleyLuceneTest extends JanusGraphIndexTest {
    public BerkeleyLuceneTest() {
        super(true, true, true);
    }

    @Test
    public void testPrintSchemaElements() {
        GraphOfTheGodsFactory.load(graph);
        mgmt = graph.openManagement();
        String expected = "99EB9D82CE85D15A9120E31969B3F861";
        String output = mgmt.printSchema();
        String outputHash = DigestUtils.md5Hex(output).toUpperCase();
        Assertions.assertEquals(outputHash, expected);
        expected = "2114C009DC359B1C9AD7D0655AC6C9BF";
        output = mgmt.printVertexLabels();
        outputHash = DigestUtils.md5Hex(output).toUpperCase();
        Assertions.assertEquals(outputHash, expected);
        expected = "1E8AAE2C887544E490948F2ACBBFE312";
        output = mgmt.printEdgeLabels();
        outputHash = DigestUtils.md5Hex(output).toUpperCase();
        Assertions.assertEquals(outputHash, expected);
        expected = "35851C8867321C8CB3E275886F40E8B9";
        output = mgmt.printPropertyKeys();
        outputHash = DigestUtils.md5Hex(output).toUpperCase();
        Assertions.assertEquals(outputHash, expected);
        expected = "FB629F06E8410B033167DCBF2434CB1D";
        output = mgmt.printIndexes();
        outputHash = DigestUtils.md5Hex(output).toUpperCase();
        Assertions.assertEquals(outputHash, expected);
    }
}

