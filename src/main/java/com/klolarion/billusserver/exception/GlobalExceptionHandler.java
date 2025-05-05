package com.klolarion.billusserver.exception;

import com.klolarion.billusserver.dto.CommonResponseDto;
import com.klolarion.billusserver.dto.InfoResponseDto;
import com.klolarion.billusserver.exception.r400.BadRequestException;
import com.klolarion.billusserver.exception.r400.InvalidBillDataException;
import com.klolarion.billusserver.exception.r401.AuthFailureException;
import com.klolarion.billusserver.exception.r401.InvalidTokenException;
import com.klolarion.billusserver.exception.r401.UnauthorizedException;
import com.klolarion.billusserver.exception.r403.AccessDeniedException;
import com.klolarion.billusserver.exception.r404.CompanyNotFoundException;
import com.klolarion.billusserver.exception.r404.ResourceNotFoundException;
import com.klolarion.billusserver.exception.r409.ConflictException;
import com.klolarion.billusserver.exception.r409.DuplicateCompanyException;
import com.klolarion.billusserver.exception.r422.ValidationException;
import com.klolarion.billusserver.exception.r500.*;
import com.klolarion.billusserver.exception.r503.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 Bad Request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<?> handleBadRequestException(BadRequestException ex) {
        log.warn("BadRequestException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.builder()
                        .status("400")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("잘못된 요청입니다")
                        .build());
    }

    // 400 Invalid Bill Data
    @ExceptionHandler(InvalidBillDataException.class)
    public ResponseEntity<?> handleInvalidBillDataException(InvalidBillDataException ex) {
        log.warn("InvalidBillDataException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.builder()
                        .status("400")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("잘못된 청구 데이터입니다")
                        .build());
    }

    // 401 Auth Failure
    @ExceptionHandler(AuthFailureException.class)
    public ResponseEntity<?> handleAuthFailureException(AuthFailureException ex) {
        log.warn("AuthFailureException - count: {}, message: {}", ex.getCount(), ex.getMessage());
        
        InfoResponseDto authResponseDto = InfoResponseDto.builder()
                .errCount(String.valueOf(ex.getCount()))
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(CommonResponseDto.builder()
                        .status("400")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("인증에 실패했습니다")
                        .data(authResponseDto)
                        .build());
    }

    // 401 Unauthorized
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("UnauthorizedException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponseDto.builder()
                        .status("401")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("인증되지 않은 요청입니다")
                        .build());
    }

    // 401 Invalid Token
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<?> handleInvalidTokenException(InvalidTokenException ex) {
        log.warn("InvalidTokenException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(CommonResponseDto.builder()
                        .status("401")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("유효하지 않은 토큰입니다")
                        .build());
    }

    // 403 Forbidden
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleForbiddenException(AccessDeniedException ex) {
        log.warn("AccessDeniedException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(CommonResponseDto.builder()
                        .status("403")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("접근이 거부되었습니다")
                        .build());
    }

    // 404 Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("ResourceNotFoundException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseDto.builder()
                        .status("404")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("요청한 리소스를 찾을 수 없습니다")
                        .build());
    }

    // 404 Company Not Found
    @ExceptionHandler(CompanyNotFoundException.class)
    public ResponseEntity<?> handleCompanyNotFoundException(CompanyNotFoundException ex) {
        log.warn("CompanyNotFoundException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(CommonResponseDto.builder()
                        .status("404")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("요청한 회사를 찾을 수 없습니다")
                        .build());
    }

    // 409 Conflict
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<?> handleConflictException(ConflictException ex) {
        log.warn("ConflictException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponseDto.builder()
                        .status("409")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("리소스 충돌이 발생했습니다")
                        .build());
    }

    // 409 Duplicate Company
    @ExceptionHandler(DuplicateCompanyException.class)
    public ResponseEntity<?> handleDuplicateCompanyException(DuplicateCompanyException ex) {
        log.warn("DuplicateCompanyException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(CommonResponseDto.builder()
                        .status("409")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("이미 존재하는 회사입니다")
                        .build());
    }

    // 422 Validation Error
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        log.warn("ValidationException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(CommonResponseDto.builder()
                        .status("422")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("입력값 검증에 실패했습니다")
                        .build());
    }

    // 500 Internal Server Error
    @ExceptionHandler(ServerException.class)
    public ResponseEntity<?> handleServerException(ServerException ex) {
        log.error("ServerException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("서버 내부 오류가 발생했습니다")
                        .build());
    }

    // 503 Service Unavailable
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<?> handleServiceUnavailableException(ServiceUnavailableException ex) {
        log.error("ServiceUnavailableException - {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(CommonResponseDto.builder()
                        .status("503")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("서비스를 일시적으로 사용할 수 없습니다")
                        .build());
    }

    // 500 Data Access Fail
    @ExceptionHandler(DataAccessFailException.class)
    public ResponseEntity<?> handleDataAccessFailException(DataAccessFailException ex) {
        log.error("DataAccessFailException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("데이터베이스 접근 오류가 발생했습니다")
                        .build());
    }

    // 500 Code Generation
    @ExceptionHandler(CodeGenerationException.class)
    public ResponseEntity<?> handleCodeGenerationException(CodeGenerationException ex) {
        log.error("CodeGenerationException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("코드 생성 중 오류가 발생했습니다")
                        .build());
    }

    // 500 Firebase Notification
    @ExceptionHandler(FirebaseNotificationException.class)
    public ResponseEntity<?> handleFirebaseNotificationException(FirebaseNotificationException ex) {
        log.error("FirebaseNotificationException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("푸시 알림 전송 중 오류가 발생했습니다")
                        .build());
    }

    // 500 Mail Send
    @ExceptionHandler(MailSendException.class)
    public ResponseEntity<?> handleMailSendException(MailSendException ex) {
        log.error("MailSendException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("이메일 전송 중 오류가 발생했습니다")
                        .build());
    }

    // 500 Redis Session
    @ExceptionHandler(RedisSessionException.class)
    public ResponseEntity<?> handleRedisSessionException(RedisSessionException ex) {
        log.error("RedisSessionException - {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message(ex.getMessage())
                        .rspCode("FAILURE")
                        .rspMessage("세션 처리 중 오류가 발생했습니다")
                        .build());
    }

    // 포괄적인 서버 오류 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleServerError(Exception ex) {
        log.error("Unhandled Exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(CommonResponseDto.builder()
                        .status("500")
                        .message("서버 내부 오류가 발생했습니다")
                        .rspCode("FAILURE")
                        .rspMessage("서버 내부 오류가 발생했습니다")
                        .build());
    }
}
