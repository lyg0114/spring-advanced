package org.example.expert.config.aop;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Aspect
@Component
public class AopAspect {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Around("@annotation(org.example.expert.config.aop.AdminLog)")
	public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
		// HttpServletRequest 가져오기
		ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = Objects.requireNonNull(attributes).getRequest();

		printRequest(joinPoint, request);

		// 메서드 실행
		Object result = joinPoint.proceed();

		// Response Body 로깅
		printResponse(result);

		return result;
	}

	@SneakyThrows
	private void printRequest(ProceedingJoinPoint joinPoint, HttpServletRequest request) {
		// 요청 헤더 정보 로깅
		Long userId = (Long)request.getAttribute("userId");
		String email = (String)request.getAttribute("email");
		String userRole = (String)request.getAttribute("userRole");
		LocalDateTime requestTime = LocalDateTime.now();
		StringBuffer requestURL = request.getRequestURL();

		log.info("Request URL: {}, HTTP Method: {}, Client IP: {}, UserId: {}, Email: {}, UserRole: {}, Request Time: {}",
			requestURL, request.getMethod(), request.getRemoteAddr(), userId, email, userRole, requestTime);

		// Request Body 로깅 (메서드 파라미터)
		Object[] args = joinPoint.getArgs();
		StringBuilder sb = new StringBuilder("");
		for (Object arg : args) {
			sb.append(objectMapper.writeValueAsString(arg));
			sb.append("\n");
		}
		log.info("Request Body: {}", sb);
	}

	private void printResponse(Object result) {
		String jsonResponse;
		try {
			jsonResponse = objectMapper.writeValueAsString(result);
		} catch (JsonProcessingException e) {
			log.error("Error converting response to JSON", e);
			jsonResponse = "Error converting response to JSON";
		}

		log.info("Response Body: {}", jsonResponse);
	}
}
