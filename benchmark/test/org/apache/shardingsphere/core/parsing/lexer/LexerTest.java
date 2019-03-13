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
package org.apache.shardingsphere.core.parsing.lexer;


import Assist.END;
import DefaultKeyword.BY;
import DefaultKeyword.DESC;
import DefaultKeyword.FROM;
import DefaultKeyword.GROUP;
import DefaultKeyword.IN;
import DefaultKeyword.ORDER;
import DefaultKeyword.SELECT;
import DefaultKeyword.SET;
import DefaultKeyword.UPDATE;
import DefaultKeyword.WHERE;
import Literals.CHARS;
import Literals.FLOAT;
import Literals.HEX;
import Literals.IDENTIFIER;
import Literals.INT;
import Symbol.COMMA;
import Symbol.DOT;
import Symbol.EQ;
import Symbol.GT;
import Symbol.LEFT_PAREN;
import Symbol.RIGHT_PAREN;
import Symbol.STAR;
import org.apache.shardingsphere.core.parsing.lexer.analyzer.Dictionary;
import org.apache.shardingsphere.core.parsing.parser.exception.SQLParsingException;
import org.junit.Test;


public final class LexerTest {
    private final Dictionary dictionary = new Dictionary();

    @Test
    public void assertNextTokenForWhitespace() {
        Lexer lexer = new Lexer("Select  \t \n * from \r\n TABLE_XXX \t", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "Select");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "from");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "TABLE_XXX");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNextTokenForOrderBy() {
        Lexer lexer = new Lexer("SELECT * FROM ORDER  ORDER \t  BY XX DESC", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "ORDER");
        LexerAssert.assertNextToken(lexer, ORDER, "ORDER");
        LexerAssert.assertNextToken(lexer, BY, "BY");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XX");
        LexerAssert.assertNextToken(lexer, DESC, "DESC");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNextTokenForGroupBy() {
        Lexer lexer = new Lexer("SELECT * FROM GROUP  Group \n  By XX DESC", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "GROUP");
        LexerAssert.assertNextToken(lexer, GROUP, "Group");
        LexerAssert.assertNextToken(lexer, BY, "By");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XX");
        LexerAssert.assertNextToken(lexer, DESC, "DESC");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNextTokenForNumber() {
        assertNextTokenForNumber("0x1e", HEX);
        assertNextTokenForNumber("0x-1e", HEX);
        assertNextTokenForNumber("123", INT);
        assertNextTokenForNumber("-123", INT);
        assertNextTokenForNumber("-.123", FLOAT);
        assertNextTokenForNumber("123.0", FLOAT);
        assertNextTokenForNumber("123e4", FLOAT);
        assertNextTokenForNumber("123E4", FLOAT);
        assertNextTokenForNumber("123e+4", FLOAT);
        assertNextTokenForNumber("123E+4", FLOAT);
        assertNextTokenForNumber("123e-4", FLOAT);
        assertNextTokenForNumber("123E-4", FLOAT);
        assertNextTokenForNumber(".5", FLOAT);
        assertNextTokenForNumber("123f", FLOAT);
        assertNextTokenForNumber("123F", FLOAT);
        assertNextTokenForNumber(".5F", FLOAT);
        assertNextTokenForNumber("123d", FLOAT);
        assertNextTokenForNumber("123D", FLOAT);
    }

    @Test
    public void assertNextTokenForString() {
        Lexer lexer = new Lexer("SELECT * FROM XXX_TABLE WHERE XX IN (\'xxx\',\'x\'\'x\'\'\'\'x\',\"xyz\",\"x\"\"yz\")", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XXX_TABLE");
        LexerAssert.assertNextToken(lexer, WHERE, "WHERE");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XX");
        LexerAssert.assertNextToken(lexer, IN, "IN");
        LexerAssert.assertNextToken(lexer, LEFT_PAREN, "(");
        LexerAssert.assertNextToken(lexer, CHARS, "xxx");
        LexerAssert.assertNextToken(lexer, COMMA, ",");
        LexerAssert.assertNextToken(lexer, CHARS, "x''x''''x");
        LexerAssert.assertNextToken(lexer, COMMA, ",");
        LexerAssert.assertNextToken(lexer, CHARS, "xyz");
        LexerAssert.assertNextToken(lexer, COMMA, ",");
        LexerAssert.assertNextToken(lexer, CHARS, "x\"\"yz");
        LexerAssert.assertNextToken(lexer, RIGHT_PAREN, ")");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNextTokenForSingleLineComment() {
        Lexer lexer = new Lexer("SELECT * FROM XXX_TABLE --x\"y`z \n WHERE XX=1 //x\"y\'z", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XXX_TABLE");
        LexerAssert.assertNextToken(lexer, WHERE, "WHERE");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XX");
        LexerAssert.assertNextToken(lexer, EQ, "=");
        LexerAssert.assertNextToken(lexer, INT, "1");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNextTokenForMultipleLineComment() {
        Lexer lexer = new Lexer("SELECT * FROM XXX_TABLE /*--xyz \n WHERE XX=1 //xyz*/ WHERE YY>2 /*--xyz //xyz*/", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XXX_TABLE");
        LexerAssert.assertNextToken(lexer, WHERE, "WHERE");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "YY");
        LexerAssert.assertNextToken(lexer, GT, ">");
        LexerAssert.assertNextToken(lexer, INT, "2");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNChar() {
        Lexer lexer = new Lexer("SELECT * FROM XXX_TABLE WHERE XX=N'xx'", dictionary);
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XXX_TABLE");
        LexerAssert.assertNextToken(lexer, WHERE, "WHERE");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XX");
        LexerAssert.assertNextToken(lexer, EQ, "=");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "N");
        LexerAssert.assertNextToken(lexer, CHARS, "xx");
    }

    @Test(expected = SQLParsingException.class)
    public void assertSyntaxErrorForUnclosedChar() {
        Lexer lexer = new Lexer("UPDATE product p SET p.title='Title's',s.description='??' WHERE p.product_id=?", dictionary);
        LexerAssert.assertNextToken(lexer, UPDATE, "UPDATE");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "product");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "p");
        LexerAssert.assertNextToken(lexer, SET, "SET");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "p");
        LexerAssert.assertNextToken(lexer, DOT, ".");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "title");
        LexerAssert.assertNextToken(lexer, EQ, "=");
        LexerAssert.assertNextToken(lexer, CHARS, "Title");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "s");
        LexerAssert.assertNextToken(lexer, CHARS, ",s.description=");
        lexer.nextToken();
    }
}

