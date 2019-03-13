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
package org.languagetool.tokenizers.zh;


import java.util.List;
import org.junit.Assert;
import org.junit.Test;


/**
 * The test of ChineseWordTokenizer.
 *
 * @author Minshan Chen
 * @author Xiaohui Wu
 * @author Jiamin Zheng
 * @author Zihao Li
 */
public class ChineseWordTokenizerTest {
    @Test
    public void testTokenize() {
        ChineseWordTokenizer wordTokenizer = new ChineseWordTokenizer();
        List<String> tokens = wordTokenizer.tokenize("????????????");
        Assert.assertEquals(tokens.size(), 7);
        Assert.assertEquals("[??/n, ??/vd, ??/v, ??/a, ?/u, ??/n, ?/w]", tokens.toString());
        List<String> tokens2 = wordTokenizer.tokenize("????????????");
        Assert.assertEquals(tokens2.size(), 10);
        Assert.assertEquals("[?/r, ??/s, ?/v, ?/u, ?/m, ?/q, ??/z, ?/u, ?/n, ?/w]", tokens2.toString());
        List<String> tokens3 = wordTokenizer.tokenize("???????????????");
        Assert.assertEquals(tokens3.size(), 11);
        Assert.assertEquals("[?/w, ?/x, ?/x, ?/w, ?/u, ??/vn, ??/n, ?/v, ??/r, ??/an, ?/w]", tokens3.toString());
        List<String> tokens4 = wordTokenizer.tokenize("????????????????");
        Assert.assertEquals(tokens4.size(), 11);
        Assert.assertEquals("[??/r, ?/u, ?/b, ??/n, ?/d, ?/v, ?/ng, ?/ng, ?/n, ????/l, ?/w]", tokens4.toString());
        List<String> tokens5 = wordTokenizer.tokenize("?????????????????????");
        Assert.assertEquals(tokens5.size(), 12);
        Assert.assertEquals("[???/nt, ?/w, ?/h, ??/n, ??/vn, ??/n, ??/v, ??/ad, ??/v, ?/u, ??/n, ?/w]", tokens5.toString());
        List<String> tokens6 = wordTokenizer.tokenize("?????????????????????");
        Assert.assertEquals(tokens6.size(), 13);
        Assert.assertEquals("[?/w, ?/y, ?/w, ?/w, ?/w, ??/ns, ???/j, ??/n, ?/f, ?/u, ??/n, ????/i, ?/w]", tokens6.toString());
        List<String> tokens7 = wordTokenizer.tokenize("???????????????????????????");
        Assert.assertEquals(tokens7.size(), 19);
        Assert.assertEquals("[?/p, ??/a, ?/u, ??/t, ?/f, ?/w, ?/r, ?/c, ??/r, ??/n, ?/k, ??/v, ?/u, ?/n, ??/n, ?/u, ??/m, ??/n, ?/w]", tokens7.toString());
        List<String> tokens8 = wordTokenizer.tokenize("?????????ThinkPad T???????????????");
        Assert.assertEquals(tokens8.size(), 20);
        Assert.assertEquals("[?/w, ??/o, ?/w, ?/w, ?/m, ?/q, ??/nz, ThinkPad/nx, , T/nx, ??/q, ??/n, ?/p, ??/nr, ?/u, ??/n, ?/v, ?/u, ??/v, ?/w]", tokens8.toString());
    }
}

