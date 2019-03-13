package org.apereo.cas.authentication.support.password;


import lombok.val;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;


/**
 * This is {@link GroovyPasswordEncoderTests}.
 *
 * @author Misagh Moayyed
 * @since 6.1.0
 */
@Tag("Groovy")
public class GroovyPasswordEncoderTests {
    @Test
    public void verifyOperation() {
        val enc = new GroovyPasswordEncoder(new ClassPathResource("GroovyPasswordEncoder.groovy"));
        val encoded = enc.encode("helloworld");
        Assertions.assertTrue(enc.matches("helloworld", encoded));
    }
}

