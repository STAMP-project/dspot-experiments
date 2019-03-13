/**
 * Copyright 2016-2019 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.graph.hook;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.user.User;


public class Log4jLoggerTest extends JSONSerialisationTest<Log4jLogger> {
    @Test
    public void shouldReturnResultWithoutModification() {
        // Given
        final Log4jLogger hook = getTestObject();
        final Object result = Mockito.mock(Object.class);
        final OperationChain opChain = new OperationChain.Builder().first(new uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects()).build();
        final User user = new User.Builder().opAuths("NoScore").build();
        // When
        final Object returnedResult = hook.postExecute(result, opChain, new uk.gov.gchq.gaffer.store.Context(new User()));
        // Then
        Assert.assertSame(result, returnedResult);
    }
}

