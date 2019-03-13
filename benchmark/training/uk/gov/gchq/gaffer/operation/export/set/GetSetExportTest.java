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
package uk.gov.gchq.gaffer.operation.export.set;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.export.set.GetSetExport;


public class GetSetExportTest extends OperationTest<GetSetExport> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetSetExport operation = new GetSetExport.Builder().key("key").jobId("jobId").start(0).end(5).build();
        // When
        byte[] json = JSONSerialiser.serialise(operation, true);
        final GetSetExport deserialisedOp = JSONSerialiser.deserialise(json, GetSetExport.class);
        // Then
        Assert.assertEquals("key", deserialisedOp.getKey());
        Assert.assertEquals("jobId", deserialisedOp.getJobId());
        Assert.assertEquals(0, deserialisedOp.getStart());
        Assert.assertEquals(5, ((int) (deserialisedOp.getEnd())));
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final GetSetExport operation = new GetSetExport.Builder().key("key").jobId("jobId").start(0).end(5).build();
        // Then
        Assert.assertEquals("key", operation.getKey());
        Assert.assertEquals("jobId", operation.getJobId());
        Assert.assertEquals(0, operation.getStart());
        Assert.assertEquals(5, ((int) (operation.getEnd())));
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(Iterable.class, outputClass);
    }
}

