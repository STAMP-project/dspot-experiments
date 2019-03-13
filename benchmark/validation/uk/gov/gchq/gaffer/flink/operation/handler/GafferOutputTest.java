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
package uk.gov.gchq.gaffer.flink.operation.handler;


import org.junit.Test;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;


public class GafferOutputTest {
    @Test
    public void shouldDelegateOpenToGafferAddedInitialise() throws Exception {
        // Given
        final GafferAdder adder = Mockito.mock(GafferAdder.class);
        final GafferOutput output = new GafferOutput(adder);
        // When
        output.open(1, 2);
        // Then
        Mockito.verify(adder).initialise();
    }

    @Test
    public void shouldDelegateWriteRecordToGafferAddedInitialise() throws Exception {
        // Given
        final GafferAdder adder = Mockito.mock(GafferAdder.class);
        final GafferOutput output = new GafferOutput(adder);
        final Element element = Mockito.mock(Element.class);
        // When
        output.writeRecord(element);
        // Then
        Mockito.verify(adder).add(element);
    }
}

