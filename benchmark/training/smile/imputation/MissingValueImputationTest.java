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
package smile.imputation;


import org.junit.Test;
import smile.data.AttributeDataset;
import smile.data.parser.ArffParser;
import smile.data.parser.DelimitedTextParser;


/**
 *
 *
 * @author Haifeng Li
 */
public class MissingValueImputationTest {
    ArffParser arffParser = new ArffParser();

    DelimitedTextParser csvParser = new DelimitedTextParser();

    AttributeDataset movement;

    AttributeDataset control;

    AttributeDataset segment;

    AttributeDataset iris;

    AttributeDataset soybean;

    public MissingValueImputationTest() {
    }

    /**
     * Test of impute method.
     */
    @Test
    public void testImpute() throws Exception {
        impute(segment);
        impute(movement);
        impute(control);
    }
}

