package org.pac4j.oauth.client;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.pac4j.core.profile.definition.ProfileDefinition;
import org.pac4j.oauth.config.OAuth20Configuration;
import org.pac4j.oauth.profile.generic.GenericOAuth20ProfileDefinition;


public class GenericOAuth20ClientTest {
    static final String AGE = "age";

    static final String IS_ADMIN = "is_admin";

    static final String BG_COLOR = "bg_color";

    static final String GENDER = "gender";

    static final String BIRTHDAY = "birthday";

    static final String BLOG = "blog";

    @Test
    public void setProfileAttrs() throws Exception {
        GenericOAuth20Client client = new GenericOAuth20Client();
        Map map = new HashMap();
        map.put(GenericOAuth20ClientTest.AGE, "Integer|age");
        // map.put("creation_time", "Date:|creation_time");
        map.put(GenericOAuth20ClientTest.IS_ADMIN, "Boolean|is_admin");
        map.put(GenericOAuth20ClientTest.BG_COLOR, "Color|bg_color");
        map.put(GenericOAuth20ClientTest.GENDER, "Gender|gender");
        map.put(GenericOAuth20ClientTest.BIRTHDAY, "Locale|birthday");
        map.put(ID, "Long|id");
        map.put(GenericOAuth20ClientTest.BLOG, "URI|blog");
        client.setProfileAttrs(map);
        client.setCallbackUrl(CALLBACK_URL);
        client.init();
        Field configurationField = OAuth20Client.class.getDeclaredField("configuration");
        configurationField.setAccessible(true);
        OAuth20Configuration configuration = ((OAuth20Configuration) (configurationField.get(client)));
        GenericOAuth20ProfileDefinition profileDefinition = ((GenericOAuth20ProfileDefinition) (configuration.getProfileDefinition()));
        Method getConverters = ProfileDefinition.class.getDeclaredMethod("getConverters");
        getConverters.setAccessible(true);
        Map<String, AttributeConverter<?>> converters = ((Map<String, AttributeConverter<?>>) (getConverters.invoke(profileDefinition)));
        Assert.assertTrue(((converters.get(GenericOAuth20ClientTest.AGE)) instanceof IntegerConverter));
        Assert.assertTrue(((converters.get(GenericOAuth20ClientTest.IS_ADMIN)) instanceof BooleanConverter));
        Assert.assertTrue(((converters.get(GenericOAuth20ClientTest.BG_COLOR)) instanceof ColorConverter));
        Assert.assertTrue(((converters.get(GenericOAuth20ClientTest.GENDER)) instanceof GenderConverter));
        Assert.assertTrue(((converters.get(GenericOAuth20ClientTest.BIRTHDAY)) instanceof LocaleConverter));
        Assert.assertTrue(((converters.get(ID)) instanceof LongConverter));
        Assert.assertTrue(((converters.get(GenericOAuth20ClientTest.BLOG)) instanceof UrlConverter));
    }
}

