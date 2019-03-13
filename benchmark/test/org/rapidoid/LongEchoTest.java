/**
 * -
 * #%L
 * rapidoid-networking
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
package org.rapidoid;


import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.commons.Str;
import org.rapidoid.docs.echoprotocol.EchoProtocol;
import org.rapidoid.util.Msc;


@Authors("Nikolche Mihajlovski")
@Since("5.4.0")
public class LongEchoTest extends NetTestCommons {
    private static final int ROUNDS = Msc.normalOrHeavy(1, 200);

    private static final String MSG = Str.mul("a", 50000);

    @Test
    public void longEcho() {
        server(new EchoProtocol(), () -> {
            for (int i = 0; i < (LongEchoTest.ROUNDS); i++) {
                connectAndExercise();
            }
        });
    }
}

