/**
 * All content copyright Terracotta, Inc., unless otherwise indicated. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.quartz.utils;


import junit.framework.TestCase;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;


/**
 *
 *
 * @author Alex Snaps
 */
public class ClassUtilsTest extends TestCase {
    public void testIsAnnotationPresentOnSuperClass() throws Exception {
        TestCase.assertTrue(ClassUtils.isAnnotationPresent(ClassUtilsTest.BaseJob.class, DisallowConcurrentExecution.class));
        TestCase.assertFalse(ClassUtils.isAnnotationPresent(ClassUtilsTest.BaseJob.class, PersistJobDataAfterExecution.class));
        TestCase.assertTrue(ClassUtils.isAnnotationPresent(ClassUtilsTest.ExtendedJob.class, DisallowConcurrentExecution.class));
        TestCase.assertFalse(ClassUtils.isAnnotationPresent(ClassUtilsTest.ExtendedJob.class, PersistJobDataAfterExecution.class));
        TestCase.assertTrue(ClassUtils.isAnnotationPresent(ClassUtilsTest.ReallyExtendedJob.class, DisallowConcurrentExecution.class));
        TestCase.assertTrue(ClassUtils.isAnnotationPresent(ClassUtilsTest.ReallyExtendedJob.class, PersistJobDataAfterExecution.class));
    }

    @DisallowConcurrentExecution
    private static class BaseJob implements Job {
        public void execute(final JobExecutionContext context) throws JobExecutionException {
            System.out.println(this.getClass().getSimpleName());
        }
    }

    private static class ExtendedJob extends ClassUtilsTest.BaseJob {}

    @PersistJobDataAfterExecution
    private static class ReallyExtendedJob extends ClassUtilsTest.ExtendedJob {}
}

