/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed To in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.androidannotations.receiver;


import java.io.IOException;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Test;


public class ReceiverRegistrationTest extends AAProcessorTestHelper {
    @Test
    public void activityWithValidReceiverAnnotationCompiles() throws IOException {
        CompileResult result = compileFiles(ActivityWithValidReceiver.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void activityWithInvalidRegisterAtDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ActivityWithInvalidRegisterAt.class);
        assertCompilationErrorOn(ActivityWithInvalidRegisterAt.class, "@Receiver", result);
    }

    @Test
    public void activityWithTwoMethodWithSameNameDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ActivityWithTwoSameNameMethod.class);
        assertCompilationErrorOn(ActivityWithTwoSameNameMethod.class, "@Receiver", result);
    }

    @Test
    public void fragmentWithValidReceiverAnnotationCompiles() throws IOException {
        CompileResult result = compileFiles(FragmentWithValidReceiver.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void serviceWithValidReceiverAnnotationCompiles() throws IOException {
        CompileResult result = compileFiles(ServiceWithValidReceiver.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void serviceWithInvalidRegisterAtDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ServiceWithInvalidReceiver.class);
        assertCompilationErrorOn(ServiceWithInvalidReceiver.class, "@Receiver", result);
        assertCompilationErrorCount(3, result);
    }

    @Test
    public void viewWithValidReceiverAnnotationCompiles() throws IOException {
        CompileResult result = compileFiles(ViewWithValidReceiver.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void viewWithInvalidRegisterAtDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ViewWithInvalidReceiver.class);
        assertCompilationErrorOn(ViewWithInvalidReceiver.class, "@Receiver", result);
        assertCompilationErrorCount(2, result);
    }
}

