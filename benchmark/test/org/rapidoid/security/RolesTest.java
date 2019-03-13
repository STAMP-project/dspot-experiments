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
package org.rapidoid.security;


import Conf.USERS;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.test.AbstractCommonsTest;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("4.4.0")
public class RolesTest extends AbstractCommonsTest {
    @Test
    public void testRolesConfig() {
        isFalse(USERS.toMap().isEmpty());
        isTrue(USERS.toMap().containsKey("niko"));
        isTrue(USERS.has("niko"));
        isFalse(USERS.sub("niko").isEmpty());
        isTrue(USERS.sub("niko").has("email"));
        eq(USERS.sub("niko").get("email"), "niko@rapidoid.org.abcde");
        eq(USERS.sub("niko").entry("password").str().or("none"), "easy");
        eq(Auth.getRolesFor("niko"), U.set("owner", "moderator", "administrator"));
        eq(Auth.getRolesFor("chuck"), U.set("moderator", "restarter"));
        eq(Auth.getRolesFor("abc"), U.set("guest", "foo", "bar"));
        eq(Auth.getRolesFor("zzz"), U.set());
        eq(Auth.getRolesFor(""), U.set());
        eq(Auth.getRolesFor(null), U.set());
    }
}

