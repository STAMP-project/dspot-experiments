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
package org.assertj.core.error;


import java.util.Map;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.MapEntry;
import org.assertj.core.description.TextDescription;
import org.assertj.core.presentation.StandardRepresentation;
import org.assertj.core.test.Maps;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;


/**
 * Tests for <code>{@link ShouldContainKeys#create(Description)}</code>.
 *
 * @author Nicolas Fran?ois
 * @author Willima Telloue
 * @author Joel Costigliola
 */
public class ShouldContainKeys_create_Test {
    @Test
    public void should_create_error_message() {
        Map<?, ?> map = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
        ErrorMessageFactory factory = ShouldContainKeys.shouldContainKeys(map, Sets.newLinkedHashSet("name"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format("[Test] %nExpecting:%n <{\"color\"=\"green\", \"name\"=\"Yoda\"}>%nto contain key:%n <\"name\">"));
    }

    @Test
    public void should_create_error_message_with_multiple_keys() {
        Map<?, ?> map = Maps.mapOf(MapEntry.entry("name", "Yoda"), MapEntry.entry("color", "green"));
        ErrorMessageFactory factory = ShouldContainKeys.shouldContainKeys(map, Sets.newLinkedHashSet("name", "color"));
        String message = factory.create(new TextDescription("Test"), new StandardRepresentation());
        Assertions.assertThat(message).isEqualTo(String.format("[Test] %nExpecting:%n <{\"color\"=\"green\", \"name\"=\"Yoda\"}>%nto contain keys:%n <[\"name\", \"color\"]>"));
    }
}

