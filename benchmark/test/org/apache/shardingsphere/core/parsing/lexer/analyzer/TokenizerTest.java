/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.shardingsphere.core.parsing.lexer.analyzer;


import Literals.FLOAT;
import Literals.HEX;
import Literals.INT;
import org.apache.shardingsphere.core.parsing.lexer.token.DefaultKeyword;
import org.apache.shardingsphere.core.parsing.lexer.token.Literals;
import org.apache.shardingsphere.core.parsing.lexer.token.Symbol;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;


public final class TokenizerTest {
    private final Dictionary dictionary = new Dictionary();

    @Test
    public void assertSkipWhitespace() {
        String sql = "SELECT *\tFROM\rTABLE_XXX\n";
        for (int i = 0; i < (sql.length()); i++) {
            Tokenizer tokenizer = new Tokenizer(sql, dictionary, i);
            int expected = i;
            if (CharType.isWhitespace(sql.charAt(i))) {
                expected += 1;
            }
            Assert.assertThat(tokenizer.skipWhitespace(), CoreMatchers.is(expected));
        }
    }

    @Test
    public void assertSkipCommentWithoutComment() {
        String sql = "SELECT * FROM XXX_TABLE";
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("_"));
        Assert.assertThat(tokenizer.skipComment(), CoreMatchers.is(sql.indexOf("_")));
    }

    @Test
    public void assertSkipSingleLineComment() {
        String singleLineCommentWithHyphen = "--x\"y`z\n";
        String singleLineCommentWithSlash = "//x\\\"y\'z\n";
        String sql = ((("SELECT * FROM XXX_TABLE " + singleLineCommentWithHyphen) + "WHERE XX") + singleLineCommentWithSlash) + "=1 ";
        Tokenizer hyphenTokenizer = new Tokenizer(sql, dictionary, sql.indexOf("-"));
        int expected = (sql.indexOf("-")) + (singleLineCommentWithHyphen.length());
        Assert.assertThat(hyphenTokenizer.skipComment(), CoreMatchers.is(expected));
        Tokenizer slashTokenizer = new Tokenizer(sql, dictionary, sql.indexOf("/"));
        expected = (sql.indexOf("/")) + (singleLineCommentWithSlash.length());
        Assert.assertThat(slashTokenizer.skipComment(), CoreMatchers.is(expected));
    }

    @Test
    public void assertSkipSingleLineMySQLComment() {
        String comment = "#x\"y`z\n";
        String sql = ("SELECT * FROM XXX_TABLE " + comment) + "WHERE XX=1";
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("#"));
        int expected = (sql.indexOf("#")) + (comment.length());
        Assert.assertThat(tokenizer.skipComment(), CoreMatchers.is(expected));
    }

    @Test
    public void assertSkipMultipleLineComment() {
        String comment = "/*--xyz \n WHERE XX=1 //xyz*/";
        String sql = ("SELECT * FROM XXX_TABLE " + comment) + "WHERE YY>2";
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("/"));
        int expected = (sql.indexOf("/")) + (comment.length());
        Assert.assertThat(tokenizer.skipComment(), CoreMatchers.is(expected));
    }

    @Test(expected = UnterminatedCharException.class)
    public void assertSkipMultipleLineCommentUnterminatedCharException() {
        String comment = "/*--xyz \n WHERE XX=1 //xyz";
        String sql = "SELECT * FROM XXX_TABLE " + comment;
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("/"));
        tokenizer.skipComment();
    }

    @Test
    public void assertSkipHint() {
        String comment = "/*--xyz \n WHERE XX=1 //xyz*/";
        String sql = ("SELECT * FROM XXX_TABLE " + comment) + "WHERE YY>2";
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("/"));
        int expected = (sql.indexOf("/")) + (comment.length());
        Assert.assertThat(tokenizer.skipHint(), CoreMatchers.is(expected));
    }

    @Test(expected = UnterminatedCharException.class)
    public void assertSkipHintUnterminatedCharException() {
        String comment = "/*--xyz \n WHERE XX=1 //xyz";
        String sql = "SELECT * FROM XXX_TABLE " + comment;
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("/"));
        tokenizer.skipHint();
    }

    @Test
    public void assertScanVariable() {
        String sql = "SELECT * FROM XXX_TABLE %s WHERE YY>2";
        assertScanVariable(sql, "@var");
        assertScanVariable(sql, "@@var");
    }

    @Test
    public void assertScanNumber() {
        String sql = "SELECT * FROM XXX_TABLE WHERE XX=%s";
        assertScanNumber(sql, "123", INT);
        assertScanNumber(sql, "-123", INT);
        assertScanNumber(sql, "123.0", FLOAT);
        assertScanNumber(sql, "123e4", FLOAT);
        assertScanNumber(sql, "123E4", FLOAT);
        assertScanNumber(sql, "123e+4", FLOAT);
        assertScanNumber(sql, "123E+4", FLOAT);
        assertScanNumber(sql, "123e-4", FLOAT);
        assertScanNumber(sql, "123E-4", FLOAT);
        assertScanNumber(sql, ".5", FLOAT);
        assertScanNumber(sql, "123f", FLOAT);
        assertScanNumber(sql, "123F", FLOAT);
        assertScanNumber(sql, ".5f", FLOAT);
        assertScanNumber(sql, ".5F", FLOAT);
        assertScanNumber(sql, "123d", FLOAT);
        assertScanNumber(sql, "123D", FLOAT);
        assertScanHexDecimal(sql, "0x1e", HEX);
        assertScanHexDecimal(sql, "0x-1e", HEX);
    }

    @Test
    public void assertScanNChars() {
        String sql = "SELECT * FROM ORDER, XX_TABLE AS `table` WHERE YY=N'xx' And group =-1 GROUP BY YY";
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("ORDER"));
        assertToken(tokenizer.scanIdentifier(), new org.apache.shardingsphere.core.parsing.lexer.token.Token(Literals.IDENTIFIER, "ORDER", sql.indexOf(",")));
        tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("GROUP"));
        assertToken(tokenizer.scanIdentifier(), new org.apache.shardingsphere.core.parsing.lexer.token.Token(DefaultKeyword.GROUP, "GROUP", ((sql.indexOf("BY")) - 1)));
        tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("`"));
        assertToken(tokenizer.scanIdentifier(), new org.apache.shardingsphere.core.parsing.lexer.token.Token(Literals.IDENTIFIER, "`table`", ((sql.indexOf("WHERE")) - 1)));
        tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("YY"));
        assertToken(tokenizer.scanIdentifier(), new org.apache.shardingsphere.core.parsing.lexer.token.Token(Literals.IDENTIFIER, "YY", sql.indexOf("=")));
        tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("=-"));
        assertToken(tokenizer.scanSymbol(), new org.apache.shardingsphere.core.parsing.lexer.token.Token(Symbol.EQ, "=", ((sql.indexOf("=-")) + 1)));
        tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("'"));
        assertToken(tokenizer.scanChars(), new org.apache.shardingsphere.core.parsing.lexer.token.Token(Literals.CHARS, "xx", ((sql.indexOf("And")) - 1)));
    }

    @Test(expected = UnterminatedCharException.class)
    public void assertScanChars() {
        String sql = "SELECT * FROM XXX_TABLE AS `TEST";
        Tokenizer tokenizer = new Tokenizer(sql, dictionary, sql.indexOf("`"));
        tokenizer.scanChars();
    }
}

