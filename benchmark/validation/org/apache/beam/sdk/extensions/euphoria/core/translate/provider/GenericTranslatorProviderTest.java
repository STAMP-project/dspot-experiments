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
package org.apache.beam.sdk.extensions.euphoria.core.translate.provider;


import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.beam.sdk.extensions.euphoria.core.translate.OperatorTranslator;
import org.junit.Assert;
import org.junit.Test;

import static org.apache.beam.sdk.extensions.euphoria.core.translate.provider.ProviderTestUtils.AnyOpTranslator.ofName;
import static org.apache.beam.sdk.extensions.euphoria.core.translate.provider.ProviderTestUtils.SecondTestOperator.of;


/**
 * Unit test of {@link GenericTranslatorProvider}.
 */
public class GenericTranslatorProviderTest {
    @Test
    public void testBuild() {
        GenericTranslatorProvider builded = GenericTranslatorProvider.newBuilder().build();
        Assert.assertNotNull(builded);
    }

    @Test
    public void testClassToTranslatorRegistration() {
        String translatorName = "translator";
        GenericTranslatorProvider provider = GenericTranslatorProvider.newBuilder().register(ProviderTestUtils.TestOperator.class, ProviderTestUtils.TestOpTranslator.ofName(translatorName)).build();
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.TestOperator>> maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        ProviderTestUtils.assertTranslator(translatorName, maybeTranslator, ProviderTestUtils.TestOpTranslator.class);
    }

    @Test
    public void testClassWithPredicateToTranslatorRegistration() {
        String translatorName = "translator";
        GenericTranslatorProvider provider = GenericTranslatorProvider.newBuilder().register(ProviderTestUtils.TestOperator.class, ( op) -> true, ProviderTestUtils.TestOpTranslator.ofName(translatorName)).build();
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.TestOperator>> maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        ProviderTestUtils.assertTranslator(translatorName, maybeTranslator, ProviderTestUtils.TestOpTranslator.class);
    }

    @Test
    public void testPredicateWithPredicateToTranslatorRegistration() {
        String translatorName = "translator";
        GenericTranslatorProvider provider = GenericTranslatorProvider.newBuilder().register(( op) -> true, ofName(translatorName)).build();
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.TestOperator>> maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        ProviderTestUtils.assertTranslator(translatorName, maybeTranslator, ProviderTestUtils.AnyOpTranslator.class);
    }

    @Test
    public void testClassWithPredicateToTranslatorFunction() {
        AtomicBoolean predicateEvalValue = new AtomicBoolean(false);
        String translatorName = "translator";
        GenericTranslatorProvider provider = GenericTranslatorProvider.newBuilder().register(ProviderTestUtils.TestOperator.class, ( op) -> predicateEvalValue.get(), ProviderTestUtils.TestOpTranslator.ofName(translatorName)).build();
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.TestOperator>> maybeTranslator;
        maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        Assert.assertFalse(maybeTranslator.isPresent());
        predicateEvalValue.set(true);
        // now predicate will return true and we should get our translator
        maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        ProviderTestUtils.assertTranslator(translatorName, maybeTranslator, ProviderTestUtils.TestOpTranslator.class);
        // we should not obtain operator for different operator class
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.SecondTestOperator>> maybeSecondTranslator = provider.findTranslator(of());
        Assert.assertFalse(maybeSecondTranslator.isPresent());
    }

    @Test
    public void testPredicateToTranslatorFunction() {
        AtomicBoolean predicateEvalValue = new AtomicBoolean(false);
        String translatorName = "translator";
        GenericTranslatorProvider provider = GenericTranslatorProvider.newBuilder().register(( op) -> predicateEvalValue.get(), ofName(translatorName)).build();
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.TestOperator>> maybeTranslator;
        maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        Assert.assertFalse(maybeTranslator.isPresent());
        Optional<OperatorTranslator<Void, Void, ProviderTestUtils.SecondTestOperator>> maybeSecondTranslator = provider.findTranslator(of());
        Assert.assertFalse(maybeSecondTranslator.isPresent());
        predicateEvalValue.set(true);
        // now predicate will return true and we should get our translator
        maybeTranslator = provider.findTranslator(ProviderTestUtils.TestOperator.of());
        ProviderTestUtils.assertTranslator(translatorName, maybeTranslator, ProviderTestUtils.AnyOpTranslator.class);
        // we should get our translator for every operator's type
        maybeSecondTranslator = provider.findTranslator(of());
        Assert.assertTrue(maybeSecondTranslator.isPresent());
    }
}

