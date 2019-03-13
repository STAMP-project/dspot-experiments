/**
 * ! ******************************************************************************
 *
 * Pentaho Data Integration
 *
 * Copyright (C) 2002-2017 by Hitachi Vantara : http://www.pentaho.com
 *
 * ******************************************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ****************************************************************************
 */
package org.pentaho.di.trans.steps.ssh;


import Const.CR;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.pentaho.di.core.encryption.Encr;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.junit.rules.RestorePDIEngineEnvironment;
import org.pentaho.di.trans.steps.loadsave.LoadSaveTester;
import org.w3c.dom.Node;


public class SSHMetaTest {
    @ClassRule
    public static RestorePDIEngineEnvironment env = new RestorePDIEngineEnvironment();

    @Test
    public void testEncryptedPasswords() throws KettleXMLException {
        String plaintextPassword = "MyEncryptedPassword";
        String plaintextPassphrase = "MyEncryptedPassPhrase";
        String plaintextProxyPassword = "MyEncryptedProxyPassword";
        SSHMeta sshMeta = new SSHMeta();
        sshMeta.setpassword(plaintextPassword);
        sshMeta.setPassphrase(plaintextPassphrase);
        sshMeta.setProxyPassword(plaintextProxyPassword);
        StringBuilder xmlString = new StringBuilder(50);
        xmlString.append(XMLHandler.getXMLHeader()).append(CR);
        xmlString.append(XMLHandler.openTag("step")).append(CR);
        xmlString.append(sshMeta.getXML());
        xmlString.append(XMLHandler.closeTag("step")).append(CR);
        Node sshXMLNode = XMLHandler.loadXMLString(xmlString.toString(), "step");
        Assert.assertEquals(Encr.encryptPasswordIfNotUsingVariables(plaintextPassword), XMLHandler.getTagValue(sshXMLNode, "password"));
        Assert.assertEquals(Encr.encryptPasswordIfNotUsingVariables(plaintextPassphrase), XMLHandler.getTagValue(sshXMLNode, "passPhrase"));
        Assert.assertEquals(Encr.encryptPasswordIfNotUsingVariables(plaintextProxyPassword), XMLHandler.getTagValue(sshXMLNode, "proxyPassword"));
    }

    @Test
    public void testRoundTrips() throws KettleException {
        List<String> commonFields = Arrays.<String>asList("dynamicCommandField", "command", "commandfieldname", "port", "servername", "userName", "password", "usePrivateKey", "keyFileName", "passPhrase", "stdOutFieldName", "stdErrFieldName", "timeOut", "proxyHost", "proxyPort", "proxyUsername", "proxyPassword");
        Map<String, String> getterMap = new HashMap<String, String>();
        getterMap.put("dynamicCommandField", "isDynamicCommand");
        getterMap.put("command", "getCommand");
        getterMap.put("commandfieldname", "getcommandfieldname");
        getterMap.put("port", "getPort");
        getterMap.put("servername", "getServerName");
        getterMap.put("userName", "getuserName");
        getterMap.put("password", "getpassword");
        getterMap.put("usePrivateKey", "isusePrivateKey");
        getterMap.put("keyFileName", "getKeyFileName");
        getterMap.put("passPhrase", "getPassphrase");
        getterMap.put("stdOutFieldName", "getStdOutFieldName");
        getterMap.put("stdErrFieldName", "getStdErrFieldName");
        getterMap.put("timeOut", "getTimeOut");
        getterMap.put("proxyHost", "getProxyHost");
        getterMap.put("proxyPort", "getProxyPort");
        getterMap.put("proxyUsername", "getProxyUsername");
        getterMap.put("proxyPassword", "getProxyPassword");
        Map<String, String> setterMap = new HashMap<String, String>();
        setterMap.put("dynamicCommandField", "setDynamicCommand");
        setterMap.put("command", "setCommand");
        setterMap.put("commandfieldname", "setcommandfieldname");
        setterMap.put("port", "setPort");
        setterMap.put("servername", "setServerName");
        setterMap.put("userName", "setuserName");
        setterMap.put("password", "setpassword");
        setterMap.put("usePrivateKey", "usePrivateKey");
        setterMap.put("keyFileName", "setKeyFileName");
        setterMap.put("passPhrase", "setPassphrase");
        setterMap.put("stdOutFieldName", "setstdOutFieldName");
        setterMap.put("stdErrFieldName", "setStdErrFieldName");
        setterMap.put("timeOut", "setTimeOut");
        setterMap.put("proxyHost", "setProxyHost");
        setterMap.put("proxyPort", "setProxyPort");
        setterMap.put("proxyUsername", "setProxyUsername");
        setterMap.put("proxyPassword", "setProxyPassword");
        LoadSaveTester tester = new LoadSaveTester(SSHMeta.class, commonFields, getterMap, setterMap);
        tester.testSerialization();
    }
}

