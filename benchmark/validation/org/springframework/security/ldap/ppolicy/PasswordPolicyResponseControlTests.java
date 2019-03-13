/**
 * Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.security.ldap.ppolicy;


import org.junit.Test;


/**
 * Tests for <tt>PasswordPolicyResponse</tt>.
 *
 * @author Luke Taylor
 */
public class PasswordPolicyResponseControlTests {
    // ~ Methods
    // ========================================================================================================
    /**
     * Useful method for obtaining data from a server for use in tests
     */
    // public void testAgainstServer() throws Exception {
    // Hashtable env = new Hashtable();
    // env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    // env.put(Context.PROVIDER_URL, "ldap://gorille:389/");
    // env.put(Context.SECURITY_AUTHENTICATION, "simple");
    // env.put(Context.SECURITY_PRINCIPAL, "cn=manager,dc=security,dc=org");
    // env.put(Context.SECURITY_CREDENTIALS, "security");
    // env.put(LdapContext.CONTROL_FACTORIES,
    // PasswordPolicyControlFactory.class.getName());
    // 
    // InitialLdapContext ctx = new InitialLdapContext(env, null);
    // 
    // Control[] rctls = { new PasswordPolicyControl(false) };
    // 
    // ctx.setRequestControls(rctls);
    // 
    // try {
    // ctx.addToEnvironment(Context.SECURITY_PRINCIPAL,
    // "uid=bob,ou=people,dc=security,dc=org" );
    // ctx.addToEnvironment(Context.SECURITY_CREDENTIALS, "bobspassword");
    // Object o = ctx.lookup("");
    // 
    // System.out.println(o);
    // 
    // } catch(NamingException ne) {
    // // Ok.
    // System.err.println(ne);
    // }
    // 
    // PasswordPolicyResponseControl ctrl = getPPolicyResponseCtl(ctx);
    // System.out.println(ctrl);
    // 
    // assertThat(ctrl).isNotNull();
    // 
    // //com.sun.jndi.ldap.LdapPoolManager.showStats(System.out);
    // }
    // private PasswordPolicyResponseControl getPPolicyResponseCtl(InitialLdapContext ctx)
    // throws NamingException {
    // Control[] ctrls = ctx.getResponseControls();
    // 
    // for (int i = 0; ctrls != null && i < ctrls.length; i++) {
    // if (ctrls[i] instanceof PasswordPolicyResponseControl) {
    // return (PasswordPolicyResponseControl) ctrls[i];
    // }
    // }
    // 
    // return null;
    // }
    @Test
    public void openLDAP33SecondsTillPasswordExpiryCtrlIsParsedCorrectly() {
        byte[] ctrlBytes = new byte[]{ 48, 5, ((byte) (160)), 3, ((byte) (160)), 1, 33 };
        PasswordPolicyResponseControl ctrl = new PasswordPolicyResponseControl(ctrlBytes);
        assertThat(ctrl.hasWarning()).isTrue();
        assertThat(ctrl.getTimeBeforeExpiration()).isEqualTo(33);
    }

    @Test
    public void openLDAP496GraceLoginsRemainingCtrlIsParsedCorrectly() {
        byte[] ctrlBytes = new byte[]{ 48, 6, ((byte) (160)), 4, ((byte) (161)), 2, 1, ((byte) (240)) };
        PasswordPolicyResponseControl ctrl = new PasswordPolicyResponseControl(ctrlBytes);
        assertThat(ctrl.hasWarning()).isTrue();
        assertThat(ctrl.getGraceLoginsRemaining()).isEqualTo(496);
    }

    static final byte[] OPENLDAP_5_LOGINS_REMAINING_CTRL = new byte[]{ 48, 5, ((byte) (160)), 3, ((byte) (161)), 1, 5 };

    @Test
    public void openLDAP5GraceLoginsRemainingCtrlIsParsedCorrectly() {
        PasswordPolicyResponseControl ctrl = new PasswordPolicyResponseControl(PasswordPolicyResponseControlTests.OPENLDAP_5_LOGINS_REMAINING_CTRL);
        assertThat(ctrl.hasWarning()).isTrue();
        assertThat(ctrl.getGraceLoginsRemaining()).isEqualTo(5);
    }

    static final byte[] OPENLDAP_LOCKED_CTRL = new byte[]{ 48, 3, ((byte) (161)), 1, 1 };

    @Test
    public void openLDAPAccountLockedCtrlIsParsedCorrectly() {
        PasswordPolicyResponseControl ctrl = new PasswordPolicyResponseControl(PasswordPolicyResponseControlTests.OPENLDAP_LOCKED_CTRL);
        assertThat(((ctrl.hasError()) && (ctrl.isLocked()))).isTrue();
        assertThat(ctrl.hasWarning()).isFalse();
    }

    @Test
    public void openLDAPPasswordExpiredCtrlIsParsedCorrectly() {
        byte[] ctrlBytes = new byte[]{ 48, 3, ((byte) (161)), 1, 0 };
        PasswordPolicyResponseControl ctrl = new PasswordPolicyResponseControl(ctrlBytes);
        assertThat(((ctrl.hasError()) && (ctrl.isExpired()))).isTrue();
        assertThat(ctrl.hasWarning()).isFalse();
    }
}

