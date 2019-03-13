package org.kie.dmn.feel.lang.examples;


import BuiltInType.LIST;
import BuiltInType.STRING;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.api.feel.runtime.events.FEELEventListener;
import org.kie.dmn.feel.FEEL;
import org.kie.dmn.feel.lang.CompiledExpression;
import org.kie.dmn.feel.lang.CompilerContext;
import org.kie.dmn.feel.lang.impl.MapBackedType;
import org.kie.dmn.feel.util.DynamicTypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CompileEvaluateTest {
    private static final Logger LOG = LoggerFactory.getLogger(CompileEvaluateTest.class);

    private static final FEEL feel = FEEL.newInstance();

    private List<FEELEvent> errors;

    private FEELEventListener errorsCountingListener;

    static {
        CompileEvaluateTest.feel.addListener(( evt) -> {
            if ((evt.getSeverity()) == FEELEvent.Severity.ERROR) {
                LOG.error("{}", evt);
                if ((evt.getSourceException().getCause()) != null) {
                    Throwable c = evt.getSourceException().getCause();
                    while (c != null) {
                        LOG.error(" caused by: {} {}", c.getClass(), ((c.getMessage()) != null ? c.getMessage() : ""));
                        c = c.getCause();
                    } 
                    LOG.error(" [stacktraces omitted.]");
                }
            } else
                if ((evt.getSeverity()) == FEELEvent.Severity.WARN) {
                    LOG.warn("{}", evt);
                }

        });
    }

    @Test
    public void test_isDynamicResolution() {
        CompilerContext ctx = CompileEvaluateTest.feel.newCompilerContext();
        ctx.addInputVariableType("Person List", LIST);
        CompiledExpression compiledExpression = CompileEvaluateTest.feel.compile("Person List[My Variable 1 = \"A\"]", ctx);
        Assert.assertThat(errors.toString(), errors.size(), is(0));
        Map<String, Object> inputs = new HashMap<>();
        List<Map<String, ?>> pList = new ArrayList<>();
        inputs.put("Person List", pList);
        pList.add(DynamicTypeUtils.prototype(DynamicTypeUtils.entry("Full Name", "Edson Tirelli"), DynamicTypeUtils.entry("My Variable 1", "A")));
        pList.add(DynamicTypeUtils.prototype(DynamicTypeUtils.entry("Full Name", "Matteo Mortari"), DynamicTypeUtils.entry("My Variable 1", "B")));
        Object result = CompileEvaluateTest.feel.evaluate(compiledExpression, inputs);
        Assert.assertThat(result, instanceOf(List.class));
        Assert.assertThat(((List<?>) (result)), hasSize(1));
        Assert.assertThat(((Map<?, ?>) (((List<?>) (result)).get(0))).get("Full Name"), is("Edson Tirelli"));
    }

    @Test
    public void test2() {
        CompilerContext ctx = CompileEvaluateTest.feel.newCompilerContext();
        ctx.addInputVariableType("MyPerson", new MapBackedType().addField("FullName", STRING));
        CompiledExpression compiledExpression = CompileEvaluateTest.feel.compile("MyPerson.fullName", ctx);
        Assert.assertThat(errors.toString(), errors.size(), is(1));
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("MyPerson", DynamicTypeUtils.prototype(DynamicTypeUtils.entry("FullName", "John Doe")));
        Object result = CompileEvaluateTest.feel.evaluate(compiledExpression, inputs);
        Assert.assertThat(result, nullValue());
    }

    @Test
    public void test2OK() {
        CompilerContext ctx = CompileEvaluateTest.feel.newCompilerContext();
        ctx.addInputVariableType("MyPerson", new MapBackedType().addField("FullName", STRING));
        CompiledExpression compiledExpression = CompileEvaluateTest.feel.compile("MyPerson.FullName", ctx);
        Map<String, Object> inputs = new HashMap<>();
        inputs.put("MyPerson", DynamicTypeUtils.prototype(DynamicTypeUtils.entry("FullName", "John Doe")));
        Object result = CompileEvaluateTest.feel.evaluate(compiledExpression, inputs);
        Assert.assertThat(result, is("John Doe"));
    }
}

