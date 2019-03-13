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
package com.atilika.kuromoji.dict;


import UserDictionary.UserDictionaryMatch;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class UserDictionaryTest {
    @Test
    public void testLookup() throws IOException {
        UserDictionary dictionary = new UserDictionary(getResource("deeplearning4j-nlp-japanese/userdict.txt"), 9, 7, 0);
        List<UserDictionary.UserDictionaryMatch> matches = dictionary.findUserDictionaryMatches("??????????");
        // Length should be three ??, ??, ??
        Assert.assertEquals(3, matches.size());
        // Test positions
        Assert.assertEquals(0, matches.get(0).getMatchStartIndex());// index of ??

        Assert.assertEquals(2, matches.get(1).getMatchStartIndex());// index of ??

        Assert.assertEquals(4, matches.get(2).getMatchStartIndex());// index of ??

        // Test lengths
        Assert.assertEquals(2, matches.get(0).getMatchLength());// length of ??

        Assert.assertEquals(2, matches.get(1).getMatchLength());// length of ??

        Assert.assertEquals(2, matches.get(2).getMatchLength());// length of ??

        List<UserDictionary.UserDictionaryMatch> matches2 = dictionary.findUserDictionaryMatches("?????????????????");
        Assert.assertEquals(6, matches2.size());
    }

    @Test
    public void testIpadicFeatures() throws IOException {
        UserDictionary dictionary = new UserDictionary(getResource("deeplearning4j-nlp-japanese/userdict.txt"), 9, 7, 0);
        Assert.assertEquals("??????,*,*,*,*,*,*,???,*", dictionary.getAllFeatures(100000000));
    }

    @Test
    public void testJumanDicFeatures() throws IOException {
        UserDictionary dictionary = new UserDictionary(getResource("deeplearning4j-nlp-japanese/userdict.txt"), 7, 5, 0);
        Assert.assertEquals("??????,*,*,*,*,???,*", dictionary.getAllFeatures(100000000));
    }

    @Test
    public void testNaistJDicFeatures() throws IOException {
        UserDictionary dictionary = new UserDictionary(getResource("deeplearning4j-nlp-japanese/userdict.txt"), 11, 7, 0);
        // This is a sample naist-jdic entry:
        // 
        // ??,1358,1358,4975,??,??,*,*,*,*,??,?????,?????,,
        // 
        // How should we treat the last features in the user dictionary?  They seem empty, but we return * for them...
        Assert.assertEquals("??????,*,*,*,*,*,*,???,*,*,*", dictionary.getAllFeatures(100000000));
    }

    @Test
    public void testUniDicFeatures() throws IOException {
        UserDictionary dictionary = new UserDictionary(getResource("deeplearning4j-nlp-japanese/userdict.txt"), 13, 7, 0);
        Assert.assertEquals("??????,*,*,*,*,*,*,???,*,*,*,*,*", dictionary.getAllFeatures(100000000));
    }

    @Test
    public void testUniDicExtendedFeatures() throws IOException {
        UserDictionary dictionary = new UserDictionary(getResource("deeplearning4j-nlp-japanese/userdict.txt"), 22, 13, 0);
        Assert.assertEquals("??????,*,*,*,*,*,*,*,*,*,*,*,*,???,*,*,*,*,*,*,*,*", dictionary.getAllFeatures(100000000));
    }

    @Test
    public void testUserDictionaryEntries() throws IOException {
        String userDictionaryEntry = "??,??,??,??????";
        UserDictionary dictionary = new UserDictionary(new ByteArrayInputStream(userDictionaryEntry.getBytes(StandardCharsets.UTF_8)), 9, 7, 0);
        List<UserDictionary.UserDictionaryMatch> matches = dictionary.findUserDictionaryMatches("?????????????????");
        Assert.assertEquals(1, matches.size());
        Assert.assertEquals(5, matches.get(0).getMatchStartIndex());
    }

    @Test
    public void testOverlappingUserDictionaryEntries() throws IOException {
        String userDictionaryEntries = "" + ("\u30af\u30ed,\u30af\u30ed,\u30af\u30ed,\u30ab\u30b9\u30bf\u30e0\u540d\u8a5e\n" + "???,???,???,??????");
        UserDictionary dictionary = new UserDictionary(new ByteArrayInputStream(userDictionaryEntries.getBytes(StandardCharsets.UTF_8)), 9, 7, 0);
        List<UserDictionary.UserDictionaryMatch> positions = dictionary.findUserDictionaryMatches("?????????????????");
        Assert.assertEquals(4, positions.get(0).getMatchStartIndex());
        Assert.assertEquals(2, positions.size());
    }
}

