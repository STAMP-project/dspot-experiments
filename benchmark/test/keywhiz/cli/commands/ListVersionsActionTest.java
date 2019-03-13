package keywhiz.cli.commands;


import com.google.common.collect.ImmutableMap;
import keywhiz.api.ApiDate;
import keywhiz.api.model.SanitizedSecret;
import keywhiz.api.model.Secret;
import keywhiz.cli.Printing;
import keywhiz.cli.configs.ListVersionsActionConfig;
import keywhiz.client.KeywhizClient;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;


public class ListVersionsActionTest {
    @Rule
    public MockitoRule mockito = MockitoJUnit.rule();

    @Mock
    KeywhizClient keywhizClient;

    @Mock
    Printing printing;

    ListVersionsActionConfig listVersionsActionConfig;

    ListVersionsAction listVersionsAction;

    private static final ApiDate NOW = ApiDate.now();

    Secret secret = new Secret(0, "secret", null, () -> "c2VjcmV0MQ==", "checksum", ListVersionsActionTest.NOW, null, ListVersionsActionTest.NOW, null, null, null, ImmutableMap.of(), 0, 1L, ListVersionsActionTest.NOW, null);

    SanitizedSecret sanitizedSecret = SanitizedSecret.fromSecret(secret);

    @Test
    public void listVersionsCallsPrint() throws Exception {
        listVersionsActionConfig.name = secret.getDisplayName();
        listVersionsActionConfig.idx = 5;
        listVersionsActionConfig.number = 15;
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getDisplayName())).thenReturn(sanitizedSecret);
        listVersionsAction.run();
        Mockito.verify(printing).printSecretVersions(keywhizClient.listSecretVersions("test-secret", 5, 15), 1L);
    }

    @Test
    public void listVersionsUsesDefaults() throws Exception {
        listVersionsActionConfig.name = secret.getDisplayName();
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getDisplayName())).thenReturn(sanitizedSecret);
        listVersionsAction.run();
        Mockito.verify(printing).printSecretVersions(keywhizClient.listSecretVersions("test-secret", 0, 10), 1L);
    }

    @Test(expected = AssertionError.class)
    public void listVersionsThrowsIfSecretDoesNotExist() throws Exception {
        listVersionsActionConfig.name = secret.getDisplayName();
        Mockito.when(keywhizClient.getSanitizedSecretByName(secret.getDisplayName())).thenThrow(new KeywhizClient.NotFoundException());
        listVersionsAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void listVersionsThrowsIfNoSecretSpecified() throws Exception {
        listVersionsActionConfig.name = null;
        listVersionsAction.run();
    }

    @Test(expected = IllegalArgumentException.class)
    public void listVersionsValidatesSecretName() throws Exception {
        listVersionsActionConfig.name = "Invalid Name";
        listVersionsAction.run();
    }
}

