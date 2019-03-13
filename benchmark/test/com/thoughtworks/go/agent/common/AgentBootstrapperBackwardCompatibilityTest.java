/**
 * Copyright 2016 ThoughtWorks, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thoughtworks.go.agent.common;


import AgentBootstrapperArgs.SslMode;
import SslVerificationMode.NONE;
import SslVerificationMode.NO_VERIFY_HOST;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;


public class AgentBootstrapperBackwardCompatibilityTest {
    @Test
    public void shouldBeBackwardCompatible() throws Exception {
        HashMap context = new HashMap();
        context.put("hostname", "ci.example.com");
        context.put("port", "8153");
        AgentBootstrapperBackwardCompatibility compatibility = new AgentBootstrapperBackwardCompatibility(context);
        Assert.assertNull(compatibility.rootCertFileAsString());
        Assert.assertNull(compatibility.rootCertFile());
        Assert.assertEquals(NONE, compatibility.sslVerificationMode());
        Assert.assertEquals("https://ci.example.com:8154/go", compatibility.sslServerUrl("8154"));
    }

    @Test
    public void shouldReturnCLIArgsIfStuffedInContext() throws Exception {
        AgentBootstrapperArgs args = new AgentBootstrapperArgs(new URL("https://go.example.com:8154/go"), new File("/path/to/certfile"), SslMode.NO_VERIFY_HOST);
        AgentBootstrapperBackwardCompatibility compatibility = new AgentBootstrapperBackwardCompatibility(args.toProperties());
        Assert.assertEquals((SystemUtils.IS_OS_WINDOWS ? "C:\\path\\to\\certfile" : "/path/to/certfile"), compatibility.rootCertFileAsString());
        Assert.assertEquals(new File("/path/to/certfile").getAbsoluteFile(), compatibility.rootCertFile());
        Assert.assertEquals(NO_VERIFY_HOST, compatibility.sslVerificationMode());
        Assert.assertEquals("https://go.example.com:8154/go", compatibility.sslServerUrl("8154"));
    }
}

