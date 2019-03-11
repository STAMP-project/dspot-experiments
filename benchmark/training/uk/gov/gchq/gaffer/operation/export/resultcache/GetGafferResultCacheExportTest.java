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
package uk.gov.gchq.gaffer.operation.export.resultcache;


import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.commonutil.iterable.CloseableIterable;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.GetGafferResultCacheExport;


public class GetGafferResultCacheExportTest extends OperationTest<GetGafferResultCacheExport> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final String key = "key";
        final GetGafferResultCacheExport op = new GetGafferResultCacheExport.Builder().key(key).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetGafferResultCacheExport deserialisedOp = JSONSerialiser.deserialise(json, GetGafferResultCacheExport.class);
        // Then
        Assert.assertEquals(key, deserialisedOp.getKey());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final String key = "key";
        final GetGafferResultCacheExport op = new GetGafferResultCacheExport.Builder().key(key).build();
        // Then
        Assert.assertEquals(key, op.getKey());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(CloseableIterable.class, outputClass);
    }
}

