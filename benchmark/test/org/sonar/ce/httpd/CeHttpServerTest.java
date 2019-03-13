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
package org.sonar.ce.httpd;


import NanoHTTPD.MIME_PLAINTEXT;
import NanoHTTPD.Response.Status.OK;
import fi.iki.elonen.NanoHTTPD;
import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import okhttp3.Response;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.sonar.process.sharedmemoryfile.DefaultProcessCommands;


public class CeHttpServerTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private static final RuntimeException FAILING_ACTION = new IllegalStateException("Simulating the action failed");

    private CeHttpServer underTest;

    private File sharedDir;

    @Test
    public void start_publishes_URL_in_IPC() {
        try (DefaultProcessCommands commands = DefaultProcessCommands.secondary(this.sharedDir, 1)) {
            assertThat(commands.getHttpUrl()).startsWith("http://127.0.0.1:");
        }
    }

    @Test
    public void return_http_response_with_code_404_and_exception_message_as_body_when_url_has_no_matching_action() throws IOException {
        String action = "/dfdsfdsfsdsd";
        Response response = CeHttpServerTest.call(((underTest.getUrl()) + action));
        assertThat(response.code()).isEqualTo(404);
        assertThat(response.body().string()).isEqualTo((("Error 404, '" + action) + "' not found."));
    }

    @Test
    public void action_is_matched_on_exact_URL() throws IOException {
        Response response = CeHttpServerTest.call(((underTest.getUrl()) + "/pompom"));
        assertIsPomPomResponse(response);
    }

    @Test
    public void action_is_matched_on_URL_ignoring_case() throws IOException {
        Response response = CeHttpServerTest.call(((underTest.getUrl()) + "/pOMpoM"));
        assertIsPomPomResponse(response);
    }

    @Test
    public void action_is_matched_on_URL_with_parameters() throws IOException {
        Response response = CeHttpServerTest.call(((underTest.getUrl()) + "/pompom?toto=2"));
        assertIsPomPomResponse(response);
    }

    @Test
    public void start_starts_http_server_and_publishes_URL_in_IPC() throws Exception {
        Response response = CeHttpServerTest.call(((underTest.getUrl()) + "/pompom?toto=2"));
        assertIsPomPomResponse(response);
    }

    @Test
    public void stop_stops_http_server() throws Exception {
        underTest.stop();
        expectedException.expect(ConnectException.class);
        CeHttpServerTest.call(underTest.getUrl());
    }

    @Test
    public void return_http_response_with_code_500_and_exception_message_as_body_when_action_throws_exception() throws IOException {
        Response response = CeHttpServerTest.call(((underTest.getUrl()) + "/failing"));
        assertThat(response.code()).isEqualTo(500);
        assertThat(response.body().string()).isEqualTo(CeHttpServerTest.FAILING_ACTION.getMessage());
    }

    private static class PomPomAction implements HttpAction {
        @Override
        public void register(ActionRegistry registry) {
            registry.register("pompom", this);
        }

        @Override
        public Response serve(NanoHTTPD.IHTTPSession session) {
            return NanoHTTPD.newFixedLengthResponse(OK, MIME_PLAINTEXT, "ok");
        }
    }

    private static class FailingAction implements HttpAction {
        @Override
        public void register(ActionRegistry registry) {
            registry.register("failing", this);
        }

        @Override
        public Response serve(NanoHTTPD.IHTTPSession session) {
            throw CeHttpServerTest.FAILING_ACTION;
        }
    }
}

