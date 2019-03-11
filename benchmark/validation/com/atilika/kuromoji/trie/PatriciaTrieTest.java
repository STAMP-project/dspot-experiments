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
package com.atilika.kuromoji.trie;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.Assert;
import org.junit.Test;


public class PatriciaTrieTest {
    @Test
    public void testRomaji() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put("a", "a");
        trie.put("b", "b");
        trie.put("ab", "ab");
        trie.put("bac", "bac");
        Assert.assertEquals("a", trie.get("a"));
        Assert.assertEquals("bac", trie.get("bac"));
        Assert.assertEquals("b", trie.get("b"));
        Assert.assertEquals("ab", trie.get("ab"));
        Assert.assertNull(trie.get("nonexistant"));
    }

    @Test
    public void testJapanese() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put("??", "sushi");
        trie.put("??", "sashimi");
        Assert.assertEquals("sushi", trie.get("??"));
        Assert.assertEquals("sashimi", trie.get("??"));
    }

    @Test(expected = NullPointerException.class)
    public void testNull() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put("null", null);
        Assert.assertEquals(null, trie.get("null"));
        trie.put(null, "null");// Throws NullPointerException

        Assert.assertTrue(false);
    }

    @Test
    public void testRandom() {
        // Generate random strings
        List<String> randoms = new ArrayList<>();
        for (int i = 0; i < 100000; i++) {
            randoms.add(UUID.randomUUID().toString());
        }
        // Insert them
        PatriciaTrie<String> trie = new PatriciaTrie();
        for (String random : randoms) {
            trie.put(random, random);
        }
        // Get and test them
        for (String random : randoms) {
            Assert.assertEquals(random, trie.get(random));
            Assert.assertTrue(trie.containsKey(random));
        }
    }

    @Test
    public void testPutAll() {
        // Generate random strings
        Map<String, String> randoms = new HashMap<>();
        for (int i = 0; i < 10000; i++) {
            String random = UUID.randomUUID().toString();
            randoms.put(random, random);
        }
        // Insert them
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.putAll(randoms);
        // Get and test them
        for (Map.Entry<String, String> random : randoms.entrySet()) {
            Assert.assertEquals(random.getValue(), trie.get(random.getKey()));
            Assert.assertTrue(trie.containsKey(random.getKey()));
        }
    }

    @Test
    public void testLongString() {
        String longMovieTitle = "???????????????????????????????????????????????????????";
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put(longMovieTitle, "found it");
        Assert.assertEquals("found it", trie.get(longMovieTitle));
    }

    @Test(expected = ClassCastException.class)
    public void testUnsupportedType() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put("hello", "world");
        Assert.assertTrue(trie.containsKey("hello"));
        trie.containsKey(new Integer(1));
        Assert.assertTrue(false);
    }

    @Test
    public void testEmpty() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        Assert.assertTrue(trie.isEmpty());
        trie.put("hello", "world");
        Assert.assertFalse(trie.isEmpty());
    }

    @Test
    public void testEmptyInsert() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        Assert.assertTrue(trie.isEmpty());
        trie.put("", "i am empty bottle of beer!");
        Assert.assertFalse(trie.isEmpty());
        Assert.assertEquals("i am empty bottle of beer!", trie.get(""));
        trie.put("", "...and i'm an empty bottle of sake");
        Assert.assertEquals("...and i'm an empty bottle of sake", trie.get(""));
    }

    @Test
    public void testClear() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        Assert.assertTrue(trie.isEmpty());
        Assert.assertEquals(0, trie.size());
        trie.put("hello", "world");
        trie.put("world", "hello");
        Assert.assertFalse(trie.isEmpty());
        trie.clear();
        Assert.assertTrue(trie.isEmpty());
        Assert.assertEquals(0, trie.size());
    }

    @Test
    public void testNaiveCollections() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put("??", "sushi");
        trie.put("??", "sashimi");
        trie.put("??", "soba");
        trie.put("????", "ramen");
        // Test keys
        Assert.assertEquals(4, trie.keySet().size());
        Assert.assertTrue(trie.keySet().containsAll(Arrays.asList(new String[]{ "??", "??", "????", "??" })));
        // Test values
        Assert.assertEquals(4, trie.values().size());
        Assert.assertTrue(trie.values().containsAll(Arrays.asList(new String[]{ "sushi", "soba", "ramen", "sashimi" })));
    }

    @Test
    public void testEscapeChars() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        trie.put("new", "no error");
        Assert.assertFalse(trie.containsKeyPrefix("new\na"));
        Assert.assertFalse(trie.containsKeyPrefix("\n"));
        Assert.assertFalse(trie.containsKeyPrefix("\t"));
    }

    @Test
    public void testPrefix() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        String[] tokyoPlaces = new String[]{ "Hachi?ji", "Tachikawa", "Musashino", "Mitaka", "?me", "Fuch?", "Akishima", "Ch?fu", "Machida", "Koganei", "Kodaira", "Hino", "Higashimurayama", "Kokubunji", "Kunitachi", "Fussa", "Komae", "Higashiyamato", "Kiyose", "Higashikurume", "Musashimurayama", "Tama", "Inagi", "Hamura", "Akiruno", "Nishit?ky?" };
        for (int i = 0; i < (tokyoPlaces.length); i++) {
            trie.put(tokyoPlaces[i], tokyoPlaces[i]);
        }
        // Prefixes of Kodaira
        Assert.assertTrue(trie.containsKeyPrefix("K"));
        Assert.assertTrue(trie.containsKeyPrefix("Ko"));
        Assert.assertTrue(trie.containsKeyPrefix("Kod"));
        Assert.assertTrue(trie.containsKeyPrefix("Koda"));
        Assert.assertTrue(trie.containsKeyPrefix("Kodai"));
        Assert.assertTrue(trie.containsKeyPrefix("Kodair"));
        Assert.assertTrue(trie.containsKeyPrefix("Kodaira"));
        Assert.assertFalse(trie.containsKeyPrefix("Kodaira "));
        Assert.assertFalse(trie.containsKeyPrefix("Kodaira  "));
        Assert.assertTrue(((trie.get("Kodaira")) != null));
        // Prefixes of Fussa
        Assert.assertFalse(trie.containsKeyPrefix("fu"));
        Assert.assertTrue(trie.containsKeyPrefix("Fu"));
        Assert.assertTrue(trie.containsKeyPrefix("Fus"));
    }

    @Test
    public void testTextScan() {
        PatriciaTrie<String> trie = new PatriciaTrie();
        String[] terms = new String[]{ "???", "sushi", "????", "tasty", "??", "japan", "??????", "i think", "??", "food", "????", "japanese food", "??", "first and foremost" };
        for (int i = 0; i < (terms.length); i += 2) {
            trie.put(terms[i], terms[(i + 1)]);
        }
        String text = "??????????????????????????????????????";
        StringBuilder builder = new StringBuilder();
        int startIndex = 0;
        while (startIndex < (text.length())) {
            int matchLength = 0;
            while (trie.containsKeyPrefix(text.substring(startIndex, ((startIndex + matchLength) + 1)))) {
                matchLength++;
            } 
            if (matchLength > 0) {
                String match = text.substring(startIndex, (startIndex + matchLength));
                builder.append("[");
                builder.append(match);
                builder.append("|");
                builder.append(trie.get(match));
                builder.append("]");
                startIndex += matchLength;
            } else {
                builder.append(text.charAt(startIndex));
                startIndex++;
            }
        } 
        Assert.assertEquals("[????|japanese food]????[??|first and foremost][????|tasty]??[???|sushi][??????|i think]???[??|japan]????????", builder.toString());
    }

    @Test
    public void testMultiThreadedTrie() throws InterruptedException {
        final int numThreads = 10;
        final int perThreadRuns = 500000;
        final int keySetSize = 1000;
        final List<Thread> threads = new ArrayList<>();
        final List<String> randoms = new ArrayList<>();
        final PatriciaTrie<Integer> trie = new PatriciaTrie();
        for (int i = 0; i < keySetSize; i++) {
            String random = UUID.randomUUID().toString();
            randoms.add(random);
            trie.put(random, i);
        }
        for (int i = 0; i < numThreads; i++) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int run = 0; run < perThreadRuns; run++) {
                        int randomIndex = ((int) ((Math.random()) * (randoms.size())));
                        String random = randoms.get(randomIndex);
                        // Test retrieve
                        Assert.assertEquals(randomIndex, ((int) (trie.get(random))));
                        int randomPrefixLength = ((int) ((Math.random()) * (random.length())));
                        // Test random prefix length prefix match
                        Assert.assertTrue(trie.containsKeyPrefix(random.substring(0, randomPrefixLength)));
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        Assert.assertTrue(true);
    }

    @Test
    public void testSimpleKey() {
        PatriciaTrie.KeyMapper<String> keyMapper = new PatriciaTrie.StringKeyMapper();
        String key = "abc";
        // a = U+0061 = 0000 0000 0110 0001
        Assert.assertFalse(keyMapper.isSet(0, key));
        Assert.assertFalse(keyMapper.isSet(1, key));
        Assert.assertFalse(keyMapper.isSet(2, key));
        Assert.assertFalse(keyMapper.isSet(3, key));
        Assert.assertFalse(keyMapper.isSet(4, key));
        Assert.assertFalse(keyMapper.isSet(5, key));
        Assert.assertFalse(keyMapper.isSet(6, key));
        Assert.assertFalse(keyMapper.isSet(7, key));
        Assert.assertFalse(keyMapper.isSet(8, key));
        Assert.assertTrue(keyMapper.isSet(9, key));
        Assert.assertTrue(keyMapper.isSet(10, key));
        Assert.assertFalse(keyMapper.isSet(11, key));
        Assert.assertFalse(keyMapper.isSet(12, key));
        Assert.assertFalse(keyMapper.isSet(13, key));
        Assert.assertFalse(keyMapper.isSet(14, key));
        Assert.assertTrue(keyMapper.isSet(15, key));
        // b = U+0062 = 0000 0000 0110 0010
        Assert.assertFalse(keyMapper.isSet(16, key));
        Assert.assertFalse(keyMapper.isSet(17, key));
        Assert.assertFalse(keyMapper.isSet(18, key));
        Assert.assertFalse(keyMapper.isSet(19, key));
        Assert.assertFalse(keyMapper.isSet(20, key));
        Assert.assertFalse(keyMapper.isSet(21, key));
        Assert.assertFalse(keyMapper.isSet(22, key));
        Assert.assertFalse(keyMapper.isSet(23, key));
        Assert.assertFalse(keyMapper.isSet(24, key));
        Assert.assertTrue(keyMapper.isSet(25, key));
        Assert.assertTrue(keyMapper.isSet(26, key));
        Assert.assertFalse(keyMapper.isSet(27, key));
        Assert.assertFalse(keyMapper.isSet(28, key));
        Assert.assertFalse(keyMapper.isSet(29, key));
        Assert.assertTrue(keyMapper.isSet(30, key));
        Assert.assertFalse(keyMapper.isSet(31, key));
        // c = U+0063 = 0000 0000 0110 0011
        Assert.assertFalse(keyMapper.isSet(32, key));
        Assert.assertFalse(keyMapper.isSet(33, key));
        Assert.assertFalse(keyMapper.isSet(34, key));
        Assert.assertFalse(keyMapper.isSet(35, key));
        Assert.assertFalse(keyMapper.isSet(36, key));
        Assert.assertFalse(keyMapper.isSet(37, key));
        Assert.assertFalse(keyMapper.isSet(38, key));
        Assert.assertFalse(keyMapper.isSet(39, key));
        Assert.assertFalse(keyMapper.isSet(40, key));
        Assert.assertTrue(keyMapper.isSet(41, key));
        Assert.assertTrue(keyMapper.isSet(42, key));
        Assert.assertFalse(keyMapper.isSet(43, key));
        Assert.assertFalse(keyMapper.isSet(44, key));
        Assert.assertFalse(keyMapper.isSet(45, key));
        Assert.assertTrue(keyMapper.isSet(46, key));
        Assert.assertTrue(keyMapper.isSet(47, key));
    }

    @Test
    public void testNullKeyMap() {
        PatriciaTrie.KeyMapper<String> keyMapper = new PatriciaTrie.StringKeyMapper();
        Assert.assertFalse(keyMapper.isSet(0, null));
        Assert.assertFalse(keyMapper.isSet(100, null));
        Assert.assertFalse(keyMapper.isSet(1000, null));
    }

    @Test
    public void testEmptyKeyMap() {
        PatriciaTrie.KeyMapper<String> keyMapper = new PatriciaTrie.StringKeyMapper();
        // Note: this is a special case handled in PatriciaTrie
        Assert.assertTrue(keyMapper.isSet(0, ""));
        Assert.assertTrue(keyMapper.isSet(100, ""));
        Assert.assertTrue(keyMapper.isSet(1000, ""));
    }

    @Test
    public void testOverflowBit() {
        PatriciaTrie.KeyMapper<String> keyMapper = new PatriciaTrie.StringKeyMapper();
        String key = "a";
        // a = U+0061 = 0000 0000 0110 0001
        Assert.assertFalse(keyMapper.isSet(0, key));
        Assert.assertFalse(keyMapper.isSet(1, key));
        Assert.assertFalse(keyMapper.isSet(2, key));
        Assert.assertFalse(keyMapper.isSet(3, key));
        Assert.assertFalse(keyMapper.isSet(4, key));
        Assert.assertFalse(keyMapper.isSet(5, key));
        Assert.assertFalse(keyMapper.isSet(6, key));
        Assert.assertFalse(keyMapper.isSet(7, key));
        Assert.assertFalse(keyMapper.isSet(8, key));
        Assert.assertTrue(keyMapper.isSet(9, key));
        Assert.assertTrue(keyMapper.isSet(10, key));
        Assert.assertFalse(keyMapper.isSet(11, key));
        Assert.assertFalse(keyMapper.isSet(12, key));
        Assert.assertFalse(keyMapper.isSet(13, key));
        Assert.assertFalse(keyMapper.isSet(14, key));
        Assert.assertTrue(keyMapper.isSet(15, key));
        // Asking for overflow bits should return 1
        Assert.assertTrue(keyMapper.isSet(16, key));
        Assert.assertTrue(keyMapper.isSet(17, key));
        Assert.assertTrue(keyMapper.isSet(100, key));
    }
}

