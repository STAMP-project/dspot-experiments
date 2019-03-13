/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2018 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.concurrency;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.provider.AbstractFileProvider;
import org.apache.commons.vfs2.provider.FileProvider;
import org.junit.ClassRule;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.core.osgi.api.VfsEmbeddedFileSystemCloser;
import org.pentaho.di.core.vfs.ConcurrentFileSystemManager;
import org.pentaho.di.core.vfs.KettleVFS;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;


public class ConcurrentFileSystemManagerTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    private DefaultFileSystemManager fileSystemManager = ((DefaultFileSystemManager) (KettleVFS.getInstance().getFileSystemManager()));

    @Test
    public void getAndPutConcurrently() throws Exception {
        int numberOfGetters = 5;
        int numberOfPutters = 1;
        AtomicBoolean condition = new AtomicBoolean(true);
        List<ConcurrentFileSystemManagerTest.Getter> getters = new ArrayList<>();
        for (int i = 0; i < numberOfGetters; i++) {
            getters.add(new ConcurrentFileSystemManagerTest.Getter(condition, this.fileSystemManager));
        }
        List<ConcurrentFileSystemManagerTest.Putter> putters = new ArrayList<>();
        for (int i = 0; i < numberOfPutters; i++) {
            putters.add(new ConcurrentFileSystemManagerTest.Putter(condition, this.fileSystemManager));
        }
        ConcurrencyTestRunner.runAndCheckNoExceptionRaised(putters, getters, condition);
    }

    @Test
    public void testNcCloseEmbeddedFileSystem() throws Exception {
        ConcurrentFileSystemManager concurrentFileSystemManager = new ConcurrentFileSystemManager();
        ConcurrentFileSystemManagerTest.MockNamedClusterProvider mockFileProvider = Mockito.mock(ConcurrentFileSystemManagerTest.MockNamedClusterProvider.class);
        concurrentFileSystemManager.addProvider(new String[]{ "hc" }, mockFileProvider);
        concurrentFileSystemManager.closeEmbeddedFileSystem("key");
        closeFileSystem("key");
    }

    @Test
    public void testNonNcCloseEmbeddedFileSystem() throws Exception {
        ConcurrentFileSystemManager concurrentFileSystemManager = new ConcurrentFileSystemManager();
        ConcurrentFileSystemManagerTest.MockNamedClusterProvider mockFileProvider = Mockito.mock(ConcurrentFileSystemManagerTest.MockNamedClusterProvider.class);
        concurrentFileSystemManager.addProvider(new String[]{ "notnc" }, mockFileProvider);
        concurrentFileSystemManager.closeEmbeddedFileSystem("key");
        closeFileSystem("key");
    }

    public interface MockNamedClusterProvider extends FileProvider , VfsEmbeddedFileSystemCloser {}

    private class Getter extends StopOnErrorCallable<Object> {
        private DefaultFileSystemManager fsm;

        Getter(AtomicBoolean condition, DefaultFileSystemManager fsm) {
            super(condition);
            this.fsm = fsm;
        }

        @Override
        Object doCall() throws Exception {
            while (condition.get()) {
                this.fsm.getSchemes();
            } 
            return null;
        }
    }

    private class Putter extends StopOnErrorCallable<Object> {
        private DefaultFileSystemManager fsm;

        AbstractFileProvider provider;

        Putter(AtomicBoolean condition, DefaultFileSystemManager fsm) {
            super(condition);
            this.fsm = fsm;
            provider = Mockito.mock(AbstractFileProvider.class);
            Mockito.doNothing().when(provider).freeUnusedResources();
        }

        @Override
        Object doCall() throws Exception {
            while (condition.get()) {
                this.fsm.addProvider("scheme", provider);
                // to register only one provider with a given scheme
                condition.set(false);
            } 
            return null;
        }
    }
}

