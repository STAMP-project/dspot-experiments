/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.apache.hadoop.security;


import CommonConfigurationKeys.HADOOP_SECURITY_TOKEN_SERVICE_USE_IP;
import CommonConfigurationKeys.ZK_AUTH;
import CredentialProviderFactory.CREDENTIAL_PROVIDER_PATH;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.CommonConfigurationKeysPublic;
import org.apache.hadoop.net.NetUtils;
import org.apache.hadoop.util.StringUtils;
import org.apache.hadoop.util.ZKUtil.ZKAuthInfo;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static SecurityUtil.HOSTNAME_PATTERN;


public class TestSecurityUtil {
    private static final String ZK_AUTH_VALUE = "a_scheme:a_password";

    @Test
    public void isOriginalTGTReturnsCorrectValues() {
        Assert.assertTrue(SecurityUtil.isTGSPrincipal(new KerberosPrincipal("krbtgt/foo@foo")));
        Assert.assertTrue(SecurityUtil.isTGSPrincipal(new KerberosPrincipal("krbtgt/foo.bar.bat@foo.bar.bat")));
        Assert.assertFalse(SecurityUtil.isTGSPrincipal(null));
        Assert.assertFalse(SecurityUtil.isTGSPrincipal(new KerberosPrincipal("blah")));
        Assert.assertFalse(SecurityUtil.isTGSPrincipal(new KerberosPrincipal("krbtgt/hello")));
        Assert.assertFalse(SecurityUtil.isTGSPrincipal(new KerberosPrincipal("krbtgt/foo@FOO")));
    }

    @Test
    public void testGetServerPrincipal() throws IOException {
        String service = "hdfs/";
        String realm = "@REALM";
        String hostname = "foohost";
        String userPrincipal = "foo@FOOREALM";
        String shouldReplace = (service + (HOSTNAME_PATTERN)) + realm;
        String replaced = (service + hostname) + realm;
        verify(shouldReplace, hostname, replaced);
        String shouldNotReplace = ((service + (HOSTNAME_PATTERN)) + "NAME") + realm;
        verify(shouldNotReplace, hostname, shouldNotReplace);
        verify(userPrincipal, hostname, userPrincipal);
        // testing reverse DNS lookup doesn't happen
        InetAddress notUsed = Mockito.mock(InetAddress.class);
        Assert.assertEquals(shouldNotReplace, SecurityUtil.getServerPrincipal(shouldNotReplace, notUsed));
        Mockito.verify(notUsed, Mockito.never()).getCanonicalHostName();
    }

    @Test
    public void testPrincipalsWithLowerCaseHosts() throws IOException {
        String service = "xyz/";
        String realm = "@REALM";
        String principalInConf = (service + (HOSTNAME_PATTERN)) + realm;
        String hostname = "FooHost";
        String principal = (service + (StringUtils.toLowerCase(hostname))) + realm;
        verify(principalInConf, hostname, principal);
    }

    @Test
    public void testLocalHostNameForNullOrWild() throws Exception {
        String local = StringUtils.toLowerCase(SecurityUtil.getLocalHostName(null));
        Assert.assertEquals((("hdfs/" + local) + "@REALM"), SecurityUtil.getServerPrincipal("hdfs/_HOST@REALM", ((String) (null))));
        Assert.assertEquals((("hdfs/" + local) + "@REALM"), SecurityUtil.getServerPrincipal("hdfs/_HOST@REALM", "0.0.0.0"));
    }

    @Test
    public void testStartsWithIncorrectSettings() throws IOException {
        Configuration conf = new Configuration();
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        String keyTabKey = "key";
        conf.set(keyTabKey, "");
        UserGroupInformation.setConfiguration(conf);
        boolean gotException = false;
        try {
            SecurityUtil.login(conf, keyTabKey, "", "");
        } catch (IOException e) {
            // expected
            gotException = true;
        }
        Assert.assertTrue("Exception for empty keytabfile name was expected", gotException);
    }

    @Test
    public void testGetHostFromPrincipal() {
        Assert.assertEquals("host", SecurityUtil.getHostFromPrincipal("service/host@realm"));
        Assert.assertEquals(null, SecurityUtil.getHostFromPrincipal("service@realm"));
    }

    @Test
    public void testBuildDTServiceName() {
        Configuration conf = new Configuration(false);
        conf.setBoolean(HADOOP_SECURITY_TOKEN_SERVICE_USE_IP, true);
        SecurityUtil.setConfiguration(conf);
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildDTServiceName(URI.create("test://LocalHost"), 123));
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildDTServiceName(URI.create("test://LocalHost:123"), 456));
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildDTServiceName(URI.create("test://127.0.0.1"), 123));
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildDTServiceName(URI.create("test://127.0.0.1:123"), 456));
    }

    @Test
    public void testBuildTokenServiceSockAddr() {
        Configuration conf = new Configuration(false);
        conf.setBoolean(HADOOP_SECURITY_TOKEN_SERVICE_USE_IP, true);
        SecurityUtil.setConfiguration(conf);
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildTokenService(new InetSocketAddress("LocalHost", 123)).toString());
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildTokenService(new InetSocketAddress("127.0.0.1", 123)).toString());
        // what goes in, comes out
        Assert.assertEquals("127.0.0.1:123", SecurityUtil.buildTokenService(NetUtils.createSocketAddr("127.0.0.1", 123)).toString());
    }

    @Test
    public void testGoodHostsAndPorts() {
        InetSocketAddress compare = NetUtils.createSocketAddrForHost("localhost", 123);
        runGoodCases(compare, "localhost", 123);
        runGoodCases(compare, "localhost:", 123);
        runGoodCases(compare, "localhost:123", 456);
    }

    @Test
    public void testBadHostsAndPorts() {
        runBadCases("", true);
        runBadCases(":", false);
        runBadCases("hdfs/", false);
        runBadCases("hdfs:/", false);
        runBadCases("hdfs://", true);
    }

    @Test
    public void testSocketAddrWithName() {
        String staticHost = "my";
        NetUtils.addStaticResolution(staticHost, "localhost");
        verifyServiceAddr("LocalHost", "127.0.0.1");
    }

    @Test
    public void testSocketAddrWithIP() {
        String staticHost = "127.0.0.1";
        NetUtils.addStaticResolution(staticHost, "localhost");
        verifyServiceAddr(staticHost, "127.0.0.1");
    }

    @Test
    public void testSocketAddrWithNameToStaticName() {
        String staticHost = "host1";
        NetUtils.addStaticResolution(staticHost, "localhost");
        verifyServiceAddr(staticHost, "127.0.0.1");
    }

    @Test
    public void testSocketAddrWithNameToStaticIP() {
        String staticHost = "host3";
        NetUtils.addStaticResolution(staticHost, "255.255.255.255");
        verifyServiceAddr(staticHost, "255.255.255.255");
    }

    // this is a bizarre case, but it's if a test tries to remap an ip address
    @Test
    public void testSocketAddrWithIPToStaticIP() {
        String staticHost = "1.2.3.4";
        NetUtils.addStaticResolution(staticHost, "255.255.255.255");
        verifyServiceAddr(staticHost, "255.255.255.255");
    }

    @Test
    public void testGetAuthenticationMethod() {
        Configuration conf = new Configuration();
        // default is simple
        conf.unset(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION);
        Assert.assertEquals(SIMPLE, SecurityUtil.getAuthenticationMethod(conf));
        // simple
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "simple");
        Assert.assertEquals(SIMPLE, SecurityUtil.getAuthenticationMethod(conf));
        // kerberos
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kerberos");
        Assert.assertEquals(KERBEROS, SecurityUtil.getAuthenticationMethod(conf));
        // bad value
        conf.set(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION, "kaboom");
        String error = null;
        try {
            SecurityUtil.getAuthenticationMethod(conf);
        } catch (Exception e) {
            error = e.toString();
        }
        Assert.assertEquals(((("java.lang.IllegalArgumentException: " + "Invalid attribute value for ") + (CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION)) + " of kaboom"), error);
    }

    @Test
    public void testSetAuthenticationMethod() {
        Configuration conf = new Configuration();
        // default
        SecurityUtil.setAuthenticationMethod(null, conf);
        Assert.assertEquals("simple", conf.get(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION));
        // simple
        SecurityUtil.setAuthenticationMethod(SIMPLE, conf);
        Assert.assertEquals("simple", conf.get(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION));
        // kerberos
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        Assert.assertEquals("kerberos", conf.get(CommonConfigurationKeysPublic.HADOOP_SECURITY_AUTHENTICATION));
    }

    @Test
    public void testAuthPlainPasswordProperty() throws Exception {
        Configuration conf = new Configuration();
        conf.set(ZK_AUTH, TestSecurityUtil.ZK_AUTH_VALUE);
        List<ZKAuthInfo> zkAuths = SecurityUtil.getZKAuthInfos(conf, ZK_AUTH);
        Assert.assertEquals(1, zkAuths.size());
        ZKAuthInfo zkAuthInfo = zkAuths.get(0);
        Assert.assertEquals("a_scheme", zkAuthInfo.getScheme());
        Assert.assertArrayEquals("a_password".getBytes(), zkAuthInfo.getAuth());
    }

    @Test
    public void testAuthPlainTextFile() throws Exception {
        Configuration conf = new Configuration();
        File passwordTxtFile = File.createTempFile(((getClass().getSimpleName()) + ".testAuthAtPathNotation-"), ".txt");
        Files.write(TestSecurityUtil.ZK_AUTH_VALUE, passwordTxtFile, StandardCharsets.UTF_8);
        try {
            conf.set(ZK_AUTH, ("@" + (passwordTxtFile.getAbsolutePath())));
            List<ZKAuthInfo> zkAuths = SecurityUtil.getZKAuthInfos(conf, ZK_AUTH);
            Assert.assertEquals(1, zkAuths.size());
            ZKAuthInfo zkAuthInfo = zkAuths.get(0);
            Assert.assertEquals("a_scheme", zkAuthInfo.getScheme());
            Assert.assertArrayEquals("a_password".getBytes(), zkAuthInfo.getAuth());
        } finally {
            boolean deleted = passwordTxtFile.delete();
            Assert.assertTrue(deleted);
        }
    }

    @Test
    public void testAuthLocalJceks() throws Exception {
        File localJceksFile = File.createTempFile(((getClass().getSimpleName()) + ".testAuthLocalJceks-"), ".localjceks");
        populateLocalJceksTestFile(localJceksFile.getAbsolutePath());
        try {
            String localJceksUri = "localjceks://file/" + (localJceksFile.getAbsolutePath());
            Configuration conf = new Configuration();
            conf.set(CREDENTIAL_PROVIDER_PATH, localJceksUri);
            List<ZKAuthInfo> zkAuths = SecurityUtil.getZKAuthInfos(conf, ZK_AUTH);
            Assert.assertEquals(1, zkAuths.size());
            ZKAuthInfo zkAuthInfo = zkAuths.get(0);
            Assert.assertEquals("a_scheme", zkAuthInfo.getScheme());
            Assert.assertArrayEquals("a_password".getBytes(), zkAuthInfo.getAuth());
        } finally {
            boolean deleted = localJceksFile.delete();
            Assert.assertTrue(deleted);
        }
    }
}

