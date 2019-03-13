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
package org.jboss.netty.util.internal;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.DefaultChannelPipeline;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.util.ThreadRenamingRunnable;
import org.junit.Assert;
import org.junit.Test;


public class StackTraceSimplifierTest {
    @Test
    public void testBasicSimplification() {
        Exception e = new Exception();
        e.setStackTrace(new StackTraceElement[]{ new StackTraceElement(ChannelBuffer.class.getName(), "a", null, 1), new StackTraceElement("com.example.Foo", "b", null, 1), new StackTraceElement(SimpleChannelHandler.class.getName(), "c", null, 1), new StackTraceElement(ThreadRenamingRunnable.class.getName(), "d", null, 1) });
        StackTraceSimplifier.simplify(e);
        StackTraceElement[] simplified = e.getStackTrace();
        Assert.assertEquals(2, simplified.length);
        Assert.assertEquals(ChannelBuffer.class.getName(), simplified[0].getClassName());
        Assert.assertEquals("com.example.Foo", simplified[1].getClassName());
    }

    @Test
    public void testNestedSimplification() {
        Exception e1 = new Exception();
        e1.setStackTrace(new StackTraceElement[]{ new StackTraceElement(ChannelBuffer.class.getName(), "a", null, 1), new StackTraceElement("com.example.Foo", "b", null, 1), new StackTraceElement(SimpleChannelHandler.class.getName(), "c", null, 1), new StackTraceElement(DefaultChannelPipeline.class.getName(), "d", null, 1), new StackTraceElement(ThreadRenamingRunnable.class.getName(), "e", null, 1) });
        Exception e2 = new Exception(e1);
        e2.setStackTrace(new StackTraceElement[]{ new StackTraceElement(Channel.class.getName(), "a", null, 1), new StackTraceElement("com.example.Bar", "b", null, 1), new StackTraceElement(SimpleChannelHandler.class.getName(), "c", null, 1), new StackTraceElement(DefaultChannelPipeline.class.getName(), "d", null, 1), new StackTraceElement(ThreadRenamingRunnable.class.getName(), "e", null, 1) });
        StackTraceSimplifier.simplify(e2);
        StackTraceElement[] simplified1 = e1.getStackTrace();
        Assert.assertEquals(2, simplified1.length);
        Assert.assertEquals(ChannelBuffer.class.getName(), simplified1[0].getClassName());
        Assert.assertEquals("com.example.Foo", simplified1[1].getClassName());
        StackTraceElement[] simplified2 = e2.getStackTrace();
        Assert.assertEquals(2, simplified2.length);
        Assert.assertEquals(Channel.class.getName(), simplified2[0].getClassName());
        Assert.assertEquals("com.example.Bar", simplified2[1].getClassName());
    }

    @Test
    public void testNettyBugDetection() {
        Exception e = new Exception();
        e.setStackTrace(new StackTraceElement[]{ new StackTraceElement(DefaultChannelPipeline.class.getName(), "a", null, 1), new StackTraceElement(ChannelBuffer.class.getName(), "a", null, 1), new StackTraceElement("com.example.Foo", "b", null, 1), new StackTraceElement(SimpleChannelHandler.class.getName(), "c", null, 1), new StackTraceElement(ThreadRenamingRunnable.class.getName(), "d", null, 1) });
        StackTraceSimplifier.simplify(e);
        StackTraceElement[] simplified = e.getStackTrace();
        Assert.assertEquals(5, simplified.length);
    }

    @Test
    public void testEmptyStackTrace() {
        Exception e = new Exception();
        e.setStackTrace(new StackTraceElement[0]);
        StackTraceSimplifier.simplify(e);
        Assert.assertEquals(0, e.getStackTrace().length);
    }

    @Test
    public void testNullStackTrace() {
        Exception e = createNiceMock(Exception.class);
        expect(e.getStackTrace()).andReturn(null).anyTimes();
        replay(e);
        StackTraceSimplifier.simplify(e);
        Assert.assertNull(e.getStackTrace());
        verify(e);
    }
}

