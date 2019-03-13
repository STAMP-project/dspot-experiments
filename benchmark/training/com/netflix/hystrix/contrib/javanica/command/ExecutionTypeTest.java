/**
 * Copyright 2015 Netflix, Inc.
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
package com.netflix.hystrix.contrib.javanica.command;


import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


@RunWith(Parameterized.class)
public class ExecutionTypeTest {
    @Test
    public void should_return_correct_execution_type() throws Exception {
        Assert.assertEquals(("Unexpected execution type for method return type: " + (methodReturnType)), expectedType, ExecutionType.getExecutionType(methodReturnType));
    }

    private final Class<?> methodReturnType;

    private final ExecutionType expectedType;

    public ExecutionTypeTest(final Class<?> methodReturnType, final ExecutionType expectedType) {
        this.methodReturnType = methodReturnType;
        this.expectedType = expectedType;
    }
}

