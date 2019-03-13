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
package smile.nlp.stemmer;


import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class PorterStemmerTest {
    public PorterStemmerTest() {
    }

    /**
     * Test of stem method, of class PorterStemmer.
     */
    @Test
    public void testStem() {
        System.out.println("stem");
        String[] words = new String[]{ "consign", "consigned", "consigning", "consignment", "consist", "consisted", "consistency", "consistent", "consistently", "consisting", "consists", "consolation", "consolations", "consolatory", "console", "consoled", "consoles", "consolidate", "consolidated", "consolidating", "consoling", "consolingly", "consols", "consonant", "consort", "consorted", "consorting", "conspicuous", "conspicuously", "conspiracy", "conspirator", "conspirators", "conspire", "conspired", "conspiring", "constable", "constables", "constance", "constancy", "constant", "knack", "knackeries", "knacks", "knag", "knave", "knaves", "knavish", "kneaded", "kneading", "knee", "kneel", "kneeled", "kneeling", "kneels", "knees", "knell", "knelt", "knew", "knick", "knif", "knife", "knight", "knightly", "knights", "knit", "knits", "knitted", "knitting", "knives", "knob", "knobs", "knock", "knocked", "knocker", "knockers", "knocking", "knocks", "knopp", "knot", "knots" };
        String[] expResult = new String[]{ "consign", "consign", "consign", "consign", "consist", "consist", "consist", "consist", "consist", "consist", "consist", "consol", "consol", "consolatori", "consol", "consol", "consol", "consolid", "consolid", "consolid", "consol", "consolingli", "consol", "conson", "consort", "consort", "consort", "conspicu", "conspicu", "conspiraci", "conspir", "conspir", "conspir", "conspir", "conspir", "constabl", "constabl", "constanc", "constanc", "constant", "knack", "knackeri", "knack", "knag", "knave", "knave", "knavish", "knead", "knead", "knee", "kneel", "kneel", "kneel", "kneel", "knee", "knell", "knelt", "knew", "knick", "knif", "knife", "knight", "knightli", "knight", "knit", "knit", "knit", "knit", "knive", "knob", "knob", "knock", "knock", "knocker", "knocker", "knock", "knock", "knopp", "knot", "knot" };
        PorterStemmer instance = new PorterStemmer();
        for (int i = 0; i < (words.length); i++) {
            String result = instance.stem(words[i]);
            Assert.assertEquals(expResult[i], result);
        }
    }
}

