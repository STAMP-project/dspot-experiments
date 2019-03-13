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
package uk.gov.gchq.gaffer.named.operation;


import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;


public class AddNamedOperationTest extends OperationTest<AddNamedOperation> {
    public static final String USER = "User";

    private static final OperationChain OPERATION_CHAIN = new OperationChain.Builder().first(new GetAdjacentIds.Builder().input(new EntitySeed("seed")).build()).build();

    @Test
    public void shouldGetOperationsWithDefaultParameters() {
        // Given
        final AddNamedOperation addNamedOperation = new AddNamedOperation.Builder().operationChain("{\"operations\":[{\"class\": \"uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds\", \"input\": [{\"vertex\": \"${testParameter}\", \"class\": \"uk.gov.gchq.gaffer.operation.data.EntitySeed\"}]}]}").description("Test Named Operation").name("Test").overwrite(false).readAccessRoles(AddNamedOperationTest.USER).writeAccessRoles(AddNamedOperationTest.USER).parameter("testParameter", new ParameterDetail.Builder().description("the seed").defaultValue("seed1").valueClass(String.class).required(false).build()).score(2).build();
        // When
        Collection<Operation> operations = addNamedOperation.getOperations();
        // Then
        Assert.assertEquals(Collections.singletonList(GetAdjacentIds.class), operations.stream().map(( o) -> o.getClass()).collect(Collectors.toList()));
        final GetAdjacentIds nestedOp = ((GetAdjacentIds) (operations.iterator().next()));
        final List<? extends EntityId> input = Lists.newArrayList(nestedOp.getInput());
        Assert.assertEquals(Collections.singletonList(new EntitySeed("seed1")), input);
    }

    @Test
    public void shouldGetOperationsWhenNoDefaultParameter() {
        // Given
        final AddNamedOperation addNamedOperation = new AddNamedOperation.Builder().operationChain("{\"operations\":[{\"class\": \"uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds\", \"input\": [{\"vertex\": \"${testParameter}\", \"class\": \"uk.gov.gchq.gaffer.operation.data.EntitySeed\"}]}]}").description("Test Named Operation").name("Test").overwrite(false).readAccessRoles(AddNamedOperationTest.USER).writeAccessRoles(AddNamedOperationTest.USER).parameter("testParameter", new ParameterDetail.Builder().description("the seed").valueClass(String.class).required(false).build()).score(2).build();
        // When
        Collection<Operation> operations = addNamedOperation.getOperations();
        // Then
        Assert.assertEquals(Collections.singletonList(GetAdjacentIds.class), operations.stream().map(( o) -> o.getClass()).collect(Collectors.toList()));
        final GetAdjacentIds nestedOp = ((GetAdjacentIds) (operations.iterator().next()));
        final List<? extends EntityId> input = Lists.newArrayList(nestedOp.getInput());
        Assert.assertEquals(Collections.singletonList(new EntitySeed(null)), input);
    }
}

