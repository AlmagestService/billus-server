package com.klolarion.billusserver.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
//email전송 Dto
public class EmailDto {
    @Size(min = 4, max = 4)
    private String code;
    @Size(min = 8,max = 50)
    private String email;
    private String title;
    private String message;
}
