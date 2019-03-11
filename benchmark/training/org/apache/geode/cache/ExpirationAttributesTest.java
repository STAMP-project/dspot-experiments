/**
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license
 * agreements. See the NOTICE file distributed with this work for additional information regarding
 * copyright ownership. The ASF licenses this file to You under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.geode.cache;


import ExpirationAction.DESTROY;
import ExpirationAction.INVALIDATE;
import org.junit.Test;

import static ExpirationAction.DESTROY;


public class ExpirationAttributesTest {
    @Test
    public void constructor() throws Exception {
        ExpirationAttributes attributes = new ExpirationAttributes();
        assertThat(attributes.getTimeout()).isEqualTo(0);
        assertThat(attributes.getAction()).isEqualTo(INVALIDATE);
        attributes = new ExpirationAttributes((-10), null);
        assertThat(attributes.getTimeout()).isEqualTo(0);
        assertThat(attributes.getAction()).isEqualTo(INVALIDATE);
        attributes = new ExpirationAttributes(10);
        assertThat(attributes.getTimeout()).isEqualTo(10);
        assertThat(attributes.getAction()).isEqualTo(INVALIDATE);
        attributes = new ExpirationAttributes(10, null);
        assertThat(attributes.getTimeout()).isEqualTo(10);
        assertThat(attributes.getAction()).isEqualTo(INVALIDATE);
        attributes = new ExpirationAttributes(20, DESTROY);
        assertThat(attributes.getTimeout()).isEqualTo(20);
        assertThat(attributes.getAction()).isEqualTo(DESTROY);
    }
}

