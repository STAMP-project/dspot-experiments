/**
 * Copyright 2018 LINE Corporation
 *
 * LINE Corporation licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package com.linecorp.armeria.common;


import com.google.common.base.Ascii;
import io.netty.channel.epoll.Epoll;
import io.netty.handler.ssl.OpenSsl;
import org.junit.Assume;
import org.junit.Test;


public class FlagsTest {
    private static final String osName = Ascii.toLowerCase(System.getProperty("os.name"));

    /**
     * Makes sure /dev/epoll is used while running tests on Linux.
     */
    @Test
    public void epollAvailableOnLinux() {
        Assume.assumeTrue(FlagsTest.osName.startsWith("linux"));
        Assume.assumeTrue(((System.getenv("WSLENV")) == null));
        Assume.assumeFalse("false".equals(System.getProperty("com.linecorp.armeria.useEpoll")));
        assertThat(Flags.useEpoll()).isTrue();
        assertThat(Epoll.isAvailable()).isTrue();
    }

    /**
     * Makes sure OpenSSL SSLEngine is used instead of JDK SSLEngine while running tests
     * on Linux, Windows and OS X.
     */
    @Test
    public void openSslAvailable() {
        Assume.assumeTrue(((((FlagsTest.osName.startsWith("linux")) || (FlagsTest.osName.startsWith("windows"))) || (FlagsTest.osName.startsWith("macosx"))) || (FlagsTest.osName.startsWith("osx"))));
        Assume.assumeFalse("false".equals(System.getProperty("com.linecorp.armeria.useOpenSsl")));
        assertThat(Flags.useOpenSsl()).isTrue();
        assertThat(OpenSsl.isAvailable()).isTrue();
    }
}

