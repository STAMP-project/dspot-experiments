/**
 * Copyright (c) 2016-present, RxJava Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */
package io.reactivex.internal.operators.observable;


import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.subjects.PublishSubject;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;


/**
 * Systematically tests that when zipping an infinite and a finite Observable,
 * the resulting Observable is finite.
 */
public class ObservableZipCompletionTest {
    BiFunction<String, String, String> concat2Strings;

    PublishSubject<String> s1;

    PublishSubject<String> s2;

    Observable<String> zipped;

    Observer<String> observer;

    InOrder inOrder;

    @Test
    public void testFirstCompletesThenSecondInfinite() {
        s1.onNext("a");
        s1.onNext("b");
        s1.onComplete();
        s2.onNext("1");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s2.onNext("2");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSecondInfiniteThenFirstCompletes() {
        s2.onNext("1");
        s2.onNext("2");
        s1.onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s1.onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        s1.onComplete();
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testSecondCompletesThenFirstInfinite() {
        s2.onNext("1");
        s2.onNext("2");
        s2.onComplete();
        s1.onNext("a");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s1.onNext("b");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void testFirstInfiniteThenSecondCompletes() {
        s1.onNext("a");
        s1.onNext("b");
        s2.onNext("1");
        inOrder.verify(observer, Mockito.times(1)).onNext("a-1");
        s2.onNext("2");
        inOrder.verify(observer, Mockito.times(1)).onNext("b-2");
        s2.onComplete();
        inOrder.verify(observer, Mockito.times(1)).onComplete();
        inOrder.verifyNoMoreInteractions();
    }
}

