/**
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.lifecycle;


import androidx.annotation.Nullable;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


@SuppressWarnings("unchecked")
@RunWith(JUnit4.class)
public class TransformationsTest {
    private LifecycleOwner mOwner;

    @Test
    public void testMap() {
        LiveData<String> source = new MutableLiveData();
        LiveData<Integer> mapped = Transformations.map(source, new androidx.arch.core.util.Function<String, Integer>() {
            @Override
            public Integer apply(String input) {
                return input.length();
            }
        });
        Observer<Integer> observer = Mockito.mock(Observer.class);
        mapped.observe(mOwner, observer);
        source.setValue("four");
        Mockito.verify(observer).onChanged(4);
    }

    @Test
    public void testSwitchMap() {
        LiveData<Integer> trigger = new MutableLiveData();
        final LiveData<String> first = new MutableLiveData();
        final LiveData<String> second = new MutableLiveData();
        LiveData<String> result = Transformations.switchMap(trigger, new androidx.arch.core.util.Function<Integer, LiveData<String>>() {
            @Override
            public LiveData<String> apply(Integer input) {
                if (input == 1) {
                    return first;
                } else {
                    return second;
                }
            }
        });
        Observer<String> observer = Mockito.mock(Observer.class);
        result.observe(mOwner, observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        first.setValue("first");
        trigger.setValue(1);
        Mockito.verify(observer).onChanged("first");
        second.setValue("second");
        Mockito.reset(observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        trigger.setValue(2);
        Mockito.verify(observer).onChanged("second");
        Mockito.reset(observer);
        first.setValue("failure");
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void testSwitchMap2() {
        LiveData<Integer> trigger = new MutableLiveData();
        final LiveData<String> first = new MutableLiveData();
        final LiveData<String> second = new MutableLiveData();
        LiveData<String> result = Transformations.switchMap(trigger, new androidx.arch.core.util.Function<Integer, LiveData<String>>() {
            @Override
            public LiveData<String> apply(Integer input) {
                if (input == 1) {
                    return first;
                } else {
                    return second;
                }
            }
        });
        Observer<String> observer = Mockito.mock(Observer.class);
        result.observe(mOwner, observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        trigger.setValue(1);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        first.setValue("fi");
        Mockito.verify(observer).onChanged("fi");
        first.setValue("rst");
        Mockito.verify(observer).onChanged("rst");
        second.setValue("second");
        Mockito.reset(observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        trigger.setValue(2);
        Mockito.verify(observer).onChanged("second");
        Mockito.reset(observer);
        first.setValue("failure");
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void testNoRedispatchSwitchMap() {
        LiveData<Integer> trigger = new MutableLiveData();
        final LiveData<String> first = new MutableLiveData();
        LiveData<String> result = Transformations.switchMap(trigger, new androidx.arch.core.util.Function<Integer, LiveData<String>>() {
            @Override
            public LiveData<String> apply(Integer input) {
                return first;
            }
        });
        Observer<String> observer = Mockito.mock(Observer.class);
        result.observe(mOwner, observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        first.setValue("first");
        trigger.setValue(1);
        Mockito.verify(observer).onChanged("first");
        Mockito.reset(observer);
        trigger.setValue(2);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
    }

    @Test
    public void testSwitchMapToNull() {
        LiveData<Integer> trigger = new MutableLiveData();
        final LiveData<String> first = new MutableLiveData();
        LiveData<String> result = Transformations.switchMap(trigger, new androidx.arch.core.util.Function<Integer, LiveData<String>>() {
            @Override
            public LiveData<String> apply(Integer input) {
                if (input == 1) {
                    return first;
                } else {
                    return null;
                }
            }
        });
        Observer<String> observer = Mockito.mock(Observer.class);
        result.observe(mOwner, observer);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        first.setValue("first");
        trigger.setValue(1);
        Mockito.verify(observer).onChanged("first");
        Mockito.reset(observer);
        trigger.setValue(2);
        Mockito.verify(observer, Mockito.never()).onChanged(ArgumentMatchers.anyString());
        MatcherAssert.assertThat(first.hasObservers(), CoreMatchers.is(false));
    }

    @Test
    public void noObsoleteValueTest() {
        MutableLiveData<Integer> numbers = new MutableLiveData();
        LiveData<Integer> squared = Transformations.map(numbers, new androidx.arch.core.util.Function<Integer, Integer>() {
            @Override
            public Integer apply(Integer input) {
                return input * input;
            }
        });
        Observer observer = Mockito.mock(Observer.class);
        squared.setValue(1);
        squared.observeForever(observer);
        Mockito.verify(observer).onChanged(1);
        squared.removeObserver(observer);
        Mockito.reset(observer);
        numbers.setValue(2);
        squared.observeForever(observer);
        Mockito.verify(observer, Mockito.only()).onChanged(4);
    }

    @Test
    public void testDistinctUntilChanged_triggersOnInitialNullValue() {
        MutableLiveData<String> originalLiveData = new MutableLiveData();
        originalLiveData.setValue(null);
        LiveData<String> dedupedLiveData = Transformations.distinctUntilChanged(originalLiveData);
        MatcherAssert.assertThat(dedupedLiveData.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        TransformationsTest.CountingObserver<String> observer = new TransformationsTest.CountingObserver<>();
        dedupedLiveData.observe(mOwner, observer);
        MatcherAssert.assertThat(observer.mTimesUpdated, CoreMatchers.is(1));
        MatcherAssert.assertThat(dedupedLiveData.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void testDistinctUntilChanged_dedupesValues() {
        MutableLiveData<String> originalLiveData = new MutableLiveData();
        LiveData<String> dedupedLiveData = Transformations.distinctUntilChanged(originalLiveData);
        MatcherAssert.assertThat(dedupedLiveData.getValue(), CoreMatchers.is(CoreMatchers.nullValue()));
        TransformationsTest.CountingObserver<String> observer = new TransformationsTest.CountingObserver<>();
        dedupedLiveData.observe(mOwner, observer);
        MatcherAssert.assertThat(observer.mTimesUpdated, CoreMatchers.is(0));
        String value = "new value";
        originalLiveData.setValue(value);
        MatcherAssert.assertThat(dedupedLiveData.getValue(), CoreMatchers.is(value));
        MatcherAssert.assertThat(observer.mTimesUpdated, CoreMatchers.is(1));
        originalLiveData.setValue(value);
        MatcherAssert.assertThat(dedupedLiveData.getValue(), CoreMatchers.is(value));
        MatcherAssert.assertThat(observer.mTimesUpdated, CoreMatchers.is(1));
        String newerValue = "newer value";
        originalLiveData.setValue(newerValue);
        MatcherAssert.assertThat(dedupedLiveData.getValue(), CoreMatchers.is(newerValue));
        MatcherAssert.assertThat(observer.mTimesUpdated, CoreMatchers.is(2));
        dedupedLiveData.removeObservers(mOwner);
    }

    private static class CountingObserver<T> implements Observer<T> {
        int mTimesUpdated;

        @Override
        public void onChanged(@Nullable
        T t) {
            ++(mTimesUpdated);
        }
    }
}

