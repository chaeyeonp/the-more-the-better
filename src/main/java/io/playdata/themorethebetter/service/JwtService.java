package io.playdata.themorethebetter.service;

import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.playdata.themorethebetter.domain.Member;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtService {
	@Value("${jwt.salt}") //application.properties의 설정정보를 key를 기반으로 보유하게 되는 변수 
	private String salt;
	
	@Value("${jwt.expmin}")
	private Long expireMin;
	
	
	/**
	 * 로그인 성공시 사용자 정보를 기반으로 JWTToken을 생성해서 반환
	 * @param user (불변 객체 - 수정하지 않을거라면 final권장)
	 * @return
	 */
	public String create(final Member user) {
		
		System.out.println("JwtService의 create()----");
		log.trace("time : {} ", expireMin);
		
		final JwtBuilder builder = Jwts.builder();
		
		builder.setHeaderParam("typ", "JWT");
		
		//토큰 제목설정, 유효기간, 담고 싶은 정보
		builder.setSubject("로그인 토큰").setExpiration(new Date(System.currentTimeMillis() + 100*60*expireMin)).
		claim("Member", user);
		
		//signature - secret key를 이용한 암호화
		builder.signWith(SignatureAlgorithm.HS256, salt.getBytes());
		
		//마지막 직렬화 처리 : 네트워크 상에서 데이터 손실없이 전송하는 구조로 구성 
		final String jwt = builder.compact();
		log.debug("토큰 발행 : {} ", jwt);
		return jwt;
	}
	
	/**
	 * 전달받은 토큰이 제대로 생성된 것인지 여부 확인
	 * 문제 발생시 RuntimeException 발생
	 * @param jwt
	 */
	public void checkValid(String jwt) {
		log.info("토큰 검증 : {} ", jwt);
		Jwts.parser().setSigningKey(salt.getBytes()).parseClaimsJws(jwt);
	}
	
	/**
	 * jwt 토큰을 분석 후에 필요한 정보 반환
	 * 토큰 유효성 검증도 동반
	 * @param jwt
	 * @return
	 */
	public Map<String, Object> get(String jwt){
		Jws<Claims> claims = null;
		
		try {
			claims = Jwts.parser().setSigningKey(salt.getBytes()).parseClaimsJws(jwt);
			
		}catch(final Exception e) {
			throw new RuntimeException();
		}
		log.trace("claims : {} ", claims);
		
		//Claims은 Map의 구현체
		return claims.getBody();
	}
}
