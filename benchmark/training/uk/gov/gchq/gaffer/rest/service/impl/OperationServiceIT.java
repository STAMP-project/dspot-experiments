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
package uk.gov.gchq.gaffer.rest.service.impl;


import java.io.IOException;
import java.util.List;
import javax.ws.rs.core.Response;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.data.GroupCounts;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.element.Entity;
import uk.gov.gchq.gaffer.operation.OperationChain;
import uk.gov.gchq.gaffer.operation.impl.CountGroups;
import uk.gov.gchq.gaffer.operation.impl.add.AddElements;
import uk.gov.gchq.gaffer.operation.impl.get.GetAllElements;
import uk.gov.gchq.gaffer.rest.AbstractRestApiIT;


public abstract class OperationServiceIT extends AbstractRestApiIT {
    @Test
    public void shouldReturnAllElements() throws IOException {
        // Given
        client.addElements(AbstractRestApiIT.DEFAULT_ELEMENTS);
        // When
        final Response response = client.executeOperation(new GetAllElements());
        // Then
        final List<Element> results = response.readEntity(new javax.ws.rs.core.GenericType<List<Element>>() {});
        verifyElements(AbstractRestApiIT.DEFAULT_ELEMENTS, results);
    }

    @Test
    public void shouldReturnGroupCounts() throws IOException {
        // Given
        client.addElements(AbstractRestApiIT.DEFAULT_ELEMENTS);
        // When
        final Response response = client.executeOperationChunked(new OperationChain.Builder().first(new GetAllElements()).then(new CountGroups()).build());
        // Then
        final GroupCounts groupCounts = response.readEntity(new javax.ws.rs.core.GenericType<GroupCounts>() {});
        verifyGroupCounts(groupCounts);
    }

    @Test
    public void shouldReturnChunkedOperationChainElements() throws IOException {
        // Given
        client.addElements(AbstractRestApiIT.DEFAULT_ELEMENTS);
        // When
        final Response response = client.executeOperationChainChunked(new OperationChain(new GetAllElements()));
        // Then
        final List<Element> results = readChunkedElements(response);
        verifyElements(AbstractRestApiIT.DEFAULT_ELEMENTS, results);
    }

    @Test
    public void shouldReturnAllChunkedOperationElements() throws IOException {
        // Given
        client.addElements(AbstractRestApiIT.DEFAULT_ELEMENTS);
        // When
        final Response response = client.executeOperationChunked(new GetAllElements());
        // Then
        final List<Element> results = readChunkedElements(response);
        verifyElements(AbstractRestApiIT.DEFAULT_ELEMENTS, results);
    }

    @Test
    public void shouldReturnChunkedOperationChainGroupCounts() throws IOException {
        // Given
        client.addElements(AbstractRestApiIT.DEFAULT_ELEMENTS);
        // When
        final Response response = client.executeOperationChainChunked(new OperationChain.Builder().first(new GetAllElements()).then(new CountGroups()).build());
        // Then
        final List<GroupCounts> results = readChunkedResults(response, new javax.ws.rs.core.GenericType<org.glassfish.jersey.client.ChunkedInput<GroupCounts>>() {});
        Assert.assertEquals(1, results.size());
        final GroupCounts groupCounts = results.get(0);
        verifyGroupCounts(groupCounts);
    }

    @Test
    public void shouldReturnNoChunkedOperationChainElementsWhenNoElementsInGraph() throws IOException {
        // When
        final Response response = client.executeOperationChainChunked(new OperationChain(new GetAllElements()));
        // Then
        final List<Element> results = readChunkedElements(response);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void shouldReturnNoChunkedOperationElementsWhenNoElementsInGraph() throws IOException {
        // When
        final Response response = client.executeOperationChunked(new GetAllElements());
        // Then
        final List<Element> results = readChunkedElements(response);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void shouldThrowErrorOnAddElements() throws IOException {
        // Given
        client.addElements(AbstractRestApiIT.DEFAULT_ELEMENTS);
        // When
        final Response response = client.executeOperation(new AddElements.Builder().input(new Entity("wrong_group", "object")).build());
        Assert.assertEquals(500, response.getStatus());
    }

    @Test
    public void shouldThrowErrorOnEmptyOperationChain() throws IOException {
        // When
        final Response response = client.executeOperationChain(new OperationChain());
        Assert.assertEquals(500, response.getStatus());
    }
}

