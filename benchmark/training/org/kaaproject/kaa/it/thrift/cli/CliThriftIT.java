/**
 * Copyright 2014-2016 CyberVision, Inc.
 *
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
 */
package org.kaaproject.kaa.it.thrift.cli;


import CommandStatus.OK;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import org.apache.thrift.TException;
import org.apache.thrift.server.TServer;
import org.junit.Assert;
import org.junit.Test;
import org.kaaproject.kaa.server.common.thrift.cli.client.BaseCliThriftClient;
import org.kaaproject.kaa.server.common.thrift.cli.client.CliSessionState;
import org.kaaproject.kaa.server.common.thrift.cli.client.OptionsProcessor;
import org.kaaproject.kaa.server.common.thrift.gen.cli.CommandResult;
import org.kaaproject.kaa.server.common.thrift.gen.cli.MemoryUsage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CliThriftIT {
    /**
     * The Constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(CliThriftIT.class);

    /**
     * The Constant HOST.
     */
    private static final String HOST = "localhost";

    /**
     * The Constant PORT.
     */
    private static final int PORT = 10090;

    private static final String THRIFT_SERVER_SHORT_NAME = "baseCliThriftService";

    /**
     * The server.
     */
    private static TServer server;

    /**
     * The thrift server thread.
     */
    private static Thread thriftServerThread;

    /**
     * The thrift server started.
     */
    private static boolean thriftServerStarted = false;

    /**
     * The CLI session.
     */
    private CliSessionState cliSession;

    /**
     * The System output stream.
     */
    private ByteArrayOutputStream systemOut;

    /**
     * The System error stream.
     */
    private ByteArrayOutputStream systemErr;

    /**
     * Test get remote server name.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testRemoteServerName() throws TException {
        cliConnect();
        Assert.assertTrue(cliSession.isRemoteMode());
        Assert.assertEquals(cliSession.remoteServerName, CliThriftIT.THRIFT_SERVER_SHORT_NAME);
    }

    /**
     * Test get memory usage.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testGetMemoryUsage() throws TException {
        cliConnect();
        MemoryUsage memoryUsage = cliSession.getClient().getMemoryUsage(true);
        Assert.assertNotNull(memoryUsage);
        Assert.assertTrue(((memoryUsage.getFree()) > 0));
        Assert.assertTrue(((memoryUsage.getMax()) > 0));
        Assert.assertTrue(((memoryUsage.getTotal()) > 0));
    }

    /**
     * Test execute unknown command.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteUnknownCommand() throws TException {
        cliConnect();
        CommandResult commandResult = cliSession.getClient().executeCommand("fakeCommand");
        Assert.assertNotNull(commandResult);
        Assert.assertEquals(commandResult.getStatus(), OK);
        Assert.assertTrue(commandResult.getMessage().startsWith("Error: unknown command 'fakeCommand'"));
    }

    /**
     * Test execute help command.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteHelpCommand() throws TException {
        cliConnect();
        CommandResult commandResult = cliSession.getClient().executeCommand("help");
        Assert.assertNotNull(commandResult);
        Assert.assertEquals(commandResult.getStatus(), OK);
        Assert.assertTrue(commandResult.getMessage().startsWith("Available commands:"));
    }

    /**
     * Test execute memory command.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteMemoryCommand() throws TException {
        cliConnect();
        CommandResult commandResult = cliSession.getClient().executeCommand("memory");
        Assert.assertNotNull(commandResult);
        Assert.assertEquals(commandResult.getStatus(), OK);
        Assert.assertTrue(commandResult.getMessage().startsWith("Memory Usage:"));
    }

    /**
     * Test execute threads command.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteThreadsCommand() throws TException {
        cliConnect();
        CommandResult commandResult = cliSession.getClient().executeCommand("threads");
        Assert.assertNotNull(commandResult);
        Assert.assertEquals(commandResult.getStatus(), OK);
        Assert.assertTrue(commandResult.getMessage().startsWith("THREADS DUMP:"));
    }

    /**
     * Test execute unknown command from cli.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteUnknownCommandFromCli() throws UnsupportedEncodingException, TException {
        cliConnect();
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("fakeCommand");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("Error: unknown command 'fakeCommand'"));
    }

    /**
     * Test execute help command from cli.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteHelpCommandFromCli() throws UnsupportedEncodingException, TException {
        cliConnect();
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("help");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("Available commands:"));
    }

    /**
     * Test execute memory command from cli.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteMemoryCommandFromCli() throws UnsupportedEncodingException, TException {
        cliConnect();
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("memory -gc");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("Memory Usage:"));
    }

    /**
     * Test execute threads command from cli.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteThreadsCommandFromCli() throws UnsupportedEncodingException, TException {
        cliConnect();
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("threads");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("THREADS DUMP:"));
    }

    /**
     * Test execute disconnect command from cli.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteDisconnectCommandFromCli() throws UnsupportedEncodingException, TException {
        cliConnect();
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("disconnect");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.trim().isEmpty());
        Assert.assertFalse(cliSession.isRemoteMode());
    }

    /**
     * Test execute disconnect command when not connected.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteDisconnectCommandWhenDisconnectedFromCli() throws UnsupportedEncodingException, TException {
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("disconnect");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        String error = systemErr.toString("UTF-8");
        Assert.assertTrue(output.trim().isEmpty());
        Assert.assertTrue(error.startsWith("Not connected!"));
        Assert.assertFalse(cliSession.isRemoteMode());
    }

    /**
     * Test execute connect command when not connected.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteConnectCommandWhenDisconnectedFromCli() throws UnsupportedEncodingException, TException {
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine(((("connect " + (CliThriftIT.HOST)) + ":") + (CliThriftIT.PORT)));
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        String error = systemErr.toString("UTF-8");
        Assert.assertTrue(output.trim().isEmpty());
        Assert.assertTrue(error.trim().isEmpty());
        Assert.assertTrue(cliSession.isRemoteMode());
    }

    /**
     * Test execute connect command to not available server when not connected.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteConnectCommandToNotAvailableServerWhenDisconnectedFromCli() throws UnsupportedEncodingException, TException {
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine(((("connect " + (CliThriftIT.HOST)) + ":") + ((CliThriftIT.PORT) + 10)));
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        String error = systemErr.toString("UTF-8");
        Assert.assertTrue(output.trim().isEmpty());
        Assert.assertTrue(error.startsWith("[Thrift Error]: "));
        Assert.assertFalse(cliSession.isRemoteMode());
    }

    /**
     * Test execute connect with invalid arguments.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteConnectWithInvalidArgumentsFromCli() throws UnsupportedEncodingException, TException {
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("connect ");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        String error = systemErr.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("Unable to parse arguments."));
        Assert.assertTrue(error.trim().isEmpty());
        Assert.assertFalse(cliSession.isRemoteMode());
    }

    /**
     * Test execute unknown command from cli in local mode.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteUnknownCommandFromCliLocal() throws UnsupportedEncodingException, TException {
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("fakeCommand");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("Unknown command 'fakeCommand'"));
    }

    /**
     * Test execute help command in local mode.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteHelpCommandFromCliLocal() throws UnsupportedEncodingException, TException {
        BaseCliThriftClient cli = new BaseCliThriftClient();
        int result = cli.processLine("help");
        Assert.assertEquals(result, 0);
        String output = systemOut.toString("UTF-8");
        String error = systemErr.toString("UTF-8");
        Assert.assertTrue(output.trim().startsWith("Available commands: "));
        Assert.assertTrue(error.trim().isEmpty());
    }

    /**
     * Test execute help command without session.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteHelpCommandWithoutSession() throws UnsupportedEncodingException, TException {
        OptionsProcessor optionsProcessor = new OptionsProcessor();
        optionsProcessor.parse(new String[]{ "-H" });
        boolean result = optionsProcessor.process(cliSession);
        Assert.assertFalse(result);
        String output = systemOut.toString("UTF-8");
        Assert.assertTrue(output.startsWith("usage: thriftCli"));
    }

    /**
     * Test execute option command without session.
     *
     * @throws TException
     * 		the t exception
     */
    @Test
    public void testExecuteUmknownCommandWithoutSession() throws UnsupportedEncodingException, TException {
        OptionsProcessor optionsProcessor = new OptionsProcessor();
        boolean result = optionsProcessor.parse(new String[]{ "--unknown" });
        Assert.assertFalse(result);
        String output = systemOut.toString("UTF-8");
        String error = systemErr.toString("UTF-8");
        Assert.assertTrue(output.startsWith("usage: thriftCli"));
        Assert.assertTrue(error.trim().equals("Unrecognized option: --unknown"));
    }
}

