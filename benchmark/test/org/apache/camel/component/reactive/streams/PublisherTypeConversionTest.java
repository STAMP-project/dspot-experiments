/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.reactive.streams;


import io.reactivex.Observable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.apache.camel.Exchange;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;


public class PublisherTypeConversionTest extends CamelTestSupport {
    @Test
    public void testConversion() throws Exception {
        CountDownLatch latch = new CountDownLatch(3);
        List<Integer> integers = new LinkedList<>();
        Observable.fromPublisher(CamelReactiveStreams.get(context).fromStream("pub", Exchange.class)).map(( x) -> x.getIn().getBody(.class)).subscribe(( n) -> {
            integers.add(n);
            latch.countDown();
        });
        Observable.fromPublisher(CamelReactiveStreams.get(context).fromStream("pub")).map(( x) -> x.getIn().getBody(.class)).subscribe(( n) -> {
            integers.add(n);
            latch.countDown();
        });
        Observable.fromPublisher(CamelReactiveStreams.get(context).fromStream("pub", Integer.class)).subscribe(( n) -> {
            integers.add(n);
            latch.countDown();
        });
        context.start();
        latch.await(5, TimeUnit.SECONDS);
        assertEquals(3, integers.size());
        for (int i : integers) {
            assertEquals(123, i);
        }
    }
}

