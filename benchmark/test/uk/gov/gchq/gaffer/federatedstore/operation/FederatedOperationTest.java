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
package uk.gov.gchq.gaffer.federatedstore.operation;


import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;


public class FederatedOperationTest {
    @Test
    public void shouldReturnTrueWhenOpChainHasFederatedOps() {
        // Given
        final OperationChain<?> opChain = new OperationChain.Builder().first(Mockito.mock(Operation.class)).then(Mockito.mock(FederatedOperation.class)).then(Mockito.mock(GetElements.class)).build();
        // When
        final boolean result = FederatedOperation.hasFederatedOperations(opChain);
        // Then
        Assert.assertTrue(result);
    }

    @Test
    public void shouldReturnFalseWhenOpChainDoesNotHaveFederatedOps() {
        // Given
        final OperationChain<?> opChain = new OperationChain.Builder().first(Mockito.mock(Operation.class)).then(Mockito.mock(GetElements.class)).build();
        // When
        final boolean result = FederatedOperation.hasFederatedOperations(opChain);
        // Then
        Assert.assertFalse(result);
    }
}

