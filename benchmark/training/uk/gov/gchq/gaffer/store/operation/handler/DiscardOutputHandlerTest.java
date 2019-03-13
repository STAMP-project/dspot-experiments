/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.store.operation.handler;


import org.hamcrest.CoreMatchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.store.Context;


public class DiscardOutputHandlerTest {
    @Test
    public void shouldDiscardOutput() throws OperationException {
        // Given
        final DiscardOutputHandler handler = new DiscardOutputHandler();
        final DiscardOutput operation = Mockito.mock(DiscardOutput.class);
        BDDMockito.given(operation.getInput()).willReturn(null);
        // When
        final Void results = handler.doOperation(operation, new Context(), null);
        // Then
        Assert.assertThat(results, Is.is(CoreMatchers.nullValue()));
    }
}

