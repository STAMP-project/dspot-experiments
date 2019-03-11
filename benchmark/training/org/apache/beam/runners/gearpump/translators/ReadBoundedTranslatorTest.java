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


import Read.Bounded;
import io.gearpump.streaming.dsl.javaapi.JavaStream;
import io.gearpump.streaming.source.DataSource;
import org.apache.beam.runners.gearpump.GearpumpPipelineOptions;
import org.apache.beam.runners.gearpump.translators.io.BoundedSourceWrapper;
import org.apache.beam.sdk.io.BoundedSource;
import org.apache.beam.sdk.io.Read;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PValue;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


/**
 * Tests for {@link ReadBoundedTranslator}.
 */
public class ReadBoundedTranslatorTest {
    private static class BoundedSourceWrapperMatcher extends ArgumentMatcher<DataSource> {
        @Override
        public boolean matches(Object o) {
            return o instanceof BoundedSourceWrapper;
        }
    }

    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testTranslate() {
        ReadBoundedTranslator translator = new ReadBoundedTranslator();
        GearpumpPipelineOptions options = PipelineOptionsFactory.create().as(GearpumpPipelineOptions.class);
        Read.Bounded transform = Mockito.mock(Bounded.class);
        BoundedSource source = Mockito.mock(BoundedSource.class);
        Mockito.when(transform.getSource()).thenReturn(source);
        TranslationContext translationContext = Mockito.mock(TranslationContext.class);
        Mockito.when(translationContext.getPipelineOptions()).thenReturn(options);
        JavaStream stream = Mockito.mock(JavaStream.class);
        PValue mockOutput = Mockito.mock(PValue.class);
        Mockito.when(translationContext.getOutput()).thenReturn(mockOutput);
        Mockito.when(translationContext.getSourceStream(ArgumentMatchers.any(DataSource.class))).thenReturn(stream);
        translator.translate(transform, translationContext);
        Mockito.verify(translationContext).getSourceStream(ArgumentMatchers.argThat(new ReadBoundedTranslatorTest.BoundedSourceWrapperMatcher()));
        Mockito.verify(translationContext).setOutputStream(mockOutput, stream);
    }
}

