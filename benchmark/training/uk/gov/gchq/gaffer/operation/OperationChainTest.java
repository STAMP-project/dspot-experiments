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
package uk.gov.gchq.gaffer.operation;


import com.fasterxml.jackson.core.type.TypeReference;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.GroupCounts;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationChain.Builder;
import uk.gov.gchq.gaffer.operation.data.EntitySeed;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.If;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.OperationImpl;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.ExportToGafferResultCache;
import uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet;
import uk.gov.gchq.gaffer.operation.impl.export.set.GetSetExport;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.operation.io.Input;
import uk.gov.gchq.gaffer.operation.io.MultiInput;
import uk.gov.gchq.gaffer.operation.io.Output;


public class OperationChainTest extends OperationsTest<OperationChain> {
    @Test
    public void shouldSerialiseAndDeserialiseOperationChain() throws SerialisationException {
        // Given
        final OperationChain opChain = new Builder().first(new OperationImpl()).then(new OperationImpl()).build();
        // When
        byte[] json = JSONSerialiser.serialise(opChain, true);
        final OperationChain deserialisedOp = JSONSerialiser.deserialise(json, OperationChain.class);
        // Then
        Assert.assertNotNull(deserialisedOp);
        Assert.assertEquals(2, deserialisedOp.getOperations().size());
        Assert.assertEquals(OperationImpl.class, deserialisedOp.getOperations().get(0).getClass());
        Assert.assertEquals(OperationImpl.class, deserialisedOp.getOperations().get(1).getClass());
    }

    @Test
    public void shouldBuildOperationChain() {
        // Given
        final AddElements addElements1 = Mockito.mock(AddElements.class);
        final AddElements addElements2 = Mockito.mock(AddElements.class);
        final GetAdjacentIds getAdj1 = Mockito.mock(GetAdjacentIds.class);
        final GetAdjacentIds getAdj2 = Mockito.mock(GetAdjacentIds.class);
        final GetAdjacentIds getAdj3 = Mockito.mock(GetAdjacentIds.class);
        final GetElements getElements1 = Mockito.mock(GetElements.class);
        final GetElements getElements2 = Mockito.mock(GetElements.class);
        final GetAllElements getAllElements = Mockito.mock(GetAllElements.class);
        final DiscardOutput discardOutput = Mockito.mock(DiscardOutput.class);
        final GetJobDetails getJobDetails = Mockito.mock(GetJobDetails.class);
        final GenerateObjects<EntityId> generateEntitySeeds = Mockito.mock(GenerateObjects.class);
        final Limit<Element> limit = Mockito.mock(Limit.class);
        final ToSet<Element> deduplicate = Mockito.mock(ToSet.class);
        final CountGroups countGroups = Mockito.mock(CountGroups.class);
        final ExportToSet<GroupCounts> exportToSet = Mockito.mock(ExportToSet.class);
        final ExportToGafferResultCache<CloseableIterable<? extends Element>> exportToGafferCache = Mockito.mock(ExportToGafferResultCache.class);
        final If<Iterable<? extends EntityId>, Iterable<? extends EntityId>> ifOp = Mockito.mock(If.class);
        // When
        final OperationChain<JobDetail> opChain = new Builder().first(addElements1).then(getAdj1).then(getAdj2).then(getElements1).then(generateEntitySeeds).then(getAdj3).then(ifOp).then(getElements2).then(deduplicate).then(limit).then(countGroups).then(exportToSet).then(discardOutput).then(getAllElements).then(exportToGafferCache).then(addElements2).then(getJobDetails).build();
        // Then
        Assert.assertArrayEquals(new Operation[]{ addElements1, getAdj1, getAdj2, getElements1, generateEntitySeeds, getAdj3, ifOp, getElements2, deduplicate, limit, countGroups, exportToSet, discardOutput, getAllElements, exportToGafferCache, addElements2, getJobDetails }, opChain.getOperationArray());
    }

    @Test
    public void shouldBuildOperationChainWithTypeUnsafe() {
        // When
        final GetAdjacentIds getAdjIds1 = new GetAdjacentIds();
        final ExportToSet<CloseableIterable<? extends EntityId>> exportToSet1 = new ExportToSet();
        final DiscardOutput discardOutput1 = new DiscardOutput();
        final GetSetExport getSetExport1 = new GetSetExport();
        final GetAdjacentIds getAdjIds2 = new GetAdjacentIds();
        final ExportToSet<CloseableIterable<? extends EntityId>> exportToSet2 = new ExportToSet();
        final DiscardOutput discardOutput2 = new DiscardOutput();
        final GetSetExport getSetExport2 = new GetSetExport();
        final OperationChain<CloseableIterable<? extends EntityId>> opChain = // we can use the type unsafe here as we know the output from the set export will be an Iterable of EntityIds
        new Builder().first(getAdjIds1).then(exportToSet1).then(discardOutput1).then(getSetExport1).thenTypeUnsafe(getAdjIds2).then(exportToSet2).then(discardOutput2).then(getSetExport2).buildTypeUnsafe();// again we can use the type unsafe here as we know the output from the set export will be an Iterable of EntityIds

        // Then
        Assert.assertArrayEquals(new Operation[]{ getAdjIds1, exportToSet1, discardOutput1, getSetExport1, getAdjIds2, exportToSet2, discardOutput2, getSetExport2 }, opChain.getOperationArray());
    }

    @Test
    public void shouldBuildOperationChainWithSingleOperation() throws SerialisationException {
        // Given
        final GetAdjacentIds getAdjacentIds = Mockito.mock(GetAdjacentIds.class);
        // When
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).build();
        // Then
        Assert.assertEquals(1, opChain.getOperations().size());
        Assert.assertSame(getAdjacentIds, opChain.getOperations().get(0));
    }

    @Test
    public void shouldBuildOperationChain_AdjEntitySeedsThenElements() throws SerialisationException {
        // Given
        final GetAdjacentIds getAdjacentIds = Mockito.mock(GetAdjacentIds.class);
        final GetElements getEdges = Mockito.mock(GetElements.class);
        // When
        final OperationChain opChain = new OperationChain.Builder().first(getAdjacentIds).then(getEdges).build();
        // Then
        Assert.assertEquals(2, opChain.getOperations().size());
        Assert.assertSame(getAdjacentIds, opChain.getOperations().get(0));
        Assert.assertSame(getEdges, opChain.getOperations().get(1));
    }

    @Test
    public void shouldDetermineOperationChainOutputType() {
        // Given
        final Operation operation1 = Mockito.mock(Operation.class);
        final GetElements operation2 = Mockito.mock(GetElements.class);
        final TypeReference typeRef = Mockito.mock(TypeReference.class);
        BDDMockito.given(operation2.getOutputTypeReference()).willReturn(typeRef);
        // When
        final OperationChain opChain = new OperationChain.Builder().first(operation1).then(operation2).build();
        // When / Then
        Assert.assertSame(typeRef, opChain.getOutputTypeReference());
    }

    @Test
    public void shouldCloseAllOperationInputs() throws IOException {
        // Given
        final Operation[] operations = new Operation[]{ Mockito.mock(Operation.class), Mockito.mock(Input.class), Mockito.mock(Input.class), Mockito.mock(MultiInput.class), Mockito.mock(Input.class) };
        // When
        final OperationChain opChain = new OperationChain(Arrays.asList(operations));
        // When
        opChain.close();
        // Then
        for (final Operation operation : operations) {
            Mockito.verify(operation).close();
        }
    }

    @Test
    public void shouldFlattenNestedOperationChain() {
        // Given
        final AddElements addElements = Mockito.mock(AddElements.class);
        final GetElements getElements = Mockito.mock(GetElements.class);
        final Limit<Element> limit = Mockito.mock(Limit.class);
        final OperationChain opChain1 = new OperationChain.Builder().first(addElements).then(getElements).build();
        final OperationChain<?> opChain2 = new OperationChain.Builder().first(opChain1).then(limit).build();
        // When
        final List<Operation> operations = opChain2.flatten();
        // Then
        final Operation first = operations.get(0);
        final Operation second = operations.get(1);
        final Operation third = operations.get(2);
        MatcherAssert.assertThat(first, IsInstanceOf.instanceOf(AddElements.class));
        MatcherAssert.assertThat(second, IsInstanceOf.instanceOf(GetElements.class));
        MatcherAssert.assertThat(third, IsInstanceOf.instanceOf(Limit.class));
    }

    @Test
    public void shouldDoAShallowClone() throws IOException {
        // Given
        final List<Operation> ops = Arrays.asList(Mockito.mock(Operation.class), Mockito.mock(Input.class), Mockito.mock(Input.class), Mockito.mock(MultiInput.class), Mockito.mock(Input.class));
        final List<Operation> clonedOps = Arrays.asList(Mockito.mock(Operation.class), Mockito.mock(Input.class), Mockito.mock(Input.class), Mockito.mock(MultiInput.class), Mockito.mock(Input.class));
        for (int i = 0; i < (ops.size()); i++) {
            BDDMockito.given(ops.get(i).shallowClone()).willReturn(clonedOps.get(i));
        }
        final OperationChain opChain = new OperationChain(ops);
        final Map<String, String> options = Mockito.mock(Map.class);
        opChain.setOptions(options);
        // When
        final OperationChain clone = opChain.shallowClone();
        // Then
        Assert.assertEquals(clonedOps, clone.getOperations());
        Assert.assertSame(options, clone.getOptions());
    }

    @Test
    public void shouldWrapOperation() throws IOException {
        // Given
        final Operation operation = Mockito.mock(Operation.class);
        final Map<String, String> options = Mockito.mock(Map.class);
        BDDMockito.given(operation.getOptions()).willReturn(options);
        // When
        final OperationChain wrappedChain = OperationChain.wrap(operation);
        // Then
        Assert.assertEquals(1, wrappedChain.getOperations().size());
        Assert.assertEquals(operation, wrappedChain.getOperations().get(0));
        Assert.assertSame(operation.getOptions(), wrappedChain.getOptions());
    }

    @Test
    public void shouldWrapOutputOperation() throws IOException {
        // Given
        final Operation operation = Mockito.mock(Output.class);
        final Map<String, String> options = Mockito.mock(Map.class);
        BDDMockito.given(operation.getOptions()).willReturn(options);
        // When
        final OperationChain wrappedChain = OperationChain.wrap(operation);
        // Then
        Assert.assertEquals(1, wrappedChain.getOperations().size());
        Assert.assertEquals(operation, wrappedChain.getOperations().get(0));
        Assert.assertSame(operation.getOptions(), wrappedChain.getOptions());
    }

    @Test
    public void shouldNotWrapOperationChain() throws IOException {
        // Given
        final Operation operation = Mockito.mock(OperationChain.class);
        final Map<String, String> options = Mockito.mock(Map.class);
        BDDMockito.given(operation.getOptions()).willReturn(options);
        // When
        final OperationChain wrappedChain = OperationChain.wrap(operation);
        // Then
        Assert.assertSame(operation, wrappedChain);
        Assert.assertSame(operation.getOptions(), wrappedChain.getOptions());
    }

    @Test
    public void shouldNotWrapOperationChainDAO() throws IOException {
        // Given
        final Operation operation = Mockito.mock(OperationChainDAO.class);
        final Map<String, String> options = Mockito.mock(Map.class);
        BDDMockito.given(operation.getOptions()).willReturn(options);
        // When
        final OperationChain wrappedChain = OperationChain.wrap(operation);
        // Then
        Assert.assertSame(operation, wrappedChain);
        Assert.assertSame(operation.getOptions(), wrappedChain.getOptions());
    }

    @Test
    public void shouldConvertToOverviewString() {
        // Given
        final OperationChain opChain = new OperationChain.Builder().first(new GetAdjacentIds.Builder().input(new EntitySeed("vertex1")).build()).then(new Limit(1)).build();
        // When
        final String overview = opChain.toOverviewString();
        // Then
        Assert.assertEquals("OperationChain[GetAdjacentIds->Limit]", overview);
    }

    @Test
    public void shouldConvertToOverviewStringWithNoOperations() {
        // Given
        final OperationChain opChain = new OperationChain();
        // When
        final String overview = opChain.toOverviewString();
        // Then
        Assert.assertEquals("OperationChain[]", overview);
    }
}

