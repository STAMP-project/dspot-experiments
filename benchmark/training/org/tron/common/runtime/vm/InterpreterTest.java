/**
 * java-tron is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * java-tron is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.tron.common.runtime.vm;


import Program.BadJumpDestinationException;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.tron.common.runtime.vm.program.InternalTransaction;
import org.tron.common.runtime.vm.program.InternalTransaction.TrxType;
import org.tron.common.runtime.vm.program.Program;
import org.tron.common.runtime.vm.program.invoke.ProgramInvokeMockImpl;
import org.tron.core.exception.ContractValidateException;
import org.tron.protos.Protocol.Transaction;


@Slf4j
public class InterpreterTest {
    private ProgramInvokeMockImpl invoke;

    private Program program;

    @Test
    public void testVMException() throws ContractValidateException {
        VM vm = new VM();
        invoke = new ProgramInvokeMockImpl();
        byte[] op = new byte[]{ 91, 96, 0, 86 };
        // 0x5b      - JUMPTEST
        // 0x60 0x00 - PUSH 0x00
        // 0x56      - JUMP to 0
        Transaction trx = Transaction.getDefaultInstance();
        InternalTransaction interTrx = new InternalTransaction(trx, TrxType.TRX_UNKNOWN_TYPE);
        program = new Program(op, invoke, interTrx);
        boolean result = false;
        try {
            while (!(program.isStopped())) {
                vm.step(program);
            } 
        } catch (Program e) {
            result = true;
        }
        Assert.assertTrue(result);
    }

    @Test
    public void JumpSingleOperation() throws ContractValidateException {
        VM vm = new VM();
        invoke = new ProgramInvokeMockImpl();
        byte[] op = new byte[]{ 86 };
        // 0x56      - JUMP
        Transaction trx = Transaction.getDefaultInstance();
        InternalTransaction interTrx = new InternalTransaction(trx, TrxType.TRX_UNKNOWN_TYPE);
        program = new Program(op, invoke, interTrx);
        boolean result = false;
        try {
            while (!(program.isStopped())) {
                vm.step(program);
            } 
        } catch (Program e) {
            // except to get stack too small exception for Jump
            result = true;
        }
        Assert.assertTrue(result);
    }

    @Test
    public void JumpToInvalidDestination() throws ContractValidateException {
        VM vm = new VM();
        invoke = new ProgramInvokeMockImpl();
        byte[] op = new byte[]{ 96, 32, 86 };
        // 0x60      - PUSH1
        // 0x20      - 20
        // 0x56      - JUMP
        Transaction trx = Transaction.getDefaultInstance();
        InternalTransaction interTrx = new InternalTransaction(trx, TrxType.TRX_UNKNOWN_TYPE);
        program = new Program(op, invoke, interTrx);
        boolean result = false;
        try {
            while (!(program.isStopped())) {
                vm.step(program);
            } 
        } catch (Program e) {
            // except to get BadJumpDestinationException for Jump
            Assert.assertTrue(e.getMessage().contains("Operation with pc isn't 'JUMPDEST': PC[32];"));
            result = true;
        }
        Assert.assertTrue(result);
    }

    @Test
    public void JumpToLargeNumberDestination() throws ContractValidateException {
        VM vm = new VM();
        invoke = new ProgramInvokeMockImpl();
        byte[] op = new byte[]{ 100, 127, 127, 127, 127, 127, 86 };
        // 0x60              - PUSH5
        // 0x7F7F7F7F7F      - 547599908735
        // 0x56              - JUMP
        Transaction trx = Transaction.getDefaultInstance();
        InternalTransaction interTrx = new InternalTransaction(trx, TrxType.TRX_UNKNOWN_TYPE);
        program = new Program(op, invoke, interTrx);
        boolean result = false;
        try {
            while (!(program.isStopped())) {
                vm.step(program);
            } 
        } catch (Program e) {
            // except to get BadJumpDestinationException for Jump
            Assert.assertTrue(e.getMessage().contains("Operation with pc isn't 'JUMPDEST': PC[-1];"));
            result = true;
        }
        Assert.assertTrue(result);
    }
}

