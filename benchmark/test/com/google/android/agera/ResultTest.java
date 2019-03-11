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


import nl.jqno.equalsverifier.EqualsVerifier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;


public final class ResultTest {
    @SuppressWarnings("ThrowableInstanceNeverThrown")
    private static final Throwable THROWABLE = new Throwable();

    private static final int VALUE = 42;

    private static final String STRING_VALUE = "stringvalue";

    private static final int OTHER_VALUE = 1;

    private static final float FLOAT_VALUE = 2;

    private static final Result<Integer> SUCCESS_WITH_VALUE = Result.success(ResultTest.VALUE);

    private static final Result<Integer> SUCCESS_WITH_OTHER_VALUE = Result.success(ResultTest.OTHER_VALUE);

    private static final Result<Float> SUCCESS_WITH_FLOAT_VALUE = Result.success(ResultTest.FLOAT_VALUE);

    private static final Result<Integer> FAILURE_WITH_THROWABLE = Result.failure(ResultTest.THROWABLE);

    private static final Result<Integer> FAILURE = Result.failure();

    private static final Result<Integer> PRESENT_WITH_VALUE = Result.present(ResultTest.VALUE);

    private static final Result<Integer> ABSENT = Result.absent();

    @Mock
    private Function<Integer, Result<Integer>> mockSucceededValueFunction;

    @Mock
    private Function<Integer, Result<Integer>> mockFailedFunction;

    @Mock
    private Function<Integer, Integer> mockValueFunction;

    @Mock
    private Function<Throwable, Integer> mockRecoverValueFunction;

    @Mock
    private Function<Throwable, Result<Integer>> mockAttemptRecoverValueFunction;

    @Mock
    private Binder<Integer, String> mockBinder;

    @Mock
    private Merger<Integer, String, Float> mockMerger;

    @Mock
    private Merger<Integer, String, Result<Float>> mockAttemptMerger;

    @Mock
    private Supplier<String> mockSupplier;

    @Mock
    private Receiver<Integer> mockReceiver;

    @Mock
    private Receiver<Throwable> mockThrowableReceiver;

    @Mock
    private Binder<Throwable, String> mockThrowableStringBinder;

    @Mock
    private Supplier<Integer> mockOtherValueSupplier;

    @Mock
    private Supplier<Result<Integer>> mockOtherValueSuccessfulAttemptSupplier;

    @Mock
    private Supplier<Result<Integer>> mockOtherValueFailingAttemptSupplier;

    @Test(expected = FailedResultException.class)
    public void shouldThrowExceptionForGetOfFailure() {
        ResultTest.FAILURE_WITH_THROWABLE.get();
    }

    @Test(expected = FailedResultException.class)
    public void shouldThrowExceptionForGetOfAbsent() {
        ResultTest.ABSENT.get();
    }

    @Test
    public void shouldReturnValueForGetOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.get(), Matchers.equalTo(ResultTest.VALUE));
    }

    @Test
    public void shouldReturnFalseForSucceededOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.succeeded(), Matchers.equalTo(false));
    }

    @Test
    public void shouldReturnTrueForSucceeded() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.succeeded(), Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnTrueForFailedOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.failed(), Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnFalseForFailedOfSucceeded() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.failed(), Matchers.equalTo(false));
    }

    @Test
    public void shouldReturnFalseForIsPresentOfAbsent() {
        MatcherAssert.assertThat(ResultTest.ABSENT.isPresent(), Matchers.equalTo(false));
    }

    @Test
    public void shouldReturnTrueForIsPresentWithValue() {
        MatcherAssert.assertThat(ResultTest.PRESENT_WITH_VALUE.isPresent(), Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnTrueForIsAbsentOfAbsent() {
        MatcherAssert.assertThat(ResultTest.ABSENT.isAbsent(), Matchers.equalTo(true));
    }

    @Test
    public void shouldReturnFalseForIsAbsentWithValue() {
        MatcherAssert.assertThat(ResultTest.PRESENT_WITH_VALUE.isAbsent(), Matchers.equalTo(false));
    }

    @Test
    public void shouldReturnFalseForIsAbsentOfNonAbsentFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE.isAbsent(), Matchers.equalTo(false));
    }

    @Test
    public void shouldReturnAbsentForFailureWithAbsentFailure() {
        MatcherAssert.assertThat(Result.<Integer>failure(ResultTest.ABSENT.getFailure()), Matchers.sameInstance(ResultTest.ABSENT));
    }

    @Test
    public void shouldReturnAbsentForOfNullableWithNull() {
        MatcherAssert.assertThat(Result.<Integer>absentIfNull(null), Matchers.sameInstance(ResultTest.ABSENT));
    }

    @Test
    public void shouldReturnPresentWithValueForOfNullableWithValue() {
        MatcherAssert.assertThat(Result.absentIfNull(ResultTest.VALUE), Matchers.equalTo(ResultTest.PRESENT_WITH_VALUE));
    }

    @Test
    public void shouldReturnExceptionForGetFailureOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE.getFailure(), Matchers.notNullValue());
    }

    @Test
    public void shouldReturnExceptionForGetFailureOfFailureOfExplicitException() {
        MatcherAssert.assertThat(Result.failure(ResultTest.THROWABLE).getFailure(), Matchers.sameInstance(ResultTest.THROWABLE));
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionForGetFailureOfSuccess() {
        ResultTest.SUCCESS_WITH_VALUE.getFailure();
    }

    @Test
    public void shouldReturnValueForOrElseOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.orElse(ResultTest.OTHER_VALUE), Matchers.equalTo(ResultTest.VALUE));
    }

    @Test
    public void shouldReturnValueForOrGetFromOfSuccessWithValue() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.orGetFrom(mockOtherValueSupplier), Matchers.equalTo(ResultTest.VALUE));
        Mockito.verifyZeroInteractions(mockOtherValueSupplier);
    }

    @Test
    public void shouldReturnSameInstanceForOrAttemptGetFromOfSuccessWithValue() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.orAttemptGetFrom(mockOtherValueSuccessfulAttemptSupplier), Matchers.sameInstance(ResultTest.SUCCESS_WITH_VALUE));
        Mockito.verifyZeroInteractions(mockOtherValueSuccessfulAttemptSupplier);
    }

    @Test
    public void shouldReturnElseValueForOrElseOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.orElse(ResultTest.OTHER_VALUE), Matchers.equalTo(ResultTest.OTHER_VALUE));
    }

    @Test
    public void shouldReturnOtherValueForOrGetFromOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.orGetFrom(mockOtherValueSupplier), Matchers.equalTo(ResultTest.OTHER_VALUE));
    }

    @Test
    public void shouldReturnSuccessWithOtherValueForOrAttemptGetFromOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.orAttemptGetFrom(mockOtherValueSuccessfulAttemptSupplier), Matchers.sameInstance(ResultTest.SUCCESS_WITH_OTHER_VALUE));
    }

    @Test
    public void shouldReturnOtherResultForOrAttemptGetFromOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.orAttemptGetFrom(mockOtherValueFailingAttemptSupplier), Matchers.sameInstance(ResultTest.FAILURE));
    }

    @Test
    public void shouldApplySendIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifSucceededSendTo(mockReceiver);
        Mockito.verify(mockReceiver).accept(ResultTest.VALUE);
    }

    @Test
    public void shouldNotApplySendIfFailed() {
        ResultTest.FAILURE_WITH_THROWABLE.ifSucceededSendTo(mockReceiver);
        Mockito.verifyZeroInteractions(mockReceiver);
    }

    @Test
    public void shouldApplyBindIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifSucceededBind(ResultTest.STRING_VALUE, mockBinder);
        Mockito.verify(mockBinder).bind(ResultTest.VALUE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldNotApplyBindIfFailed() {
        ResultTest.FAILURE_WITH_THROWABLE.ifSucceededBind(ResultTest.STRING_VALUE, mockBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
        Mockito.verifyZeroInteractions(mockBinder);
    }

    @Test
    public void shouldApplyBindFromIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifSucceededBindFrom(mockSupplier, mockBinder);
        Mockito.verify(mockBinder).bind(ResultTest.VALUE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldNotApplyBindFromIfFailed() {
        ResultTest.FAILURE_WITH_THROWABLE.ifSucceededBindFrom(mockSupplier, mockBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
        Mockito.verifyZeroInteractions(mockBinder);
    }

    @Test
    public void shouldApplySendIfFailed() {
        Result.failure(ResultTest.THROWABLE).ifFailedSendTo(mockThrowableReceiver);
        Mockito.verify(mockThrowableReceiver).accept(ResultTest.THROWABLE);
    }

    @Test
    public void shouldApplySendIfFailedExceptAbsent() {
        Result.failure(ResultTest.THROWABLE).ifNonAbsentFailureSendTo(mockThrowableReceiver);
        Mockito.verify(mockThrowableReceiver).accept(ResultTest.THROWABLE);
    }

    @Test
    public void shouldNotApplySendIfSucceededIfAbsent() {
        ResultTest.SUCCESS_WITH_VALUE.ifNonAbsentFailureSendTo(mockThrowableReceiver);
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldNotApplySendIfFailedIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifFailedSendTo(mockThrowableReceiver);
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldNotApplySendIfFailedExceptAbsentIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifFailedSendTo(mockThrowableReceiver);
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldNotApplySendIfFailedExceptAbsentIfAbsent() {
        Result.absent().ifNonAbsentFailureSendTo(mockThrowableReceiver);
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldApplySendIfFailedAbsent() {
        Result.absent().ifAbsentFailureSendTo(mockThrowableReceiver);
        Mockito.verify(mockThrowableReceiver).accept(Result.absent().getFailure());
    }

    @Test
    public void shouldNotApplySendIfFailedAbsentIfAbsent() {
        Result.failure(ResultTest.THROWABLE).ifAbsentFailureSendTo(mockThrowableReceiver);
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldNotApplySendIfFailedAbsentIfSucceeded() {
        Result.failure(ResultTest.THROWABLE).ifAbsentFailureSendTo(mockThrowableReceiver);
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldApplyBindIfFailed() {
        Result.failure(ResultTest.THROWABLE).ifFailedBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verify(mockThrowableStringBinder).bind(ResultTest.THROWABLE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldApplyBindIfFailedExceptAbsent() {
        Result.failure(ResultTest.THROWABLE).ifNonAbsentFailureBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verify(mockThrowableStringBinder).bind(ResultTest.THROWABLE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldNotApplyBindIfSucceededIfAbsent() {
        ResultTest.SUCCESS_WITH_VALUE.ifNonAbsentFailureBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
    }

    @Test
    public void shouldNotApplyBindIfFailedIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifFailedBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
    }

    @Test
    public void shouldNotApplyBindIfFailedExceptAbsentIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifFailedBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
    }

    @Test
    public void shouldNotApplyBindIfFailedExceptAbsentIfAbsent() {
        Result.absent().ifNonAbsentFailureBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
    }

    @Test
    public void shouldApplyBindIfFailedAbsent() {
        Result.absent().ifAbsentFailureBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verify(mockThrowableStringBinder).bind(Result.absent().getFailure(), ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldNotApplyBindIfFailedAbsentIfAbsent() {
        Result.failure(ResultTest.THROWABLE).ifAbsentFailureBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
    }

    @Test
    public void shouldNotApplyBindIfFailedAbsentIfSucceeded() {
        Result.failure(ResultTest.THROWABLE).ifAbsentFailureBind(ResultTest.STRING_VALUE, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
    }

    @Test
    public void shouldApplyBindFromIfFailed() {
        Result.failure(ResultTest.THROWABLE).ifFailedBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verify(mockThrowableStringBinder).bind(ResultTest.THROWABLE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldApplyBindFromIfFailedExceptAbsent() {
        Result.failure(ResultTest.THROWABLE).ifNonAbsentFailureBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verify(mockThrowableStringBinder).bind(ResultTest.THROWABLE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldNotApplyBindFromIfSucceededIfAbsent() {
        ResultTest.SUCCESS_WITH_VALUE.ifNonAbsentFailureBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldNotApplyBindFromIfFailedIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifFailedBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldNotApplyBindFromIfFailedExceptAbsentIfSucceeded() {
        ResultTest.SUCCESS_WITH_VALUE.ifFailedBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldNotApplyBindFromIfFailedExceptAbsentIfAbsent() {
        Result.absent().ifNonAbsentFailureBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldApplyBindFromIfFailedAbsent() {
        Result.absent().ifAbsentFailureBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verify(mockThrowableStringBinder).bind(Result.absent().getFailure(), ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldNotApplyBindFromIfFailedAbsentIfAbsent() {
        Result.failure(ResultTest.THROWABLE).ifAbsentFailureBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldNotApplyBindFromIfFailedAbsentIfSucceeded() {
        Result.failure(ResultTest.THROWABLE).ifAbsentFailureBindFrom(mockSupplier, mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockThrowableStringBinder);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldAllowForChainedCallsToSendIfFailed() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededSendTo(mockReceiver), Matchers.sameInstance(ResultTest.SUCCESS_WITH_VALUE));
    }

    @Test
    public void shouldAllowForChainedCallsToSendIfSucceeded() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifFailedSendTo(mockThrowableReceiver), Matchers.sameInstance(ResultTest.SUCCESS_WITH_VALUE));
        Mockito.verifyZeroInteractions(mockThrowableReceiver);
    }

    @Test
    public void shouldAllowForChainedCallsToBind() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededBind(ResultTest.STRING_VALUE, mockBinder), Matchers.sameInstance(ResultTest.SUCCESS_WITH_VALUE));
    }

    @Test
    public void shouldAllowForChainedCallsToBindFrom() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededBindFrom(mockSupplier, mockBinder), Matchers.sameInstance(ResultTest.SUCCESS_WITH_VALUE));
    }

    @Test
    public void shouldReturnFailureForMapOfFailed() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.ifSucceededMap(mockValueFunction), Matchers.sameInstance(ResultTest.FAILURE_WITH_THROWABLE));
        Mockito.verifyZeroInteractions(mockValueFunction);
    }

    @Test
    public void shouldApplyFunctionForMapOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_OTHER_VALUE.ifSucceededMap(mockValueFunction), Matchers.equalTo(ResultTest.SUCCESS_WITH_VALUE));
        Mockito.verify(mockValueFunction).apply(ResultTest.OTHER_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnFailureForMergeOfFailed() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.ifSucceededMerge(ResultTest.STRING_VALUE, mockMerger), Matchers.sameInstance(((Result) (ResultTest.FAILURE_WITH_THROWABLE))));
        Mockito.verifyZeroInteractions(mockValueFunction);
    }

    @Test
    public void shouldApplyFunctionForMergeOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededMerge(ResultTest.STRING_VALUE, mockMerger), Matchers.equalTo(ResultTest.SUCCESS_WITH_FLOAT_VALUE));
        Mockito.verify(mockMerger).merge(ResultTest.VALUE, ResultTest.STRING_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnFailureForMergeFromOfFailed() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.ifSucceededMergeFrom(mockSupplier, mockMerger), Matchers.sameInstance(((Result) (ResultTest.FAILURE_WITH_THROWABLE))));
        Mockito.verifyZeroInteractions(mockValueFunction);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldApplyFunctionForMergeFromOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededMergeFrom(mockSupplier, mockMerger), Matchers.equalTo(ResultTest.SUCCESS_WITH_FLOAT_VALUE));
        Mockito.verify(mockMerger).merge(ResultTest.VALUE, ResultTest.STRING_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnFailureForAttemptMergeOfFailed() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.ifSucceededAttemptMerge(ResultTest.STRING_VALUE, mockAttemptMerger), Matchers.sameInstance(((Result) (ResultTest.FAILURE_WITH_THROWABLE))));
        Mockito.verifyZeroInteractions(mockValueFunction);
    }

    @Test
    public void shouldApplyFunctionForAttemptMergeOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededAttemptMerge(ResultTest.STRING_VALUE, mockAttemptMerger), Matchers.equalTo(ResultTest.SUCCESS_WITH_FLOAT_VALUE));
        Mockito.verify(mockAttemptMerger).merge(ResultTest.VALUE, ResultTest.STRING_VALUE);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void shouldReturnFailureForAttemptMergeFromOfFailed() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.ifSucceededAttemptMergeFrom(mockSupplier, mockAttemptMerger), Matchers.sameInstance(((Result) (ResultTest.FAILURE_WITH_THROWABLE))));
        Mockito.verifyZeroInteractions(mockValueFunction);
        Mockito.verifyZeroInteractions(mockSupplier);
    }

    @Test
    public void shouldApplyFunctionForAttemptMergeFromOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededAttemptMergeFrom(mockSupplier, mockAttemptMerger), Matchers.equalTo(ResultTest.SUCCESS_WITH_FLOAT_VALUE));
        Mockito.verify(mockAttemptMerger).merge(ResultTest.VALUE, ResultTest.STRING_VALUE);
    }

    @Test
    public void shouldReturnFailureForFlatMapOfFailed() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.ifSucceededAttemptMap(mockSucceededValueFunction), Matchers.sameInstance(ResultTest.FAILURE_WITH_THROWABLE));
        Mockito.verifyZeroInteractions(mockSucceededValueFunction);
    }

    @Test
    public void shouldReturnValueOfSucceededFromAppliedFunctionForFlatMapOfSucceeded() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_OTHER_VALUE.ifSucceededAttemptMap(mockSucceededValueFunction), Matchers.equalTo(ResultTest.SUCCESS_WITH_VALUE));
        Mockito.verify(mockSucceededValueFunction).apply(ResultTest.OTHER_VALUE);
    }

    @Test
    public void shouldReturnFailureIfAttemptFromAppliedFunctionForFlatMapOfValueReturnsFailure() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.ifSucceededAttemptMap(mockFailedFunction), Matchers.equalTo(ResultTest.FAILURE_WITH_THROWABLE));
        Mockito.verify(mockFailedFunction).apply(ResultTest.VALUE);
    }

    @Test
    public void shouldReturnRecoverSuccessForAttemptRecoverOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.attemptRecover(mockAttemptRecoverValueFunction), Matchers.equalTo(ResultTest.SUCCESS_WITH_VALUE));
        Mockito.verify(mockAttemptRecoverValueFunction).apply(ResultTest.THROWABLE);
    }

    @Test
    public void shouldReturnRecoverValueForRecoverOfFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.recover(mockRecoverValueFunction), Matchers.equalTo(ResultTest.VALUE));
        Mockito.verify(mockRecoverValueFunction).apply(ResultTest.THROWABLE);
    }

    @Test
    public void shouldReturnSuccessForAttemptRecoverOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_OTHER_VALUE.attemptRecover(mockAttemptRecoverValueFunction), Matchers.equalTo(ResultTest.SUCCESS_WITH_OTHER_VALUE));
        Mockito.verifyZeroInteractions(mockRecoverValueFunction);
    }

    @Test
    public void shouldReturnValueForRecoverOfSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.recover(mockRecoverValueFunction), Matchers.equalTo(ResultTest.VALUE));
        Mockito.verifyZeroInteractions(mockRecoverValueFunction);
    }

    @Test
    public void shouldNotBeEqualForDifferentValues() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE, Matchers.not(Matchers.equalTo(ResultTest.SUCCESS_WITH_OTHER_VALUE)));
    }

    @Test
    public void shouldBeEqualForSameValue() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE, Matchers.equalTo(Result.success(ResultTest.VALUE)));
    }

    @Test
    public void shouldReturnValueForOrNullOnSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.orNull(), Matchers.equalTo(ResultTest.VALUE));
    }

    @Test
    public void shouldReturnNullForOrNullOnFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.orNull(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnNullForFailureOrNullOnSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.failureOrNull(), Matchers.nullValue());
    }

    @Test
    public void shouldReturnSameThrowableForFailureOrNullOnFailureWithExplicitThrowable() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE.failureOrNull(), Matchers.sameInstance(ResultTest.THROWABLE));
    }

    @Test
    public void shouldReturnSomethingForFailureOrNullOnFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE.failureOrNull(), Matchers.notNullValue());
    }

    @Test
    public void shouldReturnNullPointerExceptionForFailureOrNullOnAbsent() {
        MatcherAssert.assertThat(ResultTest.ABSENT.failureOrNull(), Matchers.instanceOf(NullPointerException.class));
    }

    @Test
    public void shouldReturnTrueForContainsOfSameValue() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE.contains(ResultTest.VALUE), Matchers.is(true));
    }

    @Test
    public void shouldReturnFalseForContainsOfOtherValue() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_OTHER_VALUE.contains(ResultTest.VALUE), Matchers.is(false));
    }

    @Test
    public void shouldReturnFalseForContainsOfFailure() {
        MatcherAssert.assertThat(ResultTest.ABSENT.contains(ResultTest.VALUE), Matchers.is(false));
    }

    @Test
    public void shouldBeSingletonForFailureWithoutExplicitThrowable() {
        MatcherAssert.assertThat(Result.failure(), Matchers.equalTo(Result.failure()));
    }

    @Test
    public void shouldVerifyEqualsForResult() {
        EqualsVerifier.forClass(Result.class).verify();
    }

    @Test
    public void shouldPrintStringRepresentationForSuccess() {
        MatcherAssert.assertThat(ResultTest.SUCCESS_WITH_VALUE, Matchers.hasToString(Matchers.not(Matchers.isEmptyOrNullString())));
    }

    @Test
    public void shouldPrintStringRepresentationForFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE, Matchers.hasToString(Matchers.not(Matchers.isEmptyOrNullString())));
    }

    @Test
    public void shouldPrintStringRepresentationForFailureWithExplicitThrowable() {
        MatcherAssert.assertThat(ResultTest.FAILURE_WITH_THROWABLE, Matchers.hasToString(Matchers.not(Matchers.isEmptyOrNullString())));
    }

    @Test
    public void shouldPrintStringRepresentationForAbsent() {
        MatcherAssert.assertThat(ResultTest.ABSENT, Matchers.hasToString(Matchers.not(Matchers.isEmptyOrNullString())));
    }

    @Test
    public void shouldReturnEmptyListForAbsent() {
        MatcherAssert.assertThat(ResultTest.ABSENT.asList(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldReturnEmptyListForFailure() {
        MatcherAssert.assertThat(ResultTest.FAILURE.asList(), Matchers.is(Matchers.empty()));
    }

    @Test
    public void shouldReturnListWithValueForPresentWithValue() {
        MatcherAssert.assertThat(ResultTest.PRESENT_WITH_VALUE.asList(), Matchers.contains(ResultTest.VALUE));
    }
}

