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
package smile.nlp.pos;


import PennTreebankPOS.JJ;
import PennTreebankPOS.NN;
import PennTreebankPOS.RB;
import PennTreebankPOS.VB;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class EnglishPOSLexiconTest {
    public EnglishPOSLexiconTest() {
    }

    /**
     * Test of get method, of class EnglishPOSLexicon.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        Assert.assertEquals(NN, EnglishPOSLexicon.get("1000000000000")[0]);
        Assert.assertEquals(NN, EnglishPOSLexicon.get("absorbent cotton")[0]);
        Assert.assertEquals(JJ, EnglishPOSLexicon.get("absorbing")[0]);
        Assert.assertEquals(VB, EnglishPOSLexicon.get("displease")[0]);
        Assert.assertEquals(RB, EnglishPOSLexicon.get("disposedly")[0]);
        Assert.assertEquals(3, EnglishPOSLexicon.get("disperse").length);
    }
}

