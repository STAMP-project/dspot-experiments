/**
 * -
 * #%L
 * rapidoid-integration-tests
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
package org.rapidoid.http;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.reverseproxy.Reverse;
import org.rapidoid.u.U;


@Authors("Nikolche Mihajlovski")
@Since("5.3.0")
public class ReverseProxySecurityTest extends IsolatedIntegrationTest {
    @Test
    public void testProxyRoles() {
        Reverse.proxy("/x").to("non-existing").roles("admin").add();
        getReq("/x");
        postData("/x", U.map("x", 1));
        putData("/x/foo", U.map("y", 2));
        deleteReq("/x/asd");
        patchData("/x/", U.map("z", 3));
    }
}

