package org.keycloak.procotol.docker.installation;


import java.security.PublicKey;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;


/**
 * Docker gets really unhappy if the key identifier is not in the format documented here:
 *
 * @see https://github.com/docker/libtrust/blob/master/key.go#L24
 */
public class DockerKeyIdentifierTest {
    String keyIdentifierString;

    PublicKey publicKey;

    @Test
    public void shoulProduceExpectedKeyFormat() {
        MatcherAssert.assertThat("Every 4 chars are not delimted by colon", keyIdentifierString.matches("([\\w]{4}:){11}[\\w]{4}"), CoreMatchers.equalTo(true));
    }
}

