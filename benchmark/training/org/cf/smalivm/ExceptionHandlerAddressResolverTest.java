package org.cf.smalivm;


import java.util.LinkedList;
import java.util.List;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualMethod;
import org.jf.dexlib2.builder.BuilderExceptionHandler;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.junit.Assert;
import org.junit.Test;


public class ExceptionHandlerAddressResolverTest {
    private static final String EXCEPTION1 = "Ljava/lang/Exception";

    private static final String EXCEPTION2 = "Ljava/lang/RuntimeException;";

    private static final String EXCEPTION3 = "Ljava/lang/NullPointerException;";

    private static VirtualMethod METHOD;

    private List<BuilderTryBlock> tryBlocks;

    private ClassManager classManager;

    private VirtualClass exceptionClass1;

    private VirtualClass exceptionClass2;

    private VirtualClass exceptionClass3;

    @Test
    public void overlappingTryBlocksWithMoreThanOneValidHandlerResolvesNearestAncestor() {
        int currentAddress = 2;
        int tryStartAddress1 = 1;
        int tryStartAddress2 = 2;
        int tryCodeUnits = 10;
        int handlerCodeAddress1 = 20;
        int handlerCodeAddress2 = 30;
        List<BuilderExceptionHandler> handlers1 = new LinkedList<BuilderExceptionHandler>();
        handlers1.add(ExceptionHandlerAddressResolverTest.buildHandler(handlerCodeAddress1, exceptionClass1));
        tryBlocks.add(ExceptionHandlerAddressResolverTest.buildTryBlock(tryStartAddress1, tryCodeUnits, handlers1));
        List<BuilderExceptionHandler> handlers2 = new LinkedList<BuilderExceptionHandler>();
        handlers2.add(ExceptionHandlerAddressResolverTest.buildHandler(handlerCodeAddress2, exceptionClass2));
        tryBlocks.add(ExceptionHandlerAddressResolverTest.buildTryBlock(tryStartAddress2, tryCodeUnits, handlers2));
        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, ExceptionHandlerAddressResolverTest.METHOD);
        int actual = exceptionResolver.resolve(ExceptionHandlerAddressResolverTest.EXCEPTION3, currentAddress);
        Assert.assertEquals(handlerCodeAddress2, actual);
    }

    @Test
    public void overlappingTryBlocksWithOneValidHandler() {
        int currentAddress = 2;
        int tryStartAddress1 = 1;
        int tryStartAddress2 = 2;
        int tryCodeUnits = 10;
        int handlerCodeAddress1 = 20;
        int handlerCodeAddress2 = 30;
        List<BuilderExceptionHandler> handlers1 = new LinkedList<BuilderExceptionHandler>();
        handlers1.add(ExceptionHandlerAddressResolverTest.buildHandler(handlerCodeAddress1, exceptionClass1));
        tryBlocks.add(ExceptionHandlerAddressResolverTest.buildTryBlock(tryStartAddress1, tryCodeUnits, handlers1));
        List<BuilderExceptionHandler> handlers2 = new LinkedList<BuilderExceptionHandler>();
        handlers2.add(ExceptionHandlerAddressResolverTest.buildHandler(handlerCodeAddress2, exceptionClass2));
        tryBlocks.add(ExceptionHandlerAddressResolverTest.buildTryBlock(tryStartAddress2, tryCodeUnits, handlers2));
        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, ExceptionHandlerAddressResolverTest.METHOD);
        String name = exceptionClass1.getName();
        int actual = exceptionResolver.resolve(name, currentAddress);
        Assert.assertEquals(handlerCodeAddress1, actual);
    }

    @Test
    public void simpleExceptionResolvedCorrectly() {
        int currentAddress = 1;
        int tryStartAddress = 1;
        int tryCodeUnits = 10;
        int handlerCodeAddress = 20;
        List<BuilderExceptionHandler> handlers = new LinkedList<BuilderExceptionHandler>();
        handlers.add(ExceptionHandlerAddressResolverTest.buildHandler(handlerCodeAddress, exceptionClass1));
        tryBlocks.add(ExceptionHandlerAddressResolverTest.buildTryBlock(tryStartAddress, tryCodeUnits, handlers));
        ExceptionHandlerAddressResolver exceptionResolver = new ExceptionHandlerAddressResolver(classManager, ExceptionHandlerAddressResolverTest.METHOD);
        String name = exceptionClass1.getName();
        int actual = exceptionResolver.resolve(name, currentAddress);
        Assert.assertEquals(handlerCodeAddress, actual);
    }
}

