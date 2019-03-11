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


import com.google.common.collect.Sets;
import java.util.HashSet;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.export.resultcache.ExportToGafferResultCache;


public class ExportToGafferResultCacheTest extends OperationTest<ExportToGafferResultCache> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final String key = "key";
        final HashSet<String> opAuths = Sets.newHashSet("1", "2");
        final ExportToGafferResultCache op = new ExportToGafferResultCache.Builder<>().opAuths(opAuths).key(key).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final ExportToGafferResultCache deserialisedOp = JSONSerialiser.deserialise(json, ExportToGafferResultCache.class);
        // Then
        Assert.assertEquals(key, deserialisedOp.getKey());
        Assert.assertEquals(opAuths, deserialisedOp.getOpAuths());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final String key = "key";
        final HashSet<String> opAuths = Sets.newHashSet("1", "2");
        final ExportToGafferResultCache op = new ExportToGafferResultCache.Builder<>().opAuths(opAuths).key(key).build();
        // Then
        Assert.assertEquals(key, op.getKey());
        Assert.assertEquals(opAuths, op.getOpAuths());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(Object.class, outputClass);
    }
}

