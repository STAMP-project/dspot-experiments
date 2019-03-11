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
package org.sonar.server.ws;


import LoggerLevel.DEBUG;
import LoggerLevel.ERROR;
import MediaTypes.JSON;
import WebService.NewAction;
import java.nio.charset.StandardCharsets;
import java.util.function.Consumer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.RequestHandler;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.log.LogTester;
import org.sonar.server.exceptions.BadRequestException;


public class WebServiceEngineTest {
    @Rule
    public LogTester logTester = new LogTester();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void load_ws_definitions_at_startup() {
        WebServiceEngine underTest = new WebServiceEngine(new WebService[]{ WebServiceEngineTest.newWs("api/foo/index", ( a) -> {
        }), WebServiceEngineTest.newWs("api/bar/index", ( a) -> {
        }) });
        underTest.start();
        try {
            assertThat(underTest.controllers()).extracting(WebService.Controller::path).containsExactlyInAnyOrder("api/foo", "api/bar");
        } finally {
            underTest.stop();
        }
    }

    @Test
    public void ws_returns_successful_response() {
        Request request = new TestRequest().setPath("/api/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("pong");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void accept_path_that_does_not_start_with_slash() {
        Request request = new TestRequest().setPath("api/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("pong");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void request_path_can_contain_valid_media_type() {
        Request request = new TestRequest().setPath("api/ping.json");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("pong");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void bad_request_if_action_suffix_is_not_supported() {
        Request request = new TestRequest().setPath("/api/ping.bat");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().status()).isEqualTo(400);
        assertThat(response.stream().mediaType()).isEqualTo(JSON);
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"Unknown action extension: bat\"}]}");
    }

    @Test
    public void test_response_with_no_content() {
        Request request = new TestRequest().setPath("api/foo");
        RequestHandler handler = ( req, resp) -> resp.noContent();
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setHandler(handler)));
        assertThat(response.stream().outputAsString()).isEmpty();
        assertThat(response.stream().status()).isEqualTo(204);
    }

    @Test
    public void return_404_if_controller_does_not_exist() {
        Request request = new TestRequest().setPath("xxx/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"Unknown url : xxx/ping\"}]}");
        assertThat(response.stream().status()).isEqualTo(404);
    }

    @Test
    public void return_404_if_action_does_not_exist() {
        Request request = new TestRequest().setPath("api/xxx");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"Unknown url : api/xxx\"}]}");
        assertThat(response.stream().status()).isEqualTo(404);
    }

    @Test
    public void fail_if_method_GET_is_not_allowed() {
        Request request = new TestRequest().setMethod("GET").setPath("api/foo");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setPost(true)));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"HTTP method POST is required\"}]}");
        assertThat(response.stream().status()).isEqualTo(405);
    }

    @Test
    public void POST_is_considered_as_GET_if_POST_is_not_supported() {
        Request request = new TestRequest().setMethod("POST").setPath("api/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("pong");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void method_PUT_is_not_allowed() {
        Request request = new TestRequest().setMethod("PUT").setPath("/api/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"HTTP method PUT is not allowed\"}]}");
        assertThat(response.stream().status()).isEqualTo(405);
    }

    @Test
    public void method_DELETE_is_not_allowed() {
        Request request = new TestRequest().setMethod("DELETE").setPath("api/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> {
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"HTTP method DELETE is not allowed\"}]}");
        assertThat(response.stream().status()).isEqualTo(405);
    }

    @Test
    public void method_POST_is_required() {
        Request request = new TestRequest().setMethod("POST").setPath("api/ping");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newPingWs(( a) -> a.setPost(true)));
        assertThat(response.stream().outputAsString()).isEqualTo("pong");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void fail_if_reading_an_undefined_parameter() {
        Request request = new TestRequest().setPath("api/foo").setParam("unknown", "Unknown");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setHandler(( req, resp) -> request.param("unknown"))));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"BUG - parameter \'unknown\' is undefined for action \'foo\'\"}]}");
        assertThat(response.stream().status()).isEqualTo(400);
    }

    @Test
    public void fail_if_request_does_not_have_required_parameter() {
        Request request = new TestRequest().setPath("api/foo").setParam("unknown", "Unknown");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> {
            a.createParam("bar").setRequired(true);
            a.setHandler(( req, resp) -> request.mandatoryParam("bar"));
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"The \'bar\' parameter is missing\"}]}");
        assertThat(response.stream().status()).isEqualTo(400);
    }

    @Test
    public void fail_if_request_does_not_have_required_parameter_even_if_handler_does_not_require_it() {
        Request request = new TestRequest().setPath("api/foo").setParam("unknown", "Unknown");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> {
            a.createParam("bar").setRequired(true);
            // do not use mandatoryParam("bar")
            a.setHandler(( req, resp) -> request.param("bar"));
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"The \'bar\' parameter is missing\"}]}");
        assertThat(response.stream().status()).isEqualTo(400);
    }

    @Test
    public void use_default_value_of_optional_parameter() {
        Request request = new TestRequest().setPath("api/print");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/print", ( a) -> {
            a.createParam("message").setDefaultValue("hello");
            a.setHandler(( req, resp) -> resp.stream().output().write(req.param("message").getBytes(StandardCharsets.UTF_8)));
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("hello");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void use_request_parameter_on_parameter_with_default_value() {
        Request request = new TestRequest().setPath("api/print").setParam("message", "bar");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/print", ( a) -> {
            a.createParam("message").setDefaultValue("default_value");
            a.setHandler(( req, resp) -> resp.stream().output().write(req.param("message").getBytes(StandardCharsets.UTF_8)));
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("bar");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void accept_parameter_value_within_defined_possible_values() {
        Request request = new TestRequest().setPath("api/foo").setParam("format", "json");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> {
            a.createParam("format").setPossibleValues("json", "xml");
            a.setHandler(( req, resp) -> resp.stream().output().write(req.mandatoryParam("format").getBytes(StandardCharsets.UTF_8)));
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("json");
        assertThat(response.stream().status()).isEqualTo(200);
    }

    @Test
    public void fail_if_parameter_value_is_not_in_defined_possible_values() {
        Request request = new TestRequest().setPath("api/foo").setParam("format", "yml");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> {
            a.createParam("format").setPossibleValues("json", "xml");
            a.setHandler(( req, resp) -> resp.stream().output().write(req.mandatoryParam("format").getBytes(StandardCharsets.UTF_8)));
        }));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"Value of parameter \'format\' (yml) must be one of: [json, xml]\"}]}");
        assertThat(response.stream().status()).isEqualTo(400);
    }

    @Test
    public void return_500_on_internal_error() {
        Request request = new TestRequest().setPath("api/foo");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newFailWs());
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"An error has occurred. Please contact your administrator\"}]}");
        assertThat(response.stream().status()).isEqualTo(500);
        assertThat(response.stream().mediaType()).isEqualTo(JSON);
        assertThat(logTester.logs(ERROR)).filteredOn(( l) -> l.contains("Fail to process request api/foo")).isNotEmpty();
    }

    @Test
    public void return_400_on_BadRequestException_with_single_message() {
        Request request = new TestRequest().setPath("api/foo");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setHandler(( req, resp) -> {
            throw BadRequestException.create("Bad request !");
        })));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"Bad request !\"}]}");
        assertThat(response.stream().status()).isEqualTo(400);
        assertThat(response.stream().mediaType()).isEqualTo(JSON);
        assertThat(logTester.logs(ERROR)).isEmpty();
    }

    @Test
    public void return_400_on_BadRequestException_with_multiple_messages() {
        Request request = new TestRequest().setPath("api/foo");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setHandler(( req, resp) -> {
            throw BadRequestException.create("one", "two", "three");
        })));
        assertThat(response.stream().outputAsString()).isEqualTo(("{\"errors\":[" + ((("{\"msg\":\"one\"}," + "{\"msg\":\"two\"},") + "{\"msg\":\"three\"}") + "]}")));
        assertThat(response.stream().status()).isEqualTo(400);
        assertThat(response.stream().mediaType()).isEqualTo(JSON);
        assertThat(logTester.logs(ERROR)).isEmpty();
    }

    @Test
    public void return_error_message_containing_character_percent() {
        Request request = new TestRequest().setPath("api/foo");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setHandler(( req, resp) -> {
            throw new IllegalArgumentException("this should not fail %s");
        })));
        assertThat(response.stream().outputAsString()).isEqualTo("{\"errors\":[{\"msg\":\"this should not fail %s\"}]}");
        assertThat(response.stream().status()).isEqualTo(400);
        assertThat(response.stream().mediaType()).isEqualTo(JSON);
    }

    @Test
    public void send_response_headers() {
        Request request = new TestRequest().setPath("api/foo");
        DumbResponse response = WebServiceEngineTest.run(request, WebServiceEngineTest.newWs("api/foo", ( a) -> a.setHandler(( req, resp) -> resp.setHeader("Content-Disposition", "attachment; filename=foo.zip"))));
        assertThat(response.getHeader("Content-Disposition")).isEqualTo("attachment; filename=foo.zip");
    }

    @Test
    public void support_aborted_request_when_response_is_already_committed() {
        Request request = new TestRequest().setPath("api/foo");
        Response response = WebServiceEngineTest.mockServletResponse(true);
        WebServiceEngineTest.run(request, response, WebServiceEngineTest.newClientAbortWs());
        // response is committed (status is already sent), so status can't be changed
        Mockito.verify(response.stream(), Mockito.never()).setStatus(ArgumentMatchers.anyInt());
        assertThat(logTester.logs(DEBUG)).contains("Request api/foo has been aborted by client");
    }

    @Test
    public void support_aborted_request_when_response_is_not_committed() {
        Request request = new TestRequest().setPath("api/foo");
        Response response = WebServiceEngineTest.mockServletResponse(false);
        WebServiceEngineTest.run(request, response, WebServiceEngineTest.newClientAbortWs());
        Mockito.verify(response.stream()).setStatus(299);
        assertThat(logTester.logs(DEBUG)).contains("Request api/foo has been aborted by client");
    }

    @Test
    public void internal_error_when_response_is_already_committed() {
        Request request = new TestRequest().setPath("api/foo");
        Response response = WebServiceEngineTest.mockServletResponse(true);
        WebServiceEngineTest.run(request, response, WebServiceEngineTest.newFailWs());
        // response is committed (status is already sent), so status can't be changed
        Mockito.verify(response.stream(), Mockito.never()).setStatus(ArgumentMatchers.anyInt());
        assertThat(logTester.logs(ERROR)).contains("Fail to process request api/foo");
    }

    @Test
    public void internal_error_when_response_is_not_committed() {
        Request request = new TestRequest().setPath("api/foo");
        Response response = WebServiceEngineTest.mockServletResponse(false);
        WebServiceEngineTest.run(request, response, WebServiceEngineTest.newFailWs());
        Mockito.verify(response.stream()).setStatus(500);
        assertThat(logTester.logs(ERROR)).contains("Fail to process request api/foo");
    }

    @Test
    public void fail_when_start_in_not_called() {
        Request request = new TestRequest().setPath("/api/ping");
        DumbResponse response = new DumbResponse();
        WebServiceEngine underTest = new WebServiceEngine(new WebService[]{ WebServiceEngineTest.newPingWs(( a) -> {
        }) });
        underTest.execute(request, response);
        assertThat(logTester.logs(ERROR)).contains("Fail to process request /api/ping");
    }
}

