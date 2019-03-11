/**
 * Copyright 2017-2019 Crown Copyright
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
package uk.gov.gchq.gaffer.operation.impl.io;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import uk.gov.gchq.gaffer.JSONSerialisationTest;
import uk.gov.gchq.gaffer.commonutil.JsonAssert;
import uk.gov.gchq.gaffer.operation.io.GenericInput;


@RunWith(Parameterized.class)
public class GenericInputTest extends JSONSerialisationTest<GenericInput> {
    @Parameterized.Parameter(0)
    public String description;

    @Parameterized.Parameter(1)
    public Object inputData;

    @Parameterized.Parameter(2)
    public String expectedJson;

    @Test
    public void shouldHandleGenericInputType() {
        // Given
        final GenericInput input = new GenericInputImpl(inputData);
        // When / Then
        final byte[] json = toJson(input);
        JsonAssert.assertEquals(getExpectedJson(), json);
        // When / Then
        final GenericInput inputFromJson = fromJson(json);
        assertInputEquals(input.getInput(), inputFromJson.getInput());
        // When / Then
        final byte[] json2 = toJson(inputFromJson);
        JsonAssert.assertEquals(getExpectedJson(), json2);
        // When / Then
        final GenericInput inputFromJson2 = fromJson(json2);
        assertInputEquals(input.getInput(), inputFromJson2.getInput());
    }
}

