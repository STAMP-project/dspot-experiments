/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.runners.gearpump.translators;


import Flatten.PCollections;
import io.gearpump.streaming.dsl.api.functions.MapFunction;
import io.gearpump.streaming.dsl.javaapi.JavaStream;
import io.gearpump.streaming.source.DataSource;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.beam.runners.gearpump.GearpumpPipelineOptions;
import org.apache.beam.runners.gearpump.translators.io.UnboundedSourceWrapper;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PValue;
import org.apache.beam.sdk.values.TupleTag;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link FlattenPCollectionsTranslator}.
 */
public class FlattenPCollectionsTranslatorTest {
    private FlattenPCollectionsTranslator translator = new FlattenPCollectionsTranslator();

    private PCollections transform = Mockito.mock(PCollections.class);

    private static class UnboundedSourceWrapperMatcher extends ArgumentMatcher<DataSource> {
        @Override
        public boolean matches(Object o) {
            return o instanceof UnboundedSourceWrapper;
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testTranslateWithEmptyCollection() {
        PCollection mockOutput = Mockito.mock(PCollection.class);
        TranslationContext translationContext = Mockito.mock(TranslationContext.class);
        Mockito.when(translationContext.getInputs()).thenReturn(Collections.EMPTY_MAP);
        Mockito.when(translationContext.getOutput()).thenReturn(mockOutput);
        Mockito.when(translationContext.getPipelineOptions()).thenReturn(PipelineOptionsFactory.as(GearpumpPipelineOptions.class));
        translator.translate(transform, translationContext);
        Mockito.verify(translationContext).getSourceStream(ArgumentMatchers.argThat(new FlattenPCollectionsTranslatorTest.UnboundedSourceWrapperMatcher()));
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testTranslateWithOneCollection() {
        JavaStream javaStream = Mockito.mock(JavaStream.class);
        TranslationContext translationContext = Mockito.mock(TranslationContext.class);
        Map<TupleTag<?>, PValue> inputs = new HashMap<>();
        TupleTag tag = Mockito.mock(TupleTag.class);
        PCollection mockCollection = Mockito.mock(PCollection.class);
        inputs.put(tag, mockCollection);
        Mockito.when(translationContext.getInputs()).thenReturn(inputs);
        Mockito.when(translationContext.getInputStream(mockCollection)).thenReturn(javaStream);
        PValue mockOutput = Mockito.mock(PValue.class);
        Mockito.when(translationContext.getOutput()).thenReturn(mockOutput);
        translator.translate(transform, translationContext);
        Mockito.verify(translationContext, Mockito.times(1)).setOutputStream(mockOutput, javaStream);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testWithMoreThanOneCollections() {
        String transformName = "transform";
        Mockito.when(transform.getName()).thenReturn(transformName);
        JavaStream javaStream1 = Mockito.mock(JavaStream.class);
        JavaStream javaStream2 = Mockito.mock(JavaStream.class);
        JavaStream mergedStream = Mockito.mock(JavaStream.class);
        TranslationContext translationContext = Mockito.mock(TranslationContext.class);
        Map<TupleTag<?>, PValue> inputs = new HashMap<>();
        TupleTag tag1 = Mockito.mock(TupleTag.class);
        PCollection mockCollection1 = Mockito.mock(PCollection.class);
        inputs.put(tag1, mockCollection1);
        TupleTag tag2 = Mockito.mock(TupleTag.class);
        PCollection mockCollection2 = Mockito.mock(PCollection.class);
        inputs.put(tag2, mockCollection2);
        PCollection output = Mockito.mock(PCollection.class);
        Mockito.when(translationContext.getInputs()).thenReturn(inputs);
        Mockito.when(translationContext.getInputStream(mockCollection1)).thenReturn(javaStream1);
        Mockito.when(translationContext.getInputStream(mockCollection2)).thenReturn(javaStream2);
        Mockito.when(javaStream1.merge(javaStream2, 1, transformName)).thenReturn(mergedStream);
        Mockito.when(javaStream2.merge(javaStream1, 1, transformName)).thenReturn(mergedStream);
        Mockito.when(translationContext.getOutput()).thenReturn(output);
        translator.translate(transform, translationContext);
        Mockito.verify(translationContext).setOutputStream(output, mergedStream);
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testWithDuplicatedCollections() {
        String transformName = "transform";
        Mockito.when(transform.getName()).thenReturn(transformName);
        JavaStream javaStream1 = Mockito.mock(JavaStream.class);
        TranslationContext translationContext = Mockito.mock(TranslationContext.class);
        Map<TupleTag<?>, PValue> inputs = new HashMap<>();
        TupleTag tag1 = Mockito.mock(TupleTag.class);
        PCollection mockCollection1 = Mockito.mock(PCollection.class);
        inputs.put(tag1, mockCollection1);
        TupleTag tag2 = Mockito.mock(TupleTag.class);
        inputs.put(tag2, mockCollection1);
        Mockito.when(translationContext.getInputs()).thenReturn(inputs);
        Mockito.when(translationContext.getInputStream(mockCollection1)).thenReturn(javaStream1);
        Mockito.when(translationContext.getPipelineOptions()).thenReturn(PipelineOptionsFactory.as(GearpumpPipelineOptions.class));
        translator.translate(transform, translationContext);
        Mockito.verify(javaStream1).map(ArgumentMatchers.any(MapFunction.class), ArgumentMatchers.eq("dummy"));
        Mockito.verify(javaStream1).merge(ArgumentMatchers.any(JavaStream.class), ArgumentMatchers.eq(1), ArgumentMatchers.eq(transformName));
    }
}

