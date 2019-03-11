package org.apereo.cas.support.saml.mdui;


import lombok.val;
import org.apereo.cas.services.RegisteredServiceTestUtils;
import org.apereo.cas.util.CollectionUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.opensaml.saml.ext.saml2mdui.Description;
import org.opensaml.saml.ext.saml2mdui.DisplayName;
import org.opensaml.saml.ext.saml2mdui.UIInfo;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.test.annotation.DirtiesContext;


/**
 * This is {@link SamlMetadataUIInfoTests}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@SpringBootTest(classes = { RefreshAutoConfiguration.class })
@DirtiesContext
public class SamlMetadataUIInfoTests {
    @Test
    public void verifyInfoNotAvailable() {
        val service = RegisteredServiceTestUtils.getRegisteredService();
        service.setPrivacyUrl("http://cas.example.org");
        service.setInformationUrl("http://cas.example.org");
        val info = new SamlMetadataUIInfo(service, "en");
        Assertions.assertEquals(service.getName(), info.getDisplayName());
        Assertions.assertEquals(service.getDescription(), info.getDescription());
        Assertions.assertEquals(service.getInformationUrl(), info.getInformationURL());
        Assertions.assertEquals("en", info.getLocale());
        Assertions.assertEquals(service.getPrivacyUrl(), info.getPrivacyStatementURL());
    }

    @Test
    public void verifyInfo() {
        val mdui = Mockito.mock(UIInfo.class);
        val description = Mockito.mock(Description.class);
        Mockito.when(description.getValue()).thenReturn("Description");
        Mockito.when(description.getXMLLang()).thenReturn("en");
        val names = Mockito.mock(DisplayName.class);
        Mockito.when(names.getValue()).thenReturn("Name");
        Mockito.when(names.getXMLLang()).thenReturn("en");
        Mockito.when(mdui.getDescriptions()).thenReturn(CollectionUtils.wrapList(description));
        Mockito.when(mdui.getDisplayNames()).thenReturn(CollectionUtils.wrapList(names));
        val service = RegisteredServiceTestUtils.getRegisteredService();
        val info = new SamlMetadataUIInfo(mdui, service);
        Assertions.assertEquals(names.getValue(), info.getDisplayName());
        Assertions.assertEquals(description.getValue(), info.getDescription());
    }
}

