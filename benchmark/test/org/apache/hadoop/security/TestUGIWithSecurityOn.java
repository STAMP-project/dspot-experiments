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


import AuthenticationMethod.KERBEROS;
import java.io.IOException;
import java.security.PrivilegedAction;
import java.util.Set;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.apache.hadoop.conf.Configuration;
import org.junit.Assert;
import org.junit.Test;


public class TestUGIWithSecurityOn {
    @Test
    public void testLogin() throws IOException {
        String nn1keyTabFilepath = (System.getProperty("kdc.resource.dir")) + "/keytabs/nn1.keytab";
        String user1keyTabFilepath = (System.getProperty("kdc.resource.dir")) + "/keytabs/user1.keytab";
        Configuration conf = new Configuration();
        SecurityUtil.setAuthenticationMethod(KERBEROS, conf);
        UserGroupInformation.setConfiguration(conf);
        UserGroupInformation ugiNn = UserGroupInformation.loginUserFromKeytabAndReturnUGI("nn1/localhost@EXAMPLE.COM", nn1keyTabFilepath);
        UserGroupInformation ugiDn = UserGroupInformation.loginUserFromKeytabAndReturnUGI("user1@EXAMPLE.COM", user1keyTabFilepath);
        Assert.assertEquals(KERBEROS, ugiNn.getAuthenticationMethod());
        Assert.assertEquals(KERBEROS, ugiDn.getAuthenticationMethod());
        try {
            UserGroupInformation.loginUserFromKeytabAndReturnUGI("bogus@EXAMPLE.COM", nn1keyTabFilepath);
            Assert.fail("Login should have failed");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Test
    public void testGetUGIFromKerberosSubject() throws IOException {
        String user1keyTabFilepath = (System.getProperty("kdc.resource.dir")) + "/keytabs/user1.keytab";
        UserGroupInformation ugi = UserGroupInformation.loginUserFromKeytabAndReturnUGI("user1@EXAMPLE.COM", user1keyTabFilepath);
        Set<KerberosPrincipal> principals = ugi.getSubject().getPrincipals(KerberosPrincipal.class);
        if (principals.isEmpty()) {
            Assert.fail("There should be a kerberos principal in the subject.");
        } else {
            UserGroupInformation ugi2 = UserGroupInformation.getUGIFromSubject(ugi.getSubject());
            if (ugi2 != null) {
                ugi2.doAs(new PrivilegedAction<Object>() {
                    @Override
                    public Object run() {
                        try {
                            UserGroupInformation ugi3 = UserGroupInformation.getCurrentUser();
                            String doAsUserName = ugi3.getUserName();
                            Assert.assertEquals(doAsUserName, "user1@EXAMPLE.COM");
                            System.out.println(("DO AS USERNAME: " + doAsUserName));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
        }
    }
}

