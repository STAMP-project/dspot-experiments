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


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class TextNodeTest {
    private TextNode m_node;

    // @Test
    // public void testAppendComment() throws CouldntSaveDataException {
    // final MockTextNodeListener listener = new MockTextNodeListener();
    // 
    // final CUserManager userManager = CUserManager.get(new MockSqlProvider());
    // final IUser user = userManager.addUser(" FOO ");
    // userManager.setCurrentActiveUser(user);
    // 
    // m_node.addListener(listener);
    // 
    // m_node.appendComment("Hannes");
    // 
    // assertEquals("Hannes", m_node.getComments().get(0).getComment());
    // assertEquals("changedText;", listener.events);
    // 
    // m_node.removeListener(listener);
    // }
    @Test
    public void testConstructor() {
        Assert.assertEquals("Fark", m_node.getComments().get(0).getComment());
        Assert.assertEquals("Text Node with: '1' comments.", m_node.toString());
    }
}

