package org.apereo.cas.authentication;


import PasswordPolicyProperties.PasswordPolicyHandlingOptions.GROOVY;
import PasswordPolicyProperties.PasswordPolicyHandlingOptions.REJECT_RESULT_CODE;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.apereo.cas.configuration.model.core.authentication.PasswordPolicyProperties;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link CoreAuthenticationUtilsTests}.
 *
 * @author Misagh Moayyed
 * @since 5.2.0
 */
public class CoreAuthenticationUtilsTests {
    @Test
    public void verifyMapTransform() {
        val results = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMap(CollectionUtils.wrapList("name", "family"));
        Assertions.assertEquals(2, results.size());
    }

    @Test
    public void verifyCredentialSelectionPredicateNone() {
        val pred = CoreAuthenticationUtils.newCredentialSelectionPredicate(null);
        Assertions.assertTrue(pred.test(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyCredentialSelectionPredicateGroovy() {
        val pred = CoreAuthenticationUtils.newCredentialSelectionPredicate("classpath:CredentialPredicate.groovy");
        Assertions.assertTrue(pred.test(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyCredentialSelectionPredicateClazz() {
        val pred = CoreAuthenticationUtils.newCredentialSelectionPredicate(CoreAuthenticationUtilsTests.PredicateExample.class.getName());
        Assertions.assertTrue(pred.test(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyCredentialSelectionPredicateRegex() {
        val pred = CoreAuthenticationUtils.newCredentialSelectionPredicate("\\w.+");
        Assertions.assertTrue(pred.test(CoreAuthenticationTestUtils.getCredentialsWithSameUsernameAndPassword()));
    }

    @Test
    public void verifyPasswordPolicy() {
        val properties = new PasswordPolicyProperties();
        Assertions.assertNotNull(CoreAuthenticationUtils.newPasswordPolicyHandlingStrategy(properties));
        properties.setStrategy(GROOVY);
        properties.getGroovy().setLocation(new ClassPathResource("passwordpolicy.groovy"));
        Assertions.assertNotNull(CoreAuthenticationUtils.newPasswordPolicyHandlingStrategy(properties));
        properties.setStrategy(REJECT_RESULT_CODE);
        Assertions.assertNotNull(CoreAuthenticationUtils.newPasswordPolicyHandlingStrategy(properties));
    }

    @Test
    public void verifyPrincipalAttributeTransformations() {
        val list = Stream.of("a1", "a2:newA2", "a1:newA1").collect(Collectors.toList());
        val result = CoreAuthenticationUtils.transformPrincipalAttributesListIntoMultiMap(list);
        Assertions.assertEquals(3, result.size());
        Assertions.assertTrue(result.containsKey("a2"));
        Assertions.assertTrue(result.containsKey("a1"));
        val map = CollectionUtils.wrap(result);
        val a2 = ((Collection) (map.get("a2")));
        Assertions.assertEquals(1, a2.size());
        val a1 = ((Collection) (map.get("a1")));
        Assertions.assertEquals(2, a1.size());
        Assertions.assertTrue(a2.contains("newA2"));
        Assertions.assertTrue(a1.contains("a1"));
        Assertions.assertTrue(a1.contains("newA1"));
    }

    public static class PredicateExample implements Predicate<Credential> {
        @Override
        public boolean test(final Credential credential) {
            return true;
        }
    }
}

