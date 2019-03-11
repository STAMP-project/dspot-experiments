/**
 * Copyright 2002-2017 the original author or authors.
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
package org.springframework.security.oauth2.core.oidc;


import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link OidcUserInfo}.
 *
 * @author Joe Grandja
 */
public class OidcUserInfoTests {
    private static final String SUB_CLAIM = "sub";

    private static final String NAME_CLAIM = "name";

    private static final String GIVEN_NAME_CLAIM = "given_name";

    private static final String FAMILY_NAME_CLAIM = "family_name";

    private static final String MIDDLE_NAME_CLAIM = "middle_name";

    private static final String NICKNAME_CLAIM = "nickname";

    private static final String PREFERRED_USERNAME_CLAIM = "preferred_username";

    private static final String PROFILE_CLAIM = "profile";

    private static final String PICTURE_CLAIM = "picture";

    private static final String WEBSITE_CLAIM = "website";

    private static final String EMAIL_CLAIM = "email";

    private static final String EMAIL_VERIFIED_CLAIM = "email_verified";

    private static final String GENDER_CLAIM = "gender";

    private static final String BIRTHDATE_CLAIM = "birthdate";

    private static final String ZONEINFO_CLAIM = "zoneinfo";

    private static final String LOCALE_CLAIM = "locale";

    private static final String PHONE_NUMBER_CLAIM = "phone_number";

    private static final String PHONE_NUMBER_VERIFIED_CLAIM = "phone_number_verified";

    private static final String ADDRESS_CLAIM = "address";

    private static final String UPDATED_AT_CLAIM = "updated_at";

    private static final String SUB_VALUE = "subject1";

    private static final String NAME_VALUE = "full_name";

    private static final String GIVEN_NAME_VALUE = "given_name";

    private static final String FAMILY_NAME_VALUE = "family_name";

    private static final String MIDDLE_NAME_VALUE = "middle_name";

    private static final String NICKNAME_VALUE = "nickname";

    private static final String PREFERRED_USERNAME_VALUE = "preferred_username";

    private static final String PROFILE_VALUE = "profile";

    private static final String PICTURE_VALUE = "picture";

    private static final String WEBSITE_VALUE = "website";

    private static final String EMAIL_VALUE = "email";

    private static final Boolean EMAIL_VERIFIED_VALUE = true;

    private static final String GENDER_VALUE = "gender";

    private static final String BIRTHDATE_VALUE = "birthdate";

    private static final String ZONEINFO_VALUE = "zoneinfo";

    private static final String LOCALE_VALUE = "locale";

    private static final String PHONE_NUMBER_VALUE = "phone_number";

    private static final Boolean PHONE_NUMBER_VERIFIED_VALUE = true;

    private static final Map<String, Object> ADDRESS_VALUE;

    private static final long UPDATED_AT_VALUE = Instant.now().minusSeconds(60).toEpochMilli();

    private static final Map<String, Object> CLAIMS;

    static {
        CLAIMS = new HashMap<>();
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.SUB_CLAIM, OidcUserInfoTests.SUB_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.NAME_CLAIM, OidcUserInfoTests.NAME_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.GIVEN_NAME_CLAIM, OidcUserInfoTests.GIVEN_NAME_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.FAMILY_NAME_CLAIM, OidcUserInfoTests.FAMILY_NAME_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.MIDDLE_NAME_CLAIM, OidcUserInfoTests.MIDDLE_NAME_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.NICKNAME_CLAIM, OidcUserInfoTests.NICKNAME_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.PREFERRED_USERNAME_CLAIM, OidcUserInfoTests.PREFERRED_USERNAME_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.PROFILE_CLAIM, OidcUserInfoTests.PROFILE_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.PICTURE_CLAIM, OidcUserInfoTests.PICTURE_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.WEBSITE_CLAIM, OidcUserInfoTests.WEBSITE_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.EMAIL_CLAIM, OidcUserInfoTests.EMAIL_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.EMAIL_VERIFIED_CLAIM, OidcUserInfoTests.EMAIL_VERIFIED_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.GENDER_CLAIM, OidcUserInfoTests.GENDER_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.BIRTHDATE_CLAIM, OidcUserInfoTests.BIRTHDATE_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.ZONEINFO_CLAIM, OidcUserInfoTests.ZONEINFO_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.LOCALE_CLAIM, OidcUserInfoTests.LOCALE_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.PHONE_NUMBER_CLAIM, OidcUserInfoTests.PHONE_NUMBER_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.PHONE_NUMBER_VERIFIED_CLAIM, OidcUserInfoTests.PHONE_NUMBER_VERIFIED_VALUE);
        ADDRESS_VALUE = new HashMap<>();
        OidcUserInfoTests.ADDRESS_VALUE.put(DefaultAddressStandardClaimTests.FORMATTED_FIELD_NAME, DefaultAddressStandardClaimTests.FORMATTED);
        OidcUserInfoTests.ADDRESS_VALUE.put(DefaultAddressStandardClaimTests.STREET_ADDRESS_FIELD_NAME, DefaultAddressStandardClaimTests.STREET_ADDRESS);
        OidcUserInfoTests.ADDRESS_VALUE.put(DefaultAddressStandardClaimTests.LOCALITY_FIELD_NAME, DefaultAddressStandardClaimTests.LOCALITY);
        OidcUserInfoTests.ADDRESS_VALUE.put(DefaultAddressStandardClaimTests.REGION_FIELD_NAME, DefaultAddressStandardClaimTests.REGION);
        OidcUserInfoTests.ADDRESS_VALUE.put(DefaultAddressStandardClaimTests.POSTAL_CODE_FIELD_NAME, DefaultAddressStandardClaimTests.POSTAL_CODE);
        OidcUserInfoTests.ADDRESS_VALUE.put(DefaultAddressStandardClaimTests.COUNTRY_FIELD_NAME, DefaultAddressStandardClaimTests.COUNTRY);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.ADDRESS_CLAIM, OidcUserInfoTests.ADDRESS_VALUE);
        OidcUserInfoTests.CLAIMS.put(OidcUserInfoTests.UPDATED_AT_CLAIM, OidcUserInfoTests.UPDATED_AT_VALUE);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorWhenClaimsIsEmptyThenThrowIllegalArgumentException() {
        new OidcUserInfo(Collections.emptyMap());
    }

    @Test
    public void constructorWhenParametersProvidedAndValidThenCreated() {
        OidcUserInfo userInfo = new OidcUserInfo(OidcUserInfoTests.CLAIMS);
        assertThat(userInfo.getClaims()).isEqualTo(OidcUserInfoTests.CLAIMS);
        assertThat(userInfo.getSubject()).isEqualTo(OidcUserInfoTests.SUB_VALUE);
        assertThat(userInfo.getFullName()).isEqualTo(OidcUserInfoTests.NAME_VALUE);
        assertThat(userInfo.getGivenName()).isEqualTo(OidcUserInfoTests.GIVEN_NAME_VALUE);
        assertThat(userInfo.getFamilyName()).isEqualTo(OidcUserInfoTests.FAMILY_NAME_VALUE);
        assertThat(userInfo.getMiddleName()).isEqualTo(OidcUserInfoTests.MIDDLE_NAME_VALUE);
        assertThat(userInfo.getNickName()).isEqualTo(OidcUserInfoTests.NICKNAME_VALUE);
        assertThat(userInfo.getPreferredUsername()).isEqualTo(OidcUserInfoTests.PREFERRED_USERNAME_VALUE);
        assertThat(userInfo.getProfile()).isEqualTo(OidcUserInfoTests.PROFILE_VALUE);
        assertThat(userInfo.getPicture()).isEqualTo(OidcUserInfoTests.PICTURE_VALUE);
        assertThat(userInfo.getWebsite()).isEqualTo(OidcUserInfoTests.WEBSITE_VALUE);
        assertThat(userInfo.getEmail()).isEqualTo(OidcUserInfoTests.EMAIL_VALUE);
        assertThat(userInfo.getEmailVerified()).isEqualTo(OidcUserInfoTests.EMAIL_VERIFIED_VALUE);
        assertThat(userInfo.getGender()).isEqualTo(OidcUserInfoTests.GENDER_VALUE);
        assertThat(userInfo.getBirthdate()).isEqualTo(OidcUserInfoTests.BIRTHDATE_VALUE);
        assertThat(userInfo.getZoneInfo()).isEqualTo(OidcUserInfoTests.ZONEINFO_VALUE);
        assertThat(userInfo.getLocale()).isEqualTo(OidcUserInfoTests.LOCALE_VALUE);
        assertThat(userInfo.getPhoneNumber()).isEqualTo(OidcUserInfoTests.PHONE_NUMBER_VALUE);
        assertThat(userInfo.getPhoneNumberVerified()).isEqualTo(OidcUserInfoTests.PHONE_NUMBER_VERIFIED_VALUE);
        assertThat(userInfo.getAddress()).isEqualTo(new DefaultAddressStandardClaim.Builder(OidcUserInfoTests.ADDRESS_VALUE).build());
        assertThat(userInfo.getUpdatedAt().getEpochSecond()).isEqualTo(OidcUserInfoTests.UPDATED_AT_VALUE);
    }
}

