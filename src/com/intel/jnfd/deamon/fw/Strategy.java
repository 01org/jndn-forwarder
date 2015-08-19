/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.intel.jnfd.deamon.fw;

import net.named_data.jndn.Name;

/**
 *
 * @author zht
 */
public class Strategy {

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }
    
    // TODO: implement this
    @Override
    public boolean equals(Object o) {
        return false;
    }
    
    private Name name;
}
