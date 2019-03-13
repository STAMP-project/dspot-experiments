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
package org.glassfish.jersey.examples.shortener.webapp;


import HttpHeaders.LOCATION;
import MediaType.TEXT_HTML_TYPE;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import java.util.Collections;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.examples.shortener.webapp.domain.ShortenedLink;
import org.glassfish.jersey.examples.shortener.webapp.service.ShortenerService;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Test;


/**
 *
 *
 * @author Michal Gajdos
 */
public class ResourcesTest extends JerseyTest {
    private static final MustacheFactory factory = new DefaultMustacheFactory();

    @Test
    public void testCreateForm() throws Exception {
        final Response response = target().request("text/html").get();
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertTrue(response.getMediaType().isCompatible(TEXT_HTML_TYPE));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo(resolveTemplate("mustache/form.mustache", Collections.singletonMap("greeting", "Link Shortener"))));
    }

    @Test
    public void testCreateLink() throws Exception {
        final Form form = new Form("link", "https://java.net/");
        final Response response = target().request("text/html").post(Entity.form(form));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(200));
        Assert.assertTrue(response.getMediaType().isCompatible(TEXT_HTML_TYPE));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo(resolveTemplate("mustache/short-link.mustache", ShortenerService.shortenLink(getBaseUri(), "https://java.net/"))));
    }

    @Test
    public void testCreateInvalidLink() throws Exception {
        final Form form = new Form("link", "java.net");
        final Response response = target().request("text/html").post(Entity.form(form));
        MatcherAssert.assertThat(response.getStatus(), CoreMatchers.equalTo(400));
        Assert.assertTrue(response.getMediaType().isCompatible(TEXT_HTML_TYPE));
        MatcherAssert.assertThat(response.readEntity(String.class), CoreMatchers.equalTo(resolveTemplate("mustache/error-form.mustache", getCreateFormValidationErrors())));
    }

    @Test
    public void testResolveLink() throws Exception {
        final Form form = new Form("link", "https://foo-domain.com/");
        final Response created = target().request("text/html").post(Entity.form(form));
        MatcherAssert.assertThat(created.getStatus(), CoreMatchers.equalTo(200));
        final ShortenedLink shortenedLink = ShortenerService.shortenLink(getBaseUri(), "https://foo-domain.com/");
        final Response resolved = target(shortenedLink.getShortened()).request().get();
        MatcherAssert.assertThat(resolved.getStatus(), CoreMatchers.equalTo(301));
        MatcherAssert.assertThat(resolved.getHeaderString(LOCATION), CoreMatchers.equalTo(shortenedLink.getOriginal().toString()));
    }
}

