/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package computer;

/**
 *
 * @author Administrator
 */
public interface DataHandlingOperations {
    public Register LDR(ISA instruction);
    public Register STR(ISA instruction);
    public Register LDA(ISA instruction);
    public Register LDX(ISA instruction);
    public Register STX(ISA instruction);
}
