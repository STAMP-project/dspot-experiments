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
package org.glassfish.jersey.tests.integration.multimodule.ejb.web1;


import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;


/**
 * Test for EJB web application resources. The JAX-RS resources come from bundled EJB library jar.
 * Run with:
 * <pre>
 * mvn clean package
 * $AS_HOME/bin/asadmin deploy ../ear/target/ejb-multimodule-ear-*.ear
 * mvn -DskipTests=false test</pre>
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 * @author Libor Kramolis (libor.kramolis at oracle.com)
 */
public class JaxRsFromEjbLibraryTest extends JerseyTest {
    @Test
    public void testRequestCountGetsIncremented() {
        final int requestCount1 = _nextCount(target().path("counter"));
        final int requestCount2 = _nextCount(target().path("counter"));
        MatcherAssert.assertThat(requestCount2, CoreMatchers.is(Matchers.greaterThan(requestCount1)));
        final int requestCount3 = _nextCount(target().path("stateless"));
        MatcherAssert.assertThat(requestCount3, CoreMatchers.is(Matchers.greaterThan(requestCount2)));
        final int requestCount4 = _nextCount(target().path("stateless"));
        MatcherAssert.assertThat(requestCount4, CoreMatchers.is(Matchers.greaterThan(requestCount3)));
        final int requestCount5 = _nextCount(target().path("stateful").path("count"));
        MatcherAssert.assertThat(requestCount5, CoreMatchers.is(Matchers.greaterThan(requestCount4)));
        final int requestCount6 = _nextCount(target().path("stateful").path("count"));
        MatcherAssert.assertThat(requestCount6, CoreMatchers.is(Matchers.greaterThan(requestCount5)));
        final int requestCount7 = _nextCount(target().path("war-stateless"));
        MatcherAssert.assertThat(requestCount7, CoreMatchers.is(Matchers.greaterThan(requestCount6)));
        final int requestCount8 = _nextCount(target().path("war-stateless"));
        MatcherAssert.assertThat(requestCount8, CoreMatchers.is(Matchers.greaterThan(requestCount7)));
    }

    @Test
    public void testUriInfoInjection() {
        _testPath(target().path("counter").path("one"), "counter/one");
        _testPath(target().path("counter").path("two"), "counter/two");
        _testPath(target().path("stateless").path("three"), "stateless/three");
        _testPath(target().path("stateless").path("four"), "stateless/four");
        _testPath(target().path("war-stateless").path("five"), "war-stateless/five");
        _testPath(target().path("war-stateless").path("six"), "war-stateless/six");
    }
}

