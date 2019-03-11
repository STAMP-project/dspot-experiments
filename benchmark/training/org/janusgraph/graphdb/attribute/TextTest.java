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
package org.janusgraph.graphdb.attribute;


import Cmp.EQUAL;
import Cmp.NOT_EQUAL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Matthias Broecheler (me@matthiasb.com)
 */
public class TextTest {
    @Test
    public void testContains() {
        String text = "This world is full of 1funny surprises! A Full Yes";
        // Contains
        Assertions.assertTrue(CONTAINS.test(text, "world"));
        Assertions.assertTrue(CONTAINS.test(text, "wOrLD"));
        Assertions.assertFalse(CONTAINS.test(text, "worl"));
        Assertions.assertTrue(CONTAINS.test(text, "this"));
        Assertions.assertTrue(CONTAINS.test(text, "yes"));
        Assertions.assertFalse(CONTAINS.test(text, "funny"));
        Assertions.assertFalse(CONTAINS.test(text, "a"));
        Assertions.assertFalse(CONTAINS.test(text, "A"));
        Assertions.assertTrue(CONTAINS.test(text, "surprises"));
        Assertions.assertTrue(CONTAINS.test(text, "FULL"));
        Assertions.assertTrue(CONTAINS.test(text, "full surprises"));
        Assertions.assertTrue(CONTAINS.test(text, "full,surprises,world"));
        Assertions.assertFalse(CONTAINS.test(text, "full bunny"));
        Assertions.assertTrue(CONTAINS.test(text, "a world"));
        // Prefix
        Assertions.assertTrue(CONTAINS_PREFIX.test(text, "worl"));
        Assertions.assertTrue(CONTAINS_PREFIX.test(text, "wORl"));
        Assertions.assertTrue(CONTAINS_PREFIX.test(text, "ye"));
        Assertions.assertTrue(CONTAINS_PREFIX.test(text, "Y"));
        Assertions.assertFalse(CONTAINS_PREFIX.test(text, "fo"));
        Assertions.assertFalse(CONTAINS_PREFIX.test(text, "of 1f"));
        Assertions.assertFalse(CONTAINS_PREFIX.test(text, "ses"));
        // Regex
        Assertions.assertTrue(CONTAINS_REGEX.test(text, "fu[l]+"));
        Assertions.assertTrue(CONTAINS_REGEX.test(text, "wor[ld]{1,2}"));
        Assertions.assertTrue(CONTAINS_REGEX.test(text, "\\dfu\\w*"));
        Assertions.assertFalse(CONTAINS_REGEX.test(text, "fo"));
        Assertions.assertFalse(CONTAINS_REGEX.test(text, "wor[l]+"));
        Assertions.assertFalse(CONTAINS_REGEX.test(text, "wor[ld]{3,5}"));
        String name = "fully funny";
        // Cmp
        Assertions.assertTrue(EQUAL.test(name, name));
        Assertions.assertFalse(NOT_EQUAL.test(name, name));
        Assertions.assertFalse(EQUAL.test("fullly funny", name));
        Assertions.assertTrue(NOT_EQUAL.test("fullly funny", name));
        // Prefix
        Assertions.assertTrue(PREFIX.test(name, "fully"));
        Assertions.assertTrue(PREFIX.test(name, "ful"));
        Assertions.assertTrue(PREFIX.test(name, "fully fu"));
        Assertions.assertFalse(PREFIX.test(name, "fun"));
        // REGEX
        Assertions.assertTrue(REGEX.test(name, "(fu[ln]*y) (fu[ln]*y)"));
        Assertions.assertFalse(REGEX.test(name, "(fu[l]*y) (fu[l]*y)"));
        Assertions.assertTrue(REGEX.test(name, "(fu[l]*y) .*"));
        // FUZZY
        String shortValue = "ah";
        Assertions.assertTrue(FUZZY.test(shortValue, "ah"));
        Assertions.assertFalse(FUZZY.test(shortValue, "ai"));
        String mediumValue = "hop";
        Assertions.assertTrue(FUZZY.test(mediumValue, "hop"));
        Assertions.assertTrue(FUZZY.test(mediumValue, "hopp"));
        Assertions.assertTrue(FUZZY.test(mediumValue, "hap"));
        Assertions.assertFalse(FUZZY.test(mediumValue, "ha"));
        Assertions.assertFalse(FUZZY.test(mediumValue, "hoopp"));
        String longValue = "surprises";
        Assertions.assertTrue(FUZZY.test(longValue, "surprises"));
        Assertions.assertTrue(FUZZY.test(longValue, "surpprises"));
        Assertions.assertTrue(FUZZY.test(longValue, "sutprises"));
        Assertions.assertTrue(FUZZY.test(longValue, "surprise"));
        Assertions.assertFalse(FUZZY.test(longValue, "surppirsses"));
        // CONTAINS_FUZZY
        // Short
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "is"));
        Assertions.assertFalse(CONTAINS_FUZZY.test(text, "si"));
        // Medium
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "full"));
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "fully"));
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "ful"));
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "fill"));
        Assertions.assertFalse(CONTAINS_FUZZY.test(text, "fu"));
        Assertions.assertFalse(CONTAINS_FUZZY.test(text, "fullest"));
        // Long
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "surprises"));
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "Surpprises"));
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "Sutrises"));
        Assertions.assertTrue(CONTAINS_FUZZY.test(text, "surprise"));
        Assertions.assertFalse(CONTAINS_FUZZY.test(text, "surppirsses"));
    }
}

