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


import java.util.List;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;
import org.rapidoid.buffer.BufUtil;
import org.rapidoid.commons.Rnd;
import org.rapidoid.docs.echoprotocol.EchoProtocol;
import org.rapidoid.u.U;
import org.rapidoid.util.Msc;


@Authors("Nikolche Mihajlovski")
@Since("2.0.0")
public class EchoProtocolTest extends NetTestCommons {
    private static final int ROUNDS = Msc.normalOrHeavy(1, 100);

    private static final int MAX_MSG_COUNT = Msc.normalOrHeavy(10, 1000);

    private static final List<String> testCases = U.list("abc\nxy\nbye\n", "abc\r\nxy\r\nbye\r\n", "abc\nbye\n", "abc\r\nbye\r\n");

    static {
        String s1 = "";
        String s2 = "";
        for (int i = 0; i < (EchoProtocolTest.MAX_MSG_COUNT); i++) {
            s1 += i + "\r\n";
            s2 += i + "\n";
        }
        EchoProtocolTest.testCases.add((s1 + "bye\r\n"));
        EchoProtocolTest.testCases.add((s2 + "bye\n"));
    }

    @Test
    public void echo() {
        server(new EchoProtocol(), this::connectAndExercise);
    }

    @Test
    public void echoAsync() {
        server(( ctx) -> {
            if (ctx.isInitial()) {
                BufUtil.doneWriting(ctx.output());
                return;
            }
            final String in = ctx.readln();
            final long handle = ctx.async();
            final long connId = ctx.connId();
            Msc.EXECUTOR.schedule(() -> ctx.resume(connId, handle, () -> {
                ctx.write(in.toUpperCase());
                ctx.write(Constants.CR_LF);
                ctx.send();
                ctx.closeIf(in.equals("bye"));
                return true;// finished

            }), Rnd.rnd(100), TimeUnit.MILLISECONDS);
        }, this::connectAndExercise);
    }
}

