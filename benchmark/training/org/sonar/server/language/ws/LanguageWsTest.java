/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.server.language.ws;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.sonar.api.resources.AbstractLanguage;
import org.sonar.api.resources.Languages;
import org.sonar.api.server.ws.WebService.Action;
import org.sonar.api.server.ws.WebService.Controller;
import org.sonar.server.ws.WsTester;


@RunWith(MockitoJUnitRunner.class)
public class LanguageWsTest {
    private static final String CONTROLLER_LANGUAGES = "api/languages";

    private static final String ACTION_LIST = "list";

    private static final String EMPTY_JSON_RESPONSE = "{\"languages\": []}";

    @Mock
    private Languages languages;

    private WsTester tester;

    @Test
    public void should_be_well_defined() {
        Controller controller = tester.controller(LanguageWsTest.CONTROLLER_LANGUAGES);
        assertThat(controller).isNotNull();
        assertThat(controller.description()).isNotEmpty();
        assertThat(controller.isInternal()).isFalse();
        assertThat(controller.path()).isEqualTo(LanguageWsTest.CONTROLLER_LANGUAGES);
        assertThat(controller.since()).isEqualTo("5.1");
        assertThat(controller.actions()).hasSize(1);
        Action list = controller.action(LanguageWsTest.ACTION_LIST);
        assertThat(list).isNotNull();
        assertThat(list.description()).isNotEmpty();
        assertThat(list.handler()).isInstanceOf(ListAction.class);
        assertThat(list.isInternal()).isFalse();
        assertThat(list.isPost()).isFalse();
        assertThat(list.responseExampleAsString()).isNotEmpty();
        assertThat(list.params()).hasSize(2);
    }

    @Test
    public void list_all_languages() throws Exception {
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).execute().assertJson(this.getClass(), "list.json");
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("ps", "2").execute().assertJson(this.getClass(), "list_limited.json");
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("ps", "4").execute().assertJson(this.getClass(), "list.json");
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("ps", "10").execute().assertJson(this.getClass(), "list.json");
    }

    @Test
    public void filter_languages_by_key_or_name() throws Exception {
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("q", "ws").execute().assertJson(this.getClass(), "list_filtered_key.json");
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("q", "o").execute().assertJson(this.getClass(), "list_filtered_name.json");
    }

    /**
     * Potential vulnerability : the query provided by user must
     * not be executed as a regexp.
     */
    @Test
    public void filter_escapes_the_user_query() throws Exception {
        // invalid regexp
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("q", "[").execute().assertJson(LanguageWsTest.EMPTY_JSON_RESPONSE);
        // do not consider param as a regexp
        tester.newGetRequest(LanguageWsTest.CONTROLLER_LANGUAGES, LanguageWsTest.ACTION_LIST).setParam("q", ".*").execute().assertJson(LanguageWsTest.EMPTY_JSON_RESPONSE);
    }

    abstract static class TestLanguage extends AbstractLanguage {
        TestLanguage(String key, String language) {
            super(key, language);
        }

        @Override
        public String[] getFileSuffixes() {
            return new String[0];
        }
    }

    static class Ook extends LanguageWsTest.TestLanguage {
        public Ook() {
            super("ook", "Ook!");
        }
    }

    static class LolCode extends LanguageWsTest.TestLanguage {
        public LolCode() {
            super("lol", "LOLCODE");
        }
    }

    static class Whitespace extends LanguageWsTest.TestLanguage {
        public Whitespace() {
            super("ws", "Whitespace");
        }
    }

    static class ArnoldC extends LanguageWsTest.TestLanguage {
        public ArnoldC() {
            super("ac", "ArnoldC");
        }
    }
}

