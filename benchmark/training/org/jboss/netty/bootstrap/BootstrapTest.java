/**
 * Copyright 2012 The Netty Project
 *
 * The Netty Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.jboss.netty.bootstrap;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.jboss.netty.util.DummyHandler;
import org.junit.Assert;
import org.junit.Test;


/**
 * A bootstrap test
 */
public class BootstrapTest {
    @Test(expected = IllegalStateException.class)
    public void shouldNotReturnNullFactory() {
        newBootstrap().getFactory();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowInitialFactoryToChange() {
        new Bootstrap(createMock(ChannelFactory.class)).setFactory(createMock(ChannelFactory.class));
    }

    @Test
    public void shouldNotAllowFactoryToChangeMoreThanOnce() {
        Bootstrap b = newBootstrap();
        ChannelFactory f = createMock(ChannelFactory.class);
        b.setFactory(f);
        Assert.assertSame(f, b.getFactory());
        try {
            b.setFactory(createMock(ChannelFactory.class));
            Assert.fail();
        } catch (IllegalStateException e) {
            // Success.
        }
        b.releaseExternalResources();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullFactory() {
        newBootstrap().setFactory(null);
    }

    @Test
    public void shouldHaveNonNullInitialPipeline() {
        Assert.assertNotNull(newBootstrap().getPipeline());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPipeline() {
        newBootstrap().setPipeline(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPipelineMap() {
        newBootstrap().setPipelineAsMap(null);
    }

    @Test
    public void shouldHaveNonNullInitialPipelineFactory() {
        Assert.assertNotNull(new Bootstrap().getPipelineFactory());
    }

    @Test
    public void shouldUpdatePipelineFactoryIfPipelineIsSet() {
        Bootstrap b = newBootstrap();
        ChannelPipelineFactory oldPipelineFactory = b.getPipelineFactory();
        b.setPipeline(createMock(ChannelPipeline.class));
        Assert.assertNotSame(oldPipelineFactory, b.getPipelineFactory());
        b.releaseExternalResources();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotReturnPipelineWhenPipelineFactoryIsSetByUser() {
        Bootstrap b = newBootstrap();
        b.setPipelineFactory(createMock(ChannelPipelineFactory.class));
        b.getPipeline();
        b.releaseExternalResources();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotReturnPipelineMapWhenPipelineFactoryIsSetByUser() {
        Bootstrap b = newBootstrap();
        b.setPipelineFactory(createMock(ChannelPipelineFactory.class));
        b.getPipelineAsMap();
        b.releaseExternalResources();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullPipelineFactory() {
        newBootstrap().setPipelineFactory(null);
    }

    @Test
    public void shouldHaveInitialEmptyPipelineMap() {
        Assert.assertTrue(newBootstrap().getPipelineAsMap().isEmpty());
    }

    @Test
    public void shouldReturnOrderedPipelineMap() {
        Bootstrap b = newBootstrap();
        ChannelPipeline p = b.getPipeline();
        p.addLast("a", new DummyHandler());
        p.addLast("b", new DummyHandler());
        p.addLast("c", new DummyHandler());
        p.addLast("d", new DummyHandler());
        Iterator<Map.Entry<String, ChannelHandler>> m = b.getPipelineAsMap().entrySet().iterator();
        Map.Entry<String, ChannelHandler> e;
        e = m.next();
        Assert.assertEquals("a", e.getKey());
        Assert.assertSame(p.get("a"), e.getValue());
        e = m.next();
        Assert.assertEquals("b", e.getKey());
        Assert.assertSame(p.get("b"), e.getValue());
        e = m.next();
        Assert.assertEquals("c", e.getKey());
        Assert.assertSame(p.get("c"), e.getValue());
        e = m.next();
        Assert.assertEquals("d", e.getKey());
        Assert.assertSame(p.get("d"), e.getValue());
        Assert.assertFalse(m.hasNext());
        b.releaseExternalResources();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotAllowUnorderedPipelineMap() {
        Map<String, ChannelHandler> m = new HashMap<String, ChannelHandler>();
        m.put("a", new DummyHandler());
        m.put("b", new DummyHandler());
        m.put("c", new DummyHandler());
        m.put("d", new DummyHandler());
        Bootstrap b = newBootstrap();
        b.setPipelineAsMap(m);
        b.releaseExternalResources();
    }

    @Test
    public void shouldHaveOrderedPipelineWhenSetFromMap() {
        Map<String, ChannelHandler> m = new LinkedHashMap<String, ChannelHandler>();
        m.put("a", new DummyHandler());
        m.put("b", new DummyHandler());
        m.put("c", new DummyHandler());
        m.put("d", new DummyHandler());
        Bootstrap b = newBootstrap();
        b.setPipelineAsMap(m);
        ChannelPipeline p = b.getPipeline();
        Assert.assertSame(p.getFirst(), m.get("a"));
        Assert.assertEquals("a", p.getContext(p.getFirst()).getName());
        p.removeFirst();
        Assert.assertSame(p.getFirst(), m.get("b"));
        Assert.assertEquals("b", p.getContext(p.getFirst()).getName());
        p.removeFirst();
        Assert.assertSame(p.getFirst(), m.get("c"));
        Assert.assertEquals("c", p.getContext(p.getFirst()).getName());
        p.removeFirst();
        Assert.assertSame(p.getFirst(), m.get("d"));
        Assert.assertEquals("d", p.getContext(p.getFirst()).getName());
        p.removeFirst();
        try {
            p.removeFirst();
            Assert.fail();
        } catch (NoSuchElementException e) {
            // Success.
        }
        b.releaseExternalResources();
    }

    @Test
    public void shouldHaveInitialEmptyOptionMap() {
        Assert.assertTrue(newBootstrap().getOptions().isEmpty());
    }

    @Test
    public void shouldUpdateOptionMapAsRequested1() {
        Bootstrap b = new Bootstrap();
        b.setOption("s", "x");
        b.setOption("b", true);
        b.setOption("i", 42);
        Map<String, Object> o = b.getOptions();
        Assert.assertEquals(3, o.size());
        Assert.assertEquals("x", o.get("s"));
        Assert.assertEquals(true, o.get("b"));
        Assert.assertEquals(42, o.get("i"));
        b.releaseExternalResources();
    }

    @Test
    public void shouldUpdateOptionMapAsRequested2() {
        Bootstrap b = newBootstrap();
        Map<String, Object> o1 = new HashMap<String, Object>();
        o1.put("s", "x");
        o1.put("b", true);
        o1.put("i", 42);
        b.setOptions(o1);
        Map<String, Object> o2 = b.getOptions();
        Assert.assertEquals(3, o2.size());
        Assert.assertEquals("x", o2.get("s"));
        Assert.assertEquals(true, o2.get("b"));
        Assert.assertEquals(42, o2.get("i"));
        Assert.assertNotSame(o1, o2);
        Assert.assertEquals(o1, o2);
        b.releaseExternalResources();
    }

    @Test
    public void shouldRemoveOptionIfValueIsNull() {
        Bootstrap b = newBootstrap();
        b.setOption("s", "x");
        Assert.assertEquals("x", b.getOption("s"));
        b.setOption("s", null);
        Assert.assertNull(b.getOption("s"));
        Assert.assertTrue(b.getOptions().isEmpty());
        b.releaseExternalResources();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullOptionKeyOnGet() {
        newBootstrap().getOption(null);
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullOptionKeyOnSet() {
        newBootstrap().setOption(null, "x");
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullOptionMap() {
        newBootstrap().setOptions(null);
    }

    @Test
    public void testReleaseSharedNotDeadlock() {
        // create bootstraps
        final ExecutorService pool = Executors.newFixedThreadPool(2);
        final ClientBootstrap client = new ClientBootstrap(new NioClientSocketChannelFactory(pool, Executors.newCachedThreadPool()));
        final ServerBootstrap server = new ServerBootstrap(new NioServerSocketChannelFactory(pool, Executors.newCachedThreadPool()));
        // release resources
        client.releaseExternalResources();
        server.releaseExternalResources();
    }
}

