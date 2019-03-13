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


import java.util.Arrays;
import java.util.Collections;
import org.apache.flink.util.Collector;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import uk.gov.gchq.gaffer.data.element.Element;
import uk.gov.gchq.gaffer.data.generator.ElementGenerator;
import uk.gov.gchq.gaffer.data.generator.OneToManyElementGenerator;
import uk.gov.gchq.gaffer.data.generator.OneToOneElementGenerator;


public class GafferMapFunctionTest {
    private static ElementGenerator<String> mockGenerator;

    private static OneToOneElementGenerator<String> mockOneToOneGenerator;

    private static OneToManyElementGenerator<String> mockOneToManyGenerator;

    @Test
    public void shouldDelegateToGafferElementGenerator() throws Exception {
        // Given
        final String csv = "1,2,3,4";
        final GafferMapFunction function = new GafferMapFunction(String.class, GafferMapFunctionTest.MockedGenerator.class);
        final Iterable expectedResults = Arrays.asList(Mockito.mock(Element.class), Mockito.mock(Element.class));
        final Collector<Element> collector = Mockito.mock(Collector.class);
        BDDMockito.given(GafferMapFunctionTest.mockGenerator.apply(Collections.singleton(csv))).willReturn(expectedResults);
        // When
        function.flatMap(csv, collector);
        // Then
        for (Element expectedResult : ((Iterable<Element>) (expectedResults))) {
            Mockito.verify(collector).collect(expectedResult);
        }
    }

    @Test
    public void shouldDelegateToGafferOneToOneElementGenerator() throws Exception {
        // Given
        final String csv = "1,2,3,4";
        final GafferMapFunction function = new GafferMapFunction(String.class, GafferMapFunctionTest.MockedOneToOneGenerator.class);
        final Element expectedResult = Mockito.mock(Element.class);
        final Collector<Element> collector = Mockito.mock(Collector.class);
        BDDMockito.given(GafferMapFunctionTest.mockOneToOneGenerator._apply(csv)).willReturn(expectedResult);
        // When
        function.flatMap(csv, collector);
        // Then
        Mockito.verify(collector).collect(expectedResult);
    }

    @Test
    public void shouldDelegateToGafferOneToManyElementGenerator() throws Exception {
        // Given
        final String csv = "1,2,3,4";
        final GafferMapFunction function = new GafferMapFunction(String.class, GafferMapFunctionTest.MockedOneToManyGenerator.class);
        final Iterable expectedResults = Arrays.asList(Mockito.mock(Element.class), Mockito.mock(Element.class));
        final Collector<Element> collector = Mockito.mock(Collector.class);
        BDDMockito.given(GafferMapFunctionTest.mockOneToManyGenerator._apply(csv)).willReturn(expectedResults);
        // When
        function.flatMap(csv, collector);
        // Then
        for (Element expectedResult : ((Iterable<Element>) (expectedResults))) {
            Mockito.verify(collector).collect(expectedResult);
        }
    }

    public static final class MockedGenerator implements ElementGenerator<String> {
        @Override
        public Iterable<? extends Element> apply(final Iterable<? extends String> strings) {
            return GafferMapFunctionTest.mockGenerator.apply(strings);
        }
    }

    public static final class MockedOneToOneGenerator implements OneToOneElementGenerator<String> {
        @Override
        public Element _apply(final String domainObject) {
            return GafferMapFunctionTest.mockOneToOneGenerator._apply(domainObject);
        }
    }

    public static final class MockedOneToManyGenerator implements OneToManyElementGenerator<String> {
        @Override
        public Iterable<Element> _apply(final String domainObject) {
            return GafferMapFunctionTest.mockOneToManyGenerator._apply(domainObject);
        }
    }
}

