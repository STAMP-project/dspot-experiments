/**
 * Copyright 2017-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.atomix.agent;


import io.atomix.core.AtomixConfig;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.argparse4j.inf.Namespace;
import org.junit.Assert;
import org.junit.Test;


/**
 * Atomix agent runner test.
 */
public class AtomixAgentTest {
    private static final Path PATH = Paths.get("target/test-logs/");

    @Test
    public void testParseArgs() {
        List<String> unknown = new ArrayList<>();
        Namespace namespace = AtomixAgent.parseArgs(new String[]{ "-c", "some.conf", "--a.b.c", "d", "--b.c.d=a" }, unknown);
        Assert.assertEquals("some.conf", namespace.getList("config").get(0).toString());
        Assert.assertEquals(3, unknown.size());
        Namespace extraArgs = AtomixAgent.parseUnknown(unknown);
        Assert.assertEquals("d", extraArgs.getString("a.b.c"));
        Assert.assertEquals("a", extraArgs.getString("b.c.d"));
    }

    @Test
    public void testCreateConfig() {
        final List<String> unknown = new ArrayList<>();
        final String path = getClass().getClassLoader().getResource("test.conf").getPath();
        final String[] args = new String[]{ "-c", path, "--cluster.node.id", "member-1", "--cluster.node.address", "localhost:5000" };
        final Namespace namespace = AtomixAgent.parseArgs(args, unknown);
        final Namespace extraArgs = AtomixAgent.parseUnknown(unknown);
        extraArgs.getAttrs().forEach(( key, value) -> System.setProperty(key, value.toString()));
        final AtomixConfig config = AtomixAgent.createConfig(namespace);
        Assert.assertEquals("member-1", config.getClusterConfig().getNodeConfig().getId().id());
        Assert.assertEquals("localhost:5000", config.getClusterConfig().getNodeConfig().getAddress().toString());
    }
}

