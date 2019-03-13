/**
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2015-2017 Oracle and/or its affiliates. All rights reserved.
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
package org.glassfish.jersey.tests.integration.multimodule.ejb.reload.web1;


import javax.ws.rs.client.WebTarget;
import org.glassfish.jersey.test.JerseyTest;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Test reload functionality for two web app test case.
 * Run with:
 * <pre>
 * mvn clean package
 * $AS_HOME/bin/asadmin deploy ../ear/target/ejb-multimodule-reload-ear-*.ear
 * mvn -DskipTests=false test</pre>
 *
 * @author Jakub Podlesak (jakub.podlesak at oracle.com)
 */
public class ReloadTest extends JerseyTest {
    @Test
    public void testReload() {
        final WebTarget nanosTarget = target().path("ejb-multimodule-reload-war1/last-init-nano-time");
        final long nanos1 = _readInitTimeNanos(nanosTarget);
        final long nanos2 = _readInitTimeNanos(nanosTarget);
        MatcherAssert.assertThat(nanos2, CoreMatchers.is(CoreMatchers.equalTo(nanos1)));
        // J-592 reproducer:
        // reload();
        // 
        // final long nanos3 = _readInitTimeNanos(nanosTarget);
        // final long nanos4 = _readInitTimeNanos(nanosTarget);
        // 
        // assertThat(nanos4, is(equalTo(nanos3)));
        // assertThat(nanos3, is(greaterThan(nanos2)));
        // 
        // reload();
        // 
        // final long nanos5 = _readInitTimeNanos(nanosTarget);
        // final long nanos6 = _readInitTimeNanos(nanosTarget);
        // 
        // assertThat(nanos6, is(equalTo(nanos5)));
        // assertThat(nanos5, is(greaterThan(nanos4)));
        // END: J-592 reproducer
    }
}

