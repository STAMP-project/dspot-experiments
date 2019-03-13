/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 * Copyright 2012-2019 the original author or authors.
 */
package org.assertj.core.api.recursive.comparison;


import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;


public class FieldLocation_from_Test {
    @Test
    public void should_build_fieldLocations_from_given_strings() {
        // GIVEN
        String[] locations = new String[]{ "foo", "bar", "foo.bar" };
        // WHEN
        List<FieldLocation> fieldLocations = FieldLocation.from(locations);
        // THEN
        Assertions.assertThat(fieldLocations).containsExactly(new FieldLocation("foo"), new FieldLocation("bar"), new FieldLocation("foo.bar"));
    }
}

