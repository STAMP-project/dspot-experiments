package com.navercorp.pinpoint.profiler.instrument;


import com.navercorp.pinpoint.common.util.ClassLoaderUtils;
import com.navercorp.pinpoint.profiler.util.BytecodeUtils;
import com.navercorp.pinpoint.profiler.util.JavaAssistUtils;
import java.util.Collections;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


/**
 *
 *
 * @author Woonduk Kang(emeroad)
 */
@RunWith(MockitoJUnitRunner.class)
public class BytecodeDumpServiceTest {
    private final String classInternalName = JavaAssistUtils.javaNameToJvmName("java.lang.String");

    @Mock
    private ASMBytecodeDisassembler disassembler;

    @InjectMocks
    private BytecodeDumpService bytecodeDumpService = new ASMBytecodeDumpService(true, true, true, Collections.singletonList(classInternalName));

    @InjectMocks
    private BytecodeDumpService disableBytecodeDumpService = new ASMBytecodeDumpService(false, false, false, Collections.singletonList(classInternalName));

    @Test
    public void dumpBytecode() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        byte[] classFile = BytecodeUtils.getClassFile(classLoader, classInternalName);
        bytecodeDumpService.dumpBytecode("testDump", classInternalName, classFile, classLoader);
        verify(this.disassembler, times(1)).dumpBytecode(classFile);
        verify(this.disassembler, times(1)).dumpVerify(classFile, classLoader);
        verify(this.disassembler, times(1)).dumpASM(classFile);
    }

    @Test
    public void dumpBytecode_disable() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        byte[] classFile = BytecodeUtils.getClassFile(classLoader, classInternalName);
        disableBytecodeDumpService.dumpBytecode("disableTestDump", classInternalName, classFile, classLoader);
        verify(this.disassembler, never()).dumpBytecode(classFile);
        verify(this.disassembler, never()).dumpVerify(classFile, classLoader);
        verify(this.disassembler, never()).dumpASM(classFile);
    }

    @Test
    public void dumpBytecode_filter() throws Exception {
        ClassLoader classLoader = ClassLoaderUtils.getDefaultClassLoader();
        byte[] classFile = BytecodeUtils.getClassFile(classLoader, classInternalName);
        bytecodeDumpService.dumpBytecode("testDump", "invalidName", classFile, classLoader);
        verify(this.disassembler, never()).dumpBytecode(classFile);
        verify(this.disassembler, never()).dumpVerify(classFile, classLoader);
        verify(this.disassembler, never()).dumpASM(classFile);
    }
}

