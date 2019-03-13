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
package org.pentaho.di.job.entries.shell;


import junit.framework.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;


public class JobEntryShellTest {
    @Mock
    private JobEntryShell jobEntryShellMock;

    /**
     * tests if Windows's EOL characters is replaced.
     *
     * @see <a href="http://jira.pentaho.com/browse/PDI-12176">Jira issue</a>
     */
    @Test
    public void replaceWinEOLtest() {
        // string is shell content from PDI-12176
        String content = "#!/bin/bash\r\n" + ("\r\n" + "echo `date` > /home/pentaho/test_output/output.txt");
        Mockito.doCallRealMethod().when(jobEntryShellMock).replaceWinEOL(ArgumentMatchers.anyString());
        content = jobEntryShellMock.replaceWinEOL(content);
        Mockito.verify(jobEntryShellMock).replaceWinEOL(ArgumentMatchers.anyString());
        String assertionFailedMessage = "Windows EOL character is detected";
        // shouldn't contains CR and CR+LF characters
        Assert.assertFalse(assertionFailedMessage, content.contains("\r\n"));
        Assert.assertFalse(assertionFailedMessage, content.contains("\r"));
    }
}

