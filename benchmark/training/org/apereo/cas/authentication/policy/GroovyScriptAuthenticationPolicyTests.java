package org.apereo.cas.authentication.policy;


import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.LinkedHashSet;
import lombok.val;
import org.apache.commons.io.FileUtils;
import org.apereo.cas.authentication.CoreAuthenticationTestUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.core.io.ResourceLoader;


/**
 * This is {@link GroovyScriptAuthenticationPolicyTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Tag("Groovy")
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
public class GroovyScriptAuthenticationPolicyTests {
    @Autowired
    private ResourceLoader resourceLoader;

    @Test
    public void verifyActionInlinedScriptPasses() throws Exception {
        val script = ("groovy {" + (" logger.info(principal.id)\n" + " return Optional.empty()\n")) + '}';
        val p = new GroovyScriptAuthenticationPolicy(resourceLoader, script);
        Assertions.assertTrue(p.isSatisfiedBy(CoreAuthenticationTestUtils.getAuthentication(), new LinkedHashSet()));
    }

    @Test
    public void verifyActionInlinedScriptFails() {
        val script = ("groovy {" + ((" import org.apereo.cas.authentication.*\n" + " logger.info(principal.id)\n") + " return Optional.of(new AuthenticationException())\n")) + '}';
        val p = new GroovyScriptAuthenticationPolicy(resourceLoader, script);
        Assertions.assertThrows(GeneralSecurityException.class, () -> p.isSatisfiedBy(CoreAuthenticationTestUtils.getAuthentication(), new LinkedHashSet()));
    }

    @Test
    public void verifyActionExternalScript() throws Exception {
        val script = ("import org.apereo.cas.authentication.*\n" + ((("def run(Object[] args) {" + " def principal = args[0]\n") + " def logger = args[1]\n") + " return Optional.of(new AuthenticationException())\n")) + '}';
        val scriptFile = new File(FileUtils.getTempDirectoryPath(), "script.groovy");
        FileUtils.write(scriptFile, script, StandardCharsets.UTF_8);
        val p = new GroovyScriptAuthenticationPolicy(resourceLoader, ("file:" + (scriptFile.getCanonicalPath())));
        Assertions.assertThrows(GeneralSecurityException.class, () -> p.isSatisfiedBy(CoreAuthenticationTestUtils.getAuthentication(), new LinkedHashSet()));
    }
}

