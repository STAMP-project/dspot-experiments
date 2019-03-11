/**
 * Copyright (c) 2007 Mockito contributors
 * This program is made available under the terms of the MIT License.
 */
package org.mockitousage.bugs;


import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockitoutil.TestBase;


// see bug 116
public class AIOOBExceptionWithAtLeastTest extends TestBase {
    interface IProgressMonitor {
        void beginTask(String s, int i);

        void worked(int i);

        void done();
    }

    @Test
    public void testCompleteProgress() throws Exception {
        AIOOBExceptionWithAtLeastTest.IProgressMonitor progressMonitor = Mockito.mock(AIOOBExceptionWithAtLeastTest.IProgressMonitor.class);
        progressMonitor.beginTask("foo", 12);
        progressMonitor.worked(10);
        progressMonitor.done();
        Mockito.verify(progressMonitor).beginTask(ArgumentMatchers.anyString(), ArgumentMatchers.anyInt());
        Mockito.verify(progressMonitor, Mockito.atLeastOnce()).worked(ArgumentMatchers.anyInt());
    }
}

