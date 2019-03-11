/**
 * The Alluxio Open Foundation licenses this work under the Apache License, version 2.0
 * (the "License"). You may not use this work except in compliance with the License, which is
 * available at www.apache.org/licenses/LICENSE-2.0
 *
 * This software is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied, as more fully set forth in the License.
 *
 * See the NOTICE file distributed with this work for information regarding copyright ownership.
 */
package alluxio.security.authentication;


import AuthType.CUSTOM;
import AuthType.KERBEROS;
import AuthType.NOSASL;
import AuthType.SIMPLE;
import PropertyKey.SECURITY_AUTHENTICATION_CUSTOM_PROVIDER_CLASS;
import PropertyKey.SECURITY_AUTHENTICATION_TYPE;
import alluxio.conf.InstancedConfiguration;
import alluxio.exception.status.UnauthenticatedException;
import alluxio.grpc.GrpcChannelBuilder;
import alluxio.grpc.GrpcServer;
import javax.security.sasl.AuthenticationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


/**
 * Unit test for {@link alluxio.grpc.GrpcChannelBuilder} and {@link alluxio.grpc.GrpcServerBuilder}.
 */
public class GrpcSecurityTest {
    /**
     * The exception expected to be thrown.
     */
    @Rule
    public ExpectedException mThrown = ExpectedException.none();

    private InstancedConfiguration mConfiguration;

    @Test
    public void testServerUnsupportedAuthentication() {
        mThrown.expect(RuntimeException.class);
        mThrown.expectMessage(("Authentication type not supported:" + (KERBEROS.name())));
        createServer(KERBEROS);
    }

    @Test
    public void testSimpleAuthentication() throws Exception {
        GrpcServer server = createServer(SIMPLE);
        server.start();
        GrpcChannelBuilder channelBuilder = GrpcChannelBuilder.newBuilder(getServerConnectAddress(server), mConfiguration);
        channelBuilder.build();
        server.shutdown();
    }

    @Test
    public void testNoSaslAuthentication() throws Exception {
        GrpcServer server = createServer(NOSASL);
        server.start();
        GrpcChannelBuilder channelBuilder = GrpcChannelBuilder.newBuilder(getServerConnectAddress(server), mConfiguration);
        channelBuilder.build();
        server.shutdown();
    }

    @Test
    public void testCustomAuthentication() throws Exception {
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, CUSTOM.getAuthName());
        mConfiguration.set(SECURITY_AUTHENTICATION_CUSTOM_PROVIDER_CLASS, GrpcSecurityTest.ExactlyMatchAuthenticationProvider.class.getName());
        GrpcServer server = createServer(CUSTOM);
        server.start();
        GrpcChannelBuilder channelBuilder = GrpcChannelBuilder.newBuilder(getServerConnectAddress(server), mConfiguration);
        channelBuilder.setCredentials(GrpcSecurityTest.ExactlyMatchAuthenticationProvider.USERNAME, GrpcSecurityTest.ExactlyMatchAuthenticationProvider.PASSWORD, null).build();
        server.shutdown();
    }

    @Test
    public void testCustomAuthenticationFails() throws Exception {
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, CUSTOM.getAuthName());
        mConfiguration.set(SECURITY_AUTHENTICATION_CUSTOM_PROVIDER_CLASS, GrpcSecurityTest.ExactlyMatchAuthenticationProvider.class.getName());
        GrpcServer server = createServer(CUSTOM);
        server.start();
        GrpcChannelBuilder channelBuilder = GrpcChannelBuilder.newBuilder(getServerConnectAddress(server), mConfiguration);
        mThrown.expect(UnauthenticatedException.class);
        channelBuilder.setCredentials("fail", "fail", null).build();
        server.shutdown();
    }

    @Test
    public void testDisabledAuthentication() throws Exception {
        GrpcServer server = createServer(SIMPLE);
        server.start();
        GrpcChannelBuilder channelBuilder = GrpcChannelBuilder.newBuilder(getServerConnectAddress(server), mConfiguration);
        channelBuilder.disableAuthentication().build();
        server.shutdown();
    }

    @Test
    public void testAuthMismatch() throws Exception {
        GrpcServer server = createServer(NOSASL);
        server.start();
        mConfiguration.set(SECURITY_AUTHENTICATION_TYPE, SIMPLE);
        GrpcChannelBuilder channelBuilder = GrpcChannelBuilder.newBuilder(getServerConnectAddress(server), mConfiguration);
        mThrown.expect(UnauthenticatedException.class);
        channelBuilder.build();
        server.shutdown();
    }

    /**
     * This customized authentication provider is used in CUSTOM mode. It authenticates the user by
     * verifying the specific username:password pair.
     */
    public static class ExactlyMatchAuthenticationProvider implements AuthenticationProvider {
        static final String USERNAME = "alluxio";

        static final String PASSWORD = "correct-password";

        @Override
        public void authenticate(String user, String password) throws AuthenticationException {
            if ((!(user.equals(GrpcSecurityTest.ExactlyMatchAuthenticationProvider.USERNAME))) || (!(password.equals(GrpcSecurityTest.ExactlyMatchAuthenticationProvider.PASSWORD)))) {
                throw new AuthenticationException("User authentication fails");
            }
        }
    }
}

