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
package org.pentaho.di.repository.pur;


import java.util.Arrays;
import java.util.List;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.pentaho.di.base.AbstractMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.job.JobMeta;
import org.pentaho.di.repository.RepositoryDirectoryInterface;
import org.pentaho.di.trans.TransMeta;


public class PurRepository_MoveAndRename_IT extends PurRepositoryTestBase {
    private final PurRepository_MoveAndRename_IT.JobAssistant jobAssistant = new PurRepository_MoveAndRename_IT.JobAssistant();

    private final PurRepository_MoveAndRename_IT.TransAssistant transAssistant = new PurRepository_MoveAndRename_IT.TransAssistant();

    public PurRepository_MoveAndRename_IT(Boolean lazyRepo) {
        super(lazyRepo);
    }

    @Test
    public void renameJob_Successfully() throws Exception {
        rename_Successfully(jobAssistant);
    }

    @Test
    public void renameTrans_Successfully() throws Exception {
        rename_Successfully(transAssistant);
    }

    @Test
    public void renameJob_CreatesNewRevision() throws Exception {
        rename_CreatesNewRevision(jobAssistant);
    }

    @Test
    public void renameTrans_CreatesNewRevision() throws Exception {
        rename_CreatesNewRevision(transAssistant);
    }

    @Test(expected = KettleException.class)
    public void renameJob_FailsIfANameConflictOccurs() throws Exception {
        rename_FailsIfANameConflictOccurs(jobAssistant);
    }

    @Test(expected = KettleException.class)
    public void renameTrans_FailsIfANameConflictOccurs() throws Exception {
        rename_FailsIfANameConflictOccurs(transAssistant);
    }

    @Test
    public void moveJob_Successfully() throws Exception {
        move_Successfully(jobAssistant);
    }

    @Test
    public void moveTrans_Successfully() throws Exception {
        move_Successfully(transAssistant);
    }

    @Test
    public void moveJob_DoesNotCreateRevision() throws Exception {
        move_DoesNotCreateRevision(jobAssistant);
    }

    @Test
    public void moveTrans_DoesNotCreateRevision() throws Exception {
        move_DoesNotCreateRevision(transAssistant);
    }

    @Test(expected = KettleException.class)
    public void moveJob_FailsIfANameConflictOccurs() throws Exception {
        move_FailsIfANameConflictOccurs(jobAssistant);
    }

    @Test(expected = KettleException.class)
    public void moveTrans_FailsIfANameConflictOccurs() throws Exception {
        move_FailsIfANameConflictOccurs(transAssistant);
    }

    @Test
    public void moveAndRenameJob_Successfully() throws Exception {
        moveAndRename_Successfully(jobAssistant);
    }

    @Test
    public void moveAndRenameTrans_Successfully() throws Exception {
        moveAndRename_Successfully(transAssistant);
    }

    @Test(expected = KettleException.class)
    public void moveAndRenameTrans_FailsIfANameConflictOccurs() throws Exception {
        moveAndRename_FailsIfANameConflictOccurs(transAssistant);
    }

    @Test(expected = KettleException.class)
    public void moveAndRenameJob_FailsIfANameConflictOccurs() throws Exception {
        moveAndRename_FailsIfANameConflictOccurs(jobAssistant);
    }

    private abstract class Assistant {
        public abstract AbstractMeta createNew();

        abstract String getType();

        public void save(AbstractMeta meta, String name, RepositoryDirectoryInterface directory) throws Exception {
            Assert.assertNotNull(directory);
            meta.setName(name);
            meta.setRepositoryDirectory(directory);
            purRepository.save(meta, null, null);
            assertExistsIn(directory, name, ((getType()) + " was not saved"));
        }

        void assertExistsIn(RepositoryDirectoryInterface dir, String name, String message) throws Exception {
            List<String> existing = getNames(dir);
            Assert.assertThat(message, existing, CoreMatchers.hasItem(name));
        }

        abstract List<String> getNames(RepositoryDirectoryInterface dir) throws Exception;

        public void rename(AbstractMeta meta, String newName) throws Exception {
            rename(meta, meta.getRepositoryDirectory(), newName);
        }

        public void move(AbstractMeta meta, RepositoryDirectoryInterface destFolder) throws Exception {
            rename(meta, destFolder, null);
        }

        public void rename(AbstractMeta meta, RepositoryDirectoryInterface destFolder, String newName) throws Exception {
            doRename(meta, destFolder, newName);
            String checkedName = (newName == null) ? meta.getName() : newName;
            assertExistsIn(destFolder, checkedName, ((getType()) + " was not renamed"));
        }

        abstract void doRename(AbstractMeta meta, RepositoryDirectoryInterface destFolder, String newName) throws Exception;
    }

    private class JobAssistant extends PurRepository_MoveAndRename_IT.Assistant {
        @Override
        public JobMeta createNew() {
            return new JobMeta();
        }

        @Override
        String getType() {
            return "Job";
        }

        @Override
        void doRename(AbstractMeta meta, RepositoryDirectoryInterface destFolder, String newName) throws Exception {
            purRepository.renameJob(meta.getObjectId(), destFolder, newName);
        }

        @Override
        List<String> getNames(RepositoryDirectoryInterface dir) throws Exception {
            return Arrays.asList(purRepository.getJobNames(dir.getObjectId(), false));
        }
    }

    private class TransAssistant extends PurRepository_MoveAndRename_IT.Assistant {
        @Override
        public TransMeta createNew() {
            return new TransMeta();
        }

        @Override
        public String getType() {
            return "Trans";
        }

        @Override
        void doRename(AbstractMeta meta, RepositoryDirectoryInterface destFolder, String newName) throws Exception {
            purRepository.renameTransformation(meta.getObjectId(), destFolder, newName);
        }

        @Override
        List<String> getNames(RepositoryDirectoryInterface dir) throws Exception {
            return Arrays.asList(purRepository.getTransformationNames(dir.getObjectId(), false));
        }
    }
}

