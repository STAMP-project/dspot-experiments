/**
 * The MIT License
 *
 * Copyright (c) 2013-2016 reark project contributors
 *
 * https://github.com/reark/reark/graphs/contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.reark.rxgithubapp.shared.utils;


import Color.RED;
import android.widget.TextView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest(AndroidSchedulers.class)
public class SubscriptionUtilsTest {
    @Test
    public void testTextViewIsUpdatedWhenValueComes() {
        TextView textView = Mockito.mock(TextView.class);
        Observable<String> observable = Observable.just("String");
        SubscriptionUtils.subscribeTextViewText(observable, textView);
        Mockito.verify(textView).setText(ArgumentMatchers.eq("String"));
    }

    @Test
    public void testTextViewIsUpdatedWithErrorMessageOnError() {
        TextView textView = Mockito.mock(TextView.class);
        Observable<String> observable = Observable.error(new NullPointerException());
        SubscriptionUtils.subscribeTextViewText(observable, textView);
        Mockito.verify(textView).setText(ArgumentMatchers.contains("NullPointerException"));
        Mockito.verify(textView).setBackgroundColor(ArgumentMatchers.eq(RED));
    }
}

