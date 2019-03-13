/**
 * !
 * Copyright 2018 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.repository.pur.provider;


import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.pentaho.di.core.plugins.PluginInterface;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.repository.RepositoryMeta;
import org.pentaho.di.repository.pur.PurRepository;


public class PurRepositoryProxyTest {
    PurRepositoryProxy proxy;

    PluginRegistry mockRegistry;

    PluginInterface mockPluginInterface;

    ClassLoader mockClassLoader;

    PurRepository mockRepository;

    @Test
    public void getDelegateTest() {
        Repository repository = null;
        try {
            Mockito.<Class<?>>when(mockClassLoader.loadClass(ArgumentMatchers.anyString())).thenReturn(Class.forName(("org.pentaho" + ".di.repository.pur.PurRepository")));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        repository = proxy.getDelegate();
        Assert.assertNotNull(repository);
    }

    @Test
    public void getLocationUrlTest() {
        String someString = "SomeString";
        String returnString;
        RepositoryMeta mockRepositoryMeta = Mockito.mock(RepositoryMeta.class);
        // Both these mocks are needed. If you take the first one the other fails... Probably the first one stubs
        // something that makes the second one work...?
        Mockito.doReturn(mockRepositoryMeta).when(proxy).createPurRepositoryMetaRepositoryMeta(ArgumentMatchers.anyString());
        Mockito.when(proxy.createPurRepositoryMetaRepositoryMeta(ArgumentMatchers.anyString())).thenReturn(mockRepositoryMeta);
        // Both these mocks are needed. If you take the first one the other fails... Probably the first one stubs
        // something that makes the second one work...?
        Mockito.doReturn(mockRepository).when(proxy).getDelegate();
        Mockito.when(proxy.getDelegate()).thenReturn(mockRepository);
        proxy.setLocationUrl(someString);
        returnString = proxy.getLocationUrl();
        Mockito.verify(proxy, Mockito.times(1)).getDelegate();
        Mockito.verify(proxy, Mockito.times(1)).createPurRepositoryMetaRepositoryMeta(someString);
        TestCase.assertEquals(someString, returnString);
    }

    @Test
    public void createPurRepositoryMetaRepositoryMetaTest() {
        RepositoryMeta repositoryMeta = null;
        try {
            Mockito.<Class<?>>when(mockClassLoader.loadClass(("org.pentaho.di.repository.pur" + ".PurRepositoryLocation"))).thenReturn(Class.forName(("org.pentaho.di.repository.pur" + ".PurRepositoryLocation")));
            Mockito.<Class<?>>when(mockClassLoader.loadClass("org.pentaho.di.repository.pur.PurRepositoryMeta")).thenReturn(Class.forName("org.pentaho.di.repository.pur.PurRepositoryMeta"));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        repositoryMeta = proxy.createPurRepositoryMetaRepositoryMeta("SomeUrl");
        Assert.assertNotNull(repositoryMeta);
    }
}

