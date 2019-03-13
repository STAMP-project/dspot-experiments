/**
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.navercorp.pinpoint.common.util;


import java.util.UUID;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 *
 * @author emeroad
 */
public class StringTraceHeaderParserTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private StringTraceHeaderParser parser = new StringTraceHeaderParser();

    @Test
    public void getIdSize() {
        String test = "3ccb94f3-a8fe-4464-bfbd-d35490afab3d";
        logger.debug("idSize={}", test.length());
    }

    @Test
    public void createStringBaseTraceHeader() {
        createAndParser(UUID.randomUUID().toString(), 123, 345, 23423, ((short) (22)));
        createAndParser(UUID.randomUUID().toString(), (-1), 2, 0, ((short) (0)));
        createAndParser(UUID.randomUUID().toString(), 234, 2, 0, ((short) (0)));
    }
}

