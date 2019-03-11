/**
 * *****************************************************************************
 * Copyright (c) 2015-2018 Skymind, Inc.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * ****************************************************************************
 */
/**
 * -*
 * Copyright ? 2010-2015 Atilika Inc. and contributors (see CONTRIBUTORS.md)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.  A copy of the
 * License is distributed with this work in the LICENSE.md file.  You may
 * also obtain a copy of the License from
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.atilika.kuromoji.compile;


import com.atilika.kuromoji.dict.CharacterDefinitions;
import com.atilika.kuromoji.dict.UnknownDictionary;
import org.junit.Assert;
import org.junit.Test;


public class UnknownDictionaryCompilerTest {
    private static UnknownDictionary unknownDictionary;

    private static CharacterDefinitions characterDefinitions;

    private static int[][] costs;

    private static int[][] references;

    private static String[][] features;

    @Test
    public void testCostsAndFeatures() {
        int[] categories = UnknownDictionaryCompilerTest.characterDefinitions.lookupCategories('?');
        // KANJI & KANJINUMERIC
        Assert.assertEquals(2, categories.length);
        Assert.assertArrayEquals(new int[]{ 5, 6 }, categories);
        // KANJI entries
        Assert.assertArrayEquals(new int[]{ 2, 3, 4, 5, 6, 7 }, UnknownDictionaryCompilerTest.unknownDictionary.lookupWordIds(categories[0]));
        // KANJI feature variety
        Assert.assertArrayEquals(new String[]{ "??", "??", "*", "*", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(2));
        Assert.assertArrayEquals(new String[]{ "??", "????", "*", "*", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(3));
        Assert.assertArrayEquals(new String[]{ "??", "????", "??", "??", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(4));
        Assert.assertArrayEquals(new String[]{ "??", "????", "??", "*", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(5));
        Assert.assertArrayEquals(new String[]{ "??", "????", "??", "??", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(6));
        Assert.assertArrayEquals(new String[]{ "??", "????", "??", "??", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(6));
        // KANJINUMERIC entry
        Assert.assertArrayEquals(new int[]{ 29 }, UnknownDictionaryCompilerTest.unknownDictionary.lookupWordIds(categories[1]));
        // KANJINUMERIC costs
        Assert.assertEquals(1295, UnknownDictionaryCompilerTest.unknownDictionary.getLeftId(29));
        Assert.assertEquals(1295, UnknownDictionaryCompilerTest.unknownDictionary.getRightId(29));
        Assert.assertEquals(27473, UnknownDictionaryCompilerTest.unknownDictionary.getWordCost(29));
        // KANJINUMERIC features
        Assert.assertArrayEquals(new String[]{ "??", "?", "*", "*", "*", "*", "*" }, UnknownDictionaryCompilerTest.unknownDictionary.getAllFeaturesArray(29));
    }
}

