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
package uk.gov.gchq.gaffer.operation.export;


import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import uk.gov.gchq.gaffer.exception.SerialisationException;
import uk.gov.gchq.gaffer.jsonserialisation.JSONSerialiser;
import uk.gov.gchq.gaffer.operation.OperationTest;
import uk.gov.gchq.gaffer.operation.impl.export.GetExports;
import uk.gov.gchq.gaffer.operation.impl.export.set.GetSetExport;


public class GetExportsTest extends OperationTest<GetExports> {
    @Test
    public void shouldJSONSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final GetExports op = new GetExports.Builder().exports(new GetSetExport.Builder().key("key1").build(), new GetSetExport.Builder().key("key2").build()).build();
        // When
        byte[] json = JSONSerialiser.serialise(op, true);
        final GetExports deserialisedOp = JSONSerialiser.deserialise(json, GetExports.class);
        // Then
        Assert.assertEquals("key1", deserialisedOp.getGetExports().get(0).getKey());
        Assert.assertEquals("key2", deserialisedOp.getGetExports().get(1).getKey());
    }

    @Test
    @Override
    public void builderShouldCreatePopulatedOperation() {
        // When
        final GetExports op = new GetExports.Builder().exports(new GetSetExport.Builder().key("key1").build(), new GetSetExport.Builder().key("key2").build()).build();
        // Then
        Assert.assertEquals("key1", op.getGetExports().get(0).getKey());
        Assert.assertEquals("key2", op.getGetExports().get(1).getKey());
    }

    @Test
    public void shouldGetOutputClass() {
        // When
        final Class<?> outputClass = getTestObject().getOutputClass();
        // Then
        Assert.assertEquals(Map.class, outputClass);
    }
}

