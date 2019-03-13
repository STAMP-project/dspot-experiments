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
package org.sonarqube.ws;


import MediaTypes.DEFAULT;
import org.junit.Test;
import org.sonar.test.TestUtils;


public class MediaTypesTest {
    @Test
    public void getByFilename_default_mime_type() {
        assertThat(MediaTypes.getByFilename("")).isEqualTo(DEFAULT);
        assertThat(MediaTypes.getByFilename("unknown.extension")).isEqualTo(DEFAULT);
    }

    @Test
    public void getByFilename() {
        assertThat(MediaTypes.getByFilename("static/sqale/sqale.css")).isEqualTo("text/css");
        assertThat(MediaTypes.getByFilename("sqale.css")).isEqualTo("text/css");
    }

    @Test
    public void only_statics() {
        assertThat(TestUtils.hasOnlyPrivateConstructors(MediaTypes.class)).isTrue();
    }
}

