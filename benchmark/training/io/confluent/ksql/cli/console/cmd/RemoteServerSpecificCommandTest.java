/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.cli.console.cmd;


import Errors.ERROR_CODE_SERVER_ERROR;
import com.google.common.collect.ImmutableList;
import io.confluent.ksql.rest.client.KsqlRestClient;
import io.confluent.ksql.rest.client.RestResponse;
import io.confluent.ksql.rest.client.exception.KsqlRestClientException;
import io.confluent.ksql.rest.entity.ServerInfo;
import io.confluent.ksql.util.Event;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.ws.rs.ProcessingException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RemoteServerSpecificCommandTest {
    private static final String INITIAL_SERVER_ADDRESS = "http://192.168.0.1:8080";

    private static final String VALID_SERVER_ADDRESS = "http://localhost:8088";

    private static final ServerInfo SERVER_INFO = new ServerInfo("1.x", "myClusterId", "myKsqlServiceId");

    @Mock
    private KsqlRestClient restClient;

    @Mock
    private Event resetCliForNewServer;

    private RemoteServerSpecificCommand command;

    private StringWriter out;

    private PrintWriter terminal;

    @Test
    public void shouldSetRestClientServerAddressWhenNonEmptyStringArg() {
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        Mockito.verify(restClient).setServerAddress(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS);
    }

    @Test(expected = KsqlRestClientException.class)
    public void shouldThrowIfRestClientThrowsOnSet() {
        // Given:
        Mockito.doThrow(new KsqlRestClientException("Boom")).when(restClient).setServerAddress("localhost:8088");
        // When:
        command.execute(ImmutableList.of("localhost:8088"), terminal);
    }

    @Test
    public void shouldPrintServerAddressWhenEmptyStringArg() {
        // When:
        command.execute(ImmutableList.of(), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.equalTo(((RemoteServerSpecificCommandTest.INITIAL_SERVER_ADDRESS) + "\n")));
        Mockito.verify(restClient, Mockito.never()).setServerAddress(ArgumentMatchers.anyString());
        Mockito.verify(resetCliForNewServer, Mockito.never()).fire();
    }

    @Test
    public void shouldPrintErrorWhenCantConnectToNewAddress() {
        // Given:
        Mockito.when(restClient.makeRootRequest()).thenThrow(new KsqlRestClientException("Failed to connect", new ProcessingException("Boom")));
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString("Boom"));
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString("Failed to connect"));
    }

    @Test
    public void shouldOutputNewServerDetails() {
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString(("Server now: " + (RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS))));
    }

    @Test
    public void shouldPrintErrorOnErrorResponseFromRestClient() {
        // Given:
        Mockito.when(restClient.makeRootRequest()).thenReturn(RestResponse.erroneous(ERROR_CODE_SERVER_ERROR, "it is broken"));
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString("it is broken"));
    }

    @Test
    public void shouldResetCliForNewServer() {
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        Mockito.verify(resetCliForNewServer).fire();
    }

    @Test
    public void shouldReportErrorIfFailedToGetRemoteKsqlServerInfo() {
        // Given:
        Mockito.when(restClient.makeRootRequest()).thenThrow(RemoteServerSpecificCommandTest.genericConnectionIssue());
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString(("Remote server at http://192.168.0.1:8080 does not appear to be a valid KSQL\n" + "server. Please ensure that the URL provided is for an active KSQL server.")));
    }

    @Test
    public void shouldReportErrorIfRemoteKsqlServerIsUsingSSL() {
        // Given:
        Mockito.when(restClient.makeRootRequest()).thenThrow(RemoteServerSpecificCommandTest.sslConnectionIssue());
        // When:
        command.execute(ImmutableList.of(RemoteServerSpecificCommandTest.VALID_SERVER_ADDRESS), terminal);
        // Then:
        MatcherAssert.assertThat(out.toString(), CoreMatchers.containsString(("Remote server at http://192.168.0.1:8080 looks to be configured to use HTTPS /\n" + (("SSL. Please refer to the KSQL documentation on how to configure the CLI for SSL:\n" + "https://docs.confluent.io/current/ksql/docs/installation/server-config/security.html") + "#configuring-cli-for-https"))));
    }

    @Test
    public void shouldGetHelp() {
        MatcherAssert.assertThat(command.getHelpMessage(), CoreMatchers.containsString("server:\n\tShow the current server"));
        MatcherAssert.assertThat(command.getHelpMessage(), CoreMatchers.containsString("server <server>:\n\tChange the current server to <server>"));
    }
}

