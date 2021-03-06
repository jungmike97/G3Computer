/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computer;

import computer.ComputerExceptions.UnexpectedInstructionException;
import computer.OperationInterface.ArithmeticLogicOperations;

/**
 *
 * @author Administrator
 */
public class ALU implements ArithmeticLogicOperations {

    private final ProcessorRegisters registers;

    public ALU(ProcessorRegisters registers) {
        this.registers = registers;
    }

    @Override
    public Register AMR(ISA instruction) {
        // IRR = GPR[r] + c(EA), where c(EA) == MBR
        this.registers.irr.setContent(this.registers.gpr[instruction.getR()].getContent() + this.registers.mbr.getContent());
        // GPR[r]
        return this.registers.gpr[instruction.getR()];
    }

    @Override
    public Register SMR(ISA instruction) {
        // IRR = GPR[r] - c(EA), where c(EA) == MBR
        this.registers.irr.setContent(this.registers.gpr[instruction.getR()].getContent() - this.registers.mbr.getContent());
        // GPR[r]
        return this.registers.gpr[instruction.getR()];
    }

    @Override
    public Register AIR(ISA instruction) {
        // IRR = GPR[r] + Immed, where Immed == (IX, I, Address)
        int immed = instruction.getIX() << 6 | instruction.getI() << 5 | instruction.getAddress();
        this.registers.irr.setContent(this.registers.gpr[instruction.getR()].getContent() + immed);
        // GPR[r]
        return this.registers.gpr[instruction.getR()];
    }

    @Override
    public Register SIR(ISA instruction) {
        // IRR = GPR[r] - Immed, where Immed == (IX, I, Address)
        int immed = instruction.getIX() << 6 | instruction.getI() << 5 | instruction.getAddress();
        this.registers.irr.setContent(this.registers.gpr[instruction.getR()].getContent() - immed);
        // GPR[r]
        return this.registers.gpr[instruction.getR()];
    }

    @Override
    public Register MLT(ISA instruction) {
        // rx, ry must be 0 or 2 (Confirmation is needed here).
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int res = this.registers.gpr[rx].getContent() * this.registers.gpr[ry].getContent();
        // GPR[rx] = high order bits
        this.registers.gpr[rx].setContent(res >> 16);
        // GPR[rx + 1] = low order bits. So, the result is ACTUALLY stored in GPR[rx + 1].
        this.registers.gpr[rx + 1].setContent(res & 0x0000ffff);
        // cc(0) = 1 if GPR[rx] != 0
        if (this.registers.gpr[rx].getContent() != 0) {
            this.registers.irr.setContent(this.registers.cc.getContent() | 1);
        } else {
            this.registers.irr.setContent(this.registers.cc.getContent());
        }
        // CC
        return this.registers.cc;
    }

    @Override
    public Register DVD(ISA instruction) {
        // rx, ry must be 0 or 2 (Confirmation is needed here).
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        // cc(2) = 1 if GPR[ry] == 0
        if (y == 0) {
            this.registers.irr.setContent(this.registers.cc.getContent() | 4);
        } else {
            this.registers.gpr[rx].setContent(x / y);
            this.registers.gpr[rx + 1].setContent(x % y);
            this.registers.irr.setContent(this.registers.cc.getContent());
        }
        // CC
        return this.registers.cc;
    }

    @Override
    public Register TRR(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        // cc(3) = 1 if GPR[rx] == GPR[ry] else 0
        if (x == y) {
            this.registers.irr.setContent(this.registers.cc.getContent() | 8);
        } else {
            this.registers.irr.setContent(this.registers.cc.getContent() & 0xfffffff7);
        }
        // CC
        return this.registers.cc;
    }

    @Override
    public Register AND(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        boolean x = this.registers.gpr[rx].getContent() != 0;
        boolean y = this.registers.gpr[ry].getContent() != 0;
        // IRR = GPR[rx] AND GPR[ry]
        this.registers.irr.setContent(x && y ? 1 : 0);
        // GPR[rx]
        return this.registers.gpr[rx];
    }

    @Override
    public Register ORR(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        boolean x = this.registers.gpr[rx].getContent() != 0;
        boolean y = this.registers.gpr[ry].getContent() != 0;
        // IRR = GPR[rx] OR GPR[ry]
        this.registers.irr.setContent(x || y ? 1 : 0);
        // GPR[rx]
        return this.registers.gpr[rx];
    }

    @Override
    public Register NOT(ISA instruction) {
        int rx = instruction.getR();
        boolean x = this.registers.gpr[rx].getContent() != 0;
        // IRR = NOT GPR[rx]
        this.registers.irr.setContent(!x ? 1 : 0);
        // GPR[rx]
        return this.registers.gpr[rx];
    }

    @Override
    public Register SRC(ISA instruction) {
        boolean left = (instruction.getIX() & 1) == 1;
        boolean logical = (instruction.getIX() >> 1 & 1) == 1;
        int count = instruction.getAddress() & 0x0000000f;
        int res = this.registers.gpr[instruction.getR()].getContent();
        if (left) {
            res <<= count;
        } else {
            // Most Significant Bit
            int msb = res >> 15;
            if (logical || msb == 0) {
                res >>= count;
            } else // Right shift and replicate the original leftmost sign bit, "1".
            {
                res = (res >> count) + (((1 << 16 - count) - 1) ^ 0x0000ffff);
            }
        }
        this.registers.irr.setContent(res);
        return this.registers.gpr[instruction.getR()];
    }

    @Override
    public Register RRC(ISA instruction) {
        boolean left = (instruction.getIX() & 1) == 1;
        int count = instruction.getAddress() & 0x0000000f;
        int res = this.registers.gpr[instruction.getR()].getContent();
        if (left) // Left shift and copy the original left part to the right
        {
            res = ((res << count) + ((res >> 16 - count) & ((1 << count) - 1))) & 0x0000ffff;
        } else // right shift and copy the original right part to the left
        {
            res = (res >> count) + ((res & ((1 << count) - 1)) << 16 - count);
        }
        this.registers.irr.setContent(res);
        return this.registers.gpr[instruction.getR()];
    }

    @Override
    public Register CMT(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        this.registers.irr.setContent(Math.max(x, y));
        return this.registers.gpr[rx];
    }

    @Override
    public Register CMB(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        this.registers.irr.setContent(Math.min(x, y));
        return this.registers.gpr[rx];
    }

    @Override
    public Register LE(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int rz = instruction.getAddress() & 0x3;
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        this.registers.irr.setContent(x <= y ? 1 : 0);
        return this.registers.gpr[rz];
    }

    @Override
    public Register GE(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int rz = instruction.getAddress() & 0x3;
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        this.registers.irr.setContent(x >= y ? 1 : 0);
        return this.registers.gpr[rz];
    }

    @Override
    public Register ET(ISA instruction) {
        int rx = instruction.getR();
        int ry = instruction.getIX();
        int rz = instruction.getAddress() & 0x3;
        int x = this.registers.gpr[rx].getContent();
        int y = this.registers.gpr[ry].getContent();
        this.registers.irr.setContent(x == y ? 1 : 0);
        return this.registers.gpr[rz];
    }

    @Override
    public Register FADD(ISA instruction) throws UnexpectedInstructionException {
        int fr = instruction.getR();
        if (fr > 1) {
            throw new UnexpectedInstructionException();
        }

        FloatNumber fn1 = new FloatNumber(this.registers.fpr[fr].getContent());
        FloatNumber fn2 = new FloatNumber(this.registers.mbr.getContent());
        FloatNumber result = fn1.add(fn2);
        if (result.isFlowed()) {
            this.registers.cc.setContent(this.registers.cc.getContent() | 1);
        }
        this.registers.irr.setContent(result.getBits());
        // FR[fr]
        return this.registers.fpr[fr];
    }

    @Override
    public Register FSUB(ISA instruction) throws UnexpectedInstructionException {
        int fr = instruction.getR();
        if (fr > 1) {
            throw new UnexpectedInstructionException();
        }

        FloatNumber fn1 = new FloatNumber(this.registers.fpr[fr].getContent());
        FloatNumber fn2 = new FloatNumber(this.registers.mbr.getContent());
        FloatNumber result = fn1.sub(fn2);
        if (result.isFlowed()) {
            this.registers.cc.setContent(this.registers.cc.getContent() | 2);
        }
        this.registers.irr.setContent(result.getBits());
        // FR[fr]
        return this.registers.fpr[fr];
    }

    @Override
    public Register CNVRT(ISA instruction) {
        int f = this.registers.gpr[instruction.getR()].getContent();
        if (f == 0) {
            this.registers.irr.setContent(new FloatNumber(this.registers.mbr.getContent()).toInteger());
            return this.registers.gpr[instruction.getR()];
        } else {
            this.registers.irr.setContent(new FloatNumber(this.registers.mbr.getContent()).getBits());
            return this.registers.fpr[0];
        }
    }
}
