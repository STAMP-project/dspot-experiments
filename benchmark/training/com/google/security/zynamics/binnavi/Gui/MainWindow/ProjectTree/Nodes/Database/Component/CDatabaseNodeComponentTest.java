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
package com.google.security.zynamics.binnavi.Gui.MainWindow.ProjectTree.Nodes.Database.Component;


import com.google.security.zynamics.binnavi.Database.MockClasses.MockDatabase;
import com.google.security.zynamics.binnavi.Database.MockClasses.MockSqlProvider;
import com.google.security.zynamics.zylib.reflection.ReflectionHelpers;
import java.util.Collection;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


@RunWith(JUnit4.class)
public final class CDatabaseNodeComponentTest {
    private MockDatabase m_database;

    private final MockSqlProvider m_provider = new MockSqlProvider();

    @Test
    public void testSimple() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
        final CDatabaseNodeComponent component = new CDatabaseNodeComponent(m_database);
        component.dispose();
        Assert.assertTrue(((Collection<?>) (ReflectionHelpers.getField(ReflectionHelpers.getField(m_database, "listeners"), "m_listeners"))).isEmpty());
    }
}

