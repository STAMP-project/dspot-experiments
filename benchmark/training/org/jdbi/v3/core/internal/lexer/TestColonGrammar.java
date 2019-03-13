/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jdbi.v3.core.internal.lexer;


import org.junit.Test;


public class TestColonGrammar extends GrammarTestCase {
    @Test
    public void testNamedOnly() throws Exception {
        expect("select id from something where name like ':foo' and id = :id and name like :name", ColonStatementLexer.LITERAL, ColonStatementLexer.QUOTED_TEXT, ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.EOF);
    }

    @Test
    public void testEmptyQuote() throws Exception {
        expect("select ''", ColonStatementLexer.LITERAL, ColonStatementLexer.QUOTED_TEXT, ColonStatementLexer.EOF);
    }

    @Test
    public void testEscapedEmptyQuote() throws Exception {
        expect("select \'\\\'\'", ColonStatementLexer.LITERAL, ColonStatementLexer.QUOTED_TEXT, ColonStatementLexer.EOF);
    }

    @Test
    public void testEscapedColon() throws Exception {
        expect("insert into foo (val) VALUE (:bar\\:\\:type)", ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.ESCAPED_TEXT, ColonStatementLexer.ESCAPED_TEXT, ColonStatementLexer.LITERAL, ColonStatementLexer.EOF);
    }

    @Test
    public void testMixed() throws Exception {
        expect("select id from something where name like ':foo' and id = ? and name like :name", ColonStatementLexer.LITERAL, ColonStatementLexer.QUOTED_TEXT, ColonStatementLexer.LITERAL, ColonStatementLexer.POSITIONAL_PARAM, ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.EOF);
    }

    @Test
    public void testThisBrokeATest() throws Exception {
        expect("insert into something (id, name) values (:id, :name)", ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.LITERAL, ColonStatementLexer.EOF);
    }

    @Test
    public void testExclamationWorks() throws Exception {
        expect("select1 != 2 from dual", ColonStatementLexer.LITERAL, ColonStatementLexer.EOF);
    }

    @Test
    public void testHashInColumnNameWorks() throws Exception {
        expect("select col# from something where id = :id", ColonStatementLexer.LITERAL, ColonStatementLexer.NAMED_PARAM, ColonStatementLexer.EOF);
    }
}

