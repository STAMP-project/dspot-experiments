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


import CreateDeploymentGroupResponse.Status.CONFLICT;
import CreateDeploymentGroupResponse.Status.CREATED;
import CreateDeploymentGroupResponse.Status.NOT_MODIFIED;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.spotify.helios.client.HeliosClient;
import com.spotify.helios.common.descriptors.DeploymentGroup;
import com.spotify.helios.common.descriptors.HostSelector;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.Namespace;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;


public class DeploymentGroupCreateCommandTest {
    private static final String GROUP_NAME = "foo-group";

    private static final List<String> HOST_SELECTORS_ARG = ImmutableList.of("foo=bar", "baz=qux");

    private static final List<HostSelector> HOST_SELECTORS = Lists.transform(DeploymentGroupCreateCommandTest.HOST_SELECTORS_ARG, new Function<String, HostSelector>() {
        @Override
        public HostSelector apply(final String input) {
            return HostSelector.parse(input);
        }
    });

    private final Namespace options = Mockito.mock(Namespace.class);

    private final HeliosClient client = Mockito.mock(HeliosClient.class);

    private final ByteArrayOutputStream baos = new ByteArrayOutputStream();

    private final PrintStream out = new PrintStream(baos);

    private final DeploymentGroupCreateCommand command = new DeploymentGroupCreateCommand(ArgumentParsers.newArgumentParser("test").addSubparsers().addParser("create-deployment-group"));

    @Test
    public void testCreateDeploymentGroup() throws Exception {
        mockCreateResponse(CREATED);
        Assert.assertEquals(0, command.run(options, client, out, false, null));
        final ArgumentCaptor<DeploymentGroup> captor = ArgumentCaptor.forClass(DeploymentGroup.class);
        Mockito.verify(client).createDeploymentGroup(captor.capture());
        Assert.assertEquals(DeploymentGroupCreateCommandTest.GROUP_NAME, captor.getValue().getName());
        Assert.assertEquals(DeploymentGroupCreateCommandTest.HOST_SELECTORS, captor.getValue().getHostSelectors());
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"name\":\"foo-group\""));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("\"hostSelectors\":[" + (("{\"label\":\"foo\",\"operand\":\"bar\",\"operator\":\"EQUALS\"}," + "{\"label\":\"baz\",\"operand\":\"qux\",\"operator\":\"EQUALS\"}") + "]"))));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("CREATED"));
    }

    @Test
    public void testCreateAlreadyExistingDeploymentGroup() throws Exception {
        mockCreateResponse(CREATED);
        Assert.assertEquals(0, command.run(options, client, out, false, null));
        mockCreateResponse(NOT_MODIFIED);
        Assert.assertEquals(0, command.run(options, client, out, false, null));
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"name\":\"foo-group\""));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("\"hostSelectors\":[" + (("{\"label\":\"foo\",\"operand\":\"bar\",\"operator\":\"EQUALS\"}," + "{\"label\":\"baz\",\"operand\":\"qux\",\"operator\":\"EQUALS\"}") + "]"))));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("NOT_MODIFIED"));
    }

    @Test
    public void testCreateConflictingDeploymentGroup() throws Exception {
        mockCreateResponse(CREATED);
        Assert.assertEquals(0, command.run(options, client, out, false, null));
        mockCreateResponse(CONFLICT);
        Assert.assertEquals(1, command.run(options, client, out, false, null));
        final String output = baos.toString();
        MatcherAssert.assertThat(output, CoreMatchers.containsString("\"name\":\"foo-group\""));
        MatcherAssert.assertThat(output, CoreMatchers.containsString(("\"hostSelectors\":[" + (("{\"label\":\"foo\",\"operand\":\"bar\",\"operator\":\"EQUALS\"}," + "{\"label\":\"baz\",\"operand\":\"qux\",\"operator\":\"EQUALS\"}") + "]"))));
        MatcherAssert.assertThat(output, CoreMatchers.containsString("CONFLICT"));
    }
}

