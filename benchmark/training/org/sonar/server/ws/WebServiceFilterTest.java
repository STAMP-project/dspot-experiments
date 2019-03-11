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


import ServletFilterHandler.INSTANCE;
import SonarQubeSide.SERVER;
import javax.servlet.FilterChain;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.SonarRuntime;
import org.sonar.api.internal.SonarRuntimeImpl;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.RequestHandler;
import org.sonar.api.server.ws.Response;
import org.sonar.api.utils.Version;


public class WebServiceFilterTest {
    private static final String RUNTIME_VERSION = "7.1.0.1234";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private WebServiceEngine webServiceEngine = Mockito.mock(WebServiceEngine.class);

    private HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    private HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    private FilterChain chain = Mockito.mock(FilterChain.class);

    private ServletOutputStream responseOutput = Mockito.mock(ServletOutputStream.class);

    private SonarRuntime runtime = SonarRuntimeImpl.forSonarQube(Version.parse(WebServiceFilterTest.RUNTIME_VERSION), SERVER);

    private WebServiceFilter underTest;

    @Test
    public void match_declared_web_services_with_optional_suffix() {
        initWebServiceEngine(WebServiceFilterTest.WsUrl.newWsUrl("api/issues", "search"), WebServiceFilterTest.WsUrl.newWsUrl("batch", "index"));
        assertThat(underTest.doGetPattern().matches("/api/issues/search")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/issues/search.protobuf")).isTrue();
        assertThat(underTest.doGetPattern().matches("/batch/index")).isTrue();
        assertThat(underTest.doGetPattern().matches("/batch/index.protobuf")).isTrue();
        assertThat(underTest.doGetPattern().matches("/foo")).isFalse();
    }

    @Test
    public void match_undeclared_web_services_starting_with_api() {
        initWebServiceEngine(WebServiceFilterTest.WsUrl.newWsUrl("api/issues", "search"));
        assertThat(underTest.doGetPattern().matches("/api/resources/index")).isTrue();
        assertThat(underTest.doGetPattern().matches("/api/user_properties")).isTrue();
    }

    @Test
    public void does_not_match_web_services_using_servlet_filter() {
        initWebServiceEngine(WebServiceFilterTest.WsUrl.newWsUrl("api/authentication", "login").setHandler(INSTANCE));
        assertThat(underTest.doGetPattern().matches("/api/authentication/login")).isFalse();
    }

    @Test
    public void does_not_match_servlet_filter_that_prefix_a_ws() {
        initWebServiceEngine(WebServiceFilterTest.WsUrl.newWsUrl("api/foo", "action").setHandler(INSTANCE), WebServiceFilterTest.WsUrl.newWsUrl("api/foo", "action_2"));
        assertThat(underTest.doGetPattern().matches("/api/foo/action")).isFalse();
        assertThat(underTest.doGetPattern().matches("/api/foo/action_2")).isTrue();
    }

    @Test
    public void does_not_match_api_properties_ws() {
        initWebServiceEngine(WebServiceFilterTest.WsUrl.newWsUrl("api/properties", "index"));
        assertThat(underTest.doGetPattern().matches("/api/properties")).isFalse();
        assertThat(underTest.doGetPattern().matches("/api/properties/index")).isFalse();
    }

    @Test
    public void execute_ws() {
        underTest = new WebServiceFilter(webServiceEngine, runtime);
        underTest.doFilter(request, response, chain);
        Mockito.verify(webServiceEngine).execute(ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    public void add_version_to_response_headers() {
        underTest = new WebServiceFilter(webServiceEngine, runtime);
        underTest.doFilter(request, response, chain);
        Mockito.verify(response).setHeader("Sonar-Version", WebServiceFilterTest.RUNTIME_VERSION);
    }

    static final class WsUrl {
        private String controller;

        private String[] actions;

        private RequestHandler requestHandler = WebServiceFilterTest.EmptyRequestHandler.INSTANCE;

        WsUrl(String controller, String... actions) {
            this.controller = controller;
            this.actions = actions;
        }

        WebServiceFilterTest.WsUrl setHandler(RequestHandler requestHandler) {
            this.requestHandler = requestHandler;
            return this;
        }

        String getController() {
            return controller;
        }

        String[] getActions() {
            return actions;
        }

        RequestHandler getRequestHandler() {
            return requestHandler;
        }

        static WebServiceFilterTest.WsUrl newWsUrl(String controller, String... actions) {
            return new WebServiceFilterTest.WsUrl(controller, actions);
        }
    }

    private enum EmptyRequestHandler implements RequestHandler {

        INSTANCE;
        @Override
        public void handle(Request request, Response response) {
            // Nothing to do
        }
    }
}

