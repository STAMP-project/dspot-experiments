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
import smile.data.SparseDataset;


/**
 *
 *
 * @author Haifeng Li
 */
public class SparseDatasetParserTest {
    public SparseDatasetParserTest() {
    }

    /**
     * Test of parse method, of class SparseDatasetParser.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        try {
            SparseDatasetParser parser = new SparseDatasetParser(1);
            SparseDataset data = parser.parse(smile.data.parser.IOUtils.getTestDataFile("text/kos.txt"));
            Assert.assertEquals(3430, data.size());
            Assert.assertEquals(6906, data.ncols());
            Assert.assertEquals(353160, data.length());
            Assert.assertEquals(2.0, data.get(0, 60), 1.0E-7);
            Assert.assertEquals(1.0, data.get(1, 1062), 1.0E-7);
            Assert.assertEquals(0.0, data.get(1, 1063), 1.0E-7);
            Assert.assertEquals(1.0, data.get(3429, 6821), 1.0E-7);
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
}

