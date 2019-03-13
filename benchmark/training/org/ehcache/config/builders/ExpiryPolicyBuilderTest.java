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
package org.ehcache.config.builders;


import java.time.Duration;
import java.util.function.Supplier;
import org.ehcache.expiry.ExpiryPolicy;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * ExpiryPolicyBuilderTest
 */
public class ExpiryPolicyBuilderTest {
    @Test
    public void testNoExpiration() {
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.noExpiration();
        MatcherAssert.assertThat(expiry, Matchers.sameInstance(ExpiryPolicy.NO_EXPIRY));
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(ExpiryPolicy.INFINITE));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.nullValue());
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.nullValue());
    }

    @Test
    public void testTTIExpiration() {
        Duration duration = Duration.ofSeconds(1L);
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToIdleExpiration(duration);
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(duration));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.equalTo(duration));
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.equalTo(duration));
        ExpiryPolicy<Object, Object> otherExpiry = ExpiryPolicyBuilder.timeToIdleExpiration(Duration.ofSeconds(1L));
        MatcherAssert.assertThat(otherExpiry, Matchers.equalTo(expiry));
    }

    @Test
    public void testTTLExpiration() {
        Duration duration = Duration.ofSeconds(1L);
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.timeToLiveExpiration(duration);
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(duration));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.nullValue());
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.equalTo(duration));
        ExpiryPolicy<Object, Object> otherExpiry = ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(1L));
        MatcherAssert.assertThat(otherExpiry, Matchers.equalTo(expiry));
    }

    @Test
    public void testExpiration() {
        Duration creation = Duration.ofSeconds(1L);
        Duration access = Duration.ofSeconds(2L);
        Duration update = Duration.ofSeconds(3L);
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.expiry().create(creation).access(access).update(update).build();
        MatcherAssert.assertThat(expiry.getExpiryForCreation(this, this), Matchers.equalTo(creation));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(this, () -> this), Matchers.equalTo(access));
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(this, () -> this, this), Matchers.equalTo(update));
    }

    @Test
    public void testExpirationFunctions() {
        Duration creation = Duration.ofSeconds(1L);
        Duration access = Duration.ofSeconds(2L);
        Duration update = Duration.ofSeconds(3L);
        ExpiryPolicy<Object, Object> expiry = ExpiryPolicyBuilder.expiry().create(( k, v) -> {
            assertThat(k, equalTo(10L));
            assertThat(v, equalTo(20L));
            return creation;
        }).access(( k, v) -> {
            assertThat(k, equalTo(10L));
            assertThat(v.get(), equalTo(20L));
            return access;
        }).update(( k, v1, v2) -> {
            assertThat(k, equalTo(10L));
            assertThat(v1.get(), equalTo(20L));
            assertThat(v2, equalTo(30L));
            return update;
        }).build();
        MatcherAssert.assertThat(expiry.getExpiryForCreation(10L, 20L), Matchers.equalTo(creation));
        MatcherAssert.assertThat(expiry.getExpiryForAccess(10L, () -> 20L), Matchers.equalTo(access));
        MatcherAssert.assertThat(expiry.getExpiryForUpdate(10L, () -> 20L, 30L), Matchers.equalTo(update));
    }
}

