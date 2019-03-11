/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.beam.sdk.fn;


import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;


/**
 * Tests for {@link IdGenerators}.
 */
@RunWith(JUnit4.class)
public class IdGeneratorsTest {
    @Test
    public void incrementing() {
        IdGenerator gen = IdGenerators.incrementingLongs();
        Assert.assertThat(gen.getId(), Matchers.equalTo("1"));
        Assert.assertThat(gen.getId(), Matchers.equalTo("2"));
    }

    @Test
    public void incrementingIndependent() {
        IdGenerator gen = IdGenerators.incrementingLongs();
        IdGenerator otherGen = IdGenerators.incrementingLongs();
        Assert.assertThat(gen.getId(), Matchers.equalTo("1"));
        Assert.assertThat(gen.getId(), Matchers.equalTo("2"));
        Assert.assertThat(otherGen.getId(), Matchers.equalTo("1"));
    }

    @Test
    public void decrementing() {
        IdGenerator gen = IdGenerators.decrementingLongs();
        Assert.assertThat(gen.getId(), Matchers.equalTo("-1"));
        Assert.assertThat(gen.getId(), Matchers.equalTo("-2"));
    }

    @Test
    public void decrementingIndependent() {
        IdGenerator gen = IdGenerators.decrementingLongs();
        IdGenerator otherGen = IdGenerators.decrementingLongs();
        Assert.assertThat(gen.getId(), Matchers.equalTo("-1"));
        Assert.assertThat(gen.getId(), Matchers.equalTo("-2"));
        Assert.assertThat(otherGen.getId(), Matchers.equalTo("-1"));
    }
}

