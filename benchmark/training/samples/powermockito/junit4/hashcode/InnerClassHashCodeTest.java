/**
 * Copyright 2014 the original author or authors.
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
package samples.powermockito.junit4.hashcode;


import java.util.HashMap;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ InnerClassHashCodeTest.SubHashMap.class })
public class InnerClassHashCodeTest {
    private static final int EXPECTED_HASH = 123456;

    @Test
    public void can_stub_inner_hash_code_method() {
        stub(method(InnerClassHashCodeTest.SubHashMap.class, "hashCode")).toReturn(InnerClassHashCodeTest.EXPECTED_HASH);
        InnerClassHashCodeTest.SubHashMap actor = new InnerClassHashCodeTest.SubHashMap();
        int hashCode = actor.hashCode();
        Assert.assertThat(hashCode, CoreMatchers.equalTo(123456));
    }

    public class SubHashMap extends HashMap {
        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            return super.equals(o);
        }
    }
}

