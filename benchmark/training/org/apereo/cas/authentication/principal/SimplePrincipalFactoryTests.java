package org.apereo.cas.authentication.principal;


import java.util.HashMap;
import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 *
 *
 * @author Misagh Moayyed
 * @since 3.0.0
 */
public class SimplePrincipalFactoryTests {
    @Test
    public void checkPrincipalCreation() {
        val fact = new DefaultPrincipalFactory();
        val map = new HashMap<String, Object>();
        map.put("a1", "v1");
        map.put("a2", "v3");
        val p = fact.createPrincipal("user", map);
        Assertions.assertTrue((p instanceof SimplePrincipal));
        Assertions.assertEquals(p.getAttributes(), map);
    }

    @Test
    public void checkPrincipalEquality() {
        val fact = new DefaultPrincipalFactory();
        val map = new HashMap<String, Object>();
        map.put("a1", "v1");
        map.put("a2", "v3");
        val p = fact.createPrincipal("user", map);
        val p2 = fact.createPrincipal("USER", map);
        Assertions.assertTrue((p instanceof SimplePrincipal));
        Assertions.assertTrue((p2 instanceof SimplePrincipal));
        Assertions.assertEquals(p.getAttributes(), map);
        Assertions.assertEquals(p2.getAttributes(), map);
        Assertions.assertEquals(p2.getAttributes(), p.getAttributes());
        Assertions.assertEquals(p, p2);
    }
}

