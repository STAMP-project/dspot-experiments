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


import java.util.Collections;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;


public class FieldLocation_compareTo_Test {
    @Test
    public void should_order_field_location_by_alphabetical_path() {
        // GIVEN
        FieldLocation fieldLocation1 = FieldLocation.fielLocation("a");
        FieldLocation fieldLocation2 = FieldLocation.fielLocation("a.b");
        FieldLocation fieldLocation3 = FieldLocation.fielLocation("aaa");
        FieldLocation fieldLocation4 = FieldLocation.fielLocation("z");
        List<FieldLocation> fieldLocations = Lists.list(fieldLocation2, fieldLocation1, fieldLocation3, fieldLocation4);
        Collections.shuffle(fieldLocations);
        // WHEN
        Collections.sort(fieldLocations);
        // THEN
        Assertions.assertThat(fieldLocations).containsExactly(fieldLocation1, fieldLocation2, fieldLocation3, fieldLocation4);
    }
}

