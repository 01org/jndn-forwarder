/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.table.pit;

import com.intel.jnfd.deamon.face.AbstractFace;
import net.named_data.jndn.Interest;

/**
 *
 * @author zht
 */
public class PitInRecord extends PitFaceRecord{

    public PitInRecord(AbstractFace face) {
        super(face);
    }
 
    public void update(Interest interest) {
        super.update(interest);
    }

    public Interest getInterest() {
        return interest;
    }
    
    
    
    private Interest interest;
}
