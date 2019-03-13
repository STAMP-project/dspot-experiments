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
package org.sonar.server.permission;


import com.google.common.collect.Lists;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.sonar.server.exceptions.BadRequestException;


public class ApplyPermissionTemplateQueryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void should_populate_with_params() {
        ApplyPermissionTemplateQuery query = ApplyPermissionTemplateQuery.create("my_template_key", Lists.newArrayList("1", "2", "3"));
        assertThat(query.getTemplateUuid()).isEqualTo("my_template_key");
        assertThat(query.getComponentKeys()).containsOnly("1", "2", "3");
    }

    @Test
    public void should_invalidate_query_with_empty_name() {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("Permission template is mandatory");
        ApplyPermissionTemplateQuery.create("", Lists.newArrayList("1", "2", "3"));
    }

    @Test
    public void should_invalidate_query_with_no_components() {
        expectedException.expect(BadRequestException.class);
        expectedException.expectMessage("No project provided. Please provide at least one project.");
        ApplyPermissionTemplateQuery.create("my_template_key", Collections.emptyList());
    }
}

