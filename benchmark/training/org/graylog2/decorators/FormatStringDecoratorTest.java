/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.decorators;


import com.floreysoft.jmte.Engine;
import org.graylog2.rest.resources.search.responses.SearchResponse;
import org.junit.Test;


public class FormatStringDecoratorTest {
    private final Engine templateEngine = new Engine();

    @Test
    public void testFormat() {
        final DecoratorImpl decorator = getDecoratorConfig("${field_a}: ${field_b}", "message", true);
        final FormatStringDecorator formatStringDecorator = new FormatStringDecorator(decorator, templateEngine);
        final SearchResponse searchResponse = getSearchResponse();
        final SearchResponse response = formatStringDecorator.apply(searchResponse);
        assertThat(response.messages().size()).isEqualTo(4);
        assertThat(response.messages().get(0).message().get("message")).isEqualTo("1: b");
        assertThat(response.messages().get(1).message().containsKey("message")).isFalse();
        assertThat(response.messages().get(2).message().containsKey("message")).isFalse();
        assertThat(response.messages().get(3).message().containsKey("message")).isFalse();
    }

    @Test
    public void formatAllowEmptyValues() {
        final DecoratorImpl decorator = getDecoratorConfig("${field_a}: ${field_b}", "message", false);
        final FormatStringDecorator formatStringDecorator = new FormatStringDecorator(decorator, templateEngine);
        final SearchResponse searchResponse = getSearchResponse();
        final SearchResponse response = formatStringDecorator.apply(searchResponse);
        assertThat(response.messages().size()).isEqualTo(4);
        assertThat(response.messages().get(0).message().get("message")).isEqualTo("1: b");
        assertThat(response.messages().get(1).message().get("message")).isEqualTo("1:");
        assertThat(response.messages().get(2).message().get("message")).isEqualTo(": b");
        assertThat(response.messages().get(3).message().get("message")).isEqualTo(":");
    }
}

