/**
 * LanguageTool, a natural language style checker
 * Copyright (C) 2012 Daniel Naber (http://www.danielnaber.de)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301
 * USA
 */
package org.languagetool.server;


import HTTPServerConfig.DEFAULT_PORT;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;


@SuppressWarnings("ResultOfObjectAllocationIgnored")
public class HTTPSServerConfigTest {
    @Test
    public void testArgumentParsing() {
        try {
            new HTTPSServerConfig(new String[]{  });
            Assert.fail();
        } catch (IllegalConfigurationException ignored) {
        }
        String propertyFile = HTTPSServerConfigTest.class.getResource("/org/languagetool/server/https-server.properties").getFile();
        HTTPSServerConfig config1 = new HTTPSServerConfig(("--public --config " + propertyFile).split(" "));
        Assert.assertThat(config1.getPort(), Is.is(DEFAULT_PORT));
        Assert.assertThat(config1.isPublicAccess(), Is.is(true));
        Assert.assertThat(config1.isVerbose(), Is.is(false));
        Assert.assertThat(config1.getKeystore().toString().replace('\\', '/'), Is.is("src/test/resources/org/languagetool/server/test-keystore.jks"));
        Assert.assertThat(config1.getKeyStorePassword(), Is.is("mytest"));
        Assert.assertThat(config1.getMaxTextLength(), Is.is(50000));
        HTTPSServerConfig config2 = new HTTPSServerConfig(("-p 9999 --config " + propertyFile).split(" "));
        Assert.assertThat(config2.getPort(), Is.is(9999));
        Assert.assertThat(config2.isPublicAccess(), Is.is(false));
        Assert.assertThat(config2.isVerbose(), Is.is(false));
        Assert.assertThat(config2.getKeystore().toString().replace('\\', '/'), Is.is("src/test/resources/org/languagetool/server/test-keystore.jks"));
        Assert.assertThat(config2.getKeyStorePassword(), Is.is("mytest"));
        Assert.assertThat(config2.getMaxTextLength(), Is.is(50000));
    }

    @Test
    public void testMinimalPropertyFile() {
        String propertyFile = HTTPSServerConfigTest.class.getResource("/org/languagetool/server/https-server-minimal.properties").getFile();
        HTTPSServerConfig config = new HTTPSServerConfig(("--config " + propertyFile).split(" "));
        Assert.assertThat(config.getPort(), Is.is(8081));
        Assert.assertThat(config.isPublicAccess(), Is.is(false));
        Assert.assertThat(config.isVerbose(), Is.is(false));
        Assert.assertThat(config.getKeystore().toString().replace('\\', '/'), Is.is("src/test/resources/org/languagetool/server/test-keystore.jks"));
        Assert.assertThat(config.getKeyStorePassword(), Is.is("mytest"));
        Assert.assertThat(config.getMaxTextLength(), Is.is(Integer.MAX_VALUE));
    }

    @Test
    public void testMissingPropertyFile() {
        String propertyFile = "/does-not-exist";
        try {
            new HTTPSServerConfig(("--config " + propertyFile).split(" "));
            Assert.fail();
        } catch (Exception ignored) {
        }
    }

    @Test
    public void testIncompletePropertyFile() {
        String propertyFile = HTTPSServerConfigTest.class.getResource("/org/languagetool/server/https-server-incomplete.properties").getFile();
        try {
            new HTTPSServerConfig(("--config " + propertyFile).split(" "));
            Assert.fail();
        } catch (IllegalConfigurationException ignored) {
        }
    }
}

