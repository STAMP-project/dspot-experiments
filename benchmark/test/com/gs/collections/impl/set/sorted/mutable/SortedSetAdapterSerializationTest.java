/**
 * Copyright 2011 Goldman Sachs.
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
package com.gs.collections.impl.set.sorted.mutable;


import com.gs.collections.impl.test.Verify;
import java.util.TreeSet;
import org.junit.Test;


public class SortedSetAdapterSerializationTest {
    @Test
    public void serializedForm() {
        Verify.assertSerializedForm(1L, ("rO0ABXNyADtjb20uZ3MuY29sbGVjdGlvbnMuaW1wbC5zZXQuc29ydGVkLm11dGFibGUuU29ydGVk\n" + ("U2V0QWRhcHRlcgAAAAAAAAABAgABTAAIZGVsZWdhdGV0ABVMamF2YS91dGlsL1NvcnRlZFNldDt4\n" + "cHNyABFqYXZhLnV0aWwuVHJlZVNldN2YUJOV7YdbAwAAeHBwdwQAAAAAeA==")), SortedSetAdapter.adapt(new TreeSet<Integer>()));
    }
}

