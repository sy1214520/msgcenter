// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import java.lang.String;
import javax.persistence.Embeddable;

privileged aspect DeviceTokenPK_Roo_Identifier {
    
    declare @type: DeviceTokenPK: @Embeddable;
    
    public DeviceTokenPK.new(String userno, String token, String type) {
        super();
        this.userno = userno;
        this.token = token;
        this.type = type;
    }

    private DeviceTokenPK.new() {
        super();
    }

    public String DeviceTokenPK.getUserno() {
        return this.userno;
    }
    
    public String DeviceTokenPK.getToken() {
        return this.token;
    }
    
    public String DeviceTokenPK.getType() {
        return this.type;
    }
    
}
