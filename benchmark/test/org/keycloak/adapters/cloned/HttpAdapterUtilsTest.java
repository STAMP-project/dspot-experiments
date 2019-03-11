/**
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.keycloak.adapters.cloned;


import KeyTypes.SIGNING;
import KeycloakSamlAdapterV1QNames.ATTR_SIGNING;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyName;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.saml.common.exceptions.ParsingException;


/**
 *
 *
 * @author hmlnarik
 */
public class HttpAdapterUtilsTest {
    @Test
    public void testExtractKeysFromSamlDescriptor() throws ParsingException {
        InputStream xmlStream = HttpAdapterUtilsTest.class.getResourceAsStream("saml-descriptor-valid.xml");
        MultivaluedHashMap<String, KeyInfo> res = HttpAdapterUtils.extractKeysFromSamlDescriptor(xmlStream);
        Assert.assertThat(res, CoreMatchers.notNullValue());
        Assert.assertThat(res.keySet(), CoreMatchers.hasItems(SIGNING.value()));
        Assert.assertThat(res.get(ATTR_SIGNING.getQName().getLocalPart()), CoreMatchers.notNullValue());
        Assert.assertThat(res.get(ATTR_SIGNING.getQName().getLocalPart()).size(), CoreMatchers.equalTo(2));
        KeyInfo ki;
        KeyName keyName;
        X509Data x509data;
        X509Certificate x509certificate;
        ki = res.get(ATTR_SIGNING.getQName().getLocalPart()).get(0);
        Assert.assertThat(ki.getContent().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(((List<Object>) (ki.getContent())), CoreMatchers.hasItem(CoreMatchers.instanceOf(X509Data.class)));
        Assert.assertThat(((List<Object>) (ki.getContent())), CoreMatchers.hasItem(CoreMatchers.instanceOf(KeyName.class)));
        keyName = getContent(ki.getContent(), KeyName.class);
        Assert.assertThat(keyName.getName(), CoreMatchers.equalTo("rJkJlvowmv1Id74GznieaAC5jU5QQp_ILzuG-GsweTI"));
        x509data = getContent(ki.getContent(), X509Data.class);
        Assert.assertThat(x509data, CoreMatchers.notNullValue());
        x509certificate = getContent(x509data.getContent(), X509Certificate.class);
        Assert.assertThat(x509certificate, CoreMatchers.notNullValue());
        Assert.assertThat(x509certificate.getSigAlgName(), CoreMatchers.equalTo("SHA256withRSA"));
        ki = res.get(ATTR_SIGNING.getQName().getLocalPart()).get(1);
        Assert.assertThat(ki.getContent().size(), CoreMatchers.equalTo(2));
        Assert.assertThat(((List<Object>) (ki.getContent())), CoreMatchers.hasItem(CoreMatchers.instanceOf(X509Data.class)));
        Assert.assertThat(((List<Object>) (ki.getContent())), CoreMatchers.hasItem(CoreMatchers.instanceOf(KeyName.class)));
        keyName = getContent(ki.getContent(), KeyName.class);
        Assert.assertThat(keyName.getName(), CoreMatchers.equalTo("BzYc4GwL8HVrAhNyNdp-lTah2DvU9jU03kby9Ynohr4"));
        x509data = getContent(ki.getContent(), X509Data.class);
        Assert.assertThat(x509data, CoreMatchers.notNullValue());
        x509certificate = getContent(x509data.getContent(), X509Certificate.class);
        Assert.assertThat(x509certificate, CoreMatchers.notNullValue());
        Assert.assertThat(x509certificate.getSigAlgName(), CoreMatchers.equalTo("SHA256withRSA"));
    }
}

