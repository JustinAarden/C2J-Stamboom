/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package stamboom.storage;

/**
 *
 * @author Justin
 */
public class PersonInFamily {
    public int PERSONNR;
    public int FAMILYNR;
    
    public PersonInFamily(int iPNR, int iFNR)
    {
        this.PERSONNR = iPNR;
        this.FAMILYNR = iFNR;
    }
}
