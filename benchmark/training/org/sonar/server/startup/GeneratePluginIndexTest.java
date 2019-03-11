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
package org.sonar.server.startup;


import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.sonar.server.platform.ServerFileSystem;
import org.sonar.server.plugins.InstalledPlugin;
import org.sonar.server.plugins.PluginFileSystem;


public class GeneratePluginIndexTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    private ServerFileSystem serverFileSystem = Mockito.mock(ServerFileSystem.class);

    private PluginFileSystem pluginFileSystem = Mockito.mock(PluginFileSystem.class);

    private File index;

    @Test
    public void shouldWriteIndex() throws IOException {
        InstalledPlugin javaPlugin = newInstalledPlugin("java", true);
        InstalledPlugin gitPlugin = newInstalledPlugin("scmgit", false);
        Mockito.when(pluginFileSystem.getInstalledFiles()).thenReturn(Arrays.asList(javaPlugin, gitPlugin));
        GeneratePluginIndex underTest = new GeneratePluginIndex(serverFileSystem, pluginFileSystem);
        underTest.start();
        List<String> lines = FileUtils.readLines(index);
        assertThat(lines).containsExactly(((("java,true," + (javaPlugin.getLoadedJar().getFile().getName())) + "|") + (javaPlugin.getLoadedJar().getMd5())), ((("scmgit,false," + (gitPlugin.getLoadedJar().getFile().getName())) + "|") + (gitPlugin.getLoadedJar().getMd5())));
        underTest.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowWhenUnableToWrite() throws IOException {
        File wrongParent = temp.newFile();
        wrongParent.createNewFile();
        File wrongIndex = new File(wrongParent, "index.txt");
        Mockito.when(serverFileSystem.getPluginIndex()).thenReturn(wrongIndex);
        start();
    }
}

