/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.clustered.client.internal.service;


import java.io.Serializable;
import java.util.Arrays;
import org.ehcache.clustered.client.service.ClusteringService;
import org.ehcache.spi.persistence.StateHolder;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.terracotta.passthrough.PassthroughClusterControl;


public class StateRepositoryWhitelistingTest {
    private PassthroughClusterControl clusterControl;

    private static String STRIPENAME = "stripe";

    private static String STRIPE_URI = "passthrough://" + (StateRepositoryWhitelistingTest.STRIPENAME);

    private ClusteringService service;

    ClusterStateRepository stateRepository;

    @Test
    public void testWhiteListedClass() throws Exception {
        StateHolder<StateRepositoryWhitelistingTest.Child, StateRepositoryWhitelistingTest.Child> testMap = stateRepository.getPersistentStateHolder("testMap", StateRepositoryWhitelistingTest.Child.class, StateRepositoryWhitelistingTest.Child.class, Arrays.asList(StateRepositoryWhitelistingTest.Child.class, StateRepositoryWhitelistingTest.Parent.class)::contains, null);
        testMap.putIfAbsent(new StateRepositoryWhitelistingTest.Child(10, 20L), new StateRepositoryWhitelistingTest.Child(20, 30L));
        Assert.assertThat(testMap.get(new StateRepositoryWhitelistingTest.Child(10, 20L)), Matchers.is(new StateRepositoryWhitelistingTest.Child(20, 30L)));
        Assert.assertThat(testMap.entrySet(), Matchers.hasSize(1));
    }

    @Test
    public void testWhiteListedMissingClass() throws Exception {
        StateHolder<StateRepositoryWhitelistingTest.Child, StateRepositoryWhitelistingTest.Child> testMap = stateRepository.getPersistentStateHolder("testMap", StateRepositoryWhitelistingTest.Child.class, StateRepositoryWhitelistingTest.Child.class, Arrays.asList(StateRepositoryWhitelistingTest.Child.class)::contains, null);
        testMap.putIfAbsent(new StateRepositoryWhitelistingTest.Child(10, 20L), new StateRepositoryWhitelistingTest.Child(20, 30L));
        try {
            Assert.assertThat(testMap.entrySet(), Matchers.hasSize(1));
        } catch (RuntimeException e) {
            Assert.assertTrue(e.getMessage().equals("Could not load class"));
        }
    }

    @Test
    public void testWhitelistingForPrimitiveClass() throws Exception {
        // No whitelisting for primitive classes are required as we do not deserialize them at client side
        StateHolder<Integer, Integer> testMap = stateRepository.getPersistentStateHolder("testMap", Integer.class, Integer.class, Arrays.asList(StateRepositoryWhitelistingTest.Child.class)::contains, null);
        testMap.putIfAbsent(new Integer(10), new Integer(20));
        Assert.assertThat(testMap.get(new Integer(10)), Matchers.is(new Integer(20)));
        Assert.assertThat(testMap.entrySet(), Matchers.hasSize(1));
    }

    private static class Parent implements Serializable {
        private static final long serialVersionUID = 1L;

        final int val;

        private Parent(int val) {
            this.val = val;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof StateRepositoryWhitelistingTest.Parent))
                return false;

            StateRepositoryWhitelistingTest.Parent testVal = ((StateRepositoryWhitelistingTest.Parent) (o));
            return (val) == (testVal.val);
        }

        @Override
        public int hashCode() {
            return val;
        }
    }

    private static class Child extends StateRepositoryWhitelistingTest.Parent implements Serializable {
        private static final long serialVersionUID = 1L;

        final long longValue;

        private Child(int val, long longValue) {
            super(val);
            this.longValue = longValue;
        }

        @Override
        public boolean equals(Object o) {
            if ((this) == o)
                return true;

            if (!(o instanceof StateRepositoryWhitelistingTest.Child))
                return false;

            StateRepositoryWhitelistingTest.Child testVal = ((StateRepositoryWhitelistingTest.Child) (o));
            return (super.equals(testVal)) && ((longValue) == (testVal.longValue));
        }

        @Override
        public int hashCode() {
            return (super.hashCode()) + (31 * (Long.hashCode(longValue)));
        }
    }
}

