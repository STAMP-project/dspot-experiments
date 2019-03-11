package org.apereo.cas.aup;


import lombok.val;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.test.context.TestPropertySource;


/**
 * This is {@link JdbcAcceptableUsagePolicyRepositoryAdvancedTests}.
 *
 * @author Martin B?hmer
 * @since 5.3.8
 */
@TestPropertySource(properties = { "cas.acceptableUsagePolicy.jdbc.tableName=users_table", "cas.acceptableUsagePolicy.aupAttributeName=aupAccepted", "cas.acceptableUsagePolicy.jdbc.aupColumn=aup", "cas.acceptableUsagePolicy.jdbc.principalIdColumn=mail", "cas.acceptableUsagePolicy.jdbc.principalIdAttribute=email", "cas.acceptableUsagePolicy.jdbc.sqlUpdateAUP=UPDATE %s SET %s=true WHERE lower(%s)=lower(?)" })
public class JdbcAcceptableUsagePolicyRepositoryAdvancedTests extends BaseJdbcAcceptableUsagePolicyRepositoryTests {
    @Test
    public void verifyRepositoryActionWithAdvancedConfig() {
        verifyRepositoryAction("casuser", CollectionUtils.wrap("aupAccepted", "false", "email", "CASuser@example.org"));
    }

    @Test
    public void determinePrincipalIdWithAdvancedConfig() {
        val principalId = determinePrincipalId("casuser", CollectionUtils.wrap("aupAccepted", "false", "email", "CASuser@example.org"));
        Assertions.assertEquals("CASuser@example.org", principalId);
    }

    @Test
    public void raiseMissingPrincipalAttributeError() {
        val exception = Assertions.assertThrows(IllegalStateException.class, () -> raiseException(CollectionUtils.wrap("aupAccepted", "false", "wrong-attribute", "CASuser@example.org")));
        Assertions.assertTrue(exception.getMessage().contains("cannot be found"));
    }

    @Test
    public void raiseEmptyPrincipalAttributeError() {
        val exception = Assertions.assertThrows(IllegalStateException.class, () -> raiseException(CollectionUtils.wrap("aupAccepted", "false", "email", "")));
        Assertions.assertTrue(exception.getMessage().contains("empty or multi-valued with an empty element"));
    }
}

