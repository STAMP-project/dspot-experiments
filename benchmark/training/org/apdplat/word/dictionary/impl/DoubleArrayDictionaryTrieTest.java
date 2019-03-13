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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import junit.framework.TestCase;
import org.apdplat.word.dictionary.Dictionary;
import org.junit.Test;


/**
 * ??????????
 *
 * @author ???
 */
public class DoubleArrayDictionaryTrieTest extends TestCase {
    @Test
    public void testAdd() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        try {
            dictionary.add("APDPlat");
            TestCase.fail();
        } catch (Exception e) {
            TestCase.assertEquals("not yet support, please use addAll method!", e.getMessage());
        }
    }

    @Test
    public void testRemove() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        try {
            dictionary.remove("APDPlat");
            TestCase.fail();
        } catch (Exception e) {
            TestCase.assertEquals("not yet support menthod!", e.getMessage());
        }
    }

    @Test
    public void testRemoveAll() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        try {
            dictionary.removeAll(Arrays.asList("APDPlat", "???"));
            TestCase.fail();
        } catch (Exception e) {
            TestCase.assertEquals("not yet support menthod!", e.getMessage());
        }
    }

    @Test
    public void testAddAll() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        List<String> words = Arrays.asList("???", "???", "???", "?", "???", "??", "??", "??");
        // ????
        dictionary.addAll(words);
        TestCase.assertEquals(3, dictionary.getMaxLength());
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("?"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("????????????", 3, 2));
        TestCase.assertEquals(true, dictionary.contains("????????????", 0, 2));
        TestCase.assertEquals(true, dictionary.contains("????????????", 10, 2));
        TestCase.assertEquals(false, dictionary.contains("?????2"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        try {
            dictionary.addAll(Arrays.asList("??", "??", "??"));
            TestCase.fail();
        } catch (Exception e) {
            TestCase.assertEquals("addAll method can just be used once after clear method!", e.getMessage());
        }
        dictionary.clear();
        dictionary.addAll(Arrays.asList("??", "??", "??"));
        TestCase.assertEquals(2, dictionary.getMaxLength());
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("?"));
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("????????????", 3, 2));
        TestCase.assertEquals(false, dictionary.contains("????????????", 0, 2));
        TestCase.assertEquals(false, dictionary.contains("????????????", 10, 2));
        TestCase.assertEquals(false, dictionary.contains("?????2"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("??"));
    }

    @Test
    public void testWhole() {
        Dictionary dictionary = new DoubleArrayDictionaryTrie();
        List<String> words = Arrays.asList("???", "???", "???", "?", "???", "??", "??", "??");
        // ????
        dictionary.addAll(words);
        TestCase.assertEquals(3, dictionary.getMaxLength());
        TestCase.assertEquals(false, dictionary.contains("?"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("?"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("????????????", 3, 2));
        TestCase.assertEquals(true, dictionary.contains("????????????", 0, 2));
        TestCase.assertEquals(true, dictionary.contains("????????????", 10, 2));
        TestCase.assertEquals(false, dictionary.contains("?????2"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        dictionary.clear();
        TestCase.assertEquals(0, dictionary.getMaxLength());
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("?"));
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("???"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("????????????", 3, 2));
        TestCase.assertEquals(false, dictionary.contains("????????????", 0, 2));
        TestCase.assertEquals(false, dictionary.contains("????????????", 10, 2));
        TestCase.assertEquals(false, dictionary.contains("?????2"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("??"));
        List<String> data = new ArrayList<>();
        data.add("??");
        data.add("??");
        data.add("?????2");
        data.addAll(words);
        dictionary.addAll(data);
        TestCase.assertEquals(6, dictionary.getMaxLength());
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("?"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("???"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("????????????", 3, 2));
        TestCase.assertEquals(true, dictionary.contains("????????????", 0, 2));
        TestCase.assertEquals(true, dictionary.contains("????????????", 10, 2));
        TestCase.assertEquals(true, dictionary.contains("?????2"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(true, dictionary.contains("??"));
        TestCase.assertEquals(false, dictionary.contains("???"));
    }

    @Test
    public void testWhole2() {
        try {
            AtomicInteger h = new AtomicInteger();
            AtomicInteger e = new AtomicInteger();
            List<String> words = Files.readAllLines(Paths.get("src/main/resources/dic.txt"));
            Dictionary dictionary = new DoubleArrayDictionaryTrie();
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
            TestCase.assertEquals(2599239, e.get());
            TestCase.assertEquals(1211555, h.get());
        } catch (Exception e) {
            e.printStackTrace();
            TestCase.fail();
        }
    }
}

