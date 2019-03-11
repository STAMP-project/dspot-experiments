package uk.gov.gchq.gaffer.integration.graph;


import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.commonutil.StreamUtil;
import uk.gov.gchq.gaffer.graph.Graph;
import uk.gov.gchq.gaffer.graph.GraphConfig;


public class GraphIT {
    @Test
    public void shouldCloseStreamsIfExceptionThrownWithStoreProperties() throws IOException {
        // Given
        final InputStream storePropertiesStream = createMockStream();
        final InputStream elementsSchemaStream = createMockStream();
        final InputStream typesSchemaStream = createMockStream();
        final InputStream aggregationSchemaStream = createMockStream();
        final InputStream validationSchemaStream = createMockStream();
        // When
        try {
            new Graph.Builder().storeProperties(storePropertiesStream).addSchema(elementsSchemaStream).addSchema(typesSchemaStream).addSchema(aggregationSchemaStream).addSchema(validationSchemaStream).build();
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            // Then
            Assert.assertNotNull(e.getMessage());
            Mockito.verify(storePropertiesStream, Mockito.atLeastOnce()).close();
            Mockito.verify(elementsSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(typesSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(aggregationSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(validationSchemaStream, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void shouldCloseStreamsIfExceptionThrownWithElementSchema() throws IOException {
        // Given
        final InputStream storePropertiesStream = StreamUtil.storeProps(getClass());
        final InputStream elementSchemaStream = createMockStream();
        final InputStream typesSchemaStream = createMockStream();
        final InputStream serialisationSchemaStream = createMockStream();
        final InputStream aggregationSchemaStream = createMockStream();
        // When
        try {
            new Graph.Builder().config(new GraphConfig.Builder().graphId("graph1").build()).storeProperties(storePropertiesStream).addSchema(elementSchemaStream).addSchema(typesSchemaStream).addSchema(serialisationSchemaStream).addSchema(aggregationSchemaStream).build();
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            // Then
            Assert.assertNotNull(e.getMessage());
            Mockito.verify(elementSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(typesSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(serialisationSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(aggregationSchemaStream, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void shouldCloseStreamsIfExceptionThrownWithTypesSchema() throws IOException {
        // Given
        final InputStream storePropertiesStream = StreamUtil.storeProps(getClass());
        final InputStream elementSchemaStream = StreamUtil.elementsSchema(getClass());
        final InputStream typesSchemaStream = createMockStream();
        final InputStream aggregationSchemaStream = createMockStream();
        final InputStream serialisationSchemaStream = createMockStream();
        // When
        try {
            new Graph.Builder().storeProperties(storePropertiesStream).addSchema(elementSchemaStream).addSchema(typesSchemaStream).addSchema(aggregationSchemaStream).addSchema(serialisationSchemaStream).build();
            Assert.fail("Exception expected");
        } catch (final Exception e) {
            // Then
            Assert.assertNotNull(e.getMessage());
            Mockito.verify(typesSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(aggregationSchemaStream, Mockito.atLeastOnce()).close();
            Mockito.verify(serialisationSchemaStream, Mockito.atLeastOnce()).close();
        }
    }

    @Test
    public void shouldCloseStreamsWhenSuccessful() throws IOException {
        // Given
        final InputStream storePropertiesStream = StreamUtil.storeProps(getClass());
        final InputStream elementsSchemaStream = StreamUtil.elementsSchema(getClass());
        final InputStream typesSchemaStream = StreamUtil.typesSchema(getClass());
        // When
        new Graph.Builder().config(new GraphConfig.Builder().graphId("graphId").build()).storeProperties(storePropertiesStream).addSchema(elementsSchemaStream).addSchema(typesSchemaStream).build();
        checkClosed(storePropertiesStream);
        checkClosed(elementsSchemaStream);
        checkClosed(typesSchemaStream);
    }
}

