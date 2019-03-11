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
import uk.gov.gchq.gaffer.commonutil.StringUtil;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;


public class FederatedOperationChainTest extends OperationTest<FederatedOperationChain> {
    @Test
    @Override
    public void shouldShallowCloneOperation() {
        // Given
        final OperationChain<CloseableIterable<? extends Element>> opChain = new OperationChain.Builder().first(new GetAllElements()).build();
        final FederatedOperationChain op = new FederatedOperationChain.Builder<>().operationChain(opChain).option("key", "value").build();
        // When
        final FederatedOperationChain clone = op.shallowClone();
        // Then
        Assert.assertNotSame(op.getOperationChain(), clone.getOperationChain());
        Assert.assertEquals(1, clone.getOperationChain().getOperations().size());
        Assert.assertEquals(GetAllElements.class, clone.getOperationChain().getOperations().get(0).getClass());
        Assert.assertEquals("value", clone.getOption("key"));
    }

    @Test
    public void shouldThrowAnErrorIfJsonDeserialiseWithoutOperationChain() {
        // Given
        final String json = String.format(("{%n" + (((("  \"class\" : \"uk.gov.gchq.gaffer.federatedstore.operation.FederatedOperationChain\",%n" + "  \"options\" : {%n") + "    \"key\" : \"value\"%n") + "  }%n") + "}")));
        // When / Then
        try {
            fromJson(StringUtil.toBytes(json));
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("operationChain is required"));
        }
    }

    @Test
    public void shouldJsonDeserialiseWithInvalidOperationChainClassName() {
        // Given
        final String json = String.format(("{%n" + (((((((((("  \"class\" : \"uk.gov.gchq.gaffer.federatedstore.operation.FederatedOperationChain\",%n" + "  \"operationChain\" : {%n") + "    \"class\" : \"uk.gov.gchq.gaffer.operation.OperationChainInvalidClassName\",%n") + "    \"operations\" : [ {%n") + "      \"class\" : \"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\"%n") + "    } ]%n") + "  },%n") + "  \"options\" : {%n") + "    \"key\" : \"value\"%n") + "  }%n") + "}")));
        // When / Then
        try {
            fromJson(StringUtil.toBytes(json));
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertTrue(e.getMessage().contains("Class name should be"));
        }
    }

    @Test
    public void shouldJsonDeserialiseWithOperationChainClassName() {
        // Given
        final String json = String.format(("{%n" + (((((((((("  \"class\" : \"uk.gov.gchq.gaffer.federatedstore.operation.FederatedOperationChain\",%n" + "  \"operationChain\" : {%n") + "    \"class\" : \"uk.gov.gchq.gaffer.operation.OperationChain\",%n") + "    \"operations\" : [ {%n") + "      \"class\" : \"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\"%n") + "    } ]%n") + "  },%n") + "  \"options\" : {%n") + "    \"key\" : \"value\"%n") + "  }%n") + "}")));
        // When
        final FederatedOperationChain deserialisedOp = fromJson(StringUtil.toBytes(json));
        // Then
        Assert.assertEquals(1, deserialisedOp.getOperationChain().getOperations().size());
        Assert.assertEquals(GetAllElements.class, deserialisedOp.getOperationChain().getOperations().get(0).getClass());
        Assert.assertEquals("value", deserialisedOp.getOption("key"));
    }

    @Test
    public void shouldJsonDeserialiseWithoutOperationChainClassName() {
        // Given
        final String json = String.format(("{%n" + ((((((((("  \"class\" : \"uk.gov.gchq.gaffer.federatedstore.operation.FederatedOperationChain\",%n" + "  \"operationChain\" : {%n") + "    \"operations\" : [ {%n") + "      \"class\" : \"uk.gov.gchq.gaffer.operation.impl.get.GetAllElements\"%n") + "    } ]%n") + "  },%n") + "  \"options\" : {%n") + "    \"key\" : \"value\"%n") + "  }%n") + "}")));
        // When
        final FederatedOperationChain deserialisedOp = fromJson(StringUtil.toBytes(json));
        // Then
        Assert.assertEquals(1, deserialisedOp.getOperationChain().getOperations().size());
        Assert.assertEquals(GetAllElements.class, deserialisedOp.getOperationChain().getOperations().get(0).getClass());
        Assert.assertEquals("value", deserialisedOp.getOption("key"));
    }
}

