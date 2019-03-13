/**
 * Copyright 2017 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.cloud.spanner;


import junit.framework.TestCase;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Unit tests for {@link com.google.cloud.spanner.SessionPoolOptions}
 */
@RunWith(Parameterized.class)
public class SessionPoolOptionsTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Parameterized.Parameter
    public int minSessions;

    @Parameterized.Parameter(1)
    public int maxSessions;

    @Test
    public void setMinMaxSessions() {
        if ((minSessions) > (maxSessions)) {
            expectedException.expect(IllegalArgumentException.class);
        }
        SessionPoolOptions options = SessionPoolOptions.newBuilder().setMinSessions(minSessions).setMaxSessions(maxSessions).build();
        TestCase.assertEquals(minSessions, options.getMinSessions());
        TestCase.assertEquals(maxSessions, options.getMaxSessions());
    }
}

