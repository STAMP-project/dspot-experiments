/**
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.manualmode.parse;


import java.io.File;
import java.nio.file.Paths;
import org.jboss.dmr.ModelNode;
import org.junit.Test;


/**
 * Tests the ability to parse the config files we ship or have shipped in the past, as well as the ability to marshal
 * them back to xml in a manner such that reparsing them produces a consistent in-memory configuration model.
 *
 * @author <a href="kabir.khan@jboss.com">Kabir Khan</a>
 * @author <a href="mailto:darran.lofthouse@jboss.com">Darran Lofthouse</a>
 * @author Brian Stansberry (c) 2011 Red Hat Inc.
 */
public class ParseAndMarshalModelsTestCase {
    private enum Version {

        AS_7_1_3(false, "7-1-3"),
        AS_7_2_0(false, "7-2-0"),
        EAP_6_0_0(true, "6-0-0"),
        EAP_6_1_0(true, "6-1-0"),
        EAP_6_2_0(true, "6-2-0"),
        EAP_6_3_0(true, "6-3-0"),
        EAP_6_4_0(true, "6-4-0"),
        EAP_7_0_0(true, "7-0-0"),
        EAP_7_1_0(true, "7-1-0"),
        EAP_7_2_0(true, "7-2-0");
        final boolean eap;

        final String versionQualifier;

        final int major;

        final int minor;

        final int micro;

        Version(boolean eap, String versionQualifier) {
            this.eap = eap;
            this.versionQualifier = versionQualifier;
            final String[] parts = this.versionQualifier.split("-");
            major = Integer.valueOf(parts[0]);
            minor = Integer.valueOf(parts[1]);
            micro = Integer.valueOf(parts[2]);
        }

        boolean is6x() {
            return (major) == 6;
        }

        boolean is7x() {
            return (major) == 7;
        }

        boolean isLessThan(int major, int minor) {
            return (((this.major) == major) && ((this.minor) < minor)) || ((this.major) < major);
        }
    }

    private static final ParseAndMarshalModelsTestCase.Version[] EAP_VERSIONS = new ParseAndMarshalModelsTestCase.Version[]{ ParseAndMarshalModelsTestCase.Version.EAP_6_0_0, ParseAndMarshalModelsTestCase.Version.EAP_6_1_0, ParseAndMarshalModelsTestCase.Version.EAP_6_2_0, ParseAndMarshalModelsTestCase.Version.EAP_6_3_0, ParseAndMarshalModelsTestCase.Version.EAP_6_4_0, ParseAndMarshalModelsTestCase.Version.EAP_7_0_0, ParseAndMarshalModelsTestCase.Version.EAP_7_1_0, ParseAndMarshalModelsTestCase.Version.EAP_7_2_0 };

    private static final ParseAndMarshalModelsTestCase.Version[] AS_VERSIONS = new ParseAndMarshalModelsTestCase.Version[]{ ParseAndMarshalModelsTestCase.Version.AS_7_1_3, ParseAndMarshalModelsTestCase.Version.AS_7_2_0 };

    private static final File JBOSS_HOME = Paths.get("target", "jbossas-parse-marshal").toFile();

    @Test
    public void testStandaloneXml() throws Exception {
        standaloneXmlTest(getOriginalStandaloneXml("standalone.xml"));
    }

    @Test
    public void testStandaloneHAXml() throws Exception {
        standaloneXmlTest(getOriginalStandaloneXml("standalone-ha.xml"));
    }

    @Test
    public void testStandaloneFullXml() throws Exception {
        standaloneXmlTest(getOriginalStandaloneXml("standalone-full.xml"));
    }

    @Test
    public void testStandaloneFullHAXml() throws Exception {
        standaloneXmlTest(getOriginalStandaloneXml("standalone-full-ha.xml"));
    }

    @Test
    public void testStandaloneMinimalisticXml() throws Exception {
        standaloneXmlTest(getDocsExampleConfigFile("standalone-minimalistic.xml"));
    }

    @Test
    public void testStandalonePicketLinkXml() throws Exception {
        standaloneXmlTest(getDocsExampleConfigFile("standalone-picketlink.xml"));
    }

    @Test
    public void testStandaloneXtsXml() throws Exception {
        standaloneXmlTest(getDocsExampleConfigFile("standalone-xts.xml"));
    }

    @Test
    public void testStandaloneJtsXml() throws Exception {
        standaloneXmlTest(getDocsExampleConfigFile("standalone-jts.xml"));
    }

    @Test
    public void testStandaloneGenericJMSXml() throws Exception {
        standaloneXmlTest(getDocsExampleConfigFile("standalone-genericjms.xml"));
    }

    @Test
    public void testStandaloneActiveMQColocatedXml() throws Exception {
        standaloneXmlTest(getDocsExampleConfigFile("standalone-activemq-colocated.xml"));
    }

    @Test
    public void testJBossASStandaloneXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, null));
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
        }
    }

    @Test
    public void testJBossASStandaloneFullHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "full-ha"));
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
        }
    }

    @Test
    public void testJBossASStandaloneFullXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "full"));
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
        }
    }

    @Test
    public void testJBossASStandaloneHornetQCollocatedXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            standaloneXmlTest(getLegacyConfigFile("standalone", version, "hornetq-colocated"));
        }
    }

    @Test
    public void testJBossASStandaloneJtsXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            standaloneXmlTest(getLegacyConfigFile("standalone", version, "jts"));
        }
    }

    @Test
    public void testJBossASStandaloneMinimalisticXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            standaloneXmlTest(getLegacyConfigFile("standalone", version, "minimalistic"));
        }
    }

    @Test
    public void testJBossASStandaloneXtsXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            standaloneXmlTest(getLegacyConfigFile("standalone", version, "xts"));
        }
    }

    @Test
    public void testEAPStandaloneFullHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "full-ha"));
            ParseAndMarshalModelsTestCase.validateWebSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateCmpSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateMessagingSubsystem(model, version);
        }
    }

    @Test
    public void testEAPStandaloneFullXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "full"));
            ParseAndMarshalModelsTestCase.validateWebSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateCmpSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateMessagingSubsystem(model, version);
        }
    }

    @Test
    public void testEAPStandaloneXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, null));
            ParseAndMarshalModelsTestCase.validateWebSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
        }
    }

    @Test
    public void testEAPStandaloneHornetQCollocatedXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // Only exists in EAP 6.x
                ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "hornetq-colocated"));
                ParseAndMarshalModelsTestCase.validateWebSubsystem(model, version);
                ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
                ParseAndMarshalModelsTestCase.validateMessagingSubsystem(model, version);
                ParseAndMarshalModelsTestCase.validateThreadsSubsystem(model, version);
                ParseAndMarshalModelsTestCase.validateJacordSubsystem(model, version);
            }
        }
    }

    @Test
    public void testEAPStandaloneJtsXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "jts"));
            ParseAndMarshalModelsTestCase.validateWebSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateThreadsSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJacordSubsystem(model, version);
        }
    }

    @Test
    public void testEAPStandaloneMinimalisticXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            standaloneXmlTest(getLegacyConfigFile("standalone", version, "minimalistic"));
        }
    }

    @Test
    public void testEAPStandalonePicketLinkXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // Did not exist until 6.3, where the tech preview was abandoned and redone
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "picketlink"));
            }
        }
    }

    @Test
    public void testEAPStandaloneXtsXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            ModelNode model = standaloneXmlTest(getLegacyConfigFile("standalone", version, "xts"));
            ParseAndMarshalModelsTestCase.validateCmpSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateWebSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJsfSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateThreadsSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateJacordSubsystem(model, version);
            ParseAndMarshalModelsTestCase.validateXtsSubsystem(model, version);
        }
    }

    @Test
    public void testEAPStandaloneAzureFullHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "azure-full-ha"));
            }
        }
    }

    @Test
    public void testEAPStandaloneAzureHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "azure-ha"));
            }
        }
    }

    @Test
    public void testEAPStandaloneEc2FullHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "ec2-full-ha"));
            }
        }
    }

    @Test
    public void testEAPStandaloneEc2HaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "ec2-ha"));
            }
        }
    }

    @Test
    public void testEAPStandaloneGenericJmsXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "genericjms"));
            }
        }
    }

    @Test
    public void testEAPStandaloneGossipFullHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "gossip-full-ha"));
            }
        }
    }

    @Test
    public void testEAPStandaloneGossipHaXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "gossip-ha"));
            }
        }
    }

    @Test
    public void testEAPStandaloneLoadBalancerXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.isLessThan(7, 1)) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "load-balancer"));
            }
        }
    }

    @Test
    public void testEAPStandaloneRtsXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            if (version.is6x()) {
                // didn't exist yet
            } else {
                standaloneXmlTest(getLegacyConfigFile("standalone", version, "rts"));
            }
        }
    }

    @Test
    public void testHostXml() throws Exception {
        hostXmlTest(getOriginalHostXml("host.xml"));
    }

    @Test
    public void testJBossASHostXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            hostXmlTest(getLegacyConfigFile("host", version, null));
        }
    }

    @Test
    public void testEAPHostXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            hostXmlTest(getLegacyConfigFile("host", version, null));
        }
    }

    @Test
    public void testDomainXml() throws Exception {
        domainXmlTest(getOriginalDomainXml("domain.xml"));
    }

    @Test
    public void testJBossASDomainXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.AS_VERSIONS) {
            ModelNode model = domainXmlTest(getLegacyConfigFile("domain", version, null));
            ParseAndMarshalModelsTestCase.validateProfiles(model, version);
        }
    }

    @Test
    public void testEAPDomainXml() throws Exception {
        for (ParseAndMarshalModelsTestCase.Version version : ParseAndMarshalModelsTestCase.EAP_VERSIONS) {
            ModelNode model = domainXmlTest(getLegacyConfigFile("domain", version, null));
            ParseAndMarshalModelsTestCase.validateProfiles(model, version);
        }
    }
}

