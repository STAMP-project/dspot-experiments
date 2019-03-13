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


import IAbsSecurityProvider.CREATE_CONTENT_ACTION;
import IAbsSecurityProvider.EXECUTE_CONTENT_ACTION;
import IAbsSecurityProvider.SCHEDULE_CONTENT_ACTION;
import RepositoryOperation.EXECUTE_JOB;
import RepositoryOperation.EXECUTE_TRANSFORMATION;
import RepositoryOperation.MODIFY_JOB;
import RepositoryOperation.MODIFY_TRANSFORMATION;
import RepositoryOperation.SCHEDULE_JOB;
import RepositoryOperation.SCHEDULE_TRANSFORMATION;
import org.junit.Test;
import org.pentaho.di.core.exception.KettleException;


public class AbsSecurityProviderTest {
    private AbsSecurityProvider provider;

    @Test(expected = KettleException.class)
    public void exceptionThrown_WhenOperationNotAllowed_ExecuteOperation() throws Exception {
        setOperationPermissions(EXECUTE_CONTENT_ACTION, false);
        provider.validateAction(EXECUTE_TRANSFORMATION);
    }

    @Test(expected = KettleException.class)
    public void exceptionThrown_WhenOperationNotAllowed_ScheduleOperation() throws Exception {
        setOperationPermissions(SCHEDULE_CONTENT_ACTION, false);
        provider.validateAction(SCHEDULE_JOB);
    }

    @Test(expected = KettleException.class)
    public void exceptionThrown_WhenOperationNotAllowed_CreateOperation() throws Exception {
        setOperationPermissions(CREATE_CONTENT_ACTION, false);
        provider.validateAction(MODIFY_JOB);
    }

    @Test
    public void noExceptionThrown_WhenOperationIsAllowed_ScheduleOperation() throws Exception {
        setOperationPermissions(EXECUTE_CONTENT_ACTION, true);
        provider.validateAction(EXECUTE_JOB);
    }

    @Test
    public void noExceptionThrown_WhenOperationIsAllowed_CreateOperation() throws Exception {
        setOperationPermissions(SCHEDULE_CONTENT_ACTION, true);
        provider.validateAction(SCHEDULE_TRANSFORMATION);
    }

    @Test
    public void noExceptionThrown_WhenOperationIsAllowed_ExecuteOperation() throws Exception {
        setOperationPermissions(CREATE_CONTENT_ACTION, true);
        provider.validateAction(MODIFY_TRANSFORMATION);
    }
}

