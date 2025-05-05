package com.klolarion.billusserver.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class ClaimsGenerator {

    public HashMap<String, Object> createExtraClaims() {
        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("clr", "ADMIN");
        extraClaims.put("cln", "BILLUS");
        return extraClaims;
    }

    public HashMap<String, Object> createCsrfClaims(String tokenBody) {
        HashMap<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("cft", tokenBody);
        return extraClaims;
    }
}
