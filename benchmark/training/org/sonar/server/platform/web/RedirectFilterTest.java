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
package org.sonar.server.platform.web;


import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Test;
import org.mockito.Mockito;


public class RedirectFilterTest {
    HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

    HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

    FilterChain chain = Mockito.mock(FilterChain.class);

    RedirectFilter underTest = new RedirectFilter();

    @Test
    public void send_redirect_when_url_contains_only_api() throws Exception {
        verifyRedirection("/api", null, "/sonarqube/api/webservices/list");
        verifyRedirection("/api/", null, "/sonarqube/api/webservices/list");
    }

    @Test
    public void send_redirect_when_url_contains_batch_with_jar() throws Exception {
        verifyRedirection("/batch/file.jar", null, "/sonarqube/batch/file?name=file.jar");
    }

    @Test
    public void send_redirect_when_url_contains_batch_bootstrap() throws Exception {
        verifyRedirection("/batch_bootstrap/index", null, "/sonarqube/batch/index");
        verifyRedirection("/batch_bootstrap/index/", null, "/sonarqube/batch/index");
    }

    @Test
    public void send_redirect_when_url_contains_profiles_export() throws Exception {
        verifyRedirection("/profiles/export", "format=pmd", "/sonarqube/api/qualityprofiles/export?format=pmd");
        verifyRedirection("/profiles/export/", "format=pmd", "/sonarqube/api/qualityprofiles/export?format=pmd");
    }

    @Test
    public void does_not_redirect_and_execute_remaining_filter_on_unknown_path() throws Exception {
        verifyNoRedirection("/api/issues/search", null);
    }
}

