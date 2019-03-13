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
package uk.gov.gchq.gaffer.operation.job;


import Export.DEFAULT_KEY;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobResults;


public class GetJobResultsTest extends OperationTest<GetJobResults> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetJobResults operation = new GetJobResults.Builder().jobId("jobId").build();
        // When
        byte[] json = JSONSerialiser.serialise(operation, true);
        final GetJobResults deserialisedOp = JSONSerialiser.deserialise(json, GetJobResults.class);
        // Then
        Assert.assertEquals("jobId", deserialisedOp.getJobId());
    }

    @Test
    public void shouldReturnNullIfSetKey() {
        // When
        final GetJobResults jobResults = new GetJobResults.Builder().key(DEFAULT_KEY).build();
        // Then
        Assert.assertThat(jobResults.getKey(), Is.is(Matchers.nullValue()));
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final GetJobResults op = new GetJobResults.Builder().jobId("jobId").build();
        // Then
        Assert.assertEquals("jobId", op.getJobId());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(CloseableIterable.class, outputClass);
    }
}

