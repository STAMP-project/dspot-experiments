/**
 * Copyright Terracotta, Inc.
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
package org.ehcache.expiry;


import java.util.concurrent.TimeUnit;
import org.ehcache.ValueSupplier;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


@SuppressWarnings("deprecation")
public class ExpirationsTest {
    @Test
    public void testNoExpiration() {
        Expiry<Object, Object> expiry = Expirations.noExpiration();
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(Duration.INFINITE));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.nullValue());
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.nullValue());
    }

    @Test
    public void testTTIExpiration() {
        Duration duration = new Duration(1L, TimeUnit.SECONDS);
        Expiry<Object, Object> expiry = Expirations.timeToIdleExpiration(duration);
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(duration));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.equalTo(duration));
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.equalTo(duration));
    }

    @Test
    public void testTTLExpiration() {
        Duration duration = new Duration(1L, TimeUnit.SECONDS);
        Expiry<Object, Object> expiry = Expirations.timeToLiveExpiration(duration);
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(duration));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.nullValue());
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.equalTo(duration));
    }

    @Test
    public void testExpiration() {
        Duration creation = new Duration(1L, TimeUnit.SECONDS);
        Duration access = new Duration(2L, TimeUnit.SECONDS);
        Duration update = new Duration(3L, TimeUnit.SECONDS);
        Expiry<Object, Object> expiry = Expirations.builder().setCreate(creation).setAccess(access).setUpdate(update).build();
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(creation));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.equalTo(access));
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.equalTo(update));
    }
}

