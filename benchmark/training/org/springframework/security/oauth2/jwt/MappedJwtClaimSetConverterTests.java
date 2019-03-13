/**
 * Copyright 2002-2018 the original author or authors.
 *
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
 */
package org.springframework.security.oauth2.jwt;


import JwtClaimNames.AUD;
import JwtClaimNames.EXP;
import JwtClaimNames.IAT;
import JwtClaimNames.ISS;
import JwtClaimNames.JTI;
import JwtClaimNames.NBF;
import JwtClaimNames.SUB;
import java.net.URI;
import java.net.URL;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.core.convert.converter.Converter;


/**
 * Tests for {@link MappedJwtClaimSetConverter}
 *
 * @author Josh Cummings
 */
public class MappedJwtClaimSetConverterTests {
    @Test
    public void convertWhenUsingCustomExpiresAtConverterThenIssuedAtConverterStillConsultsIt() {
        Instant at = Instant.ofEpochMilli(1000000000000L);
        Converter<Object, Instant> expiresAtConverter = Mockito.mock(Converter.class);
        Mockito.when(expiresAtConverter.convert(ArgumentMatchers.any())).thenReturn(at);
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.singletonMap(EXP, expiresAtConverter));
        Map<String, Object> source = new HashMap<>();
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get(IAT)).isEqualTo(Instant.ofEpochMilli(at.toEpochMilli()).minusSeconds(1));
    }

    @Test
    public void convertWhenUsingDefaultsThenBasesIssuedAtOffOfExpiration() {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> source = Collections.singletonMap(EXP, 1000000000L);
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get(EXP)).isEqualTo(Instant.ofEpochSecond(1000000000L));
        assertThat(target.get(IAT)).isEqualTo(Instant.ofEpochSecond(1000000000L).minusSeconds(1));
    }

    @Test
    public void convertWhenUsingDefaultsThenCoercesAudienceAccordingToJwtSpec() {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> source = Collections.singletonMap(AUD, "audience");
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get(AUD)).isInstanceOf(Collection.class);
        assertThat(target.get(AUD)).isEqualTo(Arrays.asList("audience"));
        source = Collections.singletonMap(AUD, Arrays.asList("one", "two"));
        target = converter.convert(source);
        assertThat(target.get(AUD)).isInstanceOf(Collection.class);
        assertThat(target.get(AUD)).isEqualTo(Arrays.asList("one", "two"));
    }

    @Test
    public void convertWhenUsingDefaultsThenCoercesAllAttributesInJwtSpec() throws Exception {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> source = new HashMap<>();
        source.put(JTI, 1);
        source.put(AUD, "audience");
        source.put(EXP, 2000000000L);
        source.put(IAT, new Date(1000000000000L));
        source.put(ISS, "https://any.url");
        source.put(NBF, 1000000000);
        source.put(SUB, 1234);
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get(JTI)).isEqualTo("1");
        assertThat(target.get(AUD)).isEqualTo(Arrays.asList("audience"));
        assertThat(target.get(EXP)).isEqualTo(Instant.ofEpochSecond(2000000000L));
        assertThat(target.get(IAT)).isEqualTo(Instant.ofEpochSecond(1000000000L));
        assertThat(target.get(ISS)).isEqualTo("https://any.url");
        assertThat(target.get(NBF)).isEqualTo(Instant.ofEpochSecond(1000000000L));
        assertThat(target.get(SUB)).isEqualTo("1234");
    }

    @Test
    public void convertWhenUsingCustomConverterThenAllOtherDefaultsAreStillUsed() throws Exception {
        Converter<Object, String> claimConverter = Mockito.mock(Converter.class);
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.singletonMap(SUB, claimConverter));
        Mockito.when(claimConverter.convert(ArgumentMatchers.any(Object.class))).thenReturn("1234");
        Map<String, Object> source = new HashMap<>();
        source.put(JTI, 1);
        source.put(AUD, "audience");
        source.put(EXP, Instant.ofEpochSecond(2000000000L));
        source.put(IAT, new Date(1000000000000L));
        source.put(ISS, URI.create("https://any.url"));
        source.put(NBF, "1000000000");
        source.put(SUB, 2345);
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get(JTI)).isEqualTo("1");
        assertThat(target.get(AUD)).isEqualTo(Arrays.asList("audience"));
        assertThat(target.get(EXP)).isEqualTo(Instant.ofEpochSecond(2000000000L));
        assertThat(target.get(IAT)).isEqualTo(Instant.ofEpochSecond(1000000000L));
        assertThat(target.get(ISS)).isEqualTo("https://any.url");
        assertThat(target.get(NBF)).isEqualTo(Instant.ofEpochSecond(1000000000L));
        assertThat(target.get(SUB)).isEqualTo("1234");
    }

    @Test
    public void convertWhenConverterReturnsNullThenClaimIsRemoved() {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> source = Collections.singletonMap(ISS, null);
        Map<String, Object> target = converter.convert(source);
        assertThat(target).doesNotContainKey(ISS);
    }

    @Test
    public void convertWhenConverterReturnsValueWhenEntryIsMissingThenEntryIsAdded() {
        Converter<Object, String> claimConverter = Mockito.mock(Converter.class);
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.singletonMap("custom-claim", claimConverter));
        Mockito.when(claimConverter.convert(ArgumentMatchers.any())).thenReturn("custom-value");
        Map<String, Object> source = new HashMap<>();
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get("custom-claim")).isEqualTo("custom-value");
    }

    @Test
    public void convertWhenUsingConstructorThenOnlyConvertersInThatMapAreUsedForConversion() {
        Converter<Object, String> claimConverter = Mockito.mock(Converter.class);
        MappedJwtClaimSetConverter converter = new MappedJwtClaimSetConverter(Collections.singletonMap(SUB, claimConverter));
        Mockito.when(claimConverter.convert(ArgumentMatchers.any(Object.class))).thenReturn("1234");
        Map<String, Object> source = new HashMap<>();
        source.put(JTI, new Object());
        source.put(AUD, new Object());
        source.put(EXP, Instant.ofEpochSecond(1L));
        source.put(IAT, Instant.ofEpochSecond(1L));
        source.put(ISS, new Object());
        source.put(NBF, new Object());
        source.put(SUB, new Object());
        Map<String, Object> target = converter.convert(source);
        assertThat(target.get(JTI)).isEqualTo(source.get(JTI));
        assertThat(target.get(AUD)).isEqualTo(source.get(AUD));
        assertThat(target.get(EXP)).isEqualTo(source.get(EXP));
        assertThat(target.get(IAT)).isEqualTo(source.get(IAT));
        assertThat(target.get(ISS)).isEqualTo(source.get(ISS));
        assertThat(target.get(NBF)).isEqualTo(source.get(NBF));
        assertThat(target.get(SUB)).isEqualTo("1234");
    }

    @Test
    public void convertWhenUsingDefaultsThenFailedConversionThrowsIllegalStateException() {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> badIssuer = Collections.singletonMap(ISS, "https://badly formed iss");
        assertThatCode(() -> converter.convert(badIssuer)).isInstanceOf(IllegalStateException.class);
        Map<String, Object> badIssuedAt = Collections.singletonMap(IAT, "badly-formed-iat");
        assertThatCode(() -> converter.convert(badIssuedAt)).isInstanceOf(IllegalStateException.class);
        Map<String, Object> badExpiresAt = Collections.singletonMap(EXP, "badly-formed-exp");
        assertThatCode(() -> converter.convert(badExpiresAt)).isInstanceOf(IllegalStateException.class);
        Map<String, Object> badNotBefore = Collections.singletonMap(NBF, "badly-formed-nbf");
        assertThatCode(() -> converter.convert(badNotBefore)).isInstanceOf(IllegalStateException.class);
    }

    // gh-6073
    @Test
    public void convertWhenIssuerIsNotAUriThenConvertsToString() {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> nonUriIssuer = Collections.singletonMap(ISS, "issuer");
        Map<String, Object> target = converter.convert(nonUriIssuer);
        assertThat(target.get(ISS)).isEqualTo("issuer");
    }

    // gh-6073
    @Test
    public void convertWhenIssuerIsOfTypeURLThenConvertsToString() throws Exception {
        MappedJwtClaimSetConverter converter = MappedJwtClaimSetConverter.withDefaults(Collections.emptyMap());
        Map<String, Object> issuer = Collections.singletonMap(ISS, new URL("https://issuer"));
        Map<String, Object> target = converter.convert(issuer);
        assertThat(target.get(ISS)).isEqualTo("https://issuer");
    }

    @Test
    public void constructWhenAnyParameterIsNullThenIllegalArgumentException() {
        assertThatCode(() -> new MappedJwtClaimSetConverter(null)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void withDefaultsWhenAnyParameterIsNullThenIllegalArgumentException() {
        assertThatCode(() -> MappedJwtClaimSetConverter.withDefaults(null)).isInstanceOf(IllegalArgumentException.class);
    }
}

