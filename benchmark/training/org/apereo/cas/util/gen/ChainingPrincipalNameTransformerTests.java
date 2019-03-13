package org.apereo.cas.util.gen;


import lombok.val;
import org.apereo.cas.util.transforms.ChainingPrincipalNameTransformer;
import org.apereo.cas.util.transforms.ConvertCasePrincipalNameTransformer;
import org.apereo.cas.util.transforms.PrefixSuffixPrincipalNameTransformer;
import org.apereo.cas.util.transforms.RegexPrincipalNameTransformer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


/**
 * This is {@link ChainingPrincipalNameTransformerTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class ChainingPrincipalNameTransformerTests {
    @Test
    public void verifyChain() {
        val t = new ChainingPrincipalNameTransformer();
        t.addTransformer(new RegexPrincipalNameTransformer("(.+)@example.org"));
        t.addTransformer(new PrefixSuffixPrincipalNameTransformer("prefix-", "-suffix"));
        t.addTransformer(new ConvertCasePrincipalNameTransformer(true));
        val uid = t.transform("casuser@example.org");
        Assertions.assertTrue("PREFIX-CASUSER-SUFFIX".equals(uid));
    }
}

