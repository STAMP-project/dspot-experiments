/**
 * *****************************************************************************
 * Copyright (c) 2010 Haifeng Li
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
package smile.data.parser;


import org.junit.Assert;
import org.junit.Test;
import smile.data.BinarySparseDataset;


/**
 *
 *
 * @author Haifeng Li
 */
public class BinarySparseDatasetParserTest {
    public BinarySparseDatasetParserTest() {
    }

    /**
     * Test of parse method, of class SparseDatasetParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        try {
            BinarySparseDatasetParser parser = new BinarySparseDatasetParser();
            BinarySparseDataset data = parser.parse(smile.data.parser.IOUtils.getTestDataFile("transaction/kosarak.dat"));
            Assert.assertEquals(990002, data.size());
            Assert.assertEquals(41271, data.ncols());
            Assert.assertEquals(1, data.get(0, 1));
            Assert.assertEquals(1, data.get(0, 2));
            Assert.assertEquals(1, data.get(0, 3));
            Assert.assertEquals(0, data.get(0, 4));
            Assert.assertEquals(1, data.get(990001, 1056));
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

