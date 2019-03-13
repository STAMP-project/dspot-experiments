/**
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.boot.logging.logback;


import Action.NAME_ATTRIBUTE;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.ContextBase;
import ch.qos.logback.core.joran.spi.ActionException;
import ch.qos.logback.core.joran.spi.InterpretationContext;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.xml.sax.Attributes;


/**
 * Tests for {@link SpringProfileAction}.
 *
 * @author Andy Wilkinson
 */
public class SpringProfileActionTests {
    private final Environment environment = Mockito.mock(Environment.class);

    private final SpringProfileAction action = new SpringProfileAction(this.environment);

    private final Context context = new ContextBase();

    private final InterpretationContext interpretationContext = new InterpretationContext(this.context, null);

    private final Attributes attributes = Mockito.mock(Attributes.class);

    @Test
    public void environmentIsQueriedWithProfileFromNameAttribute() throws ActionException {
        BDDMockito.given(this.attributes.getValue(NAME_ATTRIBUTE)).willReturn("dev");
        this.action.begin(this.interpretationContext, null, this.attributes);
        ArgumentCaptor<Profiles> profiles = ArgumentCaptor.forClass(Profiles.class);
        Mockito.verify(this.environment).acceptsProfiles(profiles.capture());
        List<String> profileNames = new ArrayList<>();
        profiles.getValue().matches(( profile) -> {
            profileNames.add(profile);
            return false;
        });
        assertThat(profileNames).containsExactly("dev");
    }

    @Test
    public void environmentIsQueriedWithMultipleProfilesFromCommaSeparatedNameAttribute() throws ActionException {
        BDDMockito.given(this.attributes.getValue(NAME_ATTRIBUTE)).willReturn("dev,qa");
        this.action.begin(this.interpretationContext, null, this.attributes);
        ArgumentCaptor<Profiles> profiles = ArgumentCaptor.forClass(Profiles.class);
        Mockito.verify(this.environment).acceptsProfiles(profiles.capture());
        List<String> profileNames = new ArrayList<>();
        profiles.getValue().matches(( profile) -> {
            profileNames.add(profile);
            return false;
        });
        assertThat(profileNames).containsExactly("dev", "qa");
    }

    @Test
    public void environmentIsQueriedWithResolvedValueWhenNameAttributeUsesAPlaceholder() throws ActionException {
        BDDMockito.given(this.attributes.getValue(NAME_ATTRIBUTE)).willReturn("${profile}");
        this.context.putProperty("profile", "dev");
        this.action.begin(this.interpretationContext, null, this.attributes);
        ArgumentCaptor<Profiles> profiles = ArgumentCaptor.forClass(Profiles.class);
        Mockito.verify(this.environment).acceptsProfiles(profiles.capture());
        List<String> profileNames = new ArrayList<>();
        profiles.getValue().matches(( profile) -> {
            profileNames.add(profile);
            return false;
        });
        assertThat(profileNames).containsExactly("dev");
    }

    @Test
    public void environmentIsQueriedWithResolvedValuesFromCommaSeparatedNameNameAttributeWithPlaceholders() throws ActionException {
        BDDMockito.given(this.attributes.getValue(NAME_ATTRIBUTE)).willReturn("${profile1},${profile2}");
        this.context.putProperty("profile1", "dev");
        this.context.putProperty("profile2", "qa");
        this.action.begin(this.interpretationContext, null, this.attributes);
        ArgumentCaptor<Profiles> profiles = ArgumentCaptor.forClass(Profiles.class);
        Mockito.verify(this.environment).acceptsProfiles(profiles.capture());
        List<String> profileNames = new ArrayList<>();
        profiles.getValue().matches(( profile) -> {
            profileNames.add(profile);
            return false;
        });
        assertThat(profileNames).containsExactly("dev", "qa");
    }
}

