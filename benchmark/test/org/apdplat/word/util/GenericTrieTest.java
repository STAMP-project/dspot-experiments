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
package org.apdplat.word.util;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author ???
 */
public class GenericTrieTest {
    private final GenericTrie<Integer> genericTrie = new GenericTrie();

    @Test
    public void testClear() {
        Assert.assertEquals(100, genericTrie.get("???").intValue());
        Assert.assertEquals(1, genericTrie.get("???????").intValue());
        genericTrie.clear();
        Assert.assertEquals(null, genericTrie.get("???"));
        Assert.assertEquals(null, genericTrie.get("???????"));
    }

    @Test
    public void testGet() {
        Assert.assertEquals(100, genericTrie.get("???").intValue());
        Assert.assertEquals(99, genericTrie.get("???").intValue());
        Assert.assertEquals(98, genericTrie.get("???").intValue());
        Assert.assertEquals(1, genericTrie.get("???????").intValue());
        Assert.assertEquals(null, genericTrie.get("?"));
        Assert.assertEquals(null, genericTrie.get("??"));
    }

    @Test
    public void testBigram() {
        try (Stream<String> lines = Files.lines(Paths.get("src/main/resources/bigram.txt")).limit(1000)) {
            GenericTrie<Integer> genericTrie = new GenericTrie();
            Map<String, Integer> map = new HashMap<>();
            lines.forEach(( line) -> {
                String[] attrs = line.split("\\s+");
                if ((attrs != null) && ((attrs.length) == 2)) {
                    map.put(attrs[0], Integer.parseInt(attrs[1]));
                    genericTrie.put(attrs[0], map.get(attrs[0]));
                }
            });
            map.keySet().forEach(( key) -> Assert.assertEquals(map.get(key).intValue(), genericTrie.get(key).intValue()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void testTrigram() {
        try (Stream<String> lines = Files.lines(Paths.get("src/main/resources/trigram.txt")).limit(1000)) {
            GenericTrie<Integer> genericTrie = new GenericTrie();
            Map<String, Integer> map = new HashMap<>();
            lines.forEach(( line) -> {
                String[] attrs = line.split("\\s+");
                if ((attrs != null) && ((attrs.length) == 2)) {
                    map.put(attrs[0], Integer.parseInt(attrs[1]));
                    genericTrie.put(attrs[0], map.get(attrs[0]));
                }
            });
            map.keySet().forEach(( key) -> Assert.assertEquals(map.get(key).intValue(), genericTrie.get(key).intValue()));
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }
}

