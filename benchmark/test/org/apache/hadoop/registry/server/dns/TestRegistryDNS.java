/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.registry.server.dns;


import DClass.IN;
import DNSKEYRecord.Protocol;
import DNSSEC.Algorithm;
import Rcode.NXDOMAIN;
import RegistryConstants.KEY_DNSSEC_ENABLED;
import RegistryConstants.KEY_DNSSEC_PRIVATE_KEY_FILE;
import RegistryConstants.KEY_DNSSEC_PUBLIC_KEY;
import RegistryConstants.KEY_DNS_DOMAIN;
import RegistryConstants.KEY_DNS_TTL;
import RegistryConstants.KEY_DNS_ZONES_DIR;
import RegistryUtils.ServiceRecordMarshal;
import Section.ADDITIONAL;
import Section.ANSWER;
import Section.AUTHORITY;
import Type.A;
import Type.AAAA;
import Type.CNAME;
import Type.NS;
import Type.PTR;
import Type.SOA;
import Type.SRV;
import Type.TXT;
import java.math.BigInteger;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.RSAPrivateKeySpec;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.apache.commons.net.util.Base64;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.registry.client.types.ServiceRecord;
import org.junit.Assert;
import org.junit.Test;
import org.xbill.DNS.AAAARecord;
import org.xbill.DNS.ARecord;
import org.xbill.DNS.DClass;
import org.xbill.DNS.DNSKEYRecord;
import org.xbill.DNS.DNSSEC;
import org.xbill.DNS.Flags;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.OPTRecord;
import org.xbill.DNS.RRSIGRecord;
import org.xbill.DNS.RRset;
import org.xbill.DNS.Record;
import org.xbill.DNS.SRVRecord;
import org.xbill.DNS.Type;


/**
 *
 */
public class TestRegistryDNS extends Assert {
    private RegistryDNS registryDNS;

    private ServiceRecordMarshal marshal;

    private static final String APPLICATION_RECORD = "{\n" + (((((((((((((((((((((((((((((((((((((((((((((("  \"type\" : \"JSONServiceRecord\",\n" + "  \"description\" : \"Slider Application Master\",\n") + "  \"external\" : [ {\n") + "    \"api\" : \"classpath:org.apache.hadoop.yarn.service.appmaster.ipc") + "\",\n") + "    \"addressType\" : \"host/port\",\n") + "    \"protocolType\" : \"hadoop/IPC\",\n") + "    \"addresses\" : [ {\n") + "      \"host\" : \"192.168.1.5\",\n") + "      \"port\" : \"1026\"\n") + "    } ]\n") + "  }, {\n") + "    \"api\" : \"http://\",\n") + "    \"addressType\" : \"uri\",\n") + "    \"protocolType\" : \"webui\",\n") + "    \"addresses\" : [ {\n") + "      \"uri\" : \"http://192.168.1.5:1027\"\n") + "    } ]\n") + "  }, {\n") + "    \"api\" : \"classpath:org.apache.hadoop.yarn.service.management\"") + ",\n") + "    \"addressType\" : \"uri\",\n") + "    \"protocolType\" : \"REST\",\n") + "    \"addresses\" : [ {\n") + "      \"uri\" : \"http://192.168.1.5:1027/ws/v1/slider/mgmt\"\n") + "    } ]\n") + "  } ],\n") + "  \"internal\" : [ {\n") + "    \"api\" : \"classpath:org.apache.hadoop.yarn.service.agents.secure") + "\",\n") + "    \"addressType\" : \"uri\",\n") + "    \"protocolType\" : \"REST\",\n") + "    \"addresses\" : [ {\n") + "      \"uri\" : \"https://192.168.1.5:47700/ws/v1/slider/agents\"\n") + "    } ]\n") + "  }, {\n") + "    \"api\" : \"classpath:org.apache.hadoop.yarn.service.agents.oneway") + "\",\n") + "    \"addressType\" : \"uri\",\n") + "    \"protocolType\" : \"REST\",\n") + "    \"addresses\" : [ {\n") + "      \"uri\" : \"https://192.168.1.5:35531/ws/v1/slider/agents\"\n") + "    } ]\n") + "  } ],\n") + "  \"yarn:id\" : \"application_1451931954322_0016\",\n") + "  \"yarn:persistence\" : \"application\"\n") + "}\n");

    static final String CONTAINER_RECORD = "{\n" + ((((((((("  \"type\" : \"JSONServiceRecord\",\n" + "  \"description\" : \"httpd-1\",\n") + "  \"external\" : [ ],\n") + "  \"internal\" : [ ],\n") + "  \"yarn:id\" : \"container_e50_1451931954322_0016_01_000002\",\n") + "  \"yarn:persistence\" : \"container\",\n") + "  \"yarn:ip\" : \"172.17.0.19\",\n") + "  \"yarn:hostname\" : \"host1\",\n") + "  \"yarn:component\" : \"httpd\"\n") + "}\n");

    static final String CONTAINER_RECORD2 = "{\n" + ((((((((("  \"type\" : \"JSONServiceRecord\",\n" + "  \"description\" : \"httpd-2\",\n") + "  \"external\" : [ ],\n") + "  \"internal\" : [ ],\n") + "  \"yarn:id\" : \"container_e50_1451931954322_0016_01_000003\",\n") + "  \"yarn:persistence\" : \"container\",\n") + "  \"yarn:ip\" : \"172.17.0.20\",\n") + "  \"yarn:hostname\" : \"host2\",\n") + "  \"yarn:component\" : \"httpd\"\n") + "}\n");

    private static final String CONTAINER_RECORD_NO_IP = "{\n" + ((((((("  \"type\" : \"JSONServiceRecord\",\n" + "  \"description\" : \"httpd-1\",\n") + "  \"external\" : [ ],\n") + "  \"internal\" : [ ],\n") + "  \"yarn:id\" : \"container_e50_1451931954322_0016_01_000002\",\n") + "  \"yarn:persistence\" : \"container\",\n") + "  \"yarn:component\" : \"httpd\"\n") + "}\n");

    private static final String CONTAINER_RECORD_YARN_PERSISTANCE_ABSENT = "{\n" + (((((((("  \"type\" : \"JSONServiceRecord\",\n" + "  \"description\" : \"httpd-1\",\n") + "  \"external\" : [ ],\n") + "  \"internal\" : [ ],\n") + "  \"yarn:id\" : \"container_e50_1451931954322_0016_01_000003\",\n") + "  \"yarn:ip\" : \"172.17.0.19\",\n") + "  \"yarn:hostname\" : \"0a134d6329bb\",\n") + "  \"yarn:component\" : \"httpd\"") + "}\n");

    @Test
    public void testAppRegistration() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.APPLICATION_RECORD.getBytes());
        getRegistryDNS().register("/registry/users/root/services/org-apache-slider/test1/", record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("test1.root.dev.test.");
        Assert.assertEquals("wrong result", "192.168.1.5", getAddress().getHostAddress());
        recs = assertDNSQuery("management-api.test1.root.dev.test.", 2);
        Assert.assertEquals("wrong target name", "test1.root.dev.test.", getTarget().toString());
        Assert.assertTrue("not an ARecord", ((recs[(isSecure() ? 2 : 1)]) instanceof ARecord));
        recs = assertDNSQuery("appmaster-ipc-api.test1.root.dev.test.", SRV, 1);
        Assert.assertTrue("not an SRV record", ((recs[0]) instanceof SRVRecord));
        Assert.assertEquals("wrong port", 1026, getPort());
        recs = assertDNSQuery("appmaster-ipc-api.test1.root.dev.test.", 2);
        Assert.assertEquals("wrong target name", "test1.root.dev.test.", getTarget().toString());
        Assert.assertTrue("not an ARecord", ((recs[(isSecure() ? 2 : 1)]) instanceof ARecord));
        recs = assertDNSQuery("http-api.test1.root.dev.test.", 2);
        Assert.assertEquals("wrong target name", "test1.root.dev.test.", getTarget().toString());
        Assert.assertTrue("not an ARecord", ((recs[(isSecure() ? 2 : 1)]) instanceof ARecord));
        recs = assertDNSQuery("http-api.test1.root.dev.test.", SRV, 1);
        Assert.assertTrue("not an SRV record", ((recs[0]) instanceof SRVRecord));
        Assert.assertEquals("wrong port", 1027, getPort());
        assertDNSQuery("test1.root.dev.test.", TXT, 3);
        assertDNSQuery("appmaster-ipc-api.test1.root.dev.test.", TXT, 1);
        assertDNSQuery("http-api.test1.root.dev.test.", TXT, 1);
        assertDNSQuery("management-api.test1.root.dev.test.", TXT, 1);
    }

    @Test
    public void testContainerRegistration() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("ctr-e50-1451931954322-0016-01-000002.dev.test.");
        Assert.assertEquals("wrong result", "172.17.0.19", getAddress().getHostAddress());
        recs = assertDNSQuery("httpd-1.test1.root.dev.test.", 1);
        Assert.assertTrue("not an ARecord", ((recs[0]) instanceof ARecord));
    }

    @Test
    public void testContainerRegistrationPersistanceAbsent() throws Exception {
        ServiceRecord record = marshal.fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD_YARN_PERSISTANCE_ABSENT.getBytes());
        registryDNS.register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000003"), record);
        Name name = Name.fromString("ctr-e50-1451931954322-0016-01-000002.dev.test.");
        Record question = Record.newRecord(name, A, IN);
        Message query = Message.newQuery(question);
        byte[] responseBytes = registryDNS.generateReply(query, null);
        Message response = new Message(responseBytes);
        Assert.assertEquals("Excepting NXDOMAIN as Record must not have regsisterd wrong", NXDOMAIN, response.getRcode());
    }

    @Test
    public void testRecordTTL() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("ctr-e50-1451931954322-0016-01-000002.dev.test.");
        Assert.assertEquals("wrong result", "172.17.0.19", getAddress().getHostAddress());
        Assert.assertEquals("wrong ttl", 30L, recs[0].getTTL());
        recs = assertDNSQuery("httpd-1.test1.root.dev.test.", 1);
        Assert.assertTrue("not an ARecord", ((recs[0]) instanceof ARecord));
        Assert.assertEquals("wrong ttl", 30L, recs[0].getTTL());
    }

    @Test
    public void testReverseLookup() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("19.0.17.172.in-addr.arpa.", PTR, 1);
        Assert.assertEquals("wrong result", "httpd-1.test1.root.dev.test.", getTarget().toString());
    }

    @Test
    public void testReverseLookupInLargeNetwork() throws Exception {
        setRegistryDNS(new RegistryDNS("TestRegistry"));
        Configuration conf = createConfiguration();
        conf.set(KEY_DNS_DOMAIN, "dev.test");
        conf.set(KEY_DNS_ZONE_SUBNET, "172.17.0.0");
        conf.set(KEY_DNS_ZONE_MASK, "255.255.224.0");
        conf.setTimeDuration(KEY_DNS_TTL, 30L, TimeUnit.SECONDS);
        getRegistryDNS().setDomainName(conf);
        getRegistryDNS().initializeZones(conf);
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("19.0.17.172.in-addr.arpa.", PTR, 1);
        Assert.assertEquals("wrong result", "httpd-1.test1.root.dev.test.", getTarget().toString());
    }

    @Test
    public void testMissingReverseLookup() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Name name = Name.fromString("19.1.17.172.in-addr.arpa.");
        Record question = Record.newRecord(name, PTR, IN);
        Message query = Message.newQuery(question);
        OPTRecord optRecord = new OPTRecord(4096, 0, 0, Flags.DO, null);
        query.addRecord(optRecord, ADDITIONAL);
        byte[] responseBytes = getRegistryDNS().generateReply(query, null);
        Message response = new Message(responseBytes);
        Assert.assertEquals("Missing record should be: ", NXDOMAIN, response.getRcode());
    }

    @Test
    public void testNoContainerIP() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD_NO_IP.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Name name = Name.fromString("ctr-e50-1451931954322-0016-01-000002.dev.test.");
        Record question = Record.newRecord(name, A, IN);
        Message query = Message.newQuery(question);
        byte[] responseBytes = getRegistryDNS().generateReply(query, null);
        Message response = new Message(responseBytes);
        Assert.assertEquals("wrong status", NXDOMAIN, response.getRcode());
    }

    @Test
    public void testDNSKEYRecord() throws Exception {
        String publicK = "AwEAAe1Jev0Az1khlQCvf0nud1/CNHQwwPEu8BNchZthdDxKPVn29yrD " + (("CHoAWjwiGsOSw3SzIPrawSbHzyJsjn0oLBhGrH6QedFGnydoxjNsw3m/ " + "SCmOjR/a7LGBAMDFKqFioi4gOyuN66svBeY+/5uw72+0ei9AQ20gqf6q ") + "l9Ozs5bV");
        // byte[] publicBytes = Base64.decodeBase64(publicK);
        // X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        // KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        // PublicKey pubKey = keyFactory.generatePublic(keySpec);
        DNSKEYRecord dnskeyRecord = new DNSKEYRecord(Name.fromString("dev.test."), DClass.IN, 0, Flags.ZONE_KEY, Protocol.DNSSEC, Algorithm.RSASHA256, Base64.decodeBase64(publicK.getBytes()));
        Assert.assertNotNull(dnskeyRecord);
        RSAPrivateKeySpec privateSpec = new RSAPrivateKeySpec(new BigInteger(1, Base64.decodeBase64(("7Ul6/QDPWSGVAK9/Se53X8I0dDDA8S7wE1yFm2F0PEo9Wfb3KsMIegBaPCIaw5LDd" + ("LMg+trBJsfPImyOfSgsGEasfpB50UafJ2jGM2zDeb9IKY6NH9rssYEAwMUq" + "oWKiLiA7K43rqy8F5j7/m7Dvb7R6L0BDbSCp/qqX07OzltU=")))), new BigInteger(1, Base64.decodeBase64(("MgbQ6DBYhskeufNGGdct0cGG/4wb0X183ggenwCv2dopDyOTPq+5xMb4Pz9Ndzgk/" + ("yCY7mpaWIu9rttGOzrR+LBRR30VobPpMK1bMnzu2C0x08oYAguVwZB79DLC" + "705qmZpiaaFB+LnhG7VtpPiOBm3UzZxdrBfeq/qaKrXid60=")))));
        KeyFactory factory = KeyFactory.getInstance("RSA");
        PrivateKey priv = factory.generatePrivate(privateSpec);
        ARecord aRecord = new ARecord(Name.fromString("some.test."), DClass.IN, 0, InetAddress.getByName("192.168.0.1"));
        Calendar cal = Calendar.getInstance();
        Date inception = cal.getTime();
        cal.add(Calendar.YEAR, 1);
        Date expiration = cal.getTime();
        RRset rrset = new RRset(aRecord);
        RRSIGRecord rrsigRecord = DNSSEC.sign(rrset, dnskeyRecord, priv, inception, expiration);
        DNSSEC.verify(rrset, rrsigRecord, dnskeyRecord);
    }

    @Test
    public void testIpv4toIpv6() throws Exception {
        InetAddress address = BaseServiceRecordProcessor.getIpv6Address(InetAddress.getByName("172.17.0.19"));
        Assert.assertTrue("not an ipv6 address", (address instanceof Inet6Address));
        Assert.assertEquals("wrong IP", "172.17.0.19", InetAddress.getByAddress(address.getAddress()).getHostAddress());
    }

    @Test
    public void testAAAALookup() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("ctr-e50-1451931954322-0016-01-000002.dev.test.", AAAA, 1);
        Assert.assertEquals("wrong result", "172.17.0.19", getAddress().getHostAddress());
        recs = assertDNSQuery("httpd-1.test1.root.dev.test.", AAAA, 1);
        Assert.assertTrue("not an ARecord", ((recs[0]) instanceof AAAARecord));
    }

    @Test
    public void testNegativeLookup() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Name name = Name.fromString("missing.dev.test.");
        Record question = Record.newRecord(name, A, IN);
        Message query = Message.newQuery(question);
        byte[] responseBytes = getRegistryDNS().generateReply(query, null);
        Message response = new Message(responseBytes);
        Assert.assertEquals("not successful", NXDOMAIN, response.getRcode());
        Assert.assertNotNull("Null response", response);
        Assert.assertEquals("Questions do not match", query.getQuestion(), response.getQuestion());
        Record[] sectionArray = response.getSectionArray(AUTHORITY);
        Assert.assertEquals("Wrong number of recs in AUTHORITY", (isSecure() ? 2 : 1), sectionArray.length);
        boolean soaFound = false;
        for (Record rec : sectionArray) {
            soaFound = (rec.getType()) == (Type.SOA);
            if (soaFound) {
                break;
            }
        }
        Assert.assertTrue("wrong record type", soaFound);
    }

    @Test
    public void testReadMasterFile() throws Exception {
        setRegistryDNS(new RegistryDNS("TestRegistry"));
        Configuration conf = new Configuration();
        conf.set(KEY_DNS_DOMAIN, "dev.test");
        conf.set(RegistryConstants.KEY_DNS_ZONE_SUBNET, "172.17.0");
        conf.setTimeDuration(KEY_DNS_TTL, 30L, TimeUnit.SECONDS);
        conf.set(KEY_DNS_ZONES_DIR, getClass().getResource("/").getFile());
        if (isSecure()) {
            conf.setBoolean(KEY_DNSSEC_ENABLED, true);
            conf.set(KEY_DNSSEC_PUBLIC_KEY, ("AwEAAe1Jev0Az1khlQCvf0nud1/CNHQwwPEu8BNchZthdDxKPVn29yrD " + (("CHoAWjwiGsOSw3SzIPrawSbHzyJsjn0oLBhGrH6QedFGnydoxjNsw3m/ " + "SCmOjR/a7LGBAMDFKqFioi4gOyuN66svBeY+/5uw72+0ei9AQ20gqf6q ") + "l9Ozs5bV")));
            conf.set(KEY_DNSSEC_PRIVATE_KEY_FILE, getClass().getResource("/test.private").getFile());
        }
        getRegistryDNS().setDomainName(conf);
        getRegistryDNS().initializeZones(conf);
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("ctr-e50-1451931954322-0016-01-000002.dev.test.");
        Assert.assertEquals("wrong result", "172.17.0.19", getAddress().getHostAddress());
        recs = assertDNSQuery("httpd-1.test1.root.dev.test.", 1);
        Assert.assertTrue("not an ARecord", ((recs[0]) instanceof ARecord));
        // lookup dyanmic reverse records
        recs = assertDNSQuery("19.0.17.172.in-addr.arpa.", PTR, 1);
        Assert.assertEquals("wrong result", "httpd-1.test1.root.dev.test.", getTarget().toString());
        // now lookup static reverse records
        Name name = Name.fromString("5.0.17.172.in-addr.arpa.");
        Record question = Record.newRecord(name, PTR, IN);
        Message query = Message.newQuery(question);
        OPTRecord optRecord = new OPTRecord(4096, 0, 0, Flags.DO, null);
        query.addRecord(optRecord, ADDITIONAL);
        byte[] responseBytes = getRegistryDNS().generateReply(query, null);
        Message response = new Message(responseBytes);
        recs = response.getSectionArray(ANSWER);
        Assert.assertEquals("wrong result", "cn005.dev.test.", getTarget().toString());
    }

    @Test
    public void testReverseZoneNames() throws Exception {
        Configuration conf = new Configuration();
        conf.set(KEY_DNS_ZONE_SUBNET, "172.26.32.0");
        conf.set(KEY_DNS_ZONE_MASK, "255.255.224.0");
        Name name = getRegistryDNS().getReverseZoneName(conf);
        Assert.assertEquals("wrong name", "26.172.in-addr.arpa.", name.toString());
    }

    @Test
    public void testSplitReverseZoneNames() throws Exception {
        Configuration conf = new Configuration();
        registryDNS = new RegistryDNS("TestRegistry");
        conf.set(KEY_DNS_DOMAIN, "example.com");
        conf.set(KEY_DNS_SPLIT_REVERSE_ZONE, "true");
        conf.set(KEY_DNS_SPLIT_REVERSE_ZONE_RANGE, "256");
        conf.set(KEY_DNS_ZONE_SUBNET, "172.26.32.0");
        conf.set(KEY_DNS_ZONE_MASK, "255.255.224.0");
        conf.setTimeDuration(KEY_DNS_TTL, 30L, TimeUnit.SECONDS);
        conf.set(KEY_DNS_ZONES_DIR, getClass().getResource("/").getFile());
        if (isSecure()) {
            conf.setBoolean(KEY_DNSSEC_ENABLED, true);
            conf.set(KEY_DNSSEC_PUBLIC_KEY, ("AwEAAe1Jev0Az1khlQCvf0nud1/CNHQwwPEu8BNchZthdDxKPVn29yrD " + (("CHoAWjwiGsOSw3SzIPrawSbHzyJsjn0oLBhGrH6QedFGnydoxjNsw3m/ " + "SCmOjR/a7LGBAMDFKqFioi4gOyuN66svBeY+/5uw72+0ei9AQ20gqf6q ") + "l9Ozs5bV")));
            conf.set(KEY_DNSSEC_PRIVATE_KEY_FILE, getClass().getResource("/test.private").getFile());
        }
        registryDNS.setDomainName(conf);
        registryDNS.setDNSSECEnabled(conf);
        registryDNS.addSplitReverseZones(conf, 4);
        Assert.assertEquals(4, registryDNS.getZoneCount());
    }

    @Test
    public void testExampleDotCom() throws Exception {
        Name name = Name.fromString("example.com.");
        Record[] records = getRegistryDNS().getRecords(name, SOA);
        Assert.assertNotNull("example.com exists:", records);
    }

    @Test
    public void testExternalCNAMERecord() throws Exception {
        setRegistryDNS(new RegistryDNS("TestRegistry"));
        Configuration conf = new Configuration();
        conf.set(KEY_DNS_DOMAIN, "dev.test");
        conf.set(RegistryConstants.KEY_DNS_ZONE_SUBNET, "172.17.0");
        conf.setTimeDuration(KEY_DNS_TTL, 30L, TimeUnit.SECONDS);
        conf.set(KEY_DNS_ZONES_DIR, getClass().getResource("/").getFile());
        getRegistryDNS().setDomainName(conf);
        getRegistryDNS().initializeZones(conf);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQueryNotNull("mail.yahoo.com.", CNAME, 1);
    }

    @Test
    public void testRootLookup() throws Exception {
        setRegistryDNS(new RegistryDNS("TestRegistry"));
        Configuration conf = new Configuration();
        conf.set(KEY_DNS_DOMAIN, "dev.test");
        conf.set(RegistryConstants.KEY_DNS_ZONE_SUBNET, "172.17.0");
        conf.setTimeDuration(KEY_DNS_TTL, 30L, TimeUnit.SECONDS);
        conf.set(KEY_DNS_ZONES_DIR, getClass().getResource("/").getFile());
        getRegistryDNS().setDomainName(conf);
        getRegistryDNS().initializeZones(conf);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQueryNotNull(".", NS, 13);
    }

    @Test
    public void testMultiARecord() throws Exception {
        ServiceRecord record = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD.getBytes());
        ServiceRecord record2 = getMarshal().fromBytes("somepath", TestRegistryDNS.CONTAINER_RECORD2.getBytes());
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000002"), record);
        getRegistryDNS().register(("/registry/users/root/services/org-apache-slider/test1/components/" + "ctr-e50-1451931954322-0016-01-000003"), record2);
        // start assessing whether correct records are available
        Record[] recs = assertDNSQuery("httpd.test1.root.dev.test.", 2);
        Assert.assertTrue("not an ARecord", ((recs[0]) instanceof ARecord));
        Assert.assertTrue("not an ARecord", ((recs[1]) instanceof ARecord));
    }

    @Test(timeout = 5000)
    public void testUpstreamFault() throws Exception {
        Name name = Name.fromString("19.0.17.172.in-addr.arpa.");
        Record[] recs = getRegistryDNS().getRecords(name, CNAME);
        Assert.assertNull("Record is not null", recs);
    }
}

