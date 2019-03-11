/**
 * Copyright 2011-2015 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parceler;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;


/**
 *
 *
 * @author John Ericksen
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class GraphTest {
    private static final int DEPTH = 10;

    private static final Random RAND = new Random(1);

    private static final AtomicLong SEQUENCE = new AtomicLong();

    private List<GraphTest.A> as;

    private List<GraphTest.B> bs;

    private List<GraphTest.C> cs;

    private List<GraphTest.D> ds;

    @Parcel
    public static class A {
        GraphTest.B b;

        GraphTest.C c;
    }

    @Parcel
    public static class B {
        GraphTest.A a;

        List<GraphTest.C> c = new ArrayList<GraphTest.C>();
    }

    @Parcel
    public static class C {
        String name;

        GraphTest.D d;
    }

    @Parcel
    public static class D {
        GraphTest.A a;
    }

    @Test
    public void testGraph() {
        for (int i = 0; i < 100; i++) {
            GraphTest.A a = generateRandomA(0);
            GraphTest.A unwrap = Parcels.unwrap(ParcelsTestUtil.wrap(a));
        }
    }
}

