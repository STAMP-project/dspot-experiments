package org.rapidoid.security;


import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;
import org.rapidoid.annotation.Authors;
import org.rapidoid.annotation.Since;


@Authors("Nikolche Mihajlovski")
@Since("4.1.0")
public class MethodSecurityTest extends SecurityTestCommons {
    private static final String ELSE = "asfdalkjsfasfd";

    private Method aa;

    private Method bb;

    private Method noAnn;

    private Method dd;

    private Method ee;

    private Method[] methods;

    private Method aa2;

    private Method bb2;

    private Method noAnn2;

    private Method dd2;

    private Method ee2;

    private Method[] methods2;

    @Test
    public void testAdminOnly() {
        isTrue(Secure.canAccessMethod("adm1", roles("adm1"), aa));
        isFalse(Secure.canAccessMethod("admin@debug", roles("admin@debug"), aa));
        isFalse(Secure.canAccessMethod("mng1", roles("mng1"), aa));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), aa));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), aa));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), aa));
        isFalse(Secure.canAccessMethod("xyz1", roles("xyz1"), aa));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), aa));
        isFalse(Secure.canAccessMethod("", roles(""), aa));
        isFalse(Secure.canAccessMethod(null, roles(null), aa));
    }

    @Test
    public void testAdminOnly2() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), aa2));
        isFalse(Secure.canAccessMethod("admin@debug", roles("admin@debug"), aa2));
        isFalse(Secure.canAccessMethod("mng1", roles("mng1"), aa2));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), aa2));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), aa2));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), aa2));
        isFalse(Secure.canAccessMethod("xyz1", roles("xyz1"), aa2));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), aa2));
        isFalse(Secure.canAccessMethod("", roles(""), aa2));
        isFalse(Secure.canAccessMethod(null, roles(null), aa2));
    }

    @Test
    public void testSeveralSpecialRoles() {
        isTrue(Secure.canAccessMethod("adm1", roles("adm1"), bb));
        isFalse(Secure.canAccessMethod("admin@debug", roles("admin@debug"), bb));
        isTrue(Secure.canAccessMethod("mng1", roles("mng1"), bb));
        isTrue(Secure.canAccessMethod("mod1", roles("mod1"), bb));
        isTrue(Secure.canAccessMethod("mod2", roles("mod2"), bb));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), bb));
        isFalse(Secure.canAccessMethod("xyz1", roles("xyz1"), bb));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), bb));
        isFalse(Secure.canAccessMethod("", roles(""), bb));
        isFalse(Secure.canAccessMethod(null, roles(null), bb));
    }

    @Test
    public void testSeveralSpecialRoles2() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), bb2));
        isFalse(Secure.canAccessMethod("admin@debug", roles("admin@debug"), aa2));
        isTrue(Secure.canAccessMethod("mng1", roles("mng1"), bb2));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), bb2));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), bb2));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), bb2));
        isFalse(Secure.canAccessMethod("xyz1", roles("xyz1"), bb2));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), bb2));
        isFalse(Secure.canAccessMethod("", roles(""), bb2));
        isFalse(Secure.canAccessMethod(null, roles(null), bb2));
    }

    @Test
    public void testNoAnnotation() {
        isTrue(Secure.canAccessMethod("adm1", roles("adm1"), noAnn));
        isTrue(Secure.canAccessMethod("mng1", roles("mng1"), noAnn));
        isTrue(Secure.canAccessMethod("mod1", roles("mod1"), noAnn));
        isTrue(Secure.canAccessMethod("mod2", roles("mod2"), noAnn));
        isTrue(Secure.canAccessMethod("abc1", roles("abc1"), noAnn));
        isTrue(Secure.canAccessMethod("xyz1", roles("xyz1"), noAnn));
        isTrue(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), noAnn));
        isTrue(Secure.canAccessMethod("", roles(""), noAnn));
        isTrue(Secure.canAccessMethod(null, roles(null), noAnn));
    }

    @Test
    public void testNoAnnotation2() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), noAnn2));
        isTrue(Secure.canAccessMethod("mng1", roles("mng1"), noAnn2));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), noAnn2));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), noAnn2));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), noAnn2));
        isTrue(Secure.canAccessMethod("xyz1", roles("xyz1"), noAnn2));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), noAnn2));
        isFalse(Secure.canAccessMethod("", roles(""), noAnn2));
        isFalse(Secure.canAccessMethod(null, roles(null), noAnn2));
    }

    @Test
    public void testCustomRole() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), dd));
        isFalse(Secure.canAccessMethod("mng1", roles("mng1"), dd));
        isTrue(Secure.canAccessMethod("mod1", roles("mod1"), dd));
        isTrue(Secure.canAccessMethod("mod2", roles("mod2"), dd));
        isTrue(Secure.canAccessMethod("abc1", roles("abc1"), dd));
        isFalse(Secure.canAccessMethod("xyz1", roles("xyz1"), dd));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), dd));
        isFalse(Secure.canAccessMethod("", roles(""), dd));
        isFalse(Secure.canAccessMethod(null, roles(null), dd));
    }

    @Test
    public void testCustomRole2() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), dd2));
        isFalse(Secure.canAccessMethod("mng1", roles("mng1"), dd2));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), dd2));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), dd2));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), dd2));
        isFalse(Secure.canAccessMethod("xyz1", roles("xyz1"), dd2));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), dd2));
        isFalse(Secure.canAccessMethod("", roles(""), dd2));
        isFalse(Secure.canAccessMethod(null, roles(null), dd2));
    }

    @Test
    public void testCustomRoles() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), ee));
        isTrue(Secure.canAccessMethod("mng1", roles("mng1"), ee));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), ee));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), ee));
        isTrue(Secure.canAccessMethod("abc1", roles("abc1"), ee));
        isTrue(Secure.canAccessMethod("xyz1", roles("xyz1"), ee));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), ee));
        isFalse(Secure.canAccessMethod("", roles(""), ee));
        isFalse(Secure.canAccessMethod(null, roles(null), ee));
    }

    @Test
    public void testCustomRoles2() {
        isFalse(Secure.canAccessMethod("adm1", roles("adm1"), ee2));
        isTrue(Secure.canAccessMethod("mng1", roles("mng1"), ee2));
        isFalse(Secure.canAccessMethod("mod1", roles("mod1"), ee2));
        isFalse(Secure.canAccessMethod("mod2", roles("mod2"), ee2));
        isFalse(Secure.canAccessMethod("abc1", roles("abc1"), ee2));
        isTrue(Secure.canAccessMethod("xyz1", roles("xyz1"), ee2));
        isFalse(Secure.canAccessMethod(MethodSecurityTest.ELSE, roles(MethodSecurityTest.ELSE), ee2));
        isFalse(Secure.canAccessMethod("", roles(""), ee2));
        isFalse(Secure.canAccessMethod(null, roles(null), ee2));
    }

    @Test
    public void testRoleAndUsernameMatch() {
        for (Method method : methods) {
            if (method != (noAnn)) {
                isFalse(Secure.canAccessMethod("administrator", roles("administrator"), method));
                isFalse(Secure.canAccessMethod("manager", roles("manager"), method));
                isFalse(Secure.canAccessMethod("moderator", roles("moderator"), method));
                isFalse(Secure.canAccessMethod("abc", roles("abc"), method));
                isFalse(Secure.canAccessMethod("xyz", roles("xyz"), method));
                isFalse(Secure.canAccessMethod("loggedin", roles("loggedin"), method));
            }
        }
    }

    @Test
    public void testRoleAndUsernameMatch2() {
        for (Method method : methods2) {
            if (method != (noAnn)) {
                isFalse(Secure.canAccessMethod("administrator", roles("administrator"), method));
                isFalse(Secure.canAccessMethod("manager", roles("manager"), method));
                isFalse(Secure.canAccessMethod("moderator", roles("moderator"), method));
                isFalse(Secure.canAccessMethod("abc", roles("abc"), method));
                isFalse(Secure.canAccessMethod("xyz", roles("xyz"), method));
                isFalse(Secure.canAccessMethod("loggedin", roles("loggedin"), method));
            }
        }
    }
}

