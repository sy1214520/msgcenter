// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.controller.dto;

import com.ruyicai.lottery.domain.Tuserinfo;
import com.ruyicai.msgcenter.domain.Letter;

privileged aspect LetterDTO_Roo_JavaBean {
    
    public Letter LetterDTO.getLetter() {
        return this.letter;
    }
    
    public void LetterDTO.setLetter(Letter letter) {
        this.letter = letter;
    }
    
    public Tuserinfo LetterDTO.getFromTuserinfo() {
        return this.fromTuserinfo;
    }
    
    public void LetterDTO.setFromTuserinfo(Tuserinfo fromTuserinfo) {
        this.fromTuserinfo = fromTuserinfo;
    }
    
    public Tuserinfo LetterDTO.getToTuserinfo() {
        return this.toTuserinfo;
    }
    
    public void LetterDTO.setToTuserinfo(Tuserinfo toTuserinfo) {
        this.toTuserinfo = toTuserinfo;
    }
    
}