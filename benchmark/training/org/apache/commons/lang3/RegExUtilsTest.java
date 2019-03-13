/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.lang3;


import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;


/**
 * Unit tests for methods of {@link org.apache.commons.lang3.RegExUtils} which been moved to their own test classes.
 */
public class RegExUtilsTest {
    @Test
    public void testRemoveAll_StringPattern() {
        Assertions.assertNull(RegExUtils.removeAll(null, Pattern.compile("")));
        Assertions.assertEquals("any", RegExUtils.removeAll("any", ((Pattern) (null))));
        Assertions.assertEquals("any", RegExUtils.removeAll("any", Pattern.compile("")));
        Assertions.assertEquals("", RegExUtils.removeAll("any", Pattern.compile(".*")));
        Assertions.assertEquals("", RegExUtils.removeAll("any", Pattern.compile(".+")));
        Assertions.assertEquals("", RegExUtils.removeAll("any", Pattern.compile(".?")));
        Assertions.assertEquals("A\nB", RegExUtils.removeAll("A<__>\n<__>B", Pattern.compile("<.*>")));
        Assertions.assertEquals("AB", RegExUtils.removeAll("A<__>\n<__>B", Pattern.compile("(?s)<.*>")));
        Assertions.assertEquals("ABC123", RegExUtils.removeAll("ABCabc123abc", Pattern.compile("[a-z]")));
        Assertions.assertEquals("AB", RegExUtils.removeAll("A<__>\n<__>B", Pattern.compile("<.*>", Pattern.DOTALL)));
        Assertions.assertEquals("AB", RegExUtils.removeAll("A<__>\\n<__>B", Pattern.compile("<.*>")));
        Assertions.assertEquals("", RegExUtils.removeAll("<A>x\\ny</A>", Pattern.compile("<A>.*</A>")));
        Assertions.assertEquals("", RegExUtils.removeAll("<A>\nxy\n</A>", Pattern.compile("<A>.*</A>", Pattern.DOTALL)));
    }

    @Test
    public void testRemoveAll_StringString() {
        Assertions.assertNull(RegExUtils.removeAll(null, ""));
        Assertions.assertEquals("any", RegExUtils.removeAll("any", ((String) (null))));
        Assertions.assertEquals("any", RegExUtils.removeAll("any", ""));
        Assertions.assertEquals("", RegExUtils.removeAll("any", ".*"));
        Assertions.assertEquals("", RegExUtils.removeAll("any", ".+"));
        Assertions.assertEquals("", RegExUtils.removeAll("any", ".?"));
        Assertions.assertEquals("A\nB", RegExUtils.removeAll("A<__>\n<__>B", "<.*>"));
        Assertions.assertEquals("AB", RegExUtils.removeAll("A<__>\n<__>B", "(?s)<.*>"));
        Assertions.assertEquals("ABC123", RegExUtils.removeAll("ABCabc123abc", "[a-z]"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> RegExUtils.removeAll("any", "{badRegexSyntax}"), "RegExUtils.removeAll expecting PatternSyntaxException");
    }

    @Test
    public void testRemoveFirst_StringPattern() {
        Assertions.assertNull(RegExUtils.removeFirst(null, Pattern.compile("")));
        Assertions.assertEquals("any", RegExUtils.removeFirst("any", ((Pattern) (null))));
        Assertions.assertEquals("any", RegExUtils.removeFirst("any", Pattern.compile("")));
        Assertions.assertEquals("", RegExUtils.removeFirst("any", Pattern.compile(".*")));
        Assertions.assertEquals("", RegExUtils.removeFirst("any", Pattern.compile(".+")));
        Assertions.assertEquals("bc", RegExUtils.removeFirst("abc", Pattern.compile(".?")));
        Assertions.assertEquals("A\n<__>B", RegExUtils.removeFirst("A<__>\n<__>B", Pattern.compile("<.*>")));
        Assertions.assertEquals("AB", RegExUtils.removeFirst("A<__>\n<__>B", Pattern.compile("(?s)<.*>")));
        Assertions.assertEquals("ABCbc123", RegExUtils.removeFirst("ABCabc123", Pattern.compile("[a-z]")));
        Assertions.assertEquals("ABC123abc", RegExUtils.removeFirst("ABCabc123abc", Pattern.compile("[a-z]+")));
    }

    @Test
    public void testRemoveFirst_StringString() {
        Assertions.assertNull(RegExUtils.removeFirst(null, ""));
        Assertions.assertEquals("any", RegExUtils.removeFirst("any", ((String) (null))));
        Assertions.assertEquals("any", RegExUtils.removeFirst("any", ""));
        Assertions.assertEquals("", RegExUtils.removeFirst("any", ".*"));
        Assertions.assertEquals("", RegExUtils.removeFirst("any", ".+"));
        Assertions.assertEquals("bc", RegExUtils.removeFirst("abc", ".?"));
        Assertions.assertEquals("A\n<__>B", RegExUtils.removeFirst("A<__>\n<__>B", "<.*>"));
        Assertions.assertEquals("AB", RegExUtils.removeFirst("A<__>\n<__>B", "(?s)<.*>"));
        Assertions.assertEquals("ABCbc123", RegExUtils.removeFirst("ABCabc123", "[a-z]"));
        Assertions.assertEquals("ABC123abc", RegExUtils.removeFirst("ABCabc123abc", "[a-z]+"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> RegExUtils.removeFirst("any", "{badRegexSyntax}"), "RegExUtils.removeFirst expecting PatternSyntaxException");
    }

    @Test
    public void testRemovePattern_StringString() {
        Assertions.assertNull(RegExUtils.removePattern(null, ""));
        Assertions.assertEquals("any", RegExUtils.removePattern("any", ((String) (null))));
        Assertions.assertEquals("", RegExUtils.removePattern("", ""));
        Assertions.assertEquals("", RegExUtils.removePattern("", ".*"));
        Assertions.assertEquals("", RegExUtils.removePattern("", ".+"));
        Assertions.assertEquals("AB", RegExUtils.removePattern("A<__>\n<__>B", "<.*>"));
        Assertions.assertEquals("AB", RegExUtils.removePattern("A<__>\\n<__>B", "<.*>"));
        Assertions.assertEquals("", RegExUtils.removePattern("<A>x\\ny</A>", "<A>.*</A>"));
        Assertions.assertEquals("", RegExUtils.removePattern("<A>\nxy\n</A>", "<A>.*</A>"));
        Assertions.assertEquals("ABC123", RegExUtils.removePattern("ABCabc123", "[a-z]"));
    }

    @Test
    public void testReplaceAll_StringPatternString() {
        Assertions.assertNull(RegExUtils.replaceAll(null, Pattern.compile(""), ""));
        Assertions.assertEquals("any", RegExUtils.replaceAll("any", ((Pattern) (null)), ""));
        Assertions.assertEquals("any", RegExUtils.replaceAll("any", Pattern.compile(""), null));
        Assertions.assertEquals("zzz", RegExUtils.replaceAll("", Pattern.compile(""), "zzz"));
        Assertions.assertEquals("zzz", RegExUtils.replaceAll("", Pattern.compile(".*"), "zzz"));
        Assertions.assertEquals("", RegExUtils.replaceAll("", Pattern.compile(".+"), "zzz"));
        Assertions.assertEquals("ZZaZZbZZcZZ", RegExUtils.replaceAll("abc", Pattern.compile(""), "ZZ"));
        Assertions.assertEquals("z\nz", RegExUtils.replaceAll("<__>\n<__>", Pattern.compile("<.*>"), "z"));
        Assertions.assertEquals("z", RegExUtils.replaceAll("<__>\n<__>", Pattern.compile("(?s)<.*>"), "z"));
        Assertions.assertEquals("z", RegExUtils.replaceAll("<__>\n<__>", Pattern.compile("<.*>", Pattern.DOTALL), "z"));
        Assertions.assertEquals("z", RegExUtils.replaceAll("<__>\\n<__>", Pattern.compile("<.*>"), "z"));
        Assertions.assertEquals("X", RegExUtils.replaceAll("<A>\nxy\n</A>", Pattern.compile("<A>.*</A>", Pattern.DOTALL), "X"));
        Assertions.assertEquals("ABC___123", RegExUtils.replaceAll("ABCabc123", Pattern.compile("[a-z]"), "_"));
        Assertions.assertEquals("ABC_123", RegExUtils.replaceAll("ABCabc123", Pattern.compile("[^A-Z0-9]+"), "_"));
        Assertions.assertEquals("ABC123", RegExUtils.replaceAll("ABCabc123", Pattern.compile("[^A-Z0-9]+"), ""));
        Assertions.assertEquals("Lorem_ipsum_dolor_sit", RegExUtils.replaceAll("Lorem ipsum  dolor   sit", Pattern.compile("( +)([a-z]+)"), "_$2"));
    }

    @Test
    public void testReplaceAll_StringStringString() {
        Assertions.assertNull(RegExUtils.replaceAll(null, "", ""));
        Assertions.assertEquals("any", RegExUtils.replaceAll("any", ((String) (null)), ""));
        Assertions.assertEquals("any", RegExUtils.replaceAll("any", "", null));
        Assertions.assertEquals("zzz", RegExUtils.replaceAll("", "", "zzz"));
        Assertions.assertEquals("zzz", RegExUtils.replaceAll("", ".*", "zzz"));
        Assertions.assertEquals("", RegExUtils.replaceAll("", ".+", "zzz"));
        Assertions.assertEquals("ZZaZZbZZcZZ", RegExUtils.replaceAll("abc", "", "ZZ"));
        Assertions.assertEquals("z\nz", RegExUtils.replaceAll("<__>\n<__>", "<.*>", "z"));
        Assertions.assertEquals("z", RegExUtils.replaceAll("<__>\n<__>", "(?s)<.*>", "z"));
        Assertions.assertEquals("ABC___123", RegExUtils.replaceAll("ABCabc123", "[a-z]", "_"));
        Assertions.assertEquals("ABC_123", RegExUtils.replaceAll("ABCabc123", "[^A-Z0-9]+", "_"));
        Assertions.assertEquals("ABC123", RegExUtils.replaceAll("ABCabc123", "[^A-Z0-9]+", ""));
        Assertions.assertEquals("Lorem_ipsum_dolor_sit", RegExUtils.replaceAll("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> RegExUtils.replaceAll("any", "{badRegexSyntax}", ""), "RegExUtils.replaceAll expecting PatternSyntaxException");
    }

    @Test
    public void testReplaceFirst_StringPatternString() {
        Assertions.assertNull(RegExUtils.replaceFirst(null, Pattern.compile(""), ""));
        Assertions.assertEquals("any", RegExUtils.replaceFirst("any", ((Pattern) (null)), ""));
        Assertions.assertEquals("any", RegExUtils.replaceFirst("any", Pattern.compile(""), null));
        Assertions.assertEquals("zzz", RegExUtils.replaceFirst("", Pattern.compile(""), "zzz"));
        Assertions.assertEquals("zzz", RegExUtils.replaceFirst("", Pattern.compile(".*"), "zzz"));
        Assertions.assertEquals("", RegExUtils.replaceFirst("", Pattern.compile(".+"), "zzz"));
        Assertions.assertEquals("ZZabc", RegExUtils.replaceFirst("abc", Pattern.compile(""), "ZZ"));
        Assertions.assertEquals("z\n<__>", RegExUtils.replaceFirst("<__>\n<__>", Pattern.compile("<.*>"), "z"));
        Assertions.assertEquals("z", RegExUtils.replaceFirst("<__>\n<__>", Pattern.compile("(?s)<.*>"), "z"));
        Assertions.assertEquals("ABC_bc123", RegExUtils.replaceFirst("ABCabc123", Pattern.compile("[a-z]"), "_"));
        Assertions.assertEquals("ABC_123abc", RegExUtils.replaceFirst("ABCabc123abc", Pattern.compile("[^A-Z0-9]+"), "_"));
        Assertions.assertEquals("ABC123abc", RegExUtils.replaceFirst("ABCabc123abc", Pattern.compile("[^A-Z0-9]+"), ""));
        Assertions.assertEquals("Lorem_ipsum  dolor   sit", RegExUtils.replaceFirst("Lorem ipsum  dolor   sit", Pattern.compile("( +)([a-z]+)"), "_$2"));
    }

    @Test
    public void testReplaceFirst_StringStringString() {
        Assertions.assertNull(RegExUtils.replaceFirst(null, "", ""));
        Assertions.assertEquals("any", RegExUtils.replaceFirst("any", ((String) (null)), ""));
        Assertions.assertEquals("any", RegExUtils.replaceFirst("any", "", null));
        Assertions.assertEquals("zzz", RegExUtils.replaceFirst("", "", "zzz"));
        Assertions.assertEquals("zzz", RegExUtils.replaceFirst("", ".*", "zzz"));
        Assertions.assertEquals("", RegExUtils.replaceFirst("", ".+", "zzz"));
        Assertions.assertEquals("ZZabc", RegExUtils.replaceFirst("abc", "", "ZZ"));
        Assertions.assertEquals("z\n<__>", RegExUtils.replaceFirst("<__>\n<__>", "<.*>", "z"));
        Assertions.assertEquals("z", RegExUtils.replaceFirst("<__>\n<__>", "(?s)<.*>", "z"));
        Assertions.assertEquals("ABC_bc123", RegExUtils.replaceFirst("ABCabc123", "[a-z]", "_"));
        Assertions.assertEquals("ABC_123abc", RegExUtils.replaceFirst("ABCabc123abc", "[^A-Z0-9]+", "_"));
        Assertions.assertEquals("ABC123abc", RegExUtils.replaceFirst("ABCabc123abc", "[^A-Z0-9]+", ""));
        Assertions.assertEquals("Lorem_ipsum  dolor   sit", RegExUtils.replaceFirst("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2"));
        Assertions.assertThrows(PatternSyntaxException.class, () -> RegExUtils.replaceFirst("any", "{badRegexSyntax}", ""), "RegExUtils.replaceFirst expecting PatternSyntaxException");
    }

    @Test
    public void testReplacePattern_StringStringString() {
        Assertions.assertNull(RegExUtils.replacePattern(null, "", ""));
        Assertions.assertEquals("any", RegExUtils.replacePattern("any", ((String) (null)), ""));
        Assertions.assertEquals("any", RegExUtils.replacePattern("any", "", null));
        Assertions.assertEquals("zzz", RegExUtils.replacePattern("", "", "zzz"));
        Assertions.assertEquals("zzz", RegExUtils.replacePattern("", ".*", "zzz"));
        Assertions.assertEquals("", RegExUtils.replacePattern("", ".+", "zzz"));
        Assertions.assertEquals("z", RegExUtils.replacePattern("<__>\n<__>", "<.*>", "z"));
        Assertions.assertEquals("z", RegExUtils.replacePattern("<__>\\n<__>", "<.*>", "z"));
        Assertions.assertEquals("X", RegExUtils.replacePattern("<A>\nxy\n</A>", "<A>.*</A>", "X"));
        Assertions.assertEquals("ABC___123", RegExUtils.replacePattern("ABCabc123", "[a-z]", "_"));
        Assertions.assertEquals("ABC_123", RegExUtils.replacePattern("ABCabc123", "[^A-Z0-9]+", "_"));
        Assertions.assertEquals("ABC123", RegExUtils.replacePattern("ABCabc123", "[^A-Z0-9]+", ""));
        Assertions.assertEquals("Lorem_ipsum_dolor_sit", RegExUtils.replacePattern("Lorem ipsum  dolor   sit", "( +)([a-z]+)", "_$2"));
    }
}

