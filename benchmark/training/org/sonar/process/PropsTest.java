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
package org.sonar.process;


import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;


@RunWith(DataProviderRunner.class)
public class PropsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nonNullValue_throws_IAE_on_non_existing_key() {
        Props props = new Props(new Properties());
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Missing property: other");
        props.nonNullValue("other");
    }

    @Test
    public void setDefault() {
        Properties p = new Properties();
        p.setProperty("foo", "foo_value");
        Props props = new Props(p);
        props.setDefault("foo", "foo_def");
        props.setDefault("bar", "bar_def");
        assertThat(props.value("foo")).isEqualTo("foo_value");
        assertThat(props.value("bar")).isEqualTo("bar_def");
        assertThat(props.value("other")).isNull();
    }

    @Test
    public void set() {
        Properties p = new Properties();
        p.setProperty("foo", "old_foo");
        Props props = new Props(p);
        props.set("foo", "new_foo");
        props.set("bar", "new_bar");
        assertThat(props.value("foo")).isEqualTo("new_foo");
        assertThat(props.value("bar")).isEqualTo("new_bar");
    }

    @Test
    public void raw_properties() {
        Properties p = new Properties();
        p.setProperty("encrypted_prop", "{aes}abcde");
        p.setProperty("clear_prop", "foo");
        Props props = new Props(p);
        assertThat(props.rawProperties()).hasSize(2);
        // do not decrypt
        assertThat(props.rawProperties().get("encrypted_prop")).isEqualTo("{aes}abcde");
        assertThat(props.rawProperties().get("clear_prop")).isEqualTo("foo");
    }

    @Test
    public void nonNullValueAsFile() throws IOException {
        File file = temp.newFile();
        Props props = new Props(new Properties());
        props.set("path", file.getAbsolutePath());
        assertThat(props.nonNullValueAsFile("path")).isEqualTo(file);
        try {
            props.nonNullValueAsFile("other_path");
            Assert.fail();
        } catch (IllegalArgumentException e) {
            assertThat(e).hasMessage("Property other_path is not set");
        }
    }
}

