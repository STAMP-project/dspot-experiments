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
package org.apache.shardingsphere.core.parsing.lexer.dialect.sqlserver;


import Assist.END;
import DefaultKeyword.FROM;
import DefaultKeyword.SELECT;
import DefaultKeyword.WHERE;
import Literals.CHARS;
import Literals.IDENTIFIER;
import Literals.INT;
import Literals.VARIABLE;
import Symbol.COLON_EQ;
import Symbol.COMMA;
import Symbol.EQ;
import Symbol.STAR;
import org.apache.shardingsphere.core.parsing.lexer.LexerAssert;
import org.junit.Test;


public final class SQLServerLexerTest {
    @Test
    public void assertNextTokenForVariable() {
        SQLServerLexer lexer = new SQLServerLexer("SELECT @x1:=1, @@global.x1 FROM XXX_TABLE");
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, VARIABLE, "@x1");
        LexerAssert.assertNextToken(lexer, COLON_EQ, ":=");
        LexerAssert.assertNextToken(lexer, INT, "1");
        LexerAssert.assertNextToken(lexer, COMMA, ",");
        LexerAssert.assertNextToken(lexer, VARIABLE, "@@global.x1");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XXX_TABLE");
        LexerAssert.assertNextToken(lexer, END, "");
    }

    @Test
    public void assertNChar() {
        SQLServerLexer lexer = new SQLServerLexer("SELECT * FROM XXX_TABLE WHERE XX=N'xx'");
        LexerAssert.assertNextToken(lexer, SELECT, "SELECT");
        LexerAssert.assertNextToken(lexer, STAR, "*");
        LexerAssert.assertNextToken(lexer, FROM, "FROM");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XXX_TABLE");
        LexerAssert.assertNextToken(lexer, WHERE, "WHERE");
        LexerAssert.assertNextToken(lexer, IDENTIFIER, "XX");
        LexerAssert.assertNextToken(lexer, EQ, "=");
        LexerAssert.assertNextToken(lexer, CHARS, "xx");
    }
}

