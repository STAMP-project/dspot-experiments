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
package org.sonar.server.app;


import TomcatContexts.Fs;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.sonar.api.utils.MessageException;
import org.sonar.process.Props;


public class TomcatContextsTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    Tomcat tomcat = Mockito.mock(Tomcat.class);

    Properties props = new Properties();

    TomcatContexts underTest = new TomcatContexts();

    @Test
    public void configure_root_webapp() throws Exception {
        props.setProperty("foo", "bar");
        StandardContext context = Mockito.mock(StandardContext.class);
        Mockito.when(tomcat.addWebapp(ArgumentMatchers.anyString(), ArgumentMatchers.anyString())).thenReturn(context);
        underTest.configure(tomcat, new Props(props));
        // configure webapp with properties
        Mockito.verify(context).addParameter("foo", "bar");
    }

    @Test
    public void create_dir_and_configure_static_directory() throws Exception {
        File dir = temp.newFolder();
        dir.delete();
        underTest.addStaticDir(tomcat, "/deploy", dir);
        assertThat(dir).isDirectory().exists();
        Mockito.verify(tomcat).addWebapp("/deploy", dir.getAbsolutePath());
    }

    @Test
    public void cleanup_static_directory_if_already_exists() throws Exception {
        File dir = temp.newFolder();
        FileUtils.touch(new File(dir, "foo.txt"));
        underTest.addStaticDir(tomcat, "/deploy", dir);
        assertThat(dir).isDirectory().exists();
        assertThat(dir.listFiles()).isEmpty();
    }

    @Test
    public void fail_if_static_directory_can_not_be_initialized() throws Exception {
        File dir = temp.newFolder();
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(("Fail to create or clean-up directory " + (dir.getAbsolutePath())));
        TomcatContexts.Fs fs = Mockito.mock(Fs.class);
        Mockito.doThrow(new IOException()).when(fs).createOrCleanupDir(ArgumentMatchers.any(File.class));
        new TomcatContexts(fs).addStaticDir(tomcat, "/deploy", dir);
    }

    @Test
    public void context_path() {
        props.setProperty("sonar.web.context", "/foo");
        assertThat(TomcatContexts.getContextPath(new Props(props))).isEqualTo("/foo");
    }

    @Test
    public void context_path_must_start_with_slash() {
        props.setProperty("sonar.web.context", "foo");
        expectedException.expect(MessageException.class);
        expectedException.expectMessage("Value of 'sonar.web.context' must start with a forward slash: 'foo'");
        underTest.configure(tomcat, new Props(props));
    }

    @Test
    public void root_context_path_must_be_blank() {
        props.setProperty("sonar.web.context", "/");
        assertThat(TomcatContexts.getContextPath(new Props(props))).isEqualTo("");
    }

    @Test
    public void default_context_path_is_root() {
        String context = TomcatContexts.getContextPath(new Props(new Properties()));
        assertThat(context).isEqualTo("");
    }
}

