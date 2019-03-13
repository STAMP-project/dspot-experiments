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


import CreateStreamingGearpumpView.CreateGearpumpPCollectionView;
import io.gearpump.streaming.dsl.javaapi.JavaStream;
import org.apache.beam.sdk.values.PCollectionView;
import org.apache.beam.sdk.values.PValue;
import org.junit.Test;
import org.mockito.Mockito;


/**
 * Tests for {@link CreateGearpumpPCollectionViewTranslator}.
 */
public class CreateGearpumpPCollectionViewTranslatorTest {
    @Test
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void testTranslate() {
        CreateGearpumpPCollectionViewTranslator translator = new CreateGearpumpPCollectionViewTranslator();
        CreateStreamingGearpumpView.CreateGearpumpPCollectionView pCollectionView = Mockito.mock(CreateGearpumpPCollectionView.class);
        JavaStream javaStream = Mockito.mock(JavaStream.class);
        TranslationContext translationContext = Mockito.mock(TranslationContext.class);
        PValue mockInput = Mockito.mock(PValue.class);
        Mockito.when(translationContext.getInput()).thenReturn(mockInput);
        Mockito.when(translationContext.getInputStream(mockInput)).thenReturn(javaStream);
        PCollectionView view = Mockito.mock(PCollectionView.class);
        Mockito.when(pCollectionView.getView()).thenReturn(view);
        translator.translate(pCollectionView, translationContext);
        Mockito.verify(translationContext, Mockito.times(1)).setOutputStream(view, javaStream);
    }
}

