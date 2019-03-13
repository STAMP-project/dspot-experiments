/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.examples.entityfiltering.security;


import javax.ws.rs.core.Feature;
import org.glassfish.jersey.examples.entityfiltering.security.domain.RestrictedEntity;
import org.glassfish.jersey.examples.entityfiltering.security.domain.RestrictedSubEntity;
import org.glassfish.jersey.message.filtering.SecurityEntityFilteringFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * {@link org.glassfish.jersey.examples.entityfiltering.security.resource.UnrestrictedResource} unit tests.
 *
 * @author Michal Gajdos
 */
@RunWith(Parameterized.class)
public class UnrestrictedResourceTest extends JerseyTest {
    public UnrestrictedResourceTest(final Class<Feature> filteringProvider) {
        super(new ResourceConfig(SecurityEntityFilteringFeature.class).packages("org.glassfish.jersey.examples.entityfiltering.security").register(filteringProvider));
        enable(TestProperties.DUMP_ENTITY);
        enable(TestProperties.LOG_TRAFFIC);
    }

    @Test
    public void testRestrictedEntity() throws Exception {
        final RestrictedEntity entity = target("unrestricted-resource").request().get(RestrictedEntity.class);
        final RestrictedSubEntity mixedField = entity.getMixedField();
        // Not null values.
        MatcherAssert.assertThat(entity.getSimpleField(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(entity.getPermitAll(), CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mixedField, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(mixedField.getManagerField(), CoreMatchers.notNullValue());
        // Null values.
        MatcherAssert.assertThat(entity.getDenyAll(), CoreMatchers.nullValue());
        MatcherAssert.assertThat(mixedField.getUserField(), CoreMatchers.nullValue());
    }
}

