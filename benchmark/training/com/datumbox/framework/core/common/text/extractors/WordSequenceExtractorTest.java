/**
 * Copyright (C) 2013-2018 Vasilis Vryniotis <bbriniotis@datumbox.com>
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
 */
package com.datumbox.framework.core.common.text.extractors;


import com.datumbox.framework.tests.abstracts.AbstractTest;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;


/**
 * Test cases for WordSequenceExtractor.
 *
 * @author Vasilis Vryniotis <bbriniotis@datumbox.com>
 */
public class WordSequenceExtractorTest extends AbstractTest {
    /**
     * Test of extract method, of class WordSequenceExtractor.
     */
    @Test
    public void testExtract() {
        logger.info("extract");
        String text = "this is a text sequence that is amazing text sequence";
        WordSequenceExtractor instance = new WordSequenceExtractor(new WordSequenceExtractor.Parameters());
        Map<Integer, String> expResult = new LinkedHashMap<>();
        expResult.put(0, "this");
        expResult.put(1, "is");
        expResult.put(2, "a");
        expResult.put(3, "text");
        expResult.put(4, "sequence");
        expResult.put(5, "that");
        expResult.put(6, "is");
        expResult.put(7, "amazing");
        expResult.put(8, "text");
        expResult.put(9, "sequence");
        Map<Integer, String> result = instance.extract(text);
        Assert.assertEquals(expResult, result);
    }
}

