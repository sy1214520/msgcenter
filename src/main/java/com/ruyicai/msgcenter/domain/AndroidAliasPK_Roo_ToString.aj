// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.domain;

import java.lang.String;

privileged aspect AndroidAliasPK_Roo_ToString {
    
    public String AndroidAliasPK.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PkgName: ").append(getPkgName()).append(", ");
        sb.append("Userno: ").append(getUserno());
        return sb.toString();
    }
    
}
