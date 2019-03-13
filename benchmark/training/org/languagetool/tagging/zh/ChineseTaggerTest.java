/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2005 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.tagging.zh;


import java.io.IOException;
import org.junit.Test;
import org.languagetool.TestTools;
import org.languagetool.tokenizers.zh.ChineseWordTokenizer;


/**
 * The test of ChineseTagger.
 *
 * @author Minshan Chen
 * @author Xiaohui Wu
 * @author Jiamin Zheng
 * @author Zihao Li
 */
public class ChineseTaggerTest {
    private ChineseTagger tagger;

    private ChineseWordTokenizer tokenizer;

    @Test
    public void testTagger() throws IOException {
        TestTools.myAssert("????????????", "??/[null]n -- ??/[null]vd -- ??/[null]v -- ??/[null]a -- ?/[null]u -- ??/[null]n -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("????????????", "?/[null]r -- ??/[null]s -- ?/[null]v -- ?/[null]u -- ?/[null]m -- ?/[null]q -- ??/[null]z -- ?/[null]u -- ?/[null]n -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("???????????????", "?/[null]w -- ?/[null]x -- ?/[null]x -- ?/[null]w -- ?/[null]u -- ??/[null]vn -- ??/[null]n -- ?/[null]v -- ??/[null]r -- ??/[null]an -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("????????????????", "??/[null]r -- ?/[null]u -- ?/[null]b -- ??/[null]n -- ?/[null]d -- ?/[null]v -- ?/[null]ng -- ?/[null]ng -- ?/[null]n -- ????/[null]l -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("?????????????????????", "???/[null]nt -- ?/[null]w -- ?/[null]h -- ??/[null]n -- ??/[null]vn -- ??/[null]n -- ??/[null]v -- ??/[null]ad -- ??/[null]v -- ?/[null]u -- ??/[null]n -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("?????????????????????", "?/[null]w -- ?/[null]y -- ?/[null]w -- ?/[null]w -- ?/[null]w -- ??/[null]ns -- ???/[null]j -- ??/[null]n -- ?/[null]f -- ?/[null]u -- ??/[null]n -- ????/[null]i -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("???????????????????????????", "?/[null]p -- ??/[null]a -- ?/[null]u -- ??/[null]t -- ?/[null]f -- ?/[null]w -- ?/[null]r -- ?/[null]c -- ??/[null]r -- ??/[null]n -- ?/[null]k -- ??/[null]v -- ?/[null]u -- ?/[null]n -- ??/[null]n -- ?/[null]u -- ??/[null]m -- ??/[null]n -- ?/[null]w", tokenizer, tagger);
        TestTools.myAssert("?????????ThinkPad T???????????????", "?/[null]w -- ??/[null]o -- ?/[null]w -- ?/[null]w -- ?/[null]m -- ?/[null]q -- ??/[null]nz -- ThinkPad/[null]nx -- T/[null]nx -- ??/[null]q -- ??/[null]n -- ?/[null]p -- ??/[null]nr -- ?/[null]u -- ??/[null]n -- ?/[null]v -- ?/[null]u -- ??/[null]v -- ?/[null]w", tokenizer, tagger);
    }
}

