/**
 * Copyright 2014 Google Inc. All Rights Reserved.
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
package com.google.security.zynamics.binnavi.API.disassembly;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.binnavi.debug.debugger.DebuggerTemplate;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class DebuggerTemplateTest {
    @Test
    public void testConstructor() {
        final DebuggerTemplate internalTemplate = new DebuggerTemplate(1, "Name", "Host", 123, new MockSqlProvider());
        final DebuggerTemplate template = new DebuggerTemplate(internalTemplate);
        Assert.assertEquals("Name", template.getName());
        Assert.assertEquals("Host", template.getHost());
        Assert.assertEquals(123, template.getPort());
        Assert.assertEquals("Debugger Template 'Name' (Host:123)", template.toString());
    }

    @Test
    public void testSetHost() throws CouldntSaveDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockDebuggerTemplateListener listener = new MockDebuggerTemplateListener();
        final DebuggerTemplate internalTemplate = new DebuggerTemplate(1, "Name", "Host", 123, new MockSqlProvider());
        final DebuggerTemplate template = new DebuggerTemplate(internalTemplate);
        template.addListener(listener);
        template.setHost("Hannes");
        Assert.assertEquals("Hannes", template.getHost());
        Assert.assertEquals("Hannes", internalTemplate.getHost());
        Assert.assertEquals("changedHost;", listener.events);
        internalTemplate.setHost("Hannes 2");
        Assert.assertEquals("Hannes 2", template.getHost());
        Assert.assertEquals("Hannes 2", internalTemplate.getHost());
        Assert.assertEquals("changedHost;changedHost;", listener.events);
        template.removeListener(listener);
    }

    @Test
    public void testSetName() throws CouldntSaveDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockDebuggerTemplateListener listener = new MockDebuggerTemplateListener();
        final DebuggerTemplate internalTemplate = new DebuggerTemplate(1, "Name", "Host", 123, new MockSqlProvider());
        final DebuggerTemplate template = new DebuggerTemplate(internalTemplate);
        template.addListener(listener);
        template.setName("Hannes");
        Assert.assertEquals("Hannes", template.getName());
        Assert.assertEquals("Hannes", internalTemplate.getName());
        Assert.assertEquals("changedName;", listener.events);
        internalTemplate.setName("Hannes 2");
        Assert.assertEquals("Hannes 2", template.getName());
        Assert.assertEquals("Hannes 2", internalTemplate.getName());
        Assert.assertEquals("changedName;changedName;", listener.events);
        template.removeListener(listener);
    }

    @Test
    public void testSetPort() throws CouldntSaveDataException, com.google.security.zynamics.binnavi.Database.Exceptions.CouldntSaveDataException {
        final MockDebuggerTemplateListener listener = new MockDebuggerTemplateListener();
        final DebuggerTemplate internalTemplate = new DebuggerTemplate(1, "Name", "Host", 123, new MockSqlProvider());
        final DebuggerTemplate template = new DebuggerTemplate(internalTemplate);
        template.addListener(listener);
        template.setPort(222);
        Assert.assertEquals(222, template.getPort());
        Assert.assertEquals(222, internalTemplate.getPort());
        Assert.assertEquals("changedPort;", listener.events);
        internalTemplate.setPort(223);
        Assert.assertEquals(223, template.getPort());
        Assert.assertEquals(223, internalTemplate.getPort());
        Assert.assertEquals("changedPort;changedPort;", listener.events);
        template.removeListener(listener);
    }
}

