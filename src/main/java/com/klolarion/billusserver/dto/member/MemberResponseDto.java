package com.klolarion.billusserver.dto.member;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 응답 Dto
 * */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponseDto {
    private String memberId;
    private String almagestId;
    private String memberName;
    private String email;
    private String tel;
    private String isBanned;
    private String lastUpdateDate;
    private String createdAt;
    private String companyId;
    private String companyName;
    private String alreadyApplied;
}
