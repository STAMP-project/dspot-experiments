package org.cf.smalivm.emulate;


import CommonTypes.CLASS;
import SideEffect.Level;
import SideEffect.Level.NONE;
import org.cf.smalivm.ExceptionFactory;
import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.dex.CommonTypes;
import org.cf.smalivm.dex.SmaliClassLoader;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.UnknownValue;
import org.cf.smalivm.type.VirtualClass;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;


public class java_lang_Class_forName_Test {
    private static final Class<?> OBJECT_CLASS = Object.class;

    private static final Class<?> STRING_CLASS = String.class;

    private VirtualMachine vm;

    private ClassManager classManager;

    private SmaliClassLoader classLoader;

    private ExecutionContextMethod method;

    private MethodState mState;

    private ExecutionContext context;

    private Configuration configuration;

    private Op op;

    private ExceptionFactory exceptionFactory;

    @Test
    public void existentSafeClassNameReturnsRealClassAndHasNoSideEffects() throws Exception {
        String className = "Ljava/lang/String;";// genuinely safe to load

        SideEffect.Level level = Level.NONE;
        setupClass(className, true, level);
        String binaryClassName = "java.lang.String";
        HeapItem item = new HeapItem(binaryClassName, CommonTypes.STRING);
        Mockito.when(mState.peekParameter(0)).thenReturn(item);
        method.execute(vm, op, context);
        Mockito.verify(mState, Mockito.times(1)).assignReturnRegister(ArgumentMatchers.eq(java_lang_Class_forName_Test.STRING_CLASS), ArgumentMatchers.eq(CLASS));
        Assert.assertEquals(level, method.getSideEffectLevel());
    }

    @Test
    public void unsafeClassNameReturnsClassAndHasNoSideEffects() throws Exception {
        String className = "Lunsafe/Class;";
        SideEffect.Level level = Level.NONE;
        VirtualClass virtualClass = setupClass(className, false, level);
        String binaryClassName = "unsafe.Class";
        Mockito.doReturn(java_lang_Class_forName_Test.OBJECT_CLASS).when(classLoader).loadClass(binaryClassName);
        HeapItem item = new HeapItem(binaryClassName, CommonTypes.STRING);
        Mockito.when(mState.peekParameter(0)).thenReturn(item);
        method.execute(vm, op, context);
        Mockito.verify(mState, Mockito.times(1)).assignReturnRegister(ArgumentMatchers.eq(java_lang_Class_forName_Test.OBJECT_CLASS), ArgumentMatchers.eq(CLASS));
        Mockito.verify(context, Mockito.times(1)).staticallyInitializeClassIfNecessary(virtualClass);
        Assert.assertEquals(level, method.getSideEffectLevel());
    }

    @Test
    public void strongSideEffectsClassNameReturnsClassAndHasStrongSideEffects() throws Exception {
        String className = "Lstrong/Class;";
        SideEffect.Level level = Level.STRONG;
        VirtualClass virtualClass = setupClass(className, false, level);
        String binaryClassName = "strong.Class";
        Mockito.doReturn(java_lang_Class_forName_Test.OBJECT_CLASS).when(classLoader).loadClass(binaryClassName);
        HeapItem item = new HeapItem(binaryClassName, CommonTypes.STRING);
        Mockito.when(mState.peekParameter(0)).thenReturn(item);
        method.execute(vm, op, context);
        Mockito.verify(mState, Mockito.times(1)).assignReturnRegister(ArgumentMatchers.eq(java_lang_Class_forName_Test.OBJECT_CLASS), ArgumentMatchers.eq(CLASS));
        Mockito.verify(context, Mockito.times(1)).staticallyInitializeClassIfNecessary(virtualClass);
        Assert.assertEquals(level, method.getSideEffectLevel());
    }

    @Test
    public void unknownClassNameThrowsExceptionAndAssignsNothing() throws Exception {
        String className = "Lunknown/Class;";
        SideEffect.Level level = Level.NONE;
        VirtualClass virtualClass = setupClass(className, false, level);
        String binaryName = "unknown.Class";
        HeapItem item = new HeapItem(binaryName, CommonTypes.STRING);
        Mockito.when(mState.peekParameter(0)).thenReturn(item);
        Mockito.when(classManager.getVirtualClass(className)).thenThrow(new RuntimeException());
        Throwable exception = Mockito.mock(Throwable.class);
        Mockito.when(exceptionFactory.build(ArgumentMatchers.eq(op), ArgumentMatchers.eq(ClassNotFoundException.class), ArgumentMatchers.eq(binaryName))).thenReturn(exception);
        method.execute(vm, op, context);
        Assert.assertEquals(1, method.getExceptions().size());
        Throwable actualException = method.getExceptions().iterator().next();
        Assert.assertEquals(exception, actualException);
        Mockito.verify(mState, Mockito.times(0)).assignReturnRegister(ArgumentMatchers.any(UnknownValue.class), ArgumentMatchers.eq(CLASS));
        Assert.assertEquals(NONE, method.getSideEffectLevel());
    }
}

