// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import java.lang.String;

privileged aspect DeviceTokenPK_Roo_ToString {
    
    public String DeviceTokenPK.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Token: ").append(getToken()).append(", ");
        sb.append("Type: ").append(getType()).append(", ");
        sb.append("Userno: ").append(getUserno());
        return sb.toString();
    }
    
}
