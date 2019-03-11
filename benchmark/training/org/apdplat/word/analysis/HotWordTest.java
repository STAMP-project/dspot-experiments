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
package org.apdplat.word.analysis;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import junit.framework.TestCase;


/**
 * ?? ??NGRAM?????
 *
 * @author ???
 */
public class HotWordTest extends TestCase {
    public void testGet() throws Exception {
        List<String> lines = Files.readAllLines(Paths.get("src/test/resources/hot-word-test-text.txt"));
        Map<String, Integer> data = HotWord.get(lines.toString(), 2);
        TestCase.assertEquals(342, data.get("??").intValue());
        TestCase.assertEquals(128, data.get("??").intValue());
        data = HotWord.get(lines.toString(), 3);
        TestCase.assertEquals(498, data.get("???").intValue());
        TestCase.assertEquals(168, data.get("???").intValue());
        TestCase.assertEquals(146, data.get("???").intValue());
        TestCase.assertEquals(92, data.get("???").intValue());
        data = HotWord.get(lines.toString(), 4);
        TestCase.assertEquals(89, data.get("????").intValue());
        TestCase.assertEquals(58, data.get("????").intValue());
        TestCase.assertEquals(22, data.get("????").intValue());
    }
}

