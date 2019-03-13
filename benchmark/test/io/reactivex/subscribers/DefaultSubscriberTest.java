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
package io.reactivex.subscribers;


import io.reactivex.Flowable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;


public class DefaultSubscriberTest {
    static final class RequestEarly extends DefaultSubscriber<Integer> {
        final List<Object> events = new ArrayList<Object>();

        RequestEarly() {
            request(5);
        }

        @Override
        protected void onStart() {
        }

        @Override
        public void onNext(Integer t) {
            events.add(t);
        }

        @Override
        public void onError(Throwable t) {
            events.add(t);
        }

        @Override
        public void onComplete() {
            events.add("Done");
        }
    }

    @Test
    public void requestUpfront() {
        DefaultSubscriberTest.RequestEarly sub = new DefaultSubscriberTest.RequestEarly();
        Flowable.range(1, 10).subscribe(sub);
        Assert.assertEquals(Collections.emptyList(), sub.events);
    }
}

