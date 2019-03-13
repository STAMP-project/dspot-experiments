/**
 * Copyright 2018 Confluent Inc.
 *
 * Licensed under the Confluent Community License (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 *
 * http://www.confluent.io/confluent-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package io.confluent.ksql.util;


import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.confluent.ksql.util.HandlerMaps.ClassHandlerMap0;
import io.confluent.ksql.util.HandlerMaps.ClassHandlerMap1;
import io.confluent.ksql.util.HandlerMaps.ClassHandlerMap2;
import io.confluent.ksql.util.HandlerMaps.ClassHandlerMapR2;
import io.confluent.ksql.util.HandlerMaps.Handler0;
import io.confluent.ksql.util.HandlerMaps.Handler1;
import io.confluent.ksql.util.HandlerMaps.Handler2;
import io.confluent.ksql.util.HandlerMaps.HandlerR0;
import io.confluent.ksql.util.HandlerMaps.HandlerR1;
import io.confluent.ksql.util.HandlerMaps.HandlerR2;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;


@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class HandlerMapsTest {
    // This field is a compile time test.
    @SuppressWarnings("unused")
    private static final ClassHandlerMap0<HandlerMapsTest.BaseType> STATIC_TEST_0 = // <-- super type lambda
    // <-- lambda
    // <-- static function
    // <-- super type handler
    // <-- member function
    HandlerMaps.forClass(HandlerMapsTest.BaseType.class).put(HandlerMapsTest.BaseType.class, HandlerMapsTest::staticHandlerBase).put(HandlerMapsTest.LeafTypeB.class, HandlerMapsTest::staticHandlerBase).put(HandlerMapsTest.LeafTypeC.class, HandlerMapsTest::staticHandlerC).put(HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.HandlerD0::new).put(HandlerMapsTest.LeafTypeF.class, HandlerMapsTest.HandlerBase0::new).build();

    // This field is a compile time test.
    @SuppressWarnings("unused")
    private static final ClassHandlerMap1<HandlerMapsTest.BaseType, HandlerMapsTest> STATIC_TEST_1 = // <-- super type lambda
    // <-- one-arg lambda
    // <-- no-arg lambda
    // <-- static function
    // <-- super type handler
    // <-- member function
    // <-- member function
    HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgType(HandlerMapsTest.class).put(HandlerMapsTest.BaseType.class, HandlerMapsTest::baseHandler1).put(HandlerMapsTest.LeafTypeA.class, HandlerMapsTest::leafAHandler1).put(HandlerMapsTest.LeafTypeB.class, HandlerMapsTest::baseHandler1).put(HandlerMapsTest.LeafTypeC.class, HandlerMapsTest::staticHandlerC).put0(HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.HandlerD0::new).put(HandlerMapsTest.LeafTypeE.class, HandlerMapsTest.HandlerE1::new).put(HandlerMapsTest.LeafTypeF.class, HandlerMapsTest.HandlerBase1::new).build();

    // This field is a compile time test.
    @SuppressWarnings("unused")
    private static final ClassHandlerMap2<HandlerMapsTest.BaseType, HandlerMapsTest, String> STATIC_TEST_2 = // <-- super type lambda
    // <-- two-arg lambda
    // <-- one-arg lambda
    // <-- no-arg lambda
    // <-- static function
    // <-- super type handler
    // <-- one-arg function
    // <-- member function
    HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(HandlerMapsTest.class, String.class).put(HandlerMapsTest.BaseType.class, HandlerMapsTest::baseHandler2).put(HandlerMapsTest.LeafTypeA.class, HandlerMapsTest::leafAHandler1).put(HandlerMapsTest.LeafTypeB.class, HandlerMapsTest::baseHandler2).put(HandlerMapsTest.LeafTypeC.class, HandlerMapsTest::staticHandlerC).put0(HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.HandlerD0::new).put1(HandlerMapsTest.LeafTypeE.class, HandlerMapsTest.HandlerE1::new).put(HandlerMapsTest.LeafTypeF.class, HandlerMapsTest.HandlerF2::new).put(HandlerMapsTest.LeafTypeG.class, HandlerMapsTest.HandlerBase2::new).build();

    // This field is a compile time test.
    @SuppressWarnings("unused")
    private static final ClassHandlerMapR2<HandlerMapsTest.BaseType, HandlerMapsTest.TwoArgsWithReturnValue, String, Number> STATIC_TEST_R2 = // <-- super type lambda
    // <-- two-arg lambda
    // <-- one-arg lambda
    // <-- no-arg lambda
    // <-- static function
    // <-- super type handler
    // <-- one-arg function
    // <-- member function
    HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(HandlerMapsTest.TwoArgsWithReturnValue.class, String.class).withReturnType(Number.class).put(HandlerMapsTest.BaseType.class, HandlerMapsTest.TwoArgsWithReturnValue::baseHandler2).put(HandlerMapsTest.LeafTypeA.class, HandlerMapsTest.TwoArgsWithReturnValue::leafAHandler1).put(HandlerMapsTest.LeafTypeB.class, HandlerMapsTest.TwoArgsWithReturnValue::baseHandler2).put(HandlerMapsTest.LeafTypeC.class, HandlerMapsTest.TwoArgsWithReturnValue::staticHandlerC).put0(HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.HandlerRD0::new).put1(HandlerMapsTest.LeafTypeE.class, HandlerMapsTest.HandlerRE1::new).put(HandlerMapsTest.LeafTypeF.class, HandlerMapsTest.HandlerRF2::new).put(HandlerMapsTest.LeafTypeG.class, HandlerMapsTest.HandlerBaseR2::new).build();

    private static final HandlerMapsTest.BaseType BASE = new HandlerMapsTest.BaseType();

    private static final HandlerMapsTest.LeafTypeA LEAF_A = new HandlerMapsTest.LeafTypeA();

    @Mock(name = "0_1")
    private Handler0<HandlerMapsTest.BaseType> handler0_1;

    @Mock(name = "0_2")
    private Handler0<HandlerMapsTest.BaseType> handler0_2;

    @Mock(name = "0_3")
    private Handler0<HandlerMapsTest.BaseType> handler0_3;

    @Mock(name = "1_1")
    private Handler1<HandlerMapsTest.BaseType, String> handler1_1;

    @Mock(name = "1_2")
    private Handler1<HandlerMapsTest.BaseType, String> handler1_2;

    @Mock(name = "1_3")
    private Handler1<HandlerMapsTest.BaseType, Object> handler1_3;

    @Mock(name = "2_1")
    private Handler2<HandlerMapsTest.BaseType, String, Integer> handler2_1;

    @Mock(name = "2_2")
    private Handler2<HandlerMapsTest.BaseType, String, Number> handler2_2;

    @Mock(name = "2_3")
    private Handler2<HandlerMapsTest.BaseType, String, Object> handler2_3;

    @Mock(name = "R2_1")
    private HandlerR2<HandlerMapsTest.BaseType, String, Integer, Long> handlerR2_1;

    @Mock(name = "R2_2")
    private HandlerR2<HandlerMapsTest.BaseType, String, Number, Double> handlerR2_2;

    @Mock(name = "R2_3")
    private HandlerR2<HandlerMapsTest.BaseType, String, Object, Number> handlerR2_3;

    private ClassHandlerMap0<HandlerMapsTest.BaseType> handlerMap0;

    private ClassHandlerMap1<HandlerMapsTest.BaseType, String> handlerMap1;

    private ClassHandlerMap2<HandlerMapsTest.BaseType, String, Integer> handlerMap2;

    private ClassHandlerMapR2<HandlerMapsTest.BaseType, String, Integer, Number> handlerMapR2;

    @Test
    public void shouldGetHandlerByType0() {
        // When:
        handlerMap0.get(HandlerMapsTest.LeafTypeA.class).handle(HandlerMapsTest.LEAF_A);
        handlerMap0.get(HandlerMapsTest.BaseType.class).handle(HandlerMapsTest.BASE);
        // Then:
        Mockito.verify(handler0_1).handle(HandlerMapsTest.LEAF_A);
        Mockito.verify(handler0_2).handle(HandlerMapsTest.BASE);
    }

    @Test
    public void shouldGetHandlerByType1() {
        // When:
        handlerMap1.get(HandlerMapsTest.LeafTypeA.class).handle("a", HandlerMapsTest.LEAF_A);
        handlerMap1.get(HandlerMapsTest.BaseType.class).handle("b", HandlerMapsTest.BASE);
        // Then:
        Mockito.verify(handler1_1).handle("a", HandlerMapsTest.LEAF_A);
        Mockito.verify(handler1_2).handle("b", HandlerMapsTest.BASE);
    }

    @Test
    public void shouldGetHandlerByType2() {
        // When:
        handlerMap2.get(HandlerMapsTest.LeafTypeA.class).handle("a", 1, HandlerMapsTest.LEAF_A);
        handlerMap2.get(HandlerMapsTest.BaseType.class).handle("b", 2, HandlerMapsTest.BASE);
        // Then:
        Mockito.verify(handler2_1).handle("a", 1, HandlerMapsTest.LEAF_A);
        Mockito.verify(handler2_2).handle("b", 2, HandlerMapsTest.BASE);
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @Test
    public void shouldGetHandlerByTypeR2() {
        // When:
        final Number r1 = handlerMapR2.get(HandlerMapsTest.LeafTypeA.class).handle("a", 1, HandlerMapsTest.LEAF_A);
        final Number r2 = handlerMapR2.get(HandlerMapsTest.BaseType.class).handle("b", 2, HandlerMapsTest.BASE);
        // Then:
        Mockito.verify(handlerR2_1).handle("a", 1, HandlerMapsTest.LEAF_A);
        Mockito.verify(handlerR2_2).handle("b", 2, HandlerMapsTest.BASE);
        MatcherAssert.assertThat(r1, Matchers.is(1L));
        MatcherAssert.assertThat(r2, Matchers.is(2.2));
    }

    @Test
    public void shouldReturnNullIfTypeNotFound0() {
        MatcherAssert.assertThat(handlerMap0.get(HandlerMapsTest.MissingType.class), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnNullIfTypeNotFound1() {
        MatcherAssert.assertThat(handlerMap1.get(HandlerMapsTest.MissingType.class), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnNullIfTypeNotFound2() {
        MatcherAssert.assertThat(handlerMap2.get(HandlerMapsTest.MissingType.class), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnNullIfTypeNotFoundR2() {
        MatcherAssert.assertThat(handlerMapR2.get(HandlerMapsTest.MissingType.class), Matchers.is(Matchers.nullValue()));
    }

    @Test
    public void shouldReturnDefaultIfTypeNotFound0() {
        MatcherAssert.assertThat(handlerMap0.getOrDefault(HandlerMapsTest.MissingType.class, handler0_3), Matchers.is(Matchers.sameInstance(handler0_3)));
    }

    @Test
    public void shouldReturnDefaultIfTypeNotFound1() {
        MatcherAssert.assertThat(handlerMap1.getOrDefault(HandlerMapsTest.MissingType.class, handler1_3), Matchers.is(Matchers.sameInstance(handler1_3)));
    }

    @Test
    public void shouldReturnDefaultIfTypeNotFound2() {
        MatcherAssert.assertThat(handlerMap2.getOrDefault(HandlerMapsTest.MissingType.class, handler2_3), Matchers.is(Matchers.sameInstance(handler2_3)));
    }

    @Test
    public void shouldReturnDefaultIfTypeNotFoundR2() {
        MatcherAssert.assertThat(handlerMapR2.getOrDefault(HandlerMapsTest.MissingType.class, handlerR2_3), Matchers.is(Matchers.sameInstance(handlerR2_3)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnDuplicateKey0() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).put(HandlerMapsTest.LeafTypeA.class, handler0_1).put(HandlerMapsTest.LeafTypeA.class, handler0_2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnDuplicateKey1() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgType(String.class).put(HandlerMapsTest.LeafTypeA.class, handler1_1).put(HandlerMapsTest.LeafTypeA.class, handler1_2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnDuplicateKey2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).put(HandlerMapsTest.LeafTypeA.class, handler2_1).put(HandlerMapsTest.LeafTypeA.class, handler2_2);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowOnDuplicateKeyR2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).withReturnType(Number.class).put(HandlerMapsTest.LeafTypeA.class, handlerR2_1).put(HandlerMapsTest.LeafTypeA.class, handlerR2_2);
    }

    @Test
    public void shouldNotThrowOnDuplicateHandler0() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).put(HandlerMapsTest.LeafTypeA.class, handler0_1).put(HandlerMapsTest.LeafTypeB.class, handler0_1);
    }

    @Test
    public void shouldNotThrowOnDuplicateHandler1() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgType(String.class).put(HandlerMapsTest.LeafTypeA.class, handler1_1).put(HandlerMapsTest.LeafTypeB.class, handler1_1);
    }

    @Test
    public void shouldNotThrowOnDuplicateHandler2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).put(HandlerMapsTest.LeafTypeA.class, handler2_1).put(HandlerMapsTest.LeafTypeB.class, handler2_1);
    }

    @Test
    public void shouldNotThrowOnDuplicateHandlerR2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).withReturnType(Number.class).put(HandlerMapsTest.LeafTypeA.class, handlerR2_1).put(HandlerMapsTest.LeafTypeB.class, handlerR2_1);
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowIfHandlerPassedWrongSubType0() {
        handlerMap0.get(HandlerMapsTest.LeafTypeA.class).handle(HandlerMapsTest.BASE);
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowIfHandlerPassedWrongSubType1() {
        handlerMap1.get(HandlerMapsTest.LeafTypeA.class).handle("a", HandlerMapsTest.BASE);
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowIfHandlerPassedWrongSubType2() {
        handlerMap2.get(HandlerMapsTest.LeafTypeA.class).handle("a", 1, HandlerMapsTest.BASE);
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowIfHandlerPassedWrongSubTypeR2() {
        handlerMap2.get(HandlerMapsTest.LeafTypeA.class).handle("a", 1, HandlerMapsTest.BASE);
    }

    @Test
    public void shouldWorkWithSuppliers0() {
        // Given:
        handlerMap0 = HandlerMaps.forClass(HandlerMapsTest.BaseType.class).put(HandlerMapsTest.LeafTypeA.class, () -> handler0_1).build();
        // When:
        handlerMap0.get(HandlerMapsTest.LeafTypeA.class).handle(HandlerMapsTest.LEAF_A);
        // Then:
        Mockito.verify(handler0_1).handle(HandlerMapsTest.LEAF_A);
    }

    @Test
    public void shouldWorkWithSuppliers1() {
        // Given:
        handlerMap1 = HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgType(String.class).put(HandlerMapsTest.LeafTypeA.class, () -> handler1_1).build();
        // When:
        handlerMap1.get(HandlerMapsTest.LeafTypeA.class).handle("A", HandlerMapsTest.LEAF_A);
        // Then:
        Mockito.verify(handler1_1).handle("A", HandlerMapsTest.LEAF_A);
    }

    @Test
    public void shouldWorkWithSuppliers2() {
        // Given:
        handlerMap2 = HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).put(HandlerMapsTest.LeafTypeA.class, () -> handler2_1).build();
        // When:
        handlerMap2.get(HandlerMapsTest.LeafTypeA.class).handle("A", 2, HandlerMapsTest.LEAF_A);
        // Then:
        Mockito.verify(handler2_1).handle("A", 2, HandlerMapsTest.LEAF_A);
    }

    @SuppressFBWarnings("RV_RETURN_VALUE_IGNORED_NO_SIDE_EFFECT")
    @Test
    public void shouldWorkWithSuppliersR2() {
        // Given:
        handlerMapR2 = HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).withReturnType(Number.class).put(HandlerMapsTest.LeafTypeA.class, () -> handlerR2_1).build();
        // When:
        handlerMapR2.get(HandlerMapsTest.LeafTypeA.class).handle("A", 2, HandlerMapsTest.LEAF_A);
        // Then:
        Mockito.verify(handlerR2_1).handle("A", 2, HandlerMapsTest.LEAF_A);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierThrows0() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).put(HandlerMapsTest.LeafTypeA.class, () -> {
            throw new RuntimeException("Boom");
        }).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierThrows1() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgType(String.class).put(HandlerMapsTest.LeafTypeA.class, () -> {
            throw new RuntimeException("Boom");
        }).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierThrows2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).put(HandlerMapsTest.LeafTypeA.class, () -> {
            throw new RuntimeException("Boom");
        }).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierThrowsR2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).withReturnType(Number.class).put(HandlerMapsTest.LeafTypeA.class, () -> {
            throw new RuntimeException("Boom");
        }).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierReturnsNullHandler0() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).put(HandlerMapsTest.LeafTypeA.class, () -> null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierReturnsNullHandler1() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgType(String.class).put(HandlerMapsTest.LeafTypeA.class, () -> null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierReturnsNullHandler2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).put(HandlerMapsTest.LeafTypeA.class, () -> null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfHandlerSupplierReturnsNullHandlerR2() {
        HandlerMaps.forClass(HandlerMapsTest.BaseType.class).withArgTypes(String.class, Integer.class).withReturnType(Number.class).put(HandlerMapsTest.LeafTypeA.class, () -> null).build();
    }

    // Compile-time check
    @SuppressFBWarnings({ "DLS_DEAD_LOCAL_STORE", "UC_USELESS_VOID_METHOD" })
    @SuppressWarnings("unused")
    @Test
    public void shouldReturnedTypedHandler() {
        // When:
        final Handler0<HandlerMapsTest.LeafTypeA> typedHandler0 = handlerMap0.getTyped(HandlerMapsTest.LeafTypeA.class);
        final Handler1<HandlerMapsTest.LeafTypeA, String> typedHandler1 = handlerMap1.getTyped(HandlerMapsTest.LeafTypeA.class);
        final Handler2<HandlerMapsTest.LeafTypeA, String, Integer> typedHandler2 = handlerMap2.getTyped(HandlerMapsTest.LeafTypeA.class);
        final HandlerR2<HandlerMapsTest.LeafTypeA, String, Integer, Number> typedHandlerR2 = handlerMapR2.getTyped(HandlerMapsTest.LeafTypeA.class);
        // Then:
        // Return value is typed to accept derived type, not base, and no cast exception was thrown.
    }

    @Test
    public void shouldGetKeySetFrom0() {
        MatcherAssert.assertThat(HandlerMapsTest.STATIC_TEST_0.keySet(), Matchers.containsInAnyOrder(HandlerMapsTest.BaseType.class, HandlerMapsTest.LeafTypeB.class, HandlerMapsTest.LeafTypeC.class, HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.LeafTypeF.class));
    }

    @Test
    public void shouldGetKeySetFrom1() {
        MatcherAssert.assertThat(HandlerMapsTest.STATIC_TEST_1.keySet(), Matchers.containsInAnyOrder(HandlerMapsTest.BaseType.class, HandlerMapsTest.LeafTypeA.class, HandlerMapsTest.LeafTypeB.class, HandlerMapsTest.LeafTypeC.class, HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.LeafTypeE.class, HandlerMapsTest.LeafTypeF.class));
    }

    @Test
    public void shouldGetKeySetFrom2() {
        MatcherAssert.assertThat(HandlerMapsTest.STATIC_TEST_2.keySet(), Matchers.containsInAnyOrder(HandlerMapsTest.BaseType.class, HandlerMapsTest.LeafTypeA.class, HandlerMapsTest.LeafTypeB.class, HandlerMapsTest.LeafTypeC.class, HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.LeafTypeE.class, HandlerMapsTest.LeafTypeF.class, HandlerMapsTest.LeafTypeG.class));
    }

    @Test
    public void shouldGetKeySetFromR2() {
        MatcherAssert.assertThat(HandlerMapsTest.STATIC_TEST_R2.keySet(), Matchers.containsInAnyOrder(HandlerMapsTest.BaseType.class, HandlerMapsTest.LeafTypeA.class, HandlerMapsTest.LeafTypeB.class, HandlerMapsTest.LeafTypeC.class, HandlerMapsTest.LeafTypeD.class, HandlerMapsTest.LeafTypeE.class, HandlerMapsTest.LeafTypeF.class, HandlerMapsTest.LeafTypeG.class));
    }

    private static class BaseType {}

    private static class LeafTypeA extends HandlerMapsTest.BaseType {}

    private static class LeafTypeB extends HandlerMapsTest.BaseType {}

    private static class LeafTypeC extends HandlerMapsTest.BaseType {}

    private static class LeafTypeD extends HandlerMapsTest.BaseType {}

    private static class LeafTypeE extends HandlerMapsTest.BaseType {}

    private static class LeafTypeF extends HandlerMapsTest.BaseType {}

    private static class LeafTypeG extends HandlerMapsTest.BaseType {}

    private static class MissingType extends HandlerMapsTest.BaseType {}

    @SuppressWarnings({ "unused", "MethodMayBeStatic" })
    private static final class TwoArgsWithReturnValue {
        private Integer baseHandler2(final String arg, final HandlerMapsTest.BaseType type) {
            return null;
        }

        private Number leafAHandler1(final HandlerMapsTest.LeafTypeA type) {
            return null;
        }

        private static Long staticHandlerC(final HandlerMapsTest.LeafTypeC type) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerBase0 implements Handler0<HandlerMapsTest.BaseType> {
        @Override
        public void handle(final HandlerMapsTest.BaseType key) {
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerBase1 implements Handler1<HandlerMapsTest.BaseType, HandlerMapsTest> {
        @Override
        public void handle(final HandlerMapsTest arg0, final HandlerMapsTest.BaseType key) {
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerBase2 implements Handler2<HandlerMapsTest.BaseType, HandlerMapsTest, String> {
        @Override
        public void handle(final HandlerMapsTest arg0, final String arg1, final HandlerMapsTest.BaseType key) {
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerD0 implements Handler0<HandlerMapsTest.LeafTypeD> {
        @Override
        public void handle(final HandlerMapsTest.LeafTypeD key) {
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerE1 implements Handler1<HandlerMapsTest.LeafTypeE, HandlerMapsTest> {
        @Override
        public void handle(final HandlerMapsTest arg0, final HandlerMapsTest.LeafTypeE key) {
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerF2 implements Handler2<HandlerMapsTest.LeafTypeF, HandlerMapsTest, String> {
        @Override
        public void handle(final HandlerMapsTest arg0, final String arg1, final HandlerMapsTest.LeafTypeF key) {
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerRD0 implements HandlerR0<HandlerMapsTest.LeafTypeD, Number> {
        @Override
        public Number handle(final HandlerMapsTest.LeafTypeD key) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerRE1 implements HandlerR1<HandlerMapsTest.LeafTypeE, HandlerMapsTest.TwoArgsWithReturnValue, Long> {
        @Override
        public Long handle(final HandlerMapsTest.TwoArgsWithReturnValue arg0, final HandlerMapsTest.LeafTypeE key) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerRF2 implements HandlerR2<HandlerMapsTest.LeafTypeF, HandlerMapsTest.TwoArgsWithReturnValue, String, Integer> {
        @Override
        public Integer handle(final HandlerMapsTest.TwoArgsWithReturnValue arg0, final String arg1, final HandlerMapsTest.LeafTypeF key) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    private static final class HandlerBaseR2 implements HandlerR2<HandlerMapsTest.BaseType, HandlerMapsTest.TwoArgsWithReturnValue, String, Double> {
        @Override
        public Double handle(final HandlerMapsTest.TwoArgsWithReturnValue arg0, final String arg1, final HandlerMapsTest.BaseType key) {
            return null;
        }
    }
}

