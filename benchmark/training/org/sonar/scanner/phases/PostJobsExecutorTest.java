/**
 * SonarQube
 * Copyright (C) 2009-2019 SonarSource SA
 * mailto:info AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package org.sonar.scanner.phases;


import java.util.Arrays;
import org.junit.Test;
import org.mockito.Mockito;
import org.sonar.scanner.bootstrap.PostJobExtensionDictionnary;
import org.sonar.scanner.postjob.PostJobWrapper;
import org.sonar.scanner.postjob.PostJobsExecutor;


public class PostJobsExecutorTest {
    private PostJobsExecutor executor;

    private PostJobExtensionDictionnary selector = Mockito.mock(PostJobExtensionDictionnary.class);

    private PostJobWrapper job1 = Mockito.mock(PostJobWrapper.class);

    private PostJobWrapper job2 = Mockito.mock(PostJobWrapper.class);

    @Test
    public void should_execute_post_jobs() {
        Mockito.when(selector.selectPostJobs()).thenReturn(Arrays.asList(job1, job2));
        Mockito.when(job1.shouldExecute()).thenReturn(true);
        executor.execute();
        Mockito.verify(job1).shouldExecute();
        Mockito.verify(job2).shouldExecute();
        Mockito.verify(job1).execute();
        Mockito.verifyNoMoreInteractions(job1, job2);
    }
}

