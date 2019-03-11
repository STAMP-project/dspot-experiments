/**
 * Copyright 2006-2018 the original author or authors.
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
package org.springframework.batch.core.launch.support;


import ExitCodeMapper.JOB_NOT_PROVIDED;
import ExitCodeMapper.JVM_EXITCODE_COMPLETED;
import ExitCodeMapper.JVM_EXITCODE_GENERIC_ERROR;
import ExitCodeMapper.JVM_EXITCODE_JOB_ERROR;
import ExitCodeMapper.NO_SUCH_JOB;
import ExitStatus.COMPLETED;
import ExitStatus.FAILED;
import junit.framework.TestCase;


public class SimpleJvmExitCodeMapperTests extends TestCase {
    private SimpleJvmExitCodeMapper ecm;

    private SimpleJvmExitCodeMapper ecm2;

    public void testGetExitCodeWithPredefinedCodes() {
        TestCase.assertEquals(ecm.intValue(COMPLETED.getExitCode()), JVM_EXITCODE_COMPLETED);
        TestCase.assertEquals(ecm.intValue(FAILED.getExitCode()), JVM_EXITCODE_GENERIC_ERROR);
        TestCase.assertEquals(ecm.intValue(JOB_NOT_PROVIDED), JVM_EXITCODE_JOB_ERROR);
        TestCase.assertEquals(ecm.intValue(NO_SUCH_JOB), JVM_EXITCODE_JOB_ERROR);
    }

    public void testGetExitCodeWithPredefinedCodesOverridden() {
        System.out.println(ecm2.intValue(COMPLETED.getExitCode()));
        TestCase.assertEquals(ecm2.intValue(COMPLETED.getExitCode()), (-1));
        TestCase.assertEquals(ecm2.intValue(FAILED.getExitCode()), (-2));
        TestCase.assertEquals(ecm2.intValue(JOB_NOT_PROVIDED), (-3));
        TestCase.assertEquals(ecm2.intValue(NO_SUCH_JOB), (-3));
    }

    public void testGetExitCodeWithCustomCode() {
        TestCase.assertEquals(ecm.intValue("MY_CUSTOM_CODE"), 3);
    }

    public void testGetExitCodeWithDefaultCode() {
        TestCase.assertEquals(ecm.intValue("UNDEFINED_CUSTOM_CODE"), JVM_EXITCODE_GENERIC_ERROR);
    }
}

