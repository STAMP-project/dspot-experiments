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


import com.google.android.agera.test.matchers.HasPrivateConstructor;
import com.google.android.agera.test.matchers.PredicateApply;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class PredicatesTest {
    private static final String ITEM = "item";

    private static final String OTHER_ITEM = "otheritem";

    @Mock
    private Predicate<Object> mockPredicateFalse;

    @Mock
    private Predicate<Object> mockPredicateTrue;

    @Mock
    private Condition mockCondition;

    @Test
    public void shouldReturnTruePredicateForTrueConditionInConditionAsPredicate() {
        MatcherAssert.assertThat(Predicates.conditionAsPredicate(Conditions.trueCondition()), Matchers.sameInstance(Predicates.truePredicate()));
    }

    @Test
    public void shouldReturnFalsePredicateForFalseConditionInConditionAsPredicate() {
        MatcherAssert.assertThat(Predicates.conditionAsPredicate(Conditions.falseCondition()), Matchers.sameInstance(Predicates.falsePredicate()));
    }

    @Test
    public void shouldReturnFalseForConditionWithFalseInConditionAsPredicate() {
        MatcherAssert.assertThat(Predicates.conditionAsPredicate(mockCondition), PredicateApply.doesNotApplyFor(new Object()));
        Mockito.verify(mockCondition).applies();
    }

    @Test
    public void shouldNegateTruePredicate() {
        MatcherAssert.assertThat(Predicates.not(Predicates.truePredicate()), Matchers.sameInstance(Predicates.falsePredicate()));
    }

    @Test
    public void shouldNegateFalsePredicate() {
        MatcherAssert.assertThat(Predicates.not(Predicates.falsePredicate()), Matchers.sameInstance(Predicates.truePredicate()));
    }

    @Test
    public void shouldNegateNonStaticFalseCondition() {
        MatcherAssert.assertThat(Predicates.not(mockPredicateFalse), PredicateApply.appliesFor(new Object()));
    }

    @Test
    public void shouldNegateNonStaticTrueCondition() {
        MatcherAssert.assertThat(Predicates.not(mockPredicateTrue), PredicateApply.doesNotApplyFor(new Object()));
    }

    @Test
    public void shouldNegatePredicateTwice() {
        MatcherAssert.assertThat(Predicates.not(Predicates.not(Predicates.truePredicate())), PredicateApply.appliesFor(new Object()));
    }

    @Test
    public void shouldReturnOriginalPredicateIfNegatedTwice() {
        MatcherAssert.assertThat(Predicates.not(Predicates.not(mockPredicateFalse)), Matchers.is(Matchers.sameInstance(mockPredicateFalse)));
    }

    @Test
    public void shouldReturnTrueForEmptyStringInEmptyStringPredicate() {
        MatcherAssert.assertThat(Predicates.emptyString(), PredicateApply.appliesFor(""));
    }

    @Test
    public void shouldReturnFalseForStringInEmptyStringPredicate() {
        MatcherAssert.assertThat(Predicates.emptyString(), PredicateApply.doesNotApplyFor("A"));
    }

    @Test
    public void shouldReturnFalseForIncorrectInstanceInInstanceOfPredicate() {
        MatcherAssert.assertThat(Predicates.instanceOf(Integer.class), PredicateApply.doesNotApplyFor(1L));
    }

    @Test
    public void shouldReturnTrueForCorrectInstanceInInstanceOfPredicate() {
        MatcherAssert.assertThat(Predicates.instanceOf(Long.class), PredicateApply.appliesFor(1L));
    }

    @Test
    public void shouldReturnTrueForAllWithNoConditions() {
        MatcherAssert.assertThat(Predicates.all(), PredicateApply.appliesFor(new Object()));
    }

    @Test
    public void shouldReturnOriginalPredicateIfAllOfOne() {
        MatcherAssert.assertThat(Predicates.all(mockPredicateFalse), Matchers.is(Matchers.sameInstance(mockPredicateFalse)));
    }

    @Test
    public void shouldReturnTrueForAllWithTrueConditions() {
        MatcherAssert.assertThat(Predicates.all(Predicates.truePredicate(), Predicates.truePredicate()), PredicateApply.appliesFor(new Object()));
    }

    @Test
    public void shouldReturnFalseForAllWithOneFalseCondition() {
        MatcherAssert.assertThat(Predicates.all(Predicates.truePredicate(), Predicates.falsePredicate()), PredicateApply.doesNotApplyFor(new Object()));
    }

    @Test
    public void shouldReturnFalseForAnyWithNoConditions() {
        MatcherAssert.assertThat(Predicates.any(), PredicateApply.doesNotApplyFor(new Object()));
    }

    @Test
    public void shouldReturnOriginalPredicateIfAnyOfOne() {
        MatcherAssert.assertThat(Predicates.any(mockPredicateFalse), Matchers.is(Matchers.sameInstance(mockPredicateFalse)));
    }

    @Test
    public void shouldReturnTrueForAnyWithOneTrueCondition() {
        MatcherAssert.assertThat(Predicates.any(Predicates.truePredicate(), Predicates.falsePredicate()), PredicateApply.appliesFor(new Object()));
    }

    @Test
    public void shouldReturnFalseForAnyWithNoTrueCondition() {
        MatcherAssert.assertThat(Predicates.any(Predicates.falsePredicate(), Predicates.falsePredicate()), PredicateApply.doesNotApplyFor(new Object()));
    }

    @Test
    public void shouldReturnFalseForEqualToWhenNotEqual() {
        MatcherAssert.assertThat(Predicates.equalTo(PredicatesTest.ITEM), PredicateApply.doesNotApplyFor(PredicatesTest.OTHER_ITEM));
    }

    @Test
    public void shouldReturnTrueForEqualToWhenEqual() {
        MatcherAssert.assertThat(Predicates.equalTo(PredicatesTest.ITEM), PredicateApply.appliesFor(PredicatesTest.ITEM));
    }

    @Test
    public void shouldReturnFalseForAnyWithNonStaticNoTrueCondition() {
        MatcherAssert.assertThat(Predicates.any(mockPredicateFalse, mockPredicateFalse), PredicateApply.doesNotApplyFor(new Object()));
    }

    @Test
    public void shouldReturnTrueForAllWithNonStaticTrueConditions() {
        MatcherAssert.assertThat(Predicates.all(mockPredicateTrue, mockPredicateTrue), PredicateApply.appliesFor(new Object()));
    }

    @Test
    public void shouldReturnFalseForAllWithNonStaticOneFalseCondition() {
        MatcherAssert.assertThat(Predicates.all(mockPredicateTrue, mockPredicateFalse), PredicateApply.doesNotApplyFor(new Object()));
    }

    @Test
    public void shouldReturnFalseForAllWithNonStaticOneStaticFalseCondition() {
        MatcherAssert.assertThat(Predicates.all(mockPredicateTrue, Predicates.falsePredicate()), Matchers.sameInstance(Predicates.falsePredicate()));
    }

    @Test
    public void shouldHavePrivateConstructor() {
        MatcherAssert.assertThat(Predicates.class, HasPrivateConstructor.hasPrivateConstructor());
    }
}

