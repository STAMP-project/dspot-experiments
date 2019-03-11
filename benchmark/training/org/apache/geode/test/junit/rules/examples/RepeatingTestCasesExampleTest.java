/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.test.junit.rules.examples;


import java.util.concurrent.atomic.AtomicInteger;
import org.apache.geode.test.junit.Repeat;
import org.apache.geode.test.junit.rules.RepeatRule;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;


/**
 * The RepeatingTestCasesExampleTest class is a test suite of test cases testing the contract and
 * functionality of the JUnit {@literal @}Repeat annotation on a test suite class test case methods.
 *
 * @see org.junit.Test
 * @see org.apache.geode.test.junit.Repeat
 * @see org.apache.geode.test.junit.rules.RepeatRule
 */
public class RepeatingTestCasesExampleTest {
    private static final AtomicInteger repeatOnceCounter = new AtomicInteger(0);

    private static final AtomicInteger repeatOnlyOnceCounter = new AtomicInteger(0);

    private static final AtomicInteger repeatTenTimesCounter = new AtomicInteger(0);

    private static final AtomicInteger repeatTwiceCounter = new AtomicInteger(0);

    @Rule
    public RepeatRule repeatRule = new RepeatRule();

    @Test
    @Repeat
    public void repeatOnce() {
        RepeatingTestCasesExampleTest.repeatOnceCounter.incrementAndGet();
        Assert.assertThat(((RepeatingTestCasesExampleTest.repeatOnceCounter.get()) <= 1), CoreMatchers.is(true));
    }

    @Test
    @Repeat(property = "tdd.example.test.case.with.non-existing.system.property")
    public void repeatOnlyOnce() {
        RepeatingTestCasesExampleTest.repeatOnlyOnceCounter.incrementAndGet();
        Assert.assertThat(((RepeatingTestCasesExampleTest.repeatOnlyOnceCounter.get()) <= 1), CoreMatchers.is(true));
    }

    @Test
    @Repeat(10)
    public void repeatTenTimes() {
        RepeatingTestCasesExampleTest.repeatTenTimesCounter.incrementAndGet();
        Assert.assertThat(((RepeatingTestCasesExampleTest.repeatTenTimesCounter.get()) <= 10), CoreMatchers.is(true));
    }

    @Test
    @Repeat(property = "tdd.example.test.case.two.repetitions")
    public void repeatTwiceCounter() {
        RepeatingTestCasesExampleTest.repeatTwiceCounter.incrementAndGet();
        Assert.assertThat(((RepeatingTestCasesExampleTest.repeatTwiceCounter.get()) <= 2), CoreMatchers.is(true));
    }
}

