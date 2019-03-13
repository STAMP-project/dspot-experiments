/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
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
package org.pentaho.di.job.entries.writetolog;


import LogLevel.ERROR;
import LogLevel.MINIMAL;
import LogLevel.NOTHING;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.di.job.Job;


/**
 *
 *
 * @author Christopher Songer
 */
public class JobEntryWriteToLogTest {
    private Job parentJob;

    private JobEntryWriteToLog jobEntry;

    @Test
    public void errorMessageIsNotLoggedWhenParentJobLogLevelIsNothing() {
        verifyErrorMessageForParentJobLogLevel(NOTHING, Mockito.never());
    }

    @Test
    public void errorMessageIsLoggedWhenParentJobLogLevelIsError() {
        verifyErrorMessageForParentJobLogLevel(ERROR, Mockito.times(1));
    }

    @Test
    public void errorMessageIsLoggedWhenParentJobLogLevelIsMinimal() {
        verifyErrorMessageForParentJobLogLevel(MINIMAL, Mockito.times(1));
    }
}

