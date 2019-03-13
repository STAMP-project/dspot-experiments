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


import PennTreebankPOS.CD;
import PennTreebankPOS.NN;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Haifeng Li
 */
public class RegexPOSTaggerTest {
    public RegexPOSTaggerTest() {
    }

    /**
     * Test of tag method, of class RegexPOSTagger.
     */
    @Test
    public void testTag() {
        System.out.println("tag");
        Assert.assertEquals(CD, RegexPOSTagger.tag("123"));
        Assert.assertEquals(CD, RegexPOSTagger.tag("1234567890"));
        Assert.assertEquals(CD, RegexPOSTagger.tag("123.45"));
        Assert.assertEquals(CD, RegexPOSTagger.tag("1,234"));
        Assert.assertEquals(CD, RegexPOSTagger.tag("1,234.5678"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("914-544-3333"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("544-3333"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("x123"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("x123"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("http://www.msnbc.msn.com/id/42231726/?GT1=43001"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("ftp://www.msnbc.msn.com/id/42231726/?GT1=43001"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("nobody@usc.edu"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("no.body@usc.edu.cn"));
        Assert.assertEquals(NN, RegexPOSTagger.tag("no_body@usc.edu.cn"));
    }
}

