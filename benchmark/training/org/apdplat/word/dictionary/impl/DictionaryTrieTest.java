/**
 * APDPlat - Application Product Development Platform
 * Copyright (c) 2013, ???, yang-shangchuan@qq.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.apdplat.word.dictionary.impl;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import org.apdplat.word.dictionary.Dictionary;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 */
public class DictionaryTrieTest {
    private DictionaryTrie trie = null;

    @Test
    public void testPrefix() {
        String prefix = "?";
        List<String> result = trie.prefix(prefix);
        Assert.assertTrue(result.contains("??"));
        Assert.assertTrue(result.contains("??"));
        prefix = "??";
        result = trie.prefix(prefix);
        Assert.assertTrue(result.contains("???"));
        prefix = "?";
        result = trie.prefix(prefix);
        Assert.assertTrue(result.contains("??"));
        Assert.assertTrue(result.contains("??"));
        prefix = "??";
        result = trie.prefix(prefix);
        Assert.assertTrue(result.contains("???"));
        Assert.assertTrue(result.contains("???"));
        Assert.assertTrue(result.contains("???"));
    }

    @Test
    public void testContains() {
        String item = "???";
        boolean expResult = true;
        boolean result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "???";
        expResult = true;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "???????";
        expResult = true;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "APDPlat";
        expResult = true;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "APP";
        expResult = true;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "APD";
        expResult = true;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "??";
        expResult = false;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "?";
        expResult = false;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "APDP";
        expResult = false;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "A";
        expResult = false;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
        item = "????";
        expResult = false;
        result = trie.contains(item);
        Assert.assertEquals(expResult, result);
    }

    @Test
    public void testWhole() {
        try {
            AtomicInteger h = new AtomicInteger();
            AtomicInteger e = new AtomicInteger();
            List<String> words = Files.readAllLines(Paths.get("src/main/resources/dic.txt"));
            Dictionary dictionary = new DictionaryTrie();
            dictionary.addAll(words);
            words.forEach(( word) -> {
                for (int j = 0; j < (word.length()); j++) {
                    String sw = word.substring(0, (j + 1));
                    for (int k = 0; k < (sw.length()); k++) {
                        if (dictionary.contains(sw, k, ((sw.length()) - k))) {
                            h.incrementAndGet();
                        } else {
                            e.incrementAndGet();
                        }
                    }
                }
            });
            Assert.assertEquals(2599239, e.get());
            Assert.assertEquals(1211555, h.get());
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}

