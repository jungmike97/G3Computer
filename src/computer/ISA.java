/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computer;

import computer.ComputerExceptions.DeviceFailureException;
import computer.ComputerExceptions.InterruptException;
import computer.ComputerExceptions.UnexpectedInstructionException;
import computer.ComputerExceptions.HaltException;
import computer.ComputerExceptions.MemoryAddressException;
import computer.OperationInterface.ControlFlowOperations;
import computer.OperationInterface.ArithmeticLogicOperations;
import computer.OperationInterface.DataHandlingOperations;

/**
 *
 * @author Administrator
 */
public class ISA {

    // Components of the intruction
    private final int opcode;
    private final int r;
    private final int ix;
    private final int i;
    private final int address;

    private final boolean ignoreFetchOperand;

    // Decode
    public ISA(int instruction) {
        this.opcode = (instruction & 0x0000fc00) >> 10;

        switch (this.opcode) {
            // HLT
            case 0:
            // TRAP
            case 036:
            // AIR
            case 06:
            // SIR
            case 07:
            // RFS
            case 015:
            // MLT
            case 020:
            // DVD
            case 021:
            // TRR
            case 022:
            // AND
            case 023:
            // ORR
            case 024:
            // NOT
            case 025:
            // CMT
            case 026:
            // CMB
            case 027:
            // SRC
            case 031:
            // RRC
            case 032:
            // LE
            case 033:
            // GE
            case 034:
            // ET
            case 035:
            // LIX
            case 043:
            // IN
            case 061:
            // OUT
            case 062:
            // CHK
            case 063:
            // GETS
            case 064:
            // PUTS
            case 065:
                this.ignoreFetchOperand = true;
                break;
            default:
                this.ignoreFetchOperand = false;
        }

        this.r = (instruction & 0x00000300) >> 8;
        this.ix = (instruction & 0x000000c0) >> 6;
        this.i = (instruction & 0x00000020) >> 5;
        this.address = instruction & 0x0000001f;
    }

    // Execute
    public Register operate(DataHandlingOperations dho, ArithmeticLogicOperations alo, ControlFlowOperations cfo)
            throws InterruptException, HaltException, UnexpectedInstructionException, DeviceFailureException, MemoryAddressException {
        switch (this.opcode) {
            case 0:
                return cfo.HLT(this);
            case 036:
                return cfo.TRAP(this);

            case 01:
                return dho.LDR(this);
            case 02:
                return dho.STR(this);
            case 03:
                return dho.LDA(this);
            case 041:
                return dho.LDX(this);
            case 042:
                return dho.STX(this);
            case 043:
                return dho.LIX(this);
            case 061:
                return dho.IN(this);
            case 062:
                return dho.OUT(this);
            case 063:
                return dho.CHK(this);
            case 064:
                return dho.GETS(this);
            case 065:
                return dho.PUTS(this);

            case 04:
                return alo.AMR(this);
            case 05:
                return alo.SMR(this);
            case 06:
                return alo.AIR(this);
            case 07:
                return alo.SIR(this);
            case 020:
                return alo.MLT(this);
            case 021:
                return alo.DVD(this);
            case 022:
                return alo.TRR(this);
            case 023:
                return alo.AND(this);
            case 024:
                return alo.ORR(this);
            case 025:
                return alo.NOT(this);
            case 026:
                return alo.CMT(this);
            case 027:
                return alo.CMB(this);
            case 031:
                return alo.SRC(this);
            case 032:
                return alo.RRC(this);
            case 033:
                return alo.LE(this);
            case 034:
                return alo.GE(this);
            case 035:
                return alo.ET(this);

            case 010:
                return cfo.JZ(this);
            case 011:
                return cfo.JNE(this);
            case 012:
                return cfo.JCC(this);
            case 013:
                return cfo.JMA(this);
            case 014:
                return cfo.JSR(this);
            case 015:
                return cfo.RFS(this);
            case 016:
                return cfo.SOB(this);
            case 017:
                return cfo.JGE(this);
            // Unexpected instruction occurred.
            default:
                throw new UnexpectedInstructionException();
        }
    }

    public int getR() {
        return this.r;
    }

    public int getIX() {
        return this.ix;
    }

    public int getI() {
        return this.i;
    }

    public int getAddress() {
        return this.address;
    }

    public boolean canSkipFetchOperand() {
        return this.ignoreFetchOperand;
    }
}
