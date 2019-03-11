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
package org.sonar.server.es;


import java.util.Date;
import java.util.List;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.test.TestUtils;


public class EsUtilsTest {
    @Test
    public void convertToDocs_empty() {
        SearchHits hits = Mockito.mock(SearchHits.class, Mockito.RETURNS_MOCKS);
        List<BaseDoc> docs = EsUtils.convertToDocs(hits, IssueDoc::new);
        assertThat(docs).isEmpty();
    }

    @Test
    public void convertToDocs() {
        SearchHits hits = Mockito.mock(SearchHits.class, Mockito.RETURNS_MOCKS);
        Mockito.when(hits.getHits()).thenReturn(new SearchHit[]{ Mockito.mock(SearchHit.class) });
        List<BaseDoc> docs = EsUtils.convertToDocs(hits, IssueDoc::new);
        assertThat(docs).hasSize(1);
    }

    @Test
    public void util_class() {
        assertThat(TestUtils.hasOnlyPrivateConstructors(EsUtils.class)).isTrue();
    }

    @Test
    public void es_date_format() {
        assertThat(EsUtils.formatDateTime(new Date(1500000000000L))).startsWith("2017-07-");
        assertThat(EsUtils.formatDateTime(null)).isNull();
        assertThat(EsUtils.parseDateTime("2017-07-14T04:40:00.000+02:00").getTime()).isEqualTo(1500000000000L);
        assertThat(EsUtils.parseDateTime(null)).isNull();
    }

    @Test
    public void test_escapeSpecialRegexChars() {
        assertThat(EsUtils.escapeSpecialRegexChars("")).isEqualTo("");
        assertThat(EsUtils.escapeSpecialRegexChars("foo")).isEqualTo("foo");
        assertThat(EsUtils.escapeSpecialRegexChars("FOO")).isEqualTo("FOO");
        assertThat(EsUtils.escapeSpecialRegexChars("foo++")).isEqualTo("foo\\+\\+");
        assertThat(EsUtils.escapeSpecialRegexChars("foo[]")).isEqualTo("foo\\[\\]");
        assertThat(EsUtils.escapeSpecialRegexChars(".*")).isEqualTo("\\.\\*");
        assertThat(EsUtils.escapeSpecialRegexChars("foo\\d")).isEqualTo("foo\\\\d");
        assertThat(EsUtils.escapeSpecialRegexChars("^")).isEqualTo("\\^");
        assertThat(EsUtils.escapeSpecialRegexChars("$")).isEqualTo("\\$");
        assertThat(EsUtils.escapeSpecialRegexChars("|")).isEqualTo("\\|");
        assertThat(EsUtils.escapeSpecialRegexChars("<")).isEqualTo("\\<");
        assertThat(EsUtils.escapeSpecialRegexChars(">")).isEqualTo("\\>");
        assertThat(EsUtils.escapeSpecialRegexChars("\"")).isEqualTo("\\\"");
        assertThat(EsUtils.escapeSpecialRegexChars("#")).isEqualTo("\\#");
        assertThat(EsUtils.escapeSpecialRegexChars("~")).isEqualTo("\\~");
        assertThat(EsUtils.escapeSpecialRegexChars("$")).isEqualTo("\\$");
        assertThat(EsUtils.escapeSpecialRegexChars("&")).isEqualTo("\\&");
        assertThat(EsUtils.escapeSpecialRegexChars("?")).isEqualTo("\\?");
        assertThat(EsUtils.escapeSpecialRegexChars("a bit of | & #<\"$ .* ^ everything")).isEqualTo("a bit of \\| \\& \\#\\<\\\"\\$ \\.\\* \\^ everything");
    }
}

