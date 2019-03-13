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
package org.graylog2.rest.resources.system;


import Duration.FIVE_SECONDS;
import Response.Status.ACCEPTED;
import com.google.common.eventbus.Subscribe;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import javax.ws.rs.core.Response;
import org.graylog2.grok.GrokPattern;
import org.graylog2.grok.GrokPatternService;
import org.graylog2.grok.GrokPatternsDeletedEvent;
import org.graylog2.grok.GrokPatternsUpdatedEvent;
import org.graylog2.grok.InMemoryGrokPatternService;
import org.graylog2.plugin.database.ValidationException;
import org.graylog2.rest.models.system.grokpattern.requests.GrokPatternTestRequest;
import org.graylog2.shared.bindings.GuiceInjectorHolder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class GrokResourceTest {
    private static final String[] GROK_LINES = new String[]{ "# Comment", "", "TEST_PATTERN_0 Foo" };

    @Rule
    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    private InMemoryGrokPatternService grokPatternService;

    private GrokResource grokResource;

    private GrokResourceTest.GrokPatternsChangedEventSubscriber subscriber;

    public GrokResourceTest() {
        GuiceInjectorHolder.createInjector(Collections.emptyList());
    }

    @Test
    public void bulkUpdatePatternsFromTextFileWithLF() throws Exception {
        final String patterns = Arrays.stream(GrokResourceTest.GROK_LINES).collect(Collectors.joining("\n"));
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(patterns.getBytes(StandardCharsets.UTF_8));
        final GrokPattern expectedPattern = GrokPattern.create("TEST_PATTERN_0", "Foo");
        final Response response = grokResource.bulkUpdatePatternsFromTextFile(inputStream, true);
        assertThat(response.getStatusInfo()).isEqualTo(ACCEPTED);
        assertThat(response.hasEntity()).isFalse();
        await().atMost(FIVE_SECONDS).until(() -> !(subscriber.events.isEmpty()));
        assertThat(subscriber.events).containsOnly(GrokPatternsUpdatedEvent.create(Collections.singleton(expectedPattern.name())));
    }

    @Test
    public void bulkUpdatePatternsFromTextFileWithCR() throws Exception {
        final String patterns = Arrays.stream(GrokResourceTest.GROK_LINES).collect(Collectors.joining("\r"));
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(patterns.getBytes(StandardCharsets.UTF_8));
        final GrokPattern expectedPattern = GrokPattern.create("TEST_PATTERN_0", "Foo");
        final Response response = grokResource.bulkUpdatePatternsFromTextFile(inputStream, true);
        assertThat(response.getStatusInfo()).isEqualTo(ACCEPTED);
        assertThat(response.hasEntity()).isFalse();
        await().atMost(FIVE_SECONDS).until(() -> !(subscriber.events.isEmpty()));
        assertThat(subscriber.events).containsOnly(GrokPatternsUpdatedEvent.create(Collections.singleton(expectedPattern.name())));
    }

    @Test
    public void bulkUpdatePatternsFromTextFileWithCRLF() throws Exception {
        final String patterns = Arrays.stream(GrokResourceTest.GROK_LINES).collect(Collectors.joining("\r\n"));
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(patterns.getBytes(StandardCharsets.UTF_8));
        final GrokPattern expectedPattern = GrokPattern.create("TEST_PATTERN_0", "Foo");
        final Response response = grokResource.bulkUpdatePatternsFromTextFile(inputStream, true);
        assertThat(response.getStatusInfo()).isEqualTo(ACCEPTED);
        assertThat(response.hasEntity()).isFalse();
        await().atMost(FIVE_SECONDS).until(() -> !(subscriber.events.isEmpty()));
        assertThat(subscriber.events).containsOnly(GrokPatternsUpdatedEvent.create(Collections.singleton(expectedPattern.name())));
    }

    @Test
    public void bulkUpdatePatternsFromTextFileWithInvalidPattern() throws Exception {
        final String patterns = "TEST_PATTERN_0 %{Foo";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(patterns.getBytes(StandardCharsets.UTF_8));
        expectedException.expect(ValidationException.class);
        expectedException.expectMessage("Invalid pattern. Did not save any patterns");
        grokResource.bulkUpdatePatternsFromTextFile(inputStream, true);
    }

    @Test
    public void bulkUpdatePatternsFromTextFileWithNoValidPatterns() throws Exception {
        final String patterns = "# Comment\nHAHAHAHA_THIS_IS_NO_GROK_PATTERN!$%\u00a7";
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(patterns.getBytes(StandardCharsets.UTF_8));
        final Response response = grokResource.bulkUpdatePatternsFromTextFile(inputStream, true);
        assertThat(response.getStatusInfo()).isEqualTo(ACCEPTED);
        assertThat(response.hasEntity()).isFalse();
        assertThat(subscriber.events).isEmpty();
    }

    @Test
    public void testPatternWithSampleData() throws Exception {
        final String sampleData = "1.2.3.4";
        final GrokPattern grokPattern = GrokPattern.create("IP", "\\d.\\d.\\d.\\d");
        grokPatternService.save(grokPattern);
        final GrokPatternTestRequest grokPatternTestRequest = GrokPatternTestRequest.create(grokPattern, sampleData);
        final Map<String, Object> expectedReturn = Collections.singletonMap("IP", "1.2.3.4");
        final Response response = grokResource.testPattern(grokPatternTestRequest);
        assertThat(response.hasEntity()).isTrue();
        assertThat(response.getEntity()).isEqualTo(expectedReturn);
    }

    static class GrokPatternsChangedEventSubscriber {
        final List<Object> events = new CopyOnWriteArrayList<>();

        @Subscribe
        public void handleUpdatedEvent(GrokPatternsUpdatedEvent event) {
            events.add(event);
        }

        @Subscribe
        public void handleDeletedEvent(GrokPatternsDeletedEvent event) {
            events.add(event);
        }
    }

    static class PermittedTestResource extends GrokResource {
        PermittedTestResource(GrokPatternService grokPatternService) {
            super(grokPatternService);
        }

        @Override
        protected boolean isPermitted(String permission) {
            return true;
        }
    }
}

