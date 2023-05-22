package com.example.uxn_api.config.jwt.oauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.uxn_api.web.user.dto.res.TokenVerifyResult;
import com.example.uxn_common.global.domain.Login;
import com.example.uxn_common.global.domain.staff.Staff;
import com.example.uxn_common.global.domain.staff.StaffAuthority;
import com.example.uxn_common.global.domain.user.User;
import com.example.uxn_common.global.domain.user.UserAuthority;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class JWTUtil {

    private static String secretKey = "SpringBoot-JWTToken-Login";

    @PostConstruct
    protected void init(){
       secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    private static final Algorithm ALGORITHM = Algorithm.HMAC256(secretKey);


    private static final long AUTH_TIME = 30*60; // 30분

//    private static final long AUTH_TIME = 2;
    private static final long REFRESH_TIME = 60*60*24*7; // 1주일

//    private static final long REFRESH_TIME = 2;




    // 토큰 생성
    public static String makeAuthToken(User user){
        Map<String, Object> headerParam = new HashMap<>();
        headerParam.put("typ","JWT");
        String authority = user.getAuthorities().stream().map(UserAuthority::getAuthority).collect(Collectors.toList()).get(0); // ROLE_USER
        return JWT.create()
                .withHeader(headerParam)
                .withSubject(user.getEmail()) // user_id
                .withClaim("authority", authority)
                .withClaim("idx",user.getIdx()) // 회원 정보를 가져오기 위해서 index 값도 넣어준다.
                .withClaim("user_name", user.getUsername())
                .withClaim("user_id", user.getEmail())
                .withClaim("exp", Instant.now().getEpochSecond()+AUTH_TIME) // 토큰 유효 시간 -> Date 클래스 사용 안하고
                .sign(ALGORITHM);
    }

    public static String makeStaffToken(Staff staff){
        Map<String, Object> headerParam = new HashMap<>();
        headerParam.put("typ","JWT");
        String authority = staff.getAuthorities().stream().map(StaffAuthority::getAuthority).collect(Collectors.toList()).get(0); // ROLE_USER
        return JWT.create()
                .withHeader(headerParam)
                .withSubject(staff.getEmail()) // user_id
                .withClaim("idx",staff.getIdx()) // 회원 정보를 가져오기 위해서 index 값도 넣어준다.
                .withClaim("staff_name", staff.getStaffName())
                .withClaim("staff_id", staff.getEmail())
                .withClaim("authority", authority)
                .withClaim("exp", Instant.now().getEpochSecond()+AUTH_TIME) // 토큰 유효 시간 -> Date 클래스 사용 안하고
                .sign(ALGORITHM);
    }

    // 리프레시 토큰 생성
    public static String makeRefreshToken(User  user){
        Map<String, Object> headerParam = new HashMap<>();
        headerParam.put("typ","JWT");
        String authority = user.getAuthorities().stream().map(UserAuthority::getAuthority).collect(Collectors.toList()).get(0);
        return JWT.create()
                .withHeader(headerParam)
                .withSubject(user.getEmail())
                .withClaim("authority", authority)
                .withClaim("idx",user.getIdx())
                .withClaim("user_name", user.getUsername())
                .withClaim("user_id", user.getEmail())
//                .withClaim("exp", Instant.now().getEpochSecond()+REFRESH_TIME)
                .withClaim("exp", Instant.now().getEpochSecond()+REFRESH_TIME)
                .sign(ALGORITHM);
    }

    public static String makeStaffRefreshToken(Staff  staff){
        Map<String, Object> headerParam = new HashMap<>();
        headerParam.put("typ","JWT");
        String authority = staff.getAuthorities().stream().map(StaffAuthority::getAuthority).collect(Collectors.toList()).get(0); // ROLE_USER
        return JWT.create()
                .withHeader(headerParam)
                .withSubject(staff.getEmail())
                .withClaim("idx",staff.getIdx())
                .withClaim("authority", authority)
                .withClaim("staff_name", staff.getStaffName())
                .withClaim("staff_id", staff.getEmail())
//                .withClaim("exp", Instant.now().getEpochSecond()+REFRESH_TIME)
                .withClaim("exp", Instant.now().getEpochSecond()+REFRESH_TIME)
                .sign(ALGORITHM);
    }

    public static TokenVerifyResult verify(String token){ // 토큰 유효성 검사

        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return TokenVerifyResult.builder()
                    .success(true) // 유효 하다면 성공
                    .userId(verify.getSubject()) // 누가 요청했는지
                    .authority(verify.getClaim("authority").asString())
                    .build();
        }catch (Exception e){
            DecodedJWT decode = JWT.require(ALGORITHM).build().verify(token);
            return TokenVerifyResult.builder()
                    .success(false) // 유효 하지 않다면 실패
                    .userId(decode.getSubject())
                    .authority(decode.getClaim("authority").asString())
                    .build();


        }
    }

    public static TokenVerifyResult refreshVerify(String token){ // 토큰 유효성 검사

        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return TokenVerifyResult.builder()
                    .success(true) // 유효 하다면 성공
                    .userId(verify.getSubject()) // 누가 요청했는지
                    .authority(verify.getClaim("authority").asString())
                    .build();
        }catch (Exception e){
            throw new TokenExpiredException("Refresh token expired"); // 리프레시 토큰 만료시

        }
    }

    public static TokenVerifyResult validVerify(String token){ // 토큰 유효성 검사

        try {
            DecodedJWT verify = JWT.require(ALGORITHM).build().verify(token);
            return TokenVerifyResult.builder()
                    .success(true) // 유효 하다면 성공
                    .userId(verify.getSubject()) // 누가 요청했는지
                    .authority(verify.getClaim("authority").asString())
                    .build();
        }catch (Exception e){
            e.printStackTrace();
            throw new TokenExpiredException("Token is not valid"); // 접근한 토큰 기한 만료시.

        }
    }

}
