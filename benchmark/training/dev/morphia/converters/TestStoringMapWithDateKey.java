/**
 * Copyright (c) 2008-2015 MongoDB, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.morphia.converters;


import dev.morphia.TestBase;
import dev.morphia.query.FindOptions;
import java.util.Date;
import java.util.Locale;
import org.junit.Assert;
import org.junit.Test;


public class TestStoringMapWithDateKey extends TestBase {
    private Locale locale;

    @Test
    public void testSaveFindEntity() {
        getMorphia().map(User.class);
        final User expectedUser = new User();
        expectedUser.addValue(new Date(), 10.0);
        getDs().save(expectedUser);
        Assert.assertNotNull(getDs().find(User.class).find(new FindOptions().limit(1)).tryNext());
    }
}

