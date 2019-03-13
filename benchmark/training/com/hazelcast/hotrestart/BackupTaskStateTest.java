/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
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
package com.hazelcast.hotrestart;


import BackupTaskState.FAILURE;
import BackupTaskState.IN_PROGRESS;
import BackupTaskState.NOT_STARTED;
import BackupTaskState.NO_TASK;
import BackupTaskState.SUCCESS;
import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class BackupTaskStateTest {
    @Test
    public void testBackupState() throws Exception {
        Assert.assertFalse(NO_TASK.isDone());
        Assert.assertFalse(NOT_STARTED.isDone());
        Assert.assertFalse(IN_PROGRESS.isDone());
        Assert.assertTrue(FAILURE.isDone());
        Assert.assertTrue(SUCCESS.isDone());
        Assert.assertFalse(NO_TASK.inProgress());
        Assert.assertTrue(NOT_STARTED.inProgress());
        Assert.assertTrue(IN_PROGRESS.inProgress());
        Assert.assertFalse(FAILURE.inProgress());
        Assert.assertFalse(SUCCESS.inProgress());
    }
}

