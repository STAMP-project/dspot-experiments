/**
 * -
 * -\-\-
 * Helios Tools
 * --
 * Copyright (C) 2016 Spotify AB
 * --
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * -/-/-
 */
package com.spotify.helios.cli.command;


import com.spotify.helios.client.HeliosClient;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import net.sourceforge.argparse4j.inf.Namespace;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;


public class HostRegisterCommandTest {
    private static final String HOST = "test-host";

    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private HostRegisterCommand command;

    @Test
    public void testNoId() throws Exception {
        Mockito.when(options.getString("host")).thenReturn(HostRegisterCommandTest.HOST);
        final int ret = command.run(options, client, out, false, null);
        Assert.assertThat(ret, Matchers.equalTo(1));
        Assert.assertThat(baos.toString(), Matchers.containsString("You must specify the hostname and id."));
    }
}

