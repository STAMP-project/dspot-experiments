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


import java.util.HashMap;
import java.util.Map;
import org.junit.Test;


/**
 * Tests for {@link DefaultAddressStandardClaim}.
 *
 * @author Joe Grandja
 */
public class DefaultAddressStandardClaimTests {
    static final String FORMATTED_FIELD_NAME = "formatted";

    static final String STREET_ADDRESS_FIELD_NAME = "street_address";

    static final String LOCALITY_FIELD_NAME = "locality";

    static final String REGION_FIELD_NAME = "region";

    static final String POSTAL_CODE_FIELD_NAME = "postal_code";

    static final String COUNTRY_FIELD_NAME = "country";

    static final String FORMATTED = "formatted";

    static final String STREET_ADDRESS = "street_address";

    static final String LOCALITY = "locality";

    static final String REGION = "region";

    static final String POSTAL_CODE = "postal_code";

    static final String COUNTRY = "country";

    @Test
    public void buildWhenAllAttributesProvidedThenAllAttributesAreSet() {
        AddressStandardClaim addressStandardClaim = new DefaultAddressStandardClaim.Builder().formatted(DefaultAddressStandardClaimTests.FORMATTED).streetAddress(DefaultAddressStandardClaimTests.STREET_ADDRESS).locality(DefaultAddressStandardClaimTests.LOCALITY).region(DefaultAddressStandardClaimTests.REGION).postalCode(DefaultAddressStandardClaimTests.POSTAL_CODE).country(DefaultAddressStandardClaimTests.COUNTRY).build();
        assertThat(addressStandardClaim.getFormatted()).isEqualTo(DefaultAddressStandardClaimTests.FORMATTED);
        assertThat(addressStandardClaim.getStreetAddress()).isEqualTo(DefaultAddressStandardClaimTests.STREET_ADDRESS);
        assertThat(addressStandardClaim.getLocality()).isEqualTo(DefaultAddressStandardClaimTests.LOCALITY);
        assertThat(addressStandardClaim.getRegion()).isEqualTo(DefaultAddressStandardClaimTests.REGION);
        assertThat(addressStandardClaim.getPostalCode()).isEqualTo(DefaultAddressStandardClaimTests.POSTAL_CODE);
        assertThat(addressStandardClaim.getCountry()).isEqualTo(DefaultAddressStandardClaimTests.COUNTRY);
    }

    @Test
    public void buildWhenAllAttributesProvidedToConstructorThenAllAttributesAreSet() {
        Map<String, Object> addressFields = new HashMap<>();
        addressFields.put(DefaultAddressStandardClaimTests.FORMATTED_FIELD_NAME, DefaultAddressStandardClaimTests.FORMATTED);
        addressFields.put(DefaultAddressStandardClaimTests.STREET_ADDRESS_FIELD_NAME, DefaultAddressStandardClaimTests.STREET_ADDRESS);
        addressFields.put(DefaultAddressStandardClaimTests.LOCALITY_FIELD_NAME, DefaultAddressStandardClaimTests.LOCALITY);
        addressFields.put(DefaultAddressStandardClaimTests.REGION_FIELD_NAME, DefaultAddressStandardClaimTests.REGION);
        addressFields.put(DefaultAddressStandardClaimTests.POSTAL_CODE_FIELD_NAME, DefaultAddressStandardClaimTests.POSTAL_CODE);
        addressFields.put(DefaultAddressStandardClaimTests.COUNTRY_FIELD_NAME, DefaultAddressStandardClaimTests.COUNTRY);
        AddressStandardClaim addressStandardClaim = new DefaultAddressStandardClaim.Builder(addressFields).build();
        assertThat(addressStandardClaim.getFormatted()).isEqualTo(DefaultAddressStandardClaimTests.FORMATTED);
        assertThat(addressStandardClaim.getStreetAddress()).isEqualTo(DefaultAddressStandardClaimTests.STREET_ADDRESS);
        assertThat(addressStandardClaim.getLocality()).isEqualTo(DefaultAddressStandardClaimTests.LOCALITY);
        assertThat(addressStandardClaim.getRegion()).isEqualTo(DefaultAddressStandardClaimTests.REGION);
        assertThat(addressStandardClaim.getPostalCode()).isEqualTo(DefaultAddressStandardClaimTests.POSTAL_CODE);
        assertThat(addressStandardClaim.getCountry()).isEqualTo(DefaultAddressStandardClaimTests.COUNTRY);
    }
}

