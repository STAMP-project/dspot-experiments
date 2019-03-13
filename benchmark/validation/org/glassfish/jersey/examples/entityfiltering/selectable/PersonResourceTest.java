/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2014-2017 Oracle and/or its affiliates. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License.  You can
 * obtain a copy of the License at
 * https://oss.oracle.com/licenses/CDDL+GPL-1.1
 * or LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at LICENSE.txt.
 *
 * GPL Classpath Exception:
 * Oracle designates this particular file as subject to the "Classpath"
 * exception as provided by Oracle in the GPL Version 2 section of the License
 * file that accompanied this code.
 *
 * Modifications:
 * If applicable, add the following below the License Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyright [year] [name of copyright owner]"
 *
 * Contributor(s):
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
package org.glassfish.jersey.examples.entityfiltering.selectable;


import SelectableEntityFilteringFeature.QUERY_PARAM_NAME;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.Feature;
import org.glassfish.jersey.examples.entityfiltering.selectable.domain.Address;
import org.glassfish.jersey.examples.entityfiltering.selectable.domain.Person;
import org.glassfish.jersey.examples.entityfiltering.selectable.domain.PhoneNumber;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.filtering.SelectableEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * {@link PersonResource} unit tests.
 *
 * @author Andy Pemberton (pembertona at gmail.com)
 * @author Michal Gajdos
 */
@RunWith(Parameterized.class)
public class PersonResourceTest extends JerseyTest {
    private final Class<Feature> filteringProvider;

    public PersonResourceTest(final Class<Feature> filteringProvider) {
        super(new ResourceConfig(SelectableEntityFilteringFeature.class).packages("org.glassfish.jersey.examples.entityfiltering.selectable").property(QUERY_PARAM_NAME, "select").register(filteringProvider));
        this.filteringProvider = filteringProvider;
        enable(TestProperties.DUMP_ENTITY);
        enable(TestProperties.LOG_TRAFFIC);
    }

    @Test
    public void testNoFilter() throws Exception {
        final Person entity = target("people").path("1234").request().get(Person.class);
        // Not null values.
        MatcherAssert.assertThat(entity.getFamilyName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getGivenName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getHonorificPrefix(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getHonorificSuffix(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getRegion(), CoreMatchers.notNullValue());
        final List<Address> addresses = entity.getAddresses();
        MatcherAssert.assertThat(addresses, CoreMatchers.notNullValue());
        final Address address = addresses.get(0);
        MatcherAssert.assertThat(address, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(address.getRegion(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(address.getStreetAddress(), CoreMatchers.notNullValue());
        PhoneNumber phoneNumber = address.getPhoneNumber();
        MatcherAssert.assertThat(phoneNumber, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(phoneNumber.getAreaCode(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(phoneNumber.getNumber(), CoreMatchers.notNullValue());
        final Map<String, PhoneNumber> phoneNumbers = entity.getPhoneNumbers();
        MatcherAssert.assertThat(phoneNumbers, CoreMatchers.notNullValue());
        // TODO: enable for MOXy as well when JERSEY-2751 gets fixed.
        if (JacksonFeature.class.isAssignableFrom(filteringProvider)) {
            phoneNumber = phoneNumbers.get("HOME");
            MatcherAssert.assertThat(phoneNumber, CoreMatchers.notNullValue());
            MatcherAssert.assertThat(phoneNumber.getAreaCode(), CoreMatchers.notNullValue());
            MatcherAssert.assertThat(phoneNumber.getNumber(), CoreMatchers.notNullValue());
        }
    }

    @Test
    public void testInvalidFilter() throws Exception {
        final Person entity = target("people").path("1234").queryParam("select", "invalid").request().get(Person.class);
        // All null values.
        MatcherAssert.assertThat(entity.getFamilyName(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getGivenName(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getHonorificPrefix(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getHonorificSuffix(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getRegion(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getAddresses(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getPhoneNumbers(), CoreMatchers.nullValue());
    }

    /**
     * Test first level filters.
     */
    @Test
    public void testFilters() throws Exception {
        final Person entity = target("people").path("1234").queryParam("select", "familyName,givenName").request().get(Person.class);
        // Not null values.
        MatcherAssert.assertThat(entity.getFamilyName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getGivenName(), CoreMatchers.notNullValue());
        // Null values.
        MatcherAssert.assertThat(entity.getAddresses(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getPhoneNumbers(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getRegion(), CoreMatchers.nullValue());
    }

    /**
     * Test 2nd and 3rd level filters.
     */
    @Test
    public void testSubFilters() throws Exception {
        final Person entity = target("people").path("1234").queryParam("select", "familyName,givenName,addresses.streetAddress,addresses.phoneNumber.areaCode").request().get(Person.class);
        // Not null values.
        MatcherAssert.assertThat(entity.getFamilyName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getGivenName(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getAddresses().get(0).getStreetAddress(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getAddresses().get(0).getPhoneNumber().getAreaCode(), CoreMatchers.notNullValue());
        // Null values.
        MatcherAssert.assertThat(entity.getRegion(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(entity.getAddresses().get(0).getPhoneNumber().getNumber(), CoreMatchers.nullValue());
    }

    /**
     * Test that 1st and 2nd level filters with the same name act as expected.
     */
    @Test
    public void testFiltersSameName() throws Exception {
        final Person firstLevel = target("people").path("1234").queryParam("select", "familyName,region").request().get(Person.class);
        final Person secondLevel = target("people").path("1234").queryParam("select", "familyName,addresses.region").request().get(Person.class);
        // Not null values.
        MatcherAssert.assertThat(firstLevel.getRegion(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(secondLevel.getAddresses().get(0).getRegion(), CoreMatchers.notNullValue());
        // Null values.
        MatcherAssert.assertThat(firstLevel.getAddresses(), CoreMatchers.nullValue());// confirms 2nd level region on addresses is null

        MatcherAssert.assertThat(secondLevel.getRegion(), CoreMatchers.nullValue());
    }
}

