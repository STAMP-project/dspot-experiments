package com.navercorp.pinpoint.plugin.druid;


import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import com.navercorp.pinpoint.bootstrap.plugin.ProfilerPluginSetupContext;
import org.junit.Test;
import org.mockito.Mockito;


public class DruidPluginTest {
    private DruidPlugin plugin = new DruidPlugin();

    @Test
    public void setTransformTemplate() {
        InstrumentContext instrumentContext = Mockito.mock(InstrumentContext.class);
        plugin.setTransformTemplate(new com.navercorp.pinpoint.bootstrap.instrument.transformer.TransformTemplate(instrumentContext));
    }

    @Test
    public void setup() {
        ProfilerPluginSetupContext profilerPluginSetupContext = Mockito.mock(ProfilerPluginSetupContext.class);
        Mockito.when(profilerPluginSetupContext.getConfig()).thenReturn(new DefaultProfilerConfig());
        plugin.setup(profilerPluginSetupContext);
    }
}

