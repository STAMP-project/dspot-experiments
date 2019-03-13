/**
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.impl.persistence;


import java.io.File;
import java.io.IOException;
import org.ehcache.CachePersistenceException;
import org.ehcache.core.spi.service.LocalPersistenceService;
import org.ehcache.impl.config.persistence.DefaultPersistenceConfiguration;
import org.ehcache.impl.internal.util.FileExistenceMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;


public class DefaultLocalPersistenceServiceTest {
    @Rule
    public final ExpectedException expectedException = ExpectedException.none();

    @Rule
    public final TemporaryFolder folder = new TemporaryFolder();

    private File testFolder;

    @Test
    public void testFailsIfDirectoryExistsButNotWritable() throws IOException {
        Assume.assumeTrue(testFolder.setWritable(false));
        try {
            try {
                final DefaultLocalPersistenceService service = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(testFolder));
                service.start(null);
                Assert.fail("Expected IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                MatcherAssert.assertThat(e.getMessage(), Matchers.equalTo(("Location isn't writable: " + (testFolder.getAbsolutePath()))));
            }
        } finally {
            testFolder.setWritable(true);
        }
    }

    @Test
    public void testFailsIfFileExistsButIsNotDirectory() throws IOException {
        File f = folder.newFile("testFailsIfFileExistsButIsNotDirectory");
        try {
            final DefaultLocalPersistenceService service = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(f));
            service.start(null);
            Assert.fail("Expected IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            MatcherAssert.assertThat(e.getMessage(), Matchers.equalTo(("Location is not a directory: " + (f.getAbsolutePath()))));
        }
    }

    @Test
    public void testFailsIfDirectoryDoesNotExistsAndIsNotCreated() throws IOException {
        Assume.assumeTrue(testFolder.setWritable(false));
        try {
            File f = new File(testFolder, "notallowed");
            try {
                final DefaultLocalPersistenceService service = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(f));
                service.start(null);
                Assert.fail("Expected IllegalArgumentException");
            } catch (IllegalArgumentException e) {
                MatcherAssert.assertThat(e.getMessage(), Matchers.equalTo(("Directory couldn't be created: " + (f.getAbsolutePath()))));
            }
        } finally {
            testFolder.setWritable(true);
        }
    }

    @Test
    public void testLocksDirectoryAndUnlocks() throws IOException {
        final DefaultLocalPersistenceService service = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(testFolder));
        service.start(null);
        MatcherAssert.assertThat(service.getLockFile().exists(), Matchers.is(true));
        service.stop();
        MatcherAssert.assertThat(service.getLockFile().exists(), Matchers.is(false));
    }

    @Test
    public void testPhysicalDestroy() throws IOException, CachePersistenceException {
        final File f = folder.newFolder("testPhysicalDestroy");
        final DefaultLocalPersistenceService service = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(f));
        service.start(null);
        MatcherAssert.assertThat(service.getLockFile().exists(), Matchers.is(true));
        MatcherAssert.assertThat(f, FileExistenceMatchers.isLocked());
        LocalPersistenceService.SafeSpaceIdentifier id = service.createSafeSpaceIdentifier("test", "test");
        service.createSafeSpace(id);
        MatcherAssert.assertThat(f, FileExistenceMatchers.containsCacheDirectory("test", "test"));
        // try to destroy the physical space without the logical id
        LocalPersistenceService.SafeSpaceIdentifier newId = service.createSafeSpaceIdentifier("test", "test");
        service.destroySafeSpace(newId, false);
        MatcherAssert.assertThat(f, Matchers.not(FileExistenceMatchers.containsCacheDirectory("test", "test")));
        service.stop();
        MatcherAssert.assertThat(f, Matchers.not(FileExistenceMatchers.isLocked()));
    }

    @Test
    public void testExclusiveLock() throws IOException {
        DefaultLocalPersistenceService service1 = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(testFolder));
        DefaultLocalPersistenceService service2 = new DefaultLocalPersistenceService(new DefaultPersistenceConfiguration(testFolder));
        service1.start(null);
        // We should not be able to lock the same directory twice
        // And we should receive a meaningful exception about it
        expectedException.expectMessage(("Persistence directory already locked by this process: " + (testFolder.getAbsolutePath())));
        service2.start(null);
    }
}

