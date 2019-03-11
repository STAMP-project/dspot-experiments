/**
 * Copyright 2016 higherfrequencytrading.com
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.openhft.chronicle.queue;


import net.openhft.chronicle.core.OS;
import net.openhft.chronicle.core.annotation.RequiredForClient;
import net.openhft.chronicle.queue.impl.single.SingleChronicleQueueBuilder;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RequiredForClient
public class MoveIndexAfterFailedTailerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoveIndexAfterFailedTailerTest.class);

    @Test
    public void test() {
        String basePath = ((((OS.TARGET) + "/") + (getClass().getSimpleName())) + "-") + (System.nanoTime());
        final SingleChronicleQueueBuilder myBuilder = SingleChronicleQueueBuilder.single(basePath).testBlockSize().timeProvider(System::currentTimeMillis).rollCycle(RollCycles.HOURLY);
        int messages = 10;
        try (final ChronicleQueue myWrite = myBuilder.build()) {
            write(myWrite, messages);
            System.out.println(myWrite.dump());
        }
        try (final ChronicleQueue myRead = myBuilder.build()) {
            read(myRead, messages);
        }
    }
}

