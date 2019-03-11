/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 */
package org.jooq.academy.section4;


import AUTHOR.ID;
import RenderNameCase.AS_IS;
import RenderNameCase.LOWER;
import RenderNameCase.PASCAL;
import RenderNameCase.UPPER;
import RenderQuotedNames.ALWAYS;
import RenderQuotedNames.NEVER;
import RenderQuotedNames.WHEN_NEEDED;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.academy.tools.Tools;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.MappedTable;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.junit.Test;


public class Example_4_2_Settings {
    @Test
    public void run() {
        Select<?> select = DSL.select().from(AUTHOR).where(ID.eq(3));
        Tools.title("A couple of settings at work - Formatting");
        System.out.println(using(SQLDialect.H2, new Settings().withRenderFormatted(false)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderFormatted(true)).render(select));
        Tools.title("A couple of settings at work - Schema");
        System.out.println(using(SQLDialect.H2, new Settings().withRenderSchema(false)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderSchema(true)).render(select));
        Tools.title("A couple of settings at work - Name case");
        System.out.println(using(SQLDialect.H2, new Settings().withRenderNameCase(AS_IS)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderNameCase(UPPER)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderNameCase(LOWER)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderNameCase(PASCAL)).render(select));
        Tools.title("A couple of settings at work - Name quoting");
        System.out.println(using(SQLDialect.H2, new Settings().withRenderQuotedNames(ALWAYS)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderQuotedNames(WHEN_NEEDED)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderQuotedNames(NEVER)).render(select));
        Tools.title("A couple of settings at work - Keyword case");
        System.out.println(using(SQLDialect.H2, new Settings().withRenderKeywordCase(RenderKeywordCase.UPPER)).render(select));
        System.out.println(using(SQLDialect.H2, new Settings().withRenderKeywordCase(RenderKeywordCase.LOWER)).render(select));
        Tools.title("A couple of settings at work - Mapping");
        System.out.println(using(SQLDialect.H2, new Settings().withRenderMapping(new RenderMapping().withSchemata(new MappedSchema().withInput("PUBLIC").withOutput("test").withTables(new MappedTable().withInput("AUTHOR").withOutput("test-author"))))).render(select));
    }
}

