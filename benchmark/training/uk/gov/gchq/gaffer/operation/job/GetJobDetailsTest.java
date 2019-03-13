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


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jobtracker.JobDetail;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.job.GetJobDetails;


public class GetJobDetailsTest extends OperationTest<GetJobDetails> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetJobDetails operation = new GetJobDetails.Builder().jobId("jobId").build();
        // When
        byte[] json = JSONSerialiser.serialise(operation, true);
        final GetJobDetails deserialisedOp = JSONSerialiser.deserialise(json, GetJobDetails.class);
        // Then
        Assert.assertEquals("jobId", deserialisedOp.getJobId());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final GetJobDetails op = new GetJobDetails.Builder().jobId("jobId").build();
        // Then
        Assert.assertEquals("jobId", op.getJobId());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(JobDetail.class, outputClass);
    }
}

