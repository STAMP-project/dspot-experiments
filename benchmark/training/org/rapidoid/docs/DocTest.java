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
package org.rapidoid.docs;


import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.rapidoid.docs.blank.BlankTest;
import org.rapidoid.http.IsolatedIntegrationTest;


/**
 * Base class used as entry point, to execute an example and generate the docs.
 * <p>
 * This test will execute the main class specified in the annotation.
 */
public abstract class DocTest extends IsolatedIntegrationTest {
    private static final String LICENSE_HEADER = "(?sm)(^|\\n)\\Q/*-\n * #" + "%L\\E(.*?)\\Q * #L%\n */\n\\E";

    private final AtomicInteger order = new AtomicInteger();

    @Test
    public void docs() {
        if ((this) instanceof BlankTest) {
            return;// not a real test

        }
        order.set(0);
        exercise();
        generateDocs();
    }
}

