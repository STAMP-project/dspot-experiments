/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2018 Daniel Naber (http://www.danielnaber.de)
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
package org.languagetool.server;


import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.languagetool.Language;
import org.languagetool.Languages;


public class UserDictTest {
    static final String USERNAME1 = "test@test.de";

    static final String API_KEY1 = "foo";

    static final String USERNAME2 = "two@test.de";

    static final String API_KEY2 = "foo-two";

    @Test
    public void testHTTPServer() throws Exception {
        HTTPServerConfig config = new HTTPServerConfig(HTTPTools.getDefaultPort());
        config.setDatabaseDriver("org.hsqldb.jdbcDriver");
        config.setDatabaseUrl("jdbc:hsqldb:mem:testdb");
        config.setDatabaseUsername("");
        config.setDatabasePassword("");
        config.setSecretTokenKey("myfoo");
        config.setCacheSize(100);
        DatabaseAccess.init(config);
        // no need to also create test tables for logging
        DatabaseLogger.getInstance().disableLogging();
        try {
            DatabaseAccess.createAndFillTestTables();
            HTTPServer server = new HTTPServer(config);
            try {
                server.run();
                Language enUS = Languages.getLanguageForShortCode("en-US");
                runTests(enUS, "This is Mysurname.", "This is Mxsurname.", "Mysurname", "MORFOLOGIK_RULE_EN_US");
                runTests(enUS, "Mysurname is my name.", "Mxsurname is my name.", "Mysurname", "MORFOLOGIK_RULE_EN_US");
                Language deDE = Languages.getLanguageForShortCode("de-DE");
                runTests(deDE, "Das ist Meinname.", "Das ist Mxinname.", "Meinname", "GERMAN_SPELLER_RULE");
                runTests(deDE, "Meinname steht hier.", "Mxinname steht hier.", "Meinname", "GERMAN_SPELLER_RULE");
                runTests(deDE, "Hier steht Sch?ckl.", "Das ist Sch?ckl.", "Sch?ckl", "GERMAN_SPELLER_RULE");
                String res = check(deDE, "Hier steht Schockl", UserDictTest.USERNAME1, UserDictTest.API_KEY1);
                Assert.assertThat(StringUtils.countMatches(res, "GERMAN_SPELLER_RULE"), CoreMatchers.is(1));// 'Sch?ckl' accepted, but not 'Schockl' (NOTE: depends on encoding/collation of database)

                try {
                    System.out.println("=== Testing multi word insertion now, ignore stack trace: ===");
                    addWord("multi word", UserDictTest.USERNAME1, UserDictTest.API_KEY1);
                    Assert.fail("Should not be able to insert multi words");
                } catch (IOException ignore) {
                }
            } finally {
                server.stop();
            }
        } finally {
            DatabaseAccess.deleteTestTables();
        }
    }
}

