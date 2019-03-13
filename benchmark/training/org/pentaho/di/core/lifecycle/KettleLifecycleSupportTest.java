/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2016 - 2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.core.lifecycle;


import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.core.plugins.PluginTypeListener;


public class KettleLifecycleSupportTest {
    private PluginRegistry registry;

    private List<PluginInterface> registeredPlugins;

    private ArgumentCaptor<PluginTypeListener> typeListenerRegistration;

    @Test
    public void testOnEnvironmentInit() throws Exception {
        final List<KettleLifecycleListener> listeners = new ArrayList<KettleLifecycleListener>();
        listeners.add(createLifecycleListener());
        KettleLifecycleSupport kettleLifecycleSupport = new KettleLifecycleSupport();
        Assert.assertNotNull(typeListenerRegistration.getValue());
        KettleLifecycleListener preInit = createLifecycleListener();
        listeners.add(preInit);
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                listeners.add(createLifecycleListener());
                return null;
            }
        }).when(preInit).onEnvironmentInit();
        Mockito.verifyNoMoreInteractions(listeners.toArray());
        // Init environment
        kettleLifecycleSupport.onEnvironmentInit();
        for (KettleLifecycleListener listener : listeners) {
            Mockito.verify(listener).onEnvironmentInit();
        }
        Mockito.verifyNoMoreInteractions(listeners.toArray());
        KettleLifecycleListener postInit = createLifecycleListener();
        Mockito.verify(postInit).onEnvironmentInit();
        Mockito.verifyNoMoreInteractions(listeners.toArray());
    }
}

