// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.msgcenter.controller.dto;

import com.ruyicai.msgcenter.controller.dto.LetterDTO;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect LetterDTO_Roo_Json {
    
    public String LetterDTO.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static LetterDTO LetterDTO.fromJsonToLetterDTO(String json) {
        return new JSONDeserializer<LetterDTO>().use(null, LetterDTO.class).deserialize(json);
    }
    
    public static String LetterDTO.toJsonArray(Collection<LetterDTO> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<LetterDTO> LetterDTO.fromJsonArrayToLetterDTO(String json) {
        return new JSONDeserializer<List<LetterDTO>>().use(null, ArrayList.class).use("values", LetterDTO.class).deserialize(json);
    }
    
}