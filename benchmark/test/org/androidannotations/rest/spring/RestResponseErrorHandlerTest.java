/**
 * Copyright (C) 2010-2016 eBusiness Information, Excilys Group
 * Copyright (C) 2016-2019 the AndroidAnnotations project
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
package org.androidannotations.rest.spring;


import java.io.IOException;
import org.androidannotations.testutils.AAProcessorTestHelper;
import org.junit.Test;


public class RestResponseErrorHandlerTest extends AAProcessorTestHelper {
    @Test
    public void clientWithValidResponseErrorHandlerCompiles() {
        CompileResult result = compileFiles(ClientWithValidResponseErrorHandler.class);
        assertCompilationSuccessful(result);
    }

    @Test
    public void clientWithNonResponseErrorHandlerDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ClientWithNonResponseErrorHandler.class);
        assertCompilationErrorOn(ClientWithNonResponseErrorHandler.class, "@Rest", result);
    }

    @Test
    public void clientWithWrongConstructorResponseErrorHandlerDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ClientWithWrongConstructorResponseErrorHandler.class);
        assertCompilationErrorOn(ClientWithWrongConstructorResponseErrorHandler.class, "@Rest", result);
    }

    @Test
    public void clientWithAbstractResponseErrorHandlerDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ClientWithAbstractResponseErrorHandler.class);
        assertCompilationErrorOn(ClientWithAbstractResponseErrorHandler.class, "@Rest", result);
    }

    @Test
    public void clientWithNonClassResponseErrorHandlerDoesNotCompile() throws IOException {
        CompileResult result = compileFiles(ClientWithNonClassResponseErrorHandler.class);
        assertCompilationErrorOn(ClientWithNonClassResponseErrorHandler.class, "@Rest", result);
    }
}

