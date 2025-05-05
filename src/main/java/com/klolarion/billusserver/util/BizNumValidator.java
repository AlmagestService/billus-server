package com.klolarion.billusserver.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class BizNumValidator {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${data-org-api-key}")
    private String DATA_ORG_API_KEY;
    // 국세청 사업자 등록 상태 조회 API URL
    private final String BIZNUM_API_URL = "https://api.odcloud.kr/api/nts-businessman/v1/status?serviceKey=";


    /**
     * 사업자번호 유효성 검증
     * @param bizNum 검증할 사업자번호
     * @throws IllegalArgumentException 유효하지 않은 사업자번호인 경우
     */
    public void validateBizNum(String bizNum) {
        if (bizNum == null || bizNum.length() != 10) {
            throw new IllegalArgumentException("유효하지 않은 사업자번호입니다.");
        }

        // 요청 헤더 설정 (Content-Type JSON)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 Body 설정 (사업자 등록번호 리스트)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("b_no", Collections.singletonList(bizNum));

        // 요청 객체 생성
        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestBody, headers);

        try {
            // API 요청 보내기 (POST)
            ResponseEntity<String> bizNumResponse = restTemplate.exchange(
                    BIZNUM_API_URL + DATA_ORG_API_KEY,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            // 응답값 확인
            if (bizNumResponse.getStatusCode() == HttpStatus.OK) {
                // JSON 응답 파싱
                JsonNode rootNode = objectMapper.readTree(bizNumResponse.getBody());

                // "status" 값을 확인하여 사업자 등록 여부 판단
                JsonNode dataNode = rootNode.path("data");
                if (dataNode.isArray() && dataNode.size() > 0) {
                    String status = dataNode.get(0).path("b_stt").asText();
                    if (!"계속사업자".equals(status)) {
                        throw new IllegalArgumentException("등록되지 않은 사업자번호입니다.");
                    }
                } else {
                    throw new IllegalArgumentException("사업자번호 검증에 실패했습니다.");
                }
            } else {
                throw new IllegalArgumentException("사업자번호 검증에 실패했습니다.");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("사업자번호 검증 중 오류가 발생했습니다.");
        }
    }

    /**
     * HTTP 요청/응답을 통한 사업자번호 검증
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param bizNum 검증할 사업자번호
     */
    public void validateBizNum(HttpServletRequest request, HttpServletResponse response, String bizNum) {
        validateBizNum(bizNum);
    }
}
