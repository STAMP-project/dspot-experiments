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
package org.glassfish.jersey.message.internal;


import java.util.Locale;
import java.util.Map;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;


/**
 * Quality unit tests.
 *
 * @author Marek Potociar (marek.potociar at oracle.com)
 */
@RunWith(Parameterized.class)
public class QualityTest {
    private static final Locale ORIGINAL_LOCALE = Locale.getDefault();

    @Parameterized.Parameter(0)
    public Locale locale;

    /**
     * Test enhancing HTT header parameter map with a quality parameter.
     */
    @Test
    public void testEnhanceWithQualityParameter() {
        Map<String, String> result;
        result = Quality.enhanceWithQualityParameter(null, "q", 1000);
        Assert.assertThat(result, CoreMatchers.equalTo(null));
        result = Quality.enhanceWithQualityParameter(null, "q", 200);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("q=0.2")));
        result = Quality.enhanceWithQualityParameter(null, "q", 220);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("q=0.22")));
        result = Quality.enhanceWithQualityParameter(null, "q", 222);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("q=0.222")));
        Map<String, String> parameters;
        parameters = QualityTest.asMap("a=b");
        result = Quality.enhanceWithQualityParameter(parameters, "q", 1000);
        Assert.assertThat(result, CoreMatchers.equalTo(parameters));
        result = Quality.enhanceWithQualityParameter(parameters, "q", 200);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=0.2")));
        result = Quality.enhanceWithQualityParameter(parameters, "q", 220);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=0.22")));
        result = Quality.enhanceWithQualityParameter(parameters, "q", 222);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=0.222")));
        // test quality parameter override
        parameters = QualityTest.asMap("a=b;q=0.3");
        result = Quality.enhanceWithQualityParameter(parameters, "q", 1000);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=1.0")));
        result = Quality.enhanceWithQualityParameter(parameters, "q", 200);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=0.2")));
        result = Quality.enhanceWithQualityParameter(parameters, "q", 220);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=0.22")));
        result = Quality.enhanceWithQualityParameter(parameters, "q", 222);
        Assert.assertThat(result, CoreMatchers.equalTo(QualityTest.asMap("a=b;q=0.222")));
    }
}

