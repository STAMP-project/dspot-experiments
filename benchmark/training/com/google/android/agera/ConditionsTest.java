/**
 * Copyright 2015 Google Inc. All Rights Reserved.
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
package com.google.android.agera;


import com.google.android.agera.test.matchers.ConditionApplies;
import com.google.android.agera.test.matchers.HasPrivateConstructor;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ConditionsTest {
    private static final int VALUE = 1;

    @Mock
    private Condition mockConditionFalse;

    @Mock
    private Condition mockConditionTrue;

    @Mock
    private Predicate<Integer> mockPredicateFalse;

    @Mock
    private Predicate<Integer> mockPredicateTrue;

    @Mock
    private Supplier<Integer> mockValueSupplier;

    @Test
    public void shouldReturnTrueForTrueCondition() {
        MatcherAssert.assertThat(Conditions.trueCondition(), ConditionApplies.applies());
    }

    @Test
    public void shouldReturnFalseForFalseCondition() {
        MatcherAssert.assertThat(Conditions.falseCondition(), ConditionApplies.doesNotApply());
    }

    @Test
    public void shouldReturnTrueForTrueStaticCondition() {
        MatcherAssert.assertThat(Conditions.staticCondition(true), Matchers.sameInstance(Conditions.trueCondition()));
    }

    @Test
    public void shouldReturnFalseForFalseStaticCondition() {
        MatcherAssert.assertThat(Conditions.staticCondition(false), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldNegateTrueCondition() {
        MatcherAssert.assertThat(Conditions.not(Conditions.trueCondition()), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldNegateFalseCondition() {
        MatcherAssert.assertThat(Conditions.not(Conditions.falseCondition()), Matchers.sameInstance(Conditions.trueCondition()));
    }

    @Test
    public void shouldNegateNonStaticFalseCondition() {
        MatcherAssert.assertThat(Conditions.not(mockConditionFalse), ConditionApplies.applies());
    }

    @Test
    public void shouldNegateNonStaticTrueCondition() {
        MatcherAssert.assertThat(Conditions.not(mockConditionTrue), ConditionApplies.doesNotApply());
    }

    @Test
    public void shouldReturnOriginalConditionIfNegatedTwice() {
        MatcherAssert.assertThat(Conditions.not(Conditions.not(mockConditionFalse)), Matchers.is(Matchers.sameInstance(mockConditionFalse)));
    }

    @Test
    public void shouldReturnTrueForAllWithNoConditions() {
        MatcherAssert.assertThat(Conditions.all(), Matchers.sameInstance(Conditions.trueCondition()));
    }

    @Test
    public void shouldReturnOriginalConditionIfAllOfOne() {
        MatcherAssert.assertThat(Conditions.all(mockConditionFalse), Matchers.is(Matchers.sameInstance(mockConditionFalse)));
    }

    @Test
    public void shouldReturnTrueForAllWithTrueConditions() {
        MatcherAssert.assertThat(Conditions.all(Conditions.trueCondition(), Conditions.trueCondition()), Matchers.sameInstance(Conditions.trueCondition()));
    }

    @Test
    public void shouldReturnFalseForAllWithOneFalseCondition() {
        MatcherAssert.assertThat(Conditions.all(Conditions.trueCondition(), Conditions.falseCondition()), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldReturnTrueForAllWithNonStaticTrueConditions() {
        MatcherAssert.assertThat(Conditions.all(mockConditionTrue, mockConditionTrue), ConditionApplies.applies());
    }

    @Test
    public void shouldReturnFalseForAllWithNonStaticOneFalseCondition() {
        MatcherAssert.assertThat(Conditions.all(mockConditionTrue, mockConditionFalse), ConditionApplies.doesNotApply());
    }

    @Test
    public void shouldReturnFalseForAllWithNonStaticOneStaticFalseCondition() {
        MatcherAssert.assertThat(Conditions.all(mockConditionTrue, Conditions.falseCondition()), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldReturnFalseForAnyWithNoConditions() {
        MatcherAssert.assertThat(Conditions.any(), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldReturnOriginalConditionIfAnyOfOne() {
        MatcherAssert.assertThat(Conditions.any(mockConditionFalse), Matchers.is(Matchers.sameInstance(mockConditionFalse)));
    }

    @Test
    public void shouldReturnTrueForAnyWithOneTrueCondition() {
        MatcherAssert.assertThat(Conditions.any(Conditions.trueCondition(), Conditions.falseCondition()), Matchers.sameInstance(Conditions.trueCondition()));
    }

    @Test
    public void shouldReturnFalseForAnyWithNoTrueCondition() {
        MatcherAssert.assertThat(Conditions.any(Conditions.falseCondition(), Conditions.falseCondition()), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldReturnTrueForAnyWithNonStaticOneTrueCondition() {
        MatcherAssert.assertThat(Conditions.any(mockConditionTrue, mockConditionFalse), ConditionApplies.applies());
    }

    @Test
    public void shouldReturnFalseForAnyWithNonStaticNoTrueCondition() {
        MatcherAssert.assertThat(Conditions.any(mockConditionFalse, mockConditionFalse), ConditionApplies.doesNotApply());
    }

    @Test
    public void shouldReturnTrueForTruePredicateAsCondition() {
        MatcherAssert.assertThat(Conditions.predicateAsCondition(Predicates.truePredicate(), mockValueSupplier), Matchers.sameInstance(Conditions.trueCondition()));
    }

    @Test
    public void shouldReturnFalseForFalsePredicateAsCondition() {
        MatcherAssert.assertThat(Conditions.predicateAsCondition(Predicates.falsePredicate(), mockValueSupplier), Matchers.sameInstance(Conditions.falseCondition()));
    }

    @Test
    public void shouldPassSupplierObjectToPredicateForTruePredicateAsCondition() {
        MatcherAssert.assertThat(Conditions.predicateAsCondition(mockPredicateTrue, mockValueSupplier), ConditionApplies.applies());
    }

    @Test
    public void shouldPassSupplierObjectToPredicateForFalsePredicateAsCondition() {
        MatcherAssert.assertThat(Conditions.predicateAsCondition(mockPredicateFalse, mockValueSupplier), ConditionApplies.doesNotApply());
        Mockito.verify(mockPredicateFalse).apply(ConditionsTest.VALUE);
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Conditions.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

