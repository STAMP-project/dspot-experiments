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
package uk.gov.gchq.gaffer.graph.hook;


import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.TestUnmodifiableOperationsImpl;
import uk.gov.gchq.gaffer.operation.impl.Count;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
import uk.gov.gchq.gaffer.operation.impl.If;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.SplitStoreFromFile;
import uk.gov.gchq.gaffer.operation.impl.Validate;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.operation.impl.output.ToVertices;
import uk.gov.gchq.gaffer.operation.util.Conditional;
import uk.gov.gchq.gaffer.store.Context;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.impl.predicate.Exists;


public class AddOperationsToChainTest extends GraphHookTest<AddOperationsToChain> {
    private static final String ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH = "addOperationsToChain.json";

    public AddOperationsToChainTest() {
        super(AddOperationsToChain.class);
    }

    @Test
    public void shouldAddAllOperationsToGetWalksOperation() throws SerialisationException {
        // Given
        final AddOperationsToChain hook = new AddOperationsToChain();
        final Map<String, List<Operation>> after = new HashMap<>();
        after.put(GetElements.class.getName(), Lists.newArrayList(new Limit()));
        hook.setAfter(after);
        hook.setEnd(Lists.newArrayList(new Limit()));
        final GetElements getElements = new GetElements();
        final Limit limit = new Limit();
        final OperationChain getWalksOperations = new OperationChain.Builder().first(getElements).build();
        final GetWalks getWalks = new GetWalks.Builder().operations(getWalksOperations).build();
        final OperationChain opChain = new OperationChain.Builder().first(getWalks).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final GetWalks expectedGetWalks = new GetWalks.Builder().operations(new OperationChain(getElements, limit)).build();
        final OperationChain expectedOpChain = new OperationChain.Builder().first(expectedGetWalks).then(limit).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsWithNoAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit();
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(getElements).then(getAllElements).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(splitStore).then(validate).then(getAdjacentIds).then(count).then(discardOutput).then(countGroups).then(getElements).then(getAllElements).then(limit).then(validate).then(count).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsWithFirstAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        User user = new User.Builder().opAuths("auth1", "auth2").build();
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(getElements).then(getAllElements).build();
        // When
        hook.preExecute(opChain, new Context(user));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(getAdjacentIds).then(getElements).then(getAllElements).then(splitStore).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsWithSecondAuthsGivenPath() throws IOException {
        // Given
        AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        User user = new User.Builder().opAuths("auth2").build();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(getElements).then(getAllElements).build();
        // When
        hook.preExecute(opChain, new Context(user));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(validate).then(getAdjacentIds).then(countGroups).then(getElements).then(getAllElements).then(splitStore).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddAllOperationsGivenJson() throws IOException {
        // Given
        final byte[] bytes;
        try (final InputStream inputStream = StreamUtil.openStream(getClass(), AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH)) {
            bytes = IOUtils.toByteArray(inputStream);
        }
        final AddOperationsToChain hook = fromJson(bytes);
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit();
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(getElements).then(getAllElements).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(splitStore).then(validate).then(getAdjacentIds).then(count).then(discardOutput).then(countGroups).then(getElements).then(getAllElements).then(limit).then(validate).then(count).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldThrowExceptionWhenAddingNullExtraOperation() throws IOException {
        // Given
        final String nullTestJson = "{\"class\": \"uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain\", \"start\":[{\"class\": null}]}";
        // When / Then
        try {
            fromJson(nullTestJson.getBytes());
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("'null'"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingEmptyExtraOperation() throws IOException {
        // Given
        final String emptyTestJson = "{\"class\": \"uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain\", \"start\":[{\"class\": \"\"}]}";
        // When / Then
        try {
            fromJson(emptyTestJson.getBytes());
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("''"));
        }
    }

    @Test
    public void shouldThrowExceptionWhenAddingFalseExtraOperation() throws IOException {
        // Given
        final String falseOperationTestJson = "{\"class\": \"uk.gov.gchq.gaffer.graph.hook.AddOperationsToChain\", \"start\":[{\"class\": \"this.Operation.Doesnt.Exist\"}]}";
        // When / Then
        try {
            fromJson(falseOperationTestJson.getBytes());
            Assert.fail("Exception expected");
        } catch (final RuntimeException e) {
            Assert.assertTrue(e.getMessage(), e.getMessage().contains("'this.Operation.Doesnt.Exist'"));
        }
    }

    @Test
    public void shouldClearListWhenAddingOperations() throws IOException {
        // Given
        final AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        hook.setBefore(null);
        hook.setAfter(null);
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation count = new Count();
        Operation getElements = new GetElements();
        final OperationChain opChain = new OperationChain.Builder().first(getElements).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(splitStore).then(getElements).then(count).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldHandleNestedOperationChain() throws SerialisationException {
        // Given
        AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count();
        Operation countGroups = new CountGroups();
        Operation getElements = new GetElements();
        If ifOp = new If.Builder<>().conditional(new Conditional(new Exists(), new GetElements())).then(new GetElements()).otherwise(new GetAllElements()).build();
        Operation getAllElements = new GetAllElements();
        Operation limit = new Limit();
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(new OperationChain.Builder().first(getElements).then(getAllElements).build()).then(ifOp).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(splitStore).then(validate).then(getAdjacentIds).then(count).then(discardOutput).then(((Operation) (new OperationChain.Builder().first(countGroups).then(getElements).then(getAllElements).then(limit).then(validate).build()))).then(new If.Builder<>().conditional(new Conditional(new Exists(), new OperationChain(new CountGroups(), new GetElements()))).then(new OperationChain(new CountGroups(), new GetElements())).otherwise(new OperationChain(new GetAllElements(), new Limit(), new Validate())).build()).then(new Count()).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldHandleIfOperationWithNoConditionalOrOtherwise() throws SerialisationException {
        // Given
        AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        If ifOp = new If.Builder<>().then(new GetElements()).build();
        final OperationChain opChain = new OperationChain.Builder().first(ifOp).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(splitStore).then(new If.Builder<>().then(new OperationChain(new CountGroups(), new GetElements())).build()).then(new Count()).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldFailQuietlyIfNestedOperationsCannotBeModified() throws SerialisationException {
        // Given
        AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        Operation discardOutput = new DiscardOutput();
        Operation splitStore = new SplitStoreFromFile();
        Operation validate = new Validate();
        Operation getAdjacentIds = new GetAdjacentIds();
        Operation count = new Count();
        Operation getElements = new GetElements();
        Operation getAllElements = new GetAllElements();
        TestUnmodifiableOperationsImpl nestedUnmodifiableOps = new TestUnmodifiableOperationsImpl(Arrays.asList(getAllElements, getElements));
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(nestedUnmodifiableOps).build();
        // When
        hook.preExecute(opChain, new Context(new User()));
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(discardOutput).then(splitStore).then(validate).then(getAdjacentIds).then(count).then(discardOutput).then(nestedUnmodifiableOps).then(count).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldAddIfOperation() throws SerialisationException {
        // Given
        final GetWalks getWalks = new GetWalks();
        final uk.gov.gchq.gaffer.operation.impl.Map map = new uk.gov.gchq.gaffer.operation.impl.Map();
        final ToVertices toVertices = new ToVertices();
        final ToSet toSet = new ToSet();
        final Exists exists = new Exists();
        final Limit limit = new Limit();
        final GetAllElements getAllElements = new GetAllElements();
        final GetElements getElements = new GetElements();
        final Conditional conditional = new Conditional();
        conditional.setPredicate(exists);
        final If ifOp = new If.Builder<>().conditional(conditional).then(getElements).otherwise(getAllElements).build();
        final AddOperationsToChain hook = new AddOperationsToChain();
        final Map<String, List<Operation>> after = new HashMap<>();
        final List<Operation> afterOps = new LinkedList<>();
        afterOps.add(ifOp);
        afterOps.add(limit);
        after.put("uk.gov.gchq.gaffer.operation.impl.output.ToSet", afterOps);
        hook.setAfter(after);
        final OperationChain opChain = new OperationChain.Builder().first(getWalks).then(map).then(toVertices).then(toSet).build();
        // When
        hook.preExecute(opChain, new Context());
        // Then
        final OperationChain expectedOpChain = new OperationChain.Builder().first(getWalks).then(map).then(toVertices).then(toSet).then(ifOp).then(limit).build();
        JsonAssert.assertEquals(JSONSerialiser.serialise(expectedOpChain), JSONSerialiser.serialise(opChain));
    }

    @Test
    public void shouldReturnClonedOperations() throws IOException {
        // Given
        final AddOperationsToChain hook = fromJson(AddOperationsToChainTest.ADD_OPERATIONS_TO_CHAIN_RESOURCE_PATH);
        // When / Then
        assertClonedOperations(hook.getStart(), hook.getStart());
        assertClonedOperations(hook.getBefore(), hook.getBefore());
        assertClonedOperations(hook.getAfter(), hook.getAfter());
        assertClonedOperations(hook.getEnd(), hook.getEnd());
    }
}

