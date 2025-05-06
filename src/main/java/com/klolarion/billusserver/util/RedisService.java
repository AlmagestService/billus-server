package com.klolarion.billusserver.util;


import com.klolarion.billusserver.exception.r401.AuthFailureException;
import com.klolarion.billusserver.exception.r403.AccessDeniedException;
import com.klolarion.billusserver.exception.r500.RedisSessionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import static com.klolarion.billusserver.util.constants.RedisKey.FAILURE_PREFIX;
import static com.klolarion.billusserver.util.constants.RedisKey.REFRESH_TOKEN_PREFIX;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    private static final int LOCK_DURATION = 10; // 잠금 지속 시간 MIN
    private static final int MAX_FAILURE_COUNT = 5;
    private static final String LOCKED_STATUS = "locked";

    /**
     * Redis 키 생성
     */
    private String generateRedisKey(String prefix, String id) {
        return prefix + ":" + id;
    }

    /**
     * 실패 카운터 처리 통합 메서드
     * 1. Redis 조회
     * 2. locked 상태 확인
     * 3. 없으면 카운터 생성 (값=1)
     * 4. 카운터 증가 처리 (1-4 사이)
     * 5. 최대 실패시 잠금 처리 (count=5)
     */
    public void authFailureCountHandler(String id) {
        String key = generateRedisKey(FAILURE_PREFIX, id);
        int count = 0;

        // 1. Redis 조회
        String value = redisTemplate.opsForValue().get(key);


        // 2. locked 상태 확인
        if (LOCKED_STATUS.equals(value)) {
            log.warn("계정 잠금 상태: id={}", id);
            throw new AccessDeniedException("계정이 잠금 상태입니다. " + LOCK_DURATION + "분 후에 다시 시도해주세요.");
        }

        // 3. 값이 없으면 카운터 생성 (값=1)
        if (value == null) {
            count = 1;
            redisTemplate.opsForValue().set(key, String.valueOf(count), LOCK_DURATION, TimeUnit.MINUTES);
            log.debug("실패 카운터 생성: id={}, count=1", id);
            throw new AuthFailureException("인증 정보 불일치. Count : " + count, count);
        }

        // 4. 현재 카운트 확인 및 증가
        count = Integer.parseInt(value);
        count++;

        // 기존 TTL 유지
        Long remainingTtl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        long ttl = (remainingTtl != null && remainingTtl > 0) ? remainingTtl : LOCK_DURATION * 60L;

        // 5. 최대 실패 횟수 도달시 잠금 처리
        if (count >= MAX_FAILURE_COUNT) {
            redisTemplate.opsForValue().set(key, LOCKED_STATUS, LOCK_DURATION, TimeUnit.MINUTES);
            log.warn("계정 잠금 처리: id={}", id);
            throw new AccessDeniedException("인증 시도 횟수를 초과했습니다. " + LOCK_DURATION + "분 후에 다시 시도해주세요.");
        }

        // 증가된 카운트 저장 (기존 TTL 유지)
        redisTemplate.opsForValue().set(key, String.valueOf(count), ttl, TimeUnit.SECONDS);
        log.debug("실패 카운트 증가: id={}, count={}, remainingTtl={}s", id, count, ttl);
        throw new AuthFailureException("인증 정보 불일치. Count : " + count, count);
    }




    //10분안에 5번 오류시 10분간 요청 차단 등록
    public void setLocker(String key, String value){ redisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES);}

    //오류카운터 최초등록 10분 만료
    public void setLockerCounter(String key, String value){ redisTemplate.opsForValue().set(key, value, 10, TimeUnit.MINUTES);}

    //오류카운터 값 수정
    public void updateLockerCounter(String key, String value){ redisTemplate.opsForValue().set(key, value);}

    //비밀번호, otp 오류시 오류카운터 증가. 오류횟수가 5회에 도달하면 locker를 호출해서 등록.
    public int lockerHandler(String key){
        int count = getLockerCount(key);
        //오류횟수가 없으면 새로 세팅. 만료시간 10분
        if(count==9){
            setLockerCounter(key, String.valueOf("1"));
            return 1;
        }
        //오류횟수가 5회 미만일시 1회 추가.
        if(count<5){
            ++count;
            //추가된 오류횟수가 5회일경우 차단등록. 만료시간 10분으로 갱신
            if(count==5) {
                setLocker(key, "locked");
                return count;
            }
            //추가된 오류횟수가 5회미만일 경우 횟수 추가
            updateLockerCounter(key, String.valueOf(count));
            return count;
        }
        return count;
    }

    //오류횟수를 조회한다. 없으면 최초등록을위해 1을 리턴한다. 이미 locked라면 0을 리턴한다.
    public int getLockerCount(String key){
        String result = redisTemplate.opsForValue().get(key);
        if(result==null){
            return 9;
        }else if(result.equals("locked")){
            return 0;
        }else {
            return Integer.parseInt(result);
        }
    }

    /**
     * 상태 삭제 (인증 성공, 로그아웃, 탈퇴)
     */
    public void resetStatus(String id) {
        String failKey = generateRedisKey(FAILURE_PREFIX, id);
        String refreshKey = generateRedisKey(FAILURE_PREFIX, id);
        try {
            redisTemplate.delete(failKey);
            redisTemplate.delete(refreshKey);
            log.debug("실패 카운트 초기화: id={}", id);
        } catch (Exception e) {
            throw new RedisSessionException("사용자 정보 처리 중 오류 발생");
        }
    }

    /**
     * 리프레시 토큰 검증 문자열 저장
     */
    public void setRefreshTokenVerification(String id, String verifyString, long expiration) {
        String key = generateRedisKey(REFRESH_TOKEN_PREFIX, id);
        try {
            redisTemplate.opsForValue().set(key, verifyString, expiration, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            throw new RedisSessionException("토큰 정보 저장 중 오류 발생");
        }
    }

    /**
     * 리프레시 토큰 검증 문자열 조회
     */
    public String getRefreshTokenVerification(String id) {
        String key = generateRedisKey(REFRESH_TOKEN_PREFIX, id);
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            throw new RedisSessionException("토큰 정보 조회 중 오류 발생");
        }
    }

}
