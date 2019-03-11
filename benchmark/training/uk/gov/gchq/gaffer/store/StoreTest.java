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
package uk.gov.gchq.gaffer.store;


import CacheProperties.CACHE_SERVICE_CLASS;
import IdentifierType.VERTEX;
import JSONSerialiser.JSON_SERIALISER_CLASS_KEY;
import JobStatus.FINISHED;
import TestGroups.EDGE;
import TestGroups.ENTITY;
import TestPropertyNames.PROP_1;
import TestPropertyNames.PROP_2;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.cache.CacheServiceLoader;
import uk.gov.gchq.gaffer.cache.impl.HashMapCacheService;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.data.element.LazyEntity;
import uk.gov.gchq.gaffer.data.element.id.EntityId;
import uk.gov.gchq.gaffer.data.elementdefinition.exception.SchemaException;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.jobtracker.JobTracker;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.named.operation.AddNamedOperation;
import uk.gov.gchq.gaffer.named.operation.DeleteNamedOperation;
import uk.gov.gchq.gaffer.named.operation.GetAllNamedOperations;
import uk.gov.gchq.gaffer.named.operation.NamedOperation;
import uk.gov.gchq.gaffer.named.view.AddNamedView;
import uk.gov.gchq.gaffer.named.view.DeleteNamedView;
import uk.gov.gchq.gaffer.named.view.GetAllNamedViews;
import uk.gov.gchq.gaffer.operation.Operation;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.OperationChainDAO;
import uk.gov.gchq.gaffer.operation.OperationException;
import uk.gov.gchq.gaffer.operation.impl.Count;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.operation.impl.DiscardOutput;
import uk.gov.gchq.gaffer.operation.impl.ForEach;
import uk.gov.gchq.gaffer.operation.impl.GetVariable;
import uk.gov.gchq.gaffer.operation.impl.GetVariables;
import uk.gov.gchq.gaffer.operation.impl.GetWalks;
import uk.gov.gchq.gaffer.operation.impl.If;
import uk.gov.gchq.gaffer.operation.impl.Limit;
import uk.gov.gchq.gaffer.operation.impl.Map;
import uk.gov.gchq.gaffer.operation.impl.Reduce;
import uk.gov.gchq.gaffer.operation.impl.SetVariable;
import uk.gov.gchq.gaffer.operation.impl.Validate;
import uk.gov.gchq.gaffer.operation.impl.ValidateOperationChain;
import uk.gov.gchq.gaffer.operation.impl.While;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.compare.Max;
import uk.gov.gchq.gaffer.operation.impl.compare.Min;
import uk.gov.gchq.gaffer.operation.impl.compare.Sort;
import uk.gov.gchq.gaffer.operation.impl.export.GetExports;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.ExportToGafferResultCache;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.GetGafferResultCacheExport;
import uk.gov.gchq.gaffer.operation.impl.export.set.ExportToSet;
import uk.gov.gchq.gaffer.operation.impl.export.set.GetSetExport;
import uk.gov.gchq.gaffer.operation.impl.function.Aggregate;
import uk.gov.gchq.gaffer.operation.impl.function.Filter;
import uk.gov.gchq.gaffer.operation.impl.function.Transform;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateElements;
import uk.gov.gchq.gaffer.operation.impl.generate.GenerateObjects;
import uk.gov.gchq.gaffer.operation.impl.get.GetAdjacentIds;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetElements;
import uk.gov.gchq.gaffer.operation.impl.job.GetAllJobDetails;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobResults;
import uk.gov.gchq.gaffer.operation.impl.join.Join;
import uk.gov.gchq.gaffer.operation.impl.output.ToArray;
import uk.gov.gchq.gaffer.operation.impl.output.ToCsv;
import uk.gov.gchq.gaffer.operation.impl.output.ToEntitySeeds;
import uk.gov.gchq.gaffer.operation.impl.output.ToList;
import uk.gov.gchq.gaffer.operation.impl.output.ToMap;
import uk.gov.gchq.gaffer.operation.impl.output.ToSet;
import uk.gov.gchq.gaffer.operation.impl.output.ToSingletonList;
import uk.gov.gchq.gaffer.operation.impl.output.ToStream;
import uk.gov.gchq.gaffer.operation.impl.output.ToVertices;
import uk.gov.gchq.gaffer.serialisation.Serialiser;
import uk.gov.gchq.gaffer.serialisation.ToBytesSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.StringSerialiser;
import uk.gov.gchq.gaffer.serialisation.implementation.tostring.StringToStringSerialiser;
import uk.gov.gchq.gaffer.store.library.GraphLibrary;
import uk.gov.gchq.gaffer.store.operation.GetSchema;
import uk.gov.gchq.gaffer.store.operation.GetTraits;
import uk.gov.gchq.gaffer.store.operation.OperationChainValidator;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclaration;
import uk.gov.gchq.gaffer.store.operation.declaration.OperationDeclarations;
import uk.gov.gchq.gaffer.store.operation.handler.CountGroupsHandler;
import uk.gov.gchq.gaffer.store.operation.handler.OperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.OutputOperationHandler;
import uk.gov.gchq.gaffer.store.operation.handler.export.set.ExportToSetHandler;
import uk.gov.gchq.gaffer.store.operation.handler.export.set.GetSetExportHandler;
import uk.gov.gchq.gaffer.store.operation.handler.generate.GenerateElementsHandler;
import uk.gov.gchq.gaffer.store.operation.handler.generate.GenerateObjectsHandler;
import uk.gov.gchq.gaffer.store.operation.handler.output.ToSetHandler;
import uk.gov.gchq.gaffer.store.schema.Schema;
import uk.gov.gchq.gaffer.store.schema.SchemaEdgeDefinition;
import uk.gov.gchq.gaffer.store.schema.SchemaOptimiser;
import uk.gov.gchq.gaffer.store.schema.TypeDefinition;
import uk.gov.gchq.gaffer.user.User;
import uk.gov.gchq.koryphe.ValidationResult;

import static uk.gov.gchq.gaffer.store.StorePropertiesTest.TestCustomJsonModules1.modules;


public class StoreTest {
    private final User user = new User("user01");

    private final Context context = new Context(user);

    private OperationHandler<AddElements> addElementsHandler;

    private OutputOperationHandler<GetElements, CloseableIterable<? extends Element>> getElementsHandler;

    private OutputOperationHandler<GetAllElements, CloseableIterable<? extends Element>> getAllElementsHandler;

    private OutputOperationHandler<GetAdjacentIds, CloseableIterable<? extends EntityId>> getAdjacentIdsHandler;

    private OperationHandler<Validate> validateHandler;

    private Schema schema;

    private SchemaOptimiser schemaOptimiser;

    private JobTracker jobTracker;

    private OperationHandler<ExportToGafferResultCache> exportToGafferResultCacheHandler;

    private OperationHandler<GetGafferResultCacheExport> getGafferResultCacheExportHandler;

    private StoreTest.StoreImpl store;

    private OperationChainValidator operationChainValidator;

    @Test
    public void shouldThrowExceptionIfGraphIdIsNull() throws Exception {
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        try {
            store.initialise(null, schema, properties);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Assert.assertNotNull(e.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionWhenPropertyIsNotSerialisable() throws StoreException {
        // Given
        final Schema mySchema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().property(PROP_1, "invalidType").build()).type("invalidType", new TypeDefinition.Builder().clazz(Object.class).serialiser(new StringSerialiser()).build()).build();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        // When
        try {
            store.initialise("graphId", mySchema, properties);
            Assert.fail();
        } catch (final SchemaException exception) {
            Assert.assertNotNull(exception.getMessage());
        }
    }

    @Test
    public void shouldCreateStoreWithValidSchemasAndRegisterOperations() throws StoreException {
        // Given
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final OperationHandler<AddElements> addElementsHandlerOverridden = Mockito.mock(OperationHandler.class);
        final OperationDeclarations opDeclarations = new OperationDeclarations.Builder().declaration(new OperationDeclaration.Builder().operation(AddElements.class).handler(addElementsHandlerOverridden).build()).build();
        BDDMockito.given(properties.getOperationDeclarations()).willReturn(opDeclarations);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        // When
        store.initialise("graphId", schema, properties);
        // Then
        Assert.assertNotNull(store.getOperationHandlerExposed(Validate.class));
        Assert.assertSame(addElementsHandlerOverridden, store.getOperationHandlerExposed(AddElements.class));
        Assert.assertSame(getAllElementsHandler, store.getOperationHandlerExposed(GetAllElements.class));
        Assert.assertTrue(((store.getOperationHandlerExposed(GenerateElements.class)) instanceof GenerateElementsHandler));
        Assert.assertTrue(((store.getOperationHandlerExposed(GenerateObjects.class)) instanceof GenerateObjectsHandler));
        Assert.assertTrue(((store.getOperationHandlerExposed(CountGroups.class)) instanceof CountGroupsHandler));
        Assert.assertTrue(((store.getOperationHandlerExposed(ToSet.class)) instanceof ToSetHandler));
        Assert.assertTrue(((store.getOperationHandlerExposed(ExportToSet.class)) instanceof ExportToSetHandler));
        Assert.assertTrue(((store.getOperationHandlerExposed(GetSetExport.class)) instanceof GetSetExportHandler));
        Assert.assertEquals(1, store.getCreateOperationHandlersCallCount());
        Assert.assertSame(schema, getSchema());
        Assert.assertSame(properties, getProperties());
        Mockito.verify(schemaOptimiser).optimise(getSchema(), true);
    }

    @Test
    public void shouldDelegateDoOperationToOperationHandler() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        final AddElements addElements = new AddElements();
        store.initialise("graphId", schema, properties);
        // When
        store.execute(addElements, context);
        // Then
        Mockito.verify(addElementsHandler).doOperation(addElements, context, store);
    }

    @Test
    public void shouldCloseOperationIfResultIsNotCloseable() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        final Operation operation = Mockito.mock(Operation.class);
        final StoreTest.StoreImpl store = new StoreTest.StoreImpl();
        store.initialise("graphId", schema, properties);
        // When
        store.handleOperation(operation, context);
        // Then
        Mockito.verify(operation).close();
    }

    @Test
    public void shouldCloseOperationIfExceptionThrown() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        final Operation operation = Mockito.mock(Operation.class);
        final StoreTest.StoreImpl store = new StoreTest.StoreImpl();
        final OperationHandler opHandler = Mockito.mock(OperationHandler.class);
        store.addOperationHandler(Operation.class, opHandler);
        store.initialise("graphId", schema, properties);
        BDDMockito.given(opHandler.doOperation(operation, context, store)).willThrow(new RuntimeException());
        // When / Then
        try {
            store.handleOperation(operation, context);
        } catch (final Exception e) {
            Mockito.verify(operation).close();
        }
    }

    @Test
    public void shouldThrowExceptionIfOperationChainIsInvalid() throws OperationException, StoreException {
        // Given
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final OperationChain opChain = new OperationChain();
        final StoreTest.StoreImpl store = new StoreTest.StoreImpl();
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        BDDMockito.given(schema.validate()).willReturn(new ValidationResult());
        ValidationResult validationResult = new ValidationResult();
        validationResult.addError("error");
        BDDMockito.given(operationChainValidator.validate(opChain, user, store)).willReturn(validationResult);
        store.initialise("graphId", schema, properties);
        // When / Then
        try {
            store.execute(opChain, context);
            Assert.fail("Exception expected");
        } catch (final IllegalArgumentException e) {
            Mockito.verify(operationChainValidator).validate(opChain, user, store);
            Assert.assertTrue(e.getMessage().contains("Operation chain"));
        }
    }

    @Test
    public void shouldCallDoUnhandledOperationWhenDoOperationWithUnknownOperationClass() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final Operation operation = Mockito.mock(Operation.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        store.initialise("graphId", schema, properties);
        // When
        store.execute(operation, context);
        // Then
        Assert.assertEquals(1, store.getDoUnhandledOperationCalls().size());
        Assert.assertSame(operation, store.getDoUnhandledOperationCalls().get(0));
    }

    @Test
    public void shouldFullyLoadLazyElement() throws StoreException {
        // Given
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final LazyEntity lazyElement = Mockito.mock(LazyEntity.class);
        final Entity entity = Mockito.mock(Entity.class);
        final Store store = new StoreTest.StoreImpl();
        BDDMockito.given(lazyElement.getGroup()).willReturn(ENTITY);
        BDDMockito.given(lazyElement.getElement()).willReturn(entity);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        store.initialise("graphId", schema, properties);
        // When
        final Element result = store.populateElement(lazyElement);
        // Then
        Assert.assertSame(entity, result);
        Mockito.verify(lazyElement).getGroup();
        Mockito.verify(lazyElement).getProperty(PROP_1);
        Mockito.verify(lazyElement).getIdentifier(VERTEX);
    }

    @Test
    public void shouldHandleMultiStepOperations() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        final CloseableIterable getElementsResult = Mockito.mock(CloseableIterable.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        final AddElements addElements1 = new AddElements();
        final GetElements getElements = new GetElements();
        final OperationChain<CloseableIterable<? extends Element>> opChain = new OperationChain.Builder().first(addElements1).then(getElements).build();
        BDDMockito.given(addElementsHandler.doOperation(addElements1, context, store)).willReturn(null);
        BDDMockito.given(getElementsHandler.doOperation(getElements, context, store)).willReturn(getElementsResult);
        store.initialise("graphId", schema, properties);
        // When
        final CloseableIterable<? extends Element> result = store.execute(opChain, context);
        // Then
        Assert.assertSame(getElementsResult, result);
    }

    @Test
    public void shouldReturnAllSupportedOperations() throws Exception {
        // Given
        final Properties cacheProperties = new Properties();
        cacheProperties.setProperty(CACHE_SERVICE_CLASS, HashMapCacheService.class.getName());
        CacheServiceLoader.initialise(cacheProperties);
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        BDDMockito.given(properties.getJobTrackerEnabled()).willReturn(true);
        store.initialise("graphId", schema, properties);
        // When
        final List<Class<? extends Operation>> supportedOperations = Lists.newArrayList(getSupportedOperations());
        // Then
        Assert.assertNotNull(supportedOperations);
        final List<Class<? extends Operation>> expectedOperations = // Export
        // Jobs
        // Output
        // Named Operations
        // Named View
        // ElementComparison
        // Validation
        // Algorithm
        // OperationChain
        // Other
        // Function
        // Context variables
        Lists.newArrayList(AddElements.class, GetElements.class, GetAdjacentIds.class, GetAllElements.class, Mockito.mock(AddElements.class).getClass(), Mockito.mock(GetElements.class).getClass(), Mockito.mock(GetAdjacentIds.class).getClass(), ExportToSet.class, GetSetExport.class, GetExports.class, ExportToGafferResultCache.class, GetGafferResultCacheExport.class, GetJobDetails.class, GetAllJobDetails.class, GetJobResults.class, ToArray.class, ToEntitySeeds.class, ToList.class, ToMap.class, ToCsv.class, ToSet.class, ToStream.class, ToVertices.class, NamedOperation.class, AddNamedOperation.class, GetAllNamedOperations.class, DeleteNamedOperation.class, AddNamedView.class, GetAllNamedViews.class, DeleteNamedView.class, Max.class, Min.class, Sort.class, ValidateOperationChain.class, GetWalks.class, OperationChain.class, OperationChainDAO.class, GenerateElements.class, GenerateObjects.class, Validate.class, Count.class, CountGroups.class, Limit.class, DiscardOutput.class, GetSchema.class, Map.class, If.class, GetTraits.class, While.class, Join.class, ToSingletonList.class, ForEach.class, Reduce.class, Filter.class, Transform.class, Aggregate.class, SetVariable.class, GetVariable.class, GetVariables.class);
        expectedOperations.sort(Comparator.comparing(Class::getName));
        supportedOperations.sort(Comparator.comparing(Class::getName));
        Assert.assertEquals(expectedOperations, supportedOperations);
    }

    @Test
    public void shouldReturnAllSupportedOperationsWhenJobTrackerIsDisabled() throws Exception {
        // Given
        final Properties cacheProperties = new Properties();
        cacheProperties.setProperty(CACHE_SERVICE_CLASS, HashMapCacheService.class.getName());
        CacheServiceLoader.initialise(cacheProperties);
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        BDDMockito.given(properties.getJobTrackerEnabled()).willReturn(false);
        store.initialise("graphId", schema, properties);
        // When
        final List<Class<? extends Operation>> supportedOperations = Lists.newArrayList(getSupportedOperations());
        // Then
        Assert.assertNotNull(supportedOperations);
        final List<Class<? extends Operation>> expectedOperations = // Export
        // Jobs are disabled
        // Output
        // Named Operations
        // Named View
        // ElementComparison
        // Validation
        // Algorithm
        // OperationChain
        // Other
        // Function
        // Context variables
        Lists.newArrayList(AddElements.class, GetElements.class, GetAdjacentIds.class, GetAllElements.class, Mockito.mock(AddElements.class).getClass(), Mockito.mock(GetElements.class).getClass(), Mockito.mock(GetAdjacentIds.class).getClass(), ExportToSet.class, GetSetExport.class, GetExports.class, ExportToGafferResultCache.class, GetGafferResultCacheExport.class, ToArray.class, ToEntitySeeds.class, ToList.class, ToMap.class, ToCsv.class, ToSet.class, ToStream.class, ToVertices.class, NamedOperation.class, AddNamedOperation.class, GetAllNamedOperations.class, DeleteNamedOperation.class, AddNamedView.class, GetAllNamedViews.class, DeleteNamedView.class, Max.class, Min.class, Sort.class, ValidateOperationChain.class, GetWalks.class, OperationChain.class, OperationChainDAO.class, GenerateElements.class, GenerateObjects.class, Validate.class, Count.class, CountGroups.class, Limit.class, DiscardOutput.class, GetSchema.class, GetTraits.class, Map.class, If.class, While.class, Join.class, ToSingletonList.class, ForEach.class, Reduce.class, Filter.class, Transform.class, Aggregate.class, SetVariable.class, GetVariable.class, GetVariables.class);
        expectedOperations.sort(Comparator.comparing(Class::getName));
        supportedOperations.sort(Comparator.comparing(Class::getName));
        Assert.assertEquals(expectedOperations, supportedOperations);
    }

    @Test
    public void shouldReturnTrueWhenOperationSupported() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        store.initialise("graphId", schema, properties);
        // WHen
        final Set<Class<? extends Operation>> supportedOperations = store.getSupportedOperations();
        for (final Class<? extends Operation> operationClass : supportedOperations) {
            final boolean isOperationClassSupported = store.isSupported(operationClass);
            // Then
            Assert.assertTrue(isOperationClassSupported);
        }
    }

    @Test
    public void shouldReturnFalseWhenUnsupportedOperationRequested() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        store.initialise("graphId", schema, properties);
        // When
        final boolean supported = store.isSupported(Operation.class);
        // Then
        Assert.assertFalse(supported);
    }

    @Test
    public void shouldHandleNullOperationSupportRequest() throws Exception {
        // Given
        final Schema schema = createSchemaMock();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        store.initialise("graphId", schema, properties);
        // When
        final boolean supported = isSupported(null);
        // Then
        Assert.assertFalse(supported);
    }

    @Test
    public void shouldExecuteOperationChainJob() throws InterruptedException, ExecutionException, OperationException, StoreException {
        // Given
        final Operation operation = Mockito.mock(Operation.class);
        final OperationChain<?> opChain = new OperationChain.Builder().first(operation).then(new ExportToGafferResultCache()).build();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        BDDMockito.given(properties.getJobTrackerEnabled()).willReturn(true);
        final Store store = new StoreTest.StoreImpl();
        final Schema schema = new Schema();
        store.initialise("graphId", schema, properties);
        // When
        final JobDetail resultJobDetail = store.executeJob(opChain, context);
        // Then
        Thread.sleep(1000);
        final ArgumentCaptor<JobDetail> jobDetail = ArgumentCaptor.forClass(JobDetail.class);
        Mockito.verify(jobTracker, Mockito.times(2)).addOrUpdateJob(jobDetail.capture(), ArgumentMatchers.eq(user));
        Assert.assertEquals(jobDetail.getAllValues().get(0), resultJobDetail);
        Assert.assertEquals(FINISHED, jobDetail.getAllValues().get(1).getStatus());
        final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(exportToGafferResultCacheHandler).doOperation(Mockito.any(ExportToGafferResultCache.class), contextCaptor.capture(), ArgumentMatchers.eq(store));
        Assert.assertSame(user, contextCaptor.getValue().getUser());
    }

    @Test
    public void shouldExecuteOperationChainJobAndExportResults() throws InterruptedException, ExecutionException, OperationException, StoreException {
        // Given
        final Operation operation = Mockito.mock(Operation.class);
        final OperationChain<?> opChain = new OperationChain(operation);
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        BDDMockito.given(properties.getJobTrackerEnabled()).willReturn(true);
        final Store store = new StoreTest.StoreImpl();
        final Schema schema = new Schema();
        store.initialise("graphId", schema, properties);
        // When
        final JobDetail resultJobDetail = store.executeJob(opChain, context);
        // Then
        Thread.sleep(1000);
        final ArgumentCaptor<JobDetail> jobDetail = ArgumentCaptor.forClass(JobDetail.class);
        Mockito.verify(jobTracker, Mockito.times(2)).addOrUpdateJob(jobDetail.capture(), ArgumentMatchers.eq(user));
        Assert.assertEquals(jobDetail.getAllValues().get(0), resultJobDetail);
        Assert.assertEquals(FINISHED, jobDetail.getAllValues().get(1).getStatus());
        final ArgumentCaptor<Context> contextCaptor = ArgumentCaptor.forClass(Context.class);
        Mockito.verify(exportToGafferResultCacheHandler).doOperation(Mockito.any(ExportToGafferResultCache.class), contextCaptor.capture(), ArgumentMatchers.eq(store));
        Assert.assertSame(user, contextCaptor.getValue().getUser());
    }

    @Test
    public void shouldGetJobTracker() throws InterruptedException, ExecutionException, OperationException, StoreException {
        // Given
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        BDDMockito.given(properties.getJobTrackerEnabled()).willReturn(true);
        final Store store = new StoreTest.StoreImpl();
        final Schema schema = new Schema();
        store.initialise("graphId", schema, properties);
        // When
        final JobTracker resultJobTracker = store.getJobTracker();
        // Then
        Assert.assertSame(jobTracker, resultJobTracker);
    }

    @Test
    public void shouldUpdateJsonSerialiser() throws StoreException {
        // Given
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJsonSerialiserClass()).willReturn(StoreTest.TestCustomJsonSerialiser1.class.getName());
        BDDMockito.given(properties.getJsonSerialiserModules()).willReturn(StorePropertiesTest.TestCustomJsonModules1.class.getName());
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        StoreTest.TestCustomJsonSerialiser1.mapper = Mockito.mock(ObjectMapper.class);
        System.setProperty(JSON_SERIALISER_CLASS_KEY, StoreTest.TestCustomJsonSerialiser1.class.getName());
        modules = Arrays.asList(Mockito.mock(Module.class), Mockito.mock(Module.class));
        final Store store = new StoreTest.StoreImpl();
        final Schema schema = new Schema();
        // When
        store.initialise("graphId", schema, properties);
        // Then
        Assert.assertEquals(StoreTest.TestCustomJsonSerialiser1.class, JSONSerialiser.getInstance().getClass());
        Assert.assertSame(StoreTest.TestCustomJsonSerialiser1.mapper, JSONSerialiser.getMapper());
        Mockito.verify(StoreTest.TestCustomJsonSerialiser1.mapper, Mockito.times(2)).registerModules(modules);
    }

    @Test
    public void shouldSetAndGetGraphLibrary() {
        // Given
        final Store store = new StoreTest.StoreImpl();
        final GraphLibrary graphLibrary = Mockito.mock(GraphLibrary.class);
        // When
        store.setGraphLibrary(graphLibrary);
        final GraphLibrary result = store.getGraphLibrary();
        // Then
        Assert.assertSame(graphLibrary, result);
    }

    @Test(expected = SchemaException.class)
    public void shouldFindInvalidSerialiser() throws Exception {
        final Class<StringToStringSerialiser> invalidSerialiserClass = StringToStringSerialiser.class;
        Schema invalidSchema = new Schema.Builder().edge(EDGE, new SchemaEdgeDefinition.Builder().source("string").destination("invalidString").directed("true").property(PROP_1, "string").property(PROP_2, "string").build()).type("string", new TypeDefinition.Builder().clazz(String.class).serialiser(new StringSerialiser()).build()).type("invalidString", new TypeDefinition.Builder().clazz(String.class).serialiser(invalidSerialiserClass.newInstance()).build()).type("true", Boolean.class).build();
        final StoreProperties properties = Mockito.mock(StoreProperties.class);
        BDDMockito.given(properties.getJobExecutorThreadCount()).willReturn(1);
        final Class<ToBytesSerialiser> validSerialiserInterface = ToBytesSerialiser.class;
        try {
            new StoreTest.StoreImpl() {
                @Override
                protected Class<? extends Serialiser> getRequiredParentSerialiserClass() {
                    return validSerialiserInterface;
                }
            }.initialise("graphId", invalidSchema, properties);
        } catch (final SchemaException e) {
            Assert.assertTrue(e.getMessage().contains(invalidSerialiserClass.getSimpleName()));
            throw e;
        }
        Assert.fail("Exception wasn't caught");
    }

    private class StoreImpl extends Store {
        private final Set<StoreTrait> TRAITS = new java.util.HashSet(Arrays.asList(StoreTrait.INGEST_AGGREGATION, StoreTrait.PRE_AGGREGATION_FILTERING, StoreTrait.TRANSFORMATION, StoreTrait.ORDERED));

        private final ArrayList<Operation> doUnhandledOperationCalls = new ArrayList<>();

        private int createOperationHandlersCallCount;

        @Override
        protected OperationChainValidator createOperationChainValidator() {
            return operationChainValidator;
        }

        @Override
        public Set<StoreTrait> getTraits() {
            return TRAITS;
        }

        public OperationHandler getOperationHandlerExposed(final Class<? extends Operation> opClass) {
            return getOperationHandler(opClass);
        }

        @Override
        protected void addAdditionalOperationHandlers() {
            (createOperationHandlersCallCount)++;
            addOperationHandler(Mockito.mock(AddElements.class).getClass(), addElementsHandler);
            addOperationHandler(Mockito.mock(GetElements.class).getClass(), ((OperationHandler) (getElementsHandler)));
            addOperationHandler(Mockito.mock(GetAdjacentIds.class).getClass(), getElementsHandler);
            addOperationHandler(Validate.class, validateHandler);
            addOperationHandler(ExportToGafferResultCache.class, exportToGafferResultCacheHandler);
            addOperationHandler(GetGafferResultCacheExport.class, getGafferResultCacheExportHandler);
        }

        @Override
        protected OutputOperationHandler<GetElements, CloseableIterable<? extends Element>> getGetElementsHandler() {
            return getElementsHandler;
        }

        @Override
        protected OutputOperationHandler<GetAllElements, CloseableIterable<? extends Element>> getGetAllElementsHandler() {
            return getAllElementsHandler;
        }

        @Override
        protected OutputOperationHandler<GetAdjacentIds, CloseableIterable<? extends EntityId>> getAdjacentIdsHandler() {
            return getAdjacentIdsHandler;
        }

        @Override
        protected OperationHandler<AddElements> getAddElementsHandler() {
            return addElementsHandler;
        }

        @Override
        protected Object doUnhandledOperation(final Operation operation, final Context context) {
            doUnhandledOperationCalls.add(operation);
            return null;
        }

        public int getCreateOperationHandlersCallCount() {
            return createOperationHandlersCallCount;
        }

        public ArrayList<Operation> getDoUnhandledOperationCalls() {
            return doUnhandledOperationCalls;
        }

        @Override
        public void optimiseSchema() {
            schemaOptimiser.optimise(getSchema(), hasTrait(StoreTrait.ORDERED));
        }

        @Override
        protected JobTracker createJobTracker() {
            if (getProperties().getJobTrackerEnabled()) {
                return jobTracker;
            }
            return null;
        }

        @Override
        protected Class<? extends Serialiser> getRequiredParentSerialiserClass() {
            return Serialiser.class;
        }
    }

    public static final class TestCustomJsonSerialiser1 extends JSONSerialiser {
        public static ObjectMapper mapper;

        public TestCustomJsonSerialiser1() {
            super(StoreTest.TestCustomJsonSerialiser1.mapper);
        }
    }
}

