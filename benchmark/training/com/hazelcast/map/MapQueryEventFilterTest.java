/**
 * Copyright (c) 2008-2019, Hazelcast, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hazelcast.map;


import com.hazelcast.test.HazelcastParallelClassRunner;
import com.hazelcast.test.HazelcastTestSupport;
import com.hazelcast.test.annotation.ParallelTest;
import com.hazelcast.test.annotation.QuickTest;
import java.io.Serializable;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;


@RunWith(HazelcastParallelClassRunner.class)
@Category({ QuickTest.class, ParallelTest.class })
public class MapQueryEventFilterTest extends HazelcastTestSupport {
    @Test
    public void test_add_and_update_event_counts_when_natural_event_types_enabled() {
        // expect 1 add event when age = 3 because of natural filtering
        // expect 1 update event when age >= 3
        test_expected_event_types(true, 1, 1);
    }

    /**
     * This tests default behaviour.
     */
    @Test
    public void test_add_and_update_event_counts_when_natural_event_types_disabled() {
        // expect 0 add event when age >= 3
        // expect 2 update events when age >= 3 because of disabled natural filtering
        test_expected_event_types(false, 0, 2);
    }

    static final class Employee implements Serializable {
        int age;

        public Employee(int age) {
            this.age = age;
        }
    }
}

