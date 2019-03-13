package com.iota.iri.conf;


import org.junit.Assert;
import org.junit.Test;


public class ZMQConfigTest {
    @Test
    public void isZmqEnabledLegacy() {
        String[] args = new String[]{ "--zmq-enabled", "true" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertTrue("ZMQ must be globally enabled", config.isZmqEnabled());
        Assert.assertTrue("ZMQ TCP must be enabled", config.isZmqEnableTcp());
        Assert.assertTrue("ZMQ IPC must be enabled", config.isZmqEnableIpc());
    }

    @Test
    public void isZmqEnabled() {
        String[] args = new String[]{ "--zmq-enable-tcp", "true", "--zmq-enable-ipc", "true" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertTrue("ZMQ must be globally enabled", config.isZmqEnabled());
        Assert.assertTrue("ZMQ TCP must be enabled", config.isZmqEnableTcp());
        Assert.assertTrue("ZMQ IPC must be enabled", config.isZmqEnableIpc());
    }

    @Test
    public void isZmqEnableTcp() {
        String[] args = new String[]{ "--zmq-enable-tcp", "true" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertEquals("ZMQ port must be the default port", 5556, config.getZmqPort());
        Assert.assertTrue("ZMQ TCP must be enabled", config.isZmqEnableTcp());
    }

    @Test
    public void isZmqEnableIpc() {
        String[] args = new String[]{ "--zmq-enable-ipc", "true" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertEquals("ZMQ ipc must be the default ipc", "ipc://iri", config.getZmqIpc());
        Assert.assertTrue("ZMQ IPC must be enabled", config.isZmqEnableIpc());
    }

    @Test
    public void getZmqPort() {
        String[] args = new String[]{ "--zmq-port", "8899" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertTrue("ZMQ TCP must be enabled", config.isZmqEnableTcp());
        Assert.assertEquals("ZMQ port must be overridden", 8899, config.getZmqPort());
    }

    @Test
    public void getZmqThreads() {
        String[] args = new String[]{ "--zmq-threads", "5" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertEquals("ZMQ threads must be overridden", 5, config.getZmqThreads());
    }

    @Test
    public void getZmqIpc() {
        String[] args = new String[]{ "--zmq-ipc", "ipc://test" };
        IotaConfig config = ConfigFactory.createIotaConfig(false);
        config.parseConfigFromArgs(args);
        Assert.assertTrue("ZMQ IPC must be enabled", config.isZmqEnableIpc());
        Assert.assertEquals("ZMQ ipc must be overridden", "ipc://test", config.getZmqIpc());
    }
}

