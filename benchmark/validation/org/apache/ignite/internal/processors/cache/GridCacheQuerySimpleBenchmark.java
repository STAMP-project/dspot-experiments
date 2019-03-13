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
package org.apache.ignite.internal.processors.cache;


import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.LongAdder;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.annotations.QuerySqlField;
import org.apache.ignite.internal.IgniteInternalFuture;
import org.apache.ignite.internal.util.GridRandom;
import org.apache.ignite.internal.util.typedef.X;
import org.apache.ignite.internal.util.typedef.internal.U;
import org.apache.ignite.testframework.junits.common.GridCommonAbstractTest;
import org.junit.Test;


/**
 *
 */
public class GridCacheQuerySimpleBenchmark extends GridCommonAbstractTest {
    /**
     *
     */
    private Ignite ignite;

    /**
     *
     *
     * @throws Exception
     * 		If failed.
     */
    @Test
    public void testPerformance() throws Exception {
        Random rnd = new GridRandom();
        final IgniteCache<Long, GridCacheQuerySimpleBenchmark.Person> c = ignite.cache("offheap-cache");
        X.println("___ PUT start");
        final int cnt = 100000;
        final int maxSalary = cnt / 10;
        for (long i = 0; i < cnt; i++)
            c.put(i, new GridCacheQuerySimpleBenchmark.Person(rnd.nextInt(maxSalary), ("Vasya " + i)));

        X.println("___ PUT end");
        final AtomicBoolean end = new AtomicBoolean();
        final LongAdder puts = new LongAdder();
        IgniteInternalFuture<?> fut0 = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Random rnd = new GridRandom();
                while (!(end.get())) {
                    long i = rnd.nextInt(cnt);
                    c.put(i, new GridCacheQuerySimpleBenchmark.Person(rnd.nextInt(maxSalary), ("Vasya " + i)));
                    puts.increment();
                } 
                return null;
            }
        }, 10);
        final LongAdder qrys = new LongAdder();
        IgniteInternalFuture<?> fut1 = multithreadedAsync(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Random rnd = new GridRandom();
                while (!(end.get())) {
                    int salary = rnd.nextInt(maxSalary);
                    c.query(new SqlFieldsQuery("select name from Person where salary = ?").setArgs(salary)).getAll();
                    qrys.increment();
                } 
                return null;
            }
        }, 10);
        int runTimeSec = 600;
        for (int s = 0; s < runTimeSec; s++) {
            Thread.sleep(1000);
            long puts0 = puts.sum();
            long qrys0 = qrys.sum();
            puts.add((-puts0));
            qrys.add((-qrys0));
            X.println(((("___ puts: " + puts0) + " qrys: ") + qrys0));
        }
        end.set(true);
        fut0.get();
        fut1.get();
        X.println("___ STOP");
    }

    /**
     *
     */
    private static class Person implements Externalizable {
        /**
         *
         */
        @QuerySqlField(index = true)
        int salary;

        /**
         *
         */
        @QuerySqlField
        String name;

        /**
         *
         */
        public Person() {
            // No-op.
        }

        /**
         *
         *
         * @param salary
         * 		Salary.
         * @param name
         * 		Name.
         */
        Person(int salary, String name) {
            this.salary = salary;
            this.name = name;
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(salary);
            U.writeString(out, name);
        }

        /**
         * {@inheritDoc }
         */
        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            salary = in.readInt();
            name = U.readString(in);
        }
    }
}

