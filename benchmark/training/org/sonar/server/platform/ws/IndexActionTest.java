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
package org.sonar.server.platform.ws;


import com.google.common.collect.ImmutableSet;
import java.net.HttpURLConnection;
import java.util.Date;
import java.util.Locale;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import org.sonar.api.platform.Server;
import org.sonar.api.utils.DateUtils;
import org.sonar.core.i18n.DefaultI18n;
import org.sonar.server.ws.TestResponse;
import org.sonar.server.ws.WsActionTester;


public class IndexActionTest {
    private static final String KEY_1 = "key1";

    private static final String KEY_2 = "key2";

    private static final String KEY_3 = "key3";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DefaultI18n i18n = Mockito.mock(DefaultI18n.class);

    private Server server = Mockito.mock(Server.class);

    private IndexAction underTest = new IndexAction(i18n, server);

    private WsActionTester ws = new WsActionTester(underTest);

    @Test
    public void allow_client_to_cache_messages() {
        Date now = new Date();
        Date aBitLater = new Date(((now.getTime()) + 1000));
        Mockito.when(server.getStartedAt()).thenReturn(now);
        TestResponse result = call(null, DateUtils.formatDateTime(aBitLater));
        Mockito.verifyZeroInteractions(i18n);
        Mockito.verify(server).getStartedAt();
        assertThat(result.getStatus()).isEqualTo(HttpURLConnection.HTTP_NOT_MODIFIED);
    }

    @Test
    public void return_all_l10n_messages_using_accept_header_with_cache_expired() {
        Date now = new Date();
        Date aBitEarlier = new Date(((now.getTime()) - 1000));
        Mockito.when(server.getStartedAt()).thenReturn(now);
        Mockito.when(i18n.getPropertyKeys()).thenReturn(ImmutableSet.of(IndexActionTest.KEY_1, IndexActionTest.KEY_2, IndexActionTest.KEY_3));
        Mockito.when(i18n.message(Locale.PRC, IndexActionTest.KEY_1, IndexActionTest.KEY_1)).thenReturn(IndexActionTest.KEY_1);
        Mockito.when(i18n.message(Locale.PRC, IndexActionTest.KEY_2, IndexActionTest.KEY_2)).thenReturn(IndexActionTest.KEY_2);
        Mockito.when(i18n.message(Locale.PRC, IndexActionTest.KEY_3, IndexActionTest.KEY_3)).thenReturn(IndexActionTest.KEY_3);
        Mockito.when(i18n.getEffectiveLocale(Locale.PRC)).thenReturn(Locale.PRC);
        TestResponse result = call(Locale.PRC.toLanguageTag(), DateUtils.formatDateTime(aBitEarlier));
        Mockito.verify(i18n).getPropertyKeys();
        Mockito.verify(i18n).message(Locale.PRC, IndexActionTest.KEY_1, IndexActionTest.KEY_1);
        Mockito.verify(i18n).message(Locale.PRC, IndexActionTest.KEY_2, IndexActionTest.KEY_2);
        Mockito.verify(i18n).message(Locale.PRC, IndexActionTest.KEY_3, IndexActionTest.KEY_3);
        assertJson(result.getInput()).isSimilarTo("{\"effectiveLocale\":\"zh-CN\", \"messages\": {\"key1\":\"key1\",\"key2\":\"key2\",\"key3\":\"key3\"}}");
    }

    @Test
    public void default_locale_is_english() {
        String key1 = "key1";
        String key2 = "key2";
        String key3 = "key3";
        Mockito.when(i18n.getPropertyKeys()).thenReturn(ImmutableSet.of(key1, key2, key3));
        Mockito.when(i18n.message(Locale.ENGLISH, key1, key1)).thenReturn(key1);
        Mockito.when(i18n.message(Locale.ENGLISH, key2, key2)).thenReturn(key2);
        Mockito.when(i18n.message(Locale.ENGLISH, key3, key3)).thenReturn(key3);
        Mockito.when(i18n.getEffectiveLocale(Locale.ENGLISH)).thenReturn(Locale.ENGLISH);
        TestResponse result = call(null, null);
        Mockito.verify(i18n).getPropertyKeys();
        Mockito.verify(i18n).message(Locale.ENGLISH, key1, key1);
        Mockito.verify(i18n).message(Locale.ENGLISH, key2, key2);
        Mockito.verify(i18n).message(Locale.ENGLISH, key3, key3);
        assertJson(result.getInput()).isSimilarTo("{\"messages\": {\"key1\":\"key1\",\"key2\":\"key2\",\"key3\":\"key3\"}}");
    }

    @Test
    public void support_BCP47_formatted_language_tags() {
        String key1 = "key1";
        Mockito.when(i18n.getPropertyKeys()).thenReturn(ImmutableSet.of(key1));
        Mockito.when(i18n.message(Locale.UK, key1, key1)).thenReturn(key1);
        Mockito.when(i18n.getEffectiveLocale(Locale.UK)).thenReturn(Locale.UK);
        TestResponse result = call("en-GB", null);
        Mockito.verify(i18n).getPropertyKeys();
        Mockito.verify(i18n).message(Locale.UK, key1, key1);
        assertJson(result.getInput()).isSimilarTo("{\"messages\": {\"key1\":\"key1\"}}");
    }

    @Test
    public void fail_when_java_formatted_language_tags() {
        String key1 = "key1";
        Mockito.when(i18n.getPropertyKeys()).thenReturn(ImmutableSet.of(key1));
        Mockito.when(i18n.message(Locale.UK, key1, key1)).thenReturn(key1);
        Mockito.when(i18n.getEffectiveLocale(Locale.UK)).thenReturn(Locale.UK);
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("'en_GB' cannot be parsed as a BCP47 language tag");
        call("en_GB", null);
    }
}

