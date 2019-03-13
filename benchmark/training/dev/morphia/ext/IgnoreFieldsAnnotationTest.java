/**
 * Copyright (C) 2010 Olafur Gauti Gudmundsson
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package dev.morphia.ext;


import dev.morphia.TestBase;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import dev.morphia.query.FindOptions;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Scott Hernandez
 */
public class IgnoreFieldsAnnotationTest extends TestBase {
    @Test
    public void testIt() {
        final IgnoreFieldsAnnotationTest.User u = new IgnoreFieldsAnnotationTest.User();
        u.email = "ScottHernandez@gmail.com";
        u.ignored = "test";
        getDs().save(u);
        final IgnoreFieldsAnnotationTest.User uLoaded = getDs().find(IgnoreFieldsAnnotationTest.User.class).find(new FindOptions().limit(1)).next();
        Assert.assertEquals("never, never", uLoaded.ignored);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.TYPE })
    @interface IgnoreFields {
        String value();
    }

    @Entity
    @IgnoreFieldsAnnotationTest.IgnoreFields("ignored")
    static class User {
        @Id
        private ObjectId id;

        private String email;

        private String ignored = "never, never";
    }
}

