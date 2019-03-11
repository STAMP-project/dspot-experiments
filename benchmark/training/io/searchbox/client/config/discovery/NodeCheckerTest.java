package io.searchbox.client.config.discovery;


import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ClientConfig;
import io.searchbox.client.config.exception.CouldNotConnectException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class NodeCheckerTest {
    ClientConfig clientConfig;

    JestClient jestClient;

    @Test
    public void testWithResolvedWithoutHostnameAddressWithCustomScheme() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, new ClientConfig.Builder("http://localhost:9200").discoveryEnabled(true).discoveryFrequency(1L, TimeUnit.SECONDS).defaultSchemeForDiscoveredNodes("https").build());
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("http_address", "inet[/192.168.2.7:9200]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("https://192.168.2.7:9200", servers.iterator().next());
    }

    @Test
    public void testEsVersion5() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("version", "5.0.1", "http", ImmutableMap.of("publish_address", "192.168.2.7:9200")))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("http://192.168.2.7:9200", servers.iterator().next());
    }

    @Test
    public void testWithResolvedWithoutHostnameAddress() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("http_address", "inet[/192.168.2.7:9200]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("http://192.168.2.7:9200", servers.iterator().next());
    }

    @Test
    public void testWithResolvedWithHostnameAddress() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("http_address", "inet[searchly.com/192.168.2.7:9200]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("http://192.168.2.7:9200", servers.iterator().next());
    }

    @Test
    public void testWithUnresolvedAddress() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("http_address", "inet[192.168.2.7:9200]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("http://192.168.2.7:9200", servers.iterator().next());
    }

    @Test
    public void testWithInvalidUnresolvedAddress() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("http_address", "inet[192.168.2.7:]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(0, servers.size());
    }

    @Test
    public void testWithInvalidResolvedAddress() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node_name", ImmutableMap.of("http_address", "inet[gg/192.168.2.7:]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(0, servers.size());
    }

    @Test
    public void testNodesInfoExceptionUsesBootstrapServerList() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenThrow(Exception.class);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("http://localhost:9200", servers.iterator().next());
    }

    @Test
    public void testNodesInfoFailureUsesBootstrapServerList() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setSucceeded(false);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        Set servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        Assert.assertEquals("http://localhost:9200", servers.iterator().next());
    }

    @Test
    public void testNodesInfoExceptionRemovesServerFromList() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        JestResult result = new JestResult(new Gson());
        result.setJsonMap(ImmutableMap.<String, Object>of("ok", "true", "nodes", ImmutableMap.of("node1", ImmutableMap.of("http_address", "inet[/192.168.2.7:9200]"), "node2", ImmutableMap.of("http_address", "inet[/192.168.2.8:9200]"), "node3", ImmutableMap.of("http_address", "inet[/192.168.2.9:9200]"))));
        result.setSucceeded(true);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenReturn(result);
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        ArgumentCaptor<LinkedHashSet> argument = ArgumentCaptor.forClass(LinkedHashSet.class);
        Mockito.verify(jestClient).setServers(argument.capture());
        Mockito.verify(jestClient).execute(ArgumentMatchers.isA(Action.class));
        Set servers = argument.getValue();
        Assert.assertEquals(3, servers.size());
        // when(jestClient.execute(isA(Action.class))).thenThrow(new HttpHostConnectException(
        // new ConnectException(), new HttpHost("192.168.2.7", 9200, "http"), null));
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenThrow(new CouldNotConnectException("http://192.168.2.7:9200", new IOException("Test HttpHostException")));
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient, Mockito.times(2)).execute(ArgumentMatchers.isA(Action.class));
        Mockito.verify(jestClient, Mockito.times(2)).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        servers = argument.getValue();
        Assert.assertEquals(2, servers.size());
        Iterator serversItr = servers.iterator();
        Assert.assertEquals("http://192.168.2.8:9200", serversItr.next());
        Assert.assertEquals("http://192.168.2.9:9200", serversItr.next());
        // fail at the 2nd node
        // when(jestClient.execute(isA(Action.class))).thenThrow(new HttpHostConnectException(
        // new ConnectException(), new HttpHost("192.168.2.8", 9200, "http"), null));
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenThrow(new CouldNotConnectException("http://192.168.2.8:9200", new IOException("Test HttpHostException")));
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient, Mockito.times(3)).execute(ArgumentMatchers.isA(Action.class));
        Mockito.verify(jestClient, Mockito.times(3)).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        serversItr = servers.iterator();
        Assert.assertEquals("http://192.168.2.9:9200", serversItr.next());
        // fail at the last node, fail back to bootstrap
        // when(jestClient.execute(isA(Action.class))).thenThrow(new HttpHostConnectException(
        // new ConnectException(), new HttpHost("192.168.2.9", 9200, "http"), null));
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenThrow(new CouldNotConnectException("http://192.168.2.9:9200", new IOException("Test HttpHostException")));
        nodeChecker.runOneIteration();
        Mockito.verify(jestClient, Mockito.times(4)).execute(ArgumentMatchers.isA(Action.class));
        Mockito.verify(jestClient, Mockito.times(4)).setServers(argument.capture());
        Mockito.verifyNoMoreInteractions(jestClient);
        servers = argument.getValue();
        Assert.assertEquals(1, servers.size());
        serversItr = servers.iterator();
        Assert.assertEquals("http://localhost:9200", serversItr.next());
    }

    @Test
    public void testNodesInfoExceptionBeforeNodesDiscovered() throws Exception {
        NodeChecker nodeChecker = new NodeChecker(jestClient, clientConfig);
        Mockito.when(jestClient.execute(ArgumentMatchers.isA(Action.class))).thenThrow(new CouldNotConnectException("http://localhost:9200", new IOException("Test HttpHostException")));
        nodeChecker.runOneIteration();
        Assert.assertNotNull(nodeChecker.discoveredServerList);
        Assert.assertEquals(0, nodeChecker.discoveredServerList.size());
    }
}

