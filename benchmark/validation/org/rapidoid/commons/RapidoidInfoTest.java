/**
 * -
 * #%L
 * rapidoid-commons
 * %%
 * Copyright (C) 2014 - 2018 Nikolche Mihajlovski and contributors
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.rapidoid.commons;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.AbstractCommonsTest;


@Authors("Nikolche Mihajlovski")
@Since("5.2.5")
public class RapidoidInfoTest extends AbstractCommonsTest {
    @Test
    public void testVersion() {
        notNull(RapidoidInfo.version());
        isTrue(RapidoidInfo.version().startsWith("6."));
    }

    @Test
    public void testBuiltOn() {
        notNull(RapidoidInfo.builtOn());
        isTrue(RapidoidInfo.builtOn().matches("201\\d-\\d{2}-\\d{2}\\s\\d{2}:\\d{2}\\s\\w{3}"));
    }
}

