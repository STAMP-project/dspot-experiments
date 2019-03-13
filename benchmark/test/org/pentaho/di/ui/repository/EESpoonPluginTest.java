/**
 * !
 * Copyright 2010 - 2017 Hitachi Vantara.  All rights reserved.
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
package org.pentaho.di.ui.repository;


import SpoonLifeCycleEvent.REPOSITORY_CHANGED;
import SpoonLifeCycleEvent.REPOSITORY_CONNECTED;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.repository.IRepositoryService;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEEDatabaseConnection;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEEJob;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEERepositoryDirectory;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEERepositoryUser;
import org.pentaho.di.ui.repository.pur.repositoryexplorer.model.UIEETransformation;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIDatabaseConnection;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIJob;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIObjectRegistry;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryDirectory;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UIRepositoryUser;
import org.pentaho.di.ui.repository.repositoryexplorer.model.UITransformation;
import org.pentaho.di.ui.spoon.Spoon;


public class EESpoonPluginTest {
    private Spoon spoon = Mockito.mock(Spoon.class);

    private Repository repository = Mockito.mock(Repository.class);

    private EESpoonPlugin eeSpoonPlugin = Mockito.mock(EESpoonPlugin.class);

    @Test
    public void testOnEvent_REPOSITORY_CHANGED_noservice() {
        Mockito.doReturn(spoon).when(eeSpoonPlugin).getSpoonInstance();
        eeSpoonPlugin.onEvent(REPOSITORY_CHANGED);
        UIObjectRegistry registry = UIObjectRegistry.getInstance();
        Assert.assertEquals(UIRepositoryUser.class, registry.getRegisteredUIRepositoryUserClass());
        Assert.assertEquals(UIRepositoryDirectory.class, registry.getRegisteredUIRepositoryDirectoryClass());
        Assert.assertEquals(UIDatabaseConnection.class, registry.getRegisteredUIDatabaseConnectionClass());
        Assert.assertEquals(UIJob.class, registry.getRegisteredUIJobClass());
        Assert.assertEquals(UITransformation.class, registry.getRegisteredUITransformationClass());
    }

    @Test
    public void testOnEvent_REPOSITORY_CHANGED_service() throws KettleException {
        Mockito.doReturn(spoon).when(eeSpoonPlugin).getSpoonInstance();
        Mockito.doReturn(true).when(repository).hasService(anyClass());
        eeSpoonPlugin.onEvent(REPOSITORY_CHANGED);
        UIObjectRegistry registry = UIObjectRegistry.getInstance();
        Assert.assertEquals(UIEERepositoryUser.class, registry.getRegisteredUIRepositoryUserClass());
        Assert.assertEquals(UIEERepositoryDirectory.class, registry.getRegisteredUIRepositoryDirectoryClass());
        Assert.assertEquals(UIEEDatabaseConnection.class, registry.getRegisteredUIDatabaseConnectionClass());
        Assert.assertEquals(UIEEJob.class, registry.getRegisteredUIJobClass());
        Assert.assertEquals(UIEETransformation.class, registry.getRegisteredUITransformationClass());
    }

    @Test
    public void testOnEvent_REPOSITORY_CONNECTED_noservice() {
        Mockito.doReturn(null).doReturn(spoon).when(eeSpoonPlugin).getSpoonInstance();
        eeSpoonPlugin.onEvent(REPOSITORY_CONNECTED);
        UIObjectRegistry registry = UIObjectRegistry.getInstance();
        Assert.assertEquals(UIRepositoryUser.class, registry.getRegisteredUIRepositoryUserClass());
        Assert.assertEquals(UIRepositoryDirectory.class, registry.getRegisteredUIRepositoryDirectoryClass());
        Assert.assertEquals(UIEEDatabaseConnection.class, registry.getRegisteredUIDatabaseConnectionClass());
        Assert.assertEquals(UIJob.class, registry.getRegisteredUIJobClass());
        Assert.assertEquals(UITransformation.class, registry.getRegisteredUITransformationClass());
    }

    @Test
    public void testOnEvent_REPOSITORY_CONNECTED_service() throws KettleException {
        Mockito.doReturn(true).when(repository).hasService(anyClass());
        Mockito.doReturn(null).doReturn(spoon).when(eeSpoonPlugin).getSpoonInstance();
        eeSpoonPlugin.onEvent(REPOSITORY_CONNECTED);
        UIObjectRegistry registry = UIObjectRegistry.getInstance();
        Assert.assertEquals(UIEERepositoryUser.class, registry.getRegisteredUIRepositoryUserClass());
        Assert.assertEquals(UIEERepositoryDirectory.class, registry.getRegisteredUIRepositoryDirectoryClass());
        Assert.assertEquals(UIEEDatabaseConnection.class, registry.getRegisteredUIDatabaseConnectionClass());
        Assert.assertEquals(UIEEJob.class, registry.getRegisteredUIJobClass());
        Assert.assertEquals(UIEETransformation.class, registry.getRegisteredUITransformationClass());
    }

    private class AnyClassMatcher extends ArgumentMatcher<Class<? extends IRepositoryService>> {
        @Override
        public boolean matches(final Object arg) {
            return true;
        }
    }
}

