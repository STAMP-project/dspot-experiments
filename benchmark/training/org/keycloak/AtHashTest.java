/**
 * Copyright 2016 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.keycloak;


import Algorithm.ES256;
import Algorithm.RS256;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;


/**
 * See "at_hash" in OIDC specification
 *
 * @author <a href="mailto:mposolda@redhat.com">Marek Posolda</a>
 */
public class AtHashTest {
    static {
        if ((Security.getProvider("BC")) == null)
            Security.addProvider(new BouncyCastleProvider());

    }

    @Test
    public void testAtHashRsa() throws Exception {
        verifyHash(RS256, "jHkWEdUXMU1BwAsC4vtUsZwnNvTIxEl0z9K3vx5KF0Y", "77QmUPtjPfzWtF2AnpK9RQ");
        verifyHash(RS256, "ya29.eQETFbFOkAs8nWHcmYXKwEi0Zz46NfsrUU_KuQLOLTwWS40y6Fb99aVzEXC0U14m61lcPMIr1hEIBA", "aUAkJG-u6x4RTWuILWy-CA");
    }

    @Test
    public void testAtHashEs() throws Exception {
        verifyHash(ES256, "jHkWEdUXMU1BwAsC4vtUsZwnNvTIxEl0z9K3vx5KF0Y", "77QmUPtjPfzWtF2AnpK9RQ");
        verifyHash(ES256, "ya29.eQETFbFOkAs8nWHcmYXKwEi0Zz46NfsrUU_KuQLOLTwWS40y6Fb99aVzEXC0U14m61lcPMIr1hEIBA", "aUAkJG-u6x4RTWuILWy-CA");
    }
}

