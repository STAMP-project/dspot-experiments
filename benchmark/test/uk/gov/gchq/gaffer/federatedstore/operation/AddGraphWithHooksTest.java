/**
 * Copyright 2018-2019 Crown Copyright
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
import uk.gov.gchq.gaffer.federatedstore.operation.AddGraphWithHooks.Builder;
import uk.gov.gchq.gaffer.operation.OperationTest;


public class AddGraphWithHooksTest extends OperationTest<AddGraphWithHooks> {
    private static final String EXPECTED_GRAPH_ID = "testGraphID";

    @Test
    public void shouldShallowCloneOperationWithNulls() {
        final AddGraphWithHooks a = new Builder().graphId(null).parentPropertiesId(null).parentSchemaIds(null).schema(null).graphAuths(null).storeProperties(null).build();
        final AddGraphWithHooks b = a.shallowClone();
        Assert.assertEquals(a.getGraphId(), b.getGraphId());
        Assert.assertEquals(a.getStoreProperties(), b.getStoreProperties());
        Assert.assertEquals(a.getSchema(), b.getSchema());
        Assert.assertEquals(a.getGraphAuths(), b.getGraphAuths());
    }
}

