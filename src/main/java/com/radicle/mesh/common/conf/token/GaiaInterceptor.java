package com.radicle.mesh.common.conf.token;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;

import com.radicle.mesh.privilege.service.AuthorisationRepository;
import com.radicle.mesh.privilege.service.domain.Authorisation;

@Component
public class GaiaInterceptor implements HandlerInterceptor {

	private static final String DEMO = "demo-";
	private static final String API_KEY = "ApiKey";
	private static final String BLOCKSTACK = "blockstack-";
	private static final Logger logger = LogManager.getLogger(GaiaInterceptor.class);
	private static final String X_FORWARDED_HOST = "X-Forwarded-Host";
	private static final String X_FORWARDED_SERVER = "X-Forwarded-Server";
	private static final String AUTHORIZATION = "Authorization";
	private static final String Identity_Address = "IdentityAddress";
	private static final String ALLOWED_PATHS = "/lsat/v1/lightning/alice/getInfo /v1/invoice /v1/payment /v1/verify /lightning/alice/getInfo /api/exchange/rates /bitcoin/getRadPayConfig /bitcoin/getPaymentAddress /trading/taxonomy/fetch /trading/user/contactEmail";
	private static final String ALLOWED_PATH_BTC = "/bitcoin/address";
	private static final String ALLOWED_PATH_LND = "/bitcoin/invoice";
	private static final String ALLOWED_PATH_INV = "/lightning";
	private static final Set<String> whitelist = new HashSet<String>();
	static {
		whitelist.add("radicle_art.id.blockstack");
		whitelist.add("yakahead.id.blockstack");
		whitelist.add("figis.id.blockstack");
		whitelist.add("mijoco.id.blockstack");
		whitelist.add("mike.personal.id");
	}
	@Autowired
	private AuthorisationRepository authorisationRepository;


	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		try {
			if (handler instanceof HandlerMethod) {
				String path = request.getRequestURI();
				if (isProtected(request, path)) {
					logger.info("Protected domain: " + path);
					Enumeration<String> headers = request.getHeaderNames();
					while (headers.hasMoreElements()) {
						String header = headers.nextElement();
						// logger.info("header: " + header);
					}
					String address = response.getHeader(Identity_Address);
					String authToken = request.getHeader(AUTHORIZATION);
					if (authToken == null) {
						address = request.getHeader(Identity_Address);
						authToken = request.getHeader(AUTHORIZATION);
						if (authToken == null) {
							// throw new Exception("Failed validation - no auth token");
							request.getSession().setAttribute("USERNAME", "unknown");
							return HandlerInterceptor.super.preHandle(request, response, handler);
						}
					}
					authToken = authToken.split(" ")[1]; // stripe out Bearer string before passing along..
					UserTokenAuthentication v1Authentication = UserTokenAuthentication.getInstance(authToken);
					boolean auth;
					try {
						auth = v1Authentication.isAuthenticationValid(address);
					} catch (Exception e) {
						logger.info("Unable to verify token. Is identity address present as custom header? Identity Address: " + address);
					}
					String stxAddress = v1Authentication.getUsername();
					logger.info("Protected domain: request from " + stxAddress);
					Authorisation authorisation = authorisationRepository.findByStxAddress(stxAddress);
//					if (authorisation != null  && !authorisation.isWhitelisted(stxAddress)) {
//						throw new RuntimeException("Not allowed");
//					}
					if (authorisation != null) {
						request.getSession().setAttribute("authorisation", authorisation);
					}
					request.getSession().setAttribute("username", stxAddress);
				}
			} else if (handler instanceof AbstractHandlerMapping) {
				// error occurred..
				logger.info("Error mapping.");
			} else {
				logger.info("Unknown request.");
			}
		} catch (Exception e) {
			throw e;
		}
		return HandlerInterceptor.super.preHandle(request, response, handler);
	}
	
	private boolean isProtected(HttpServletRequest request, String path) {
		boolean protectd = false;
		if (request.getMethod().equalsIgnoreCase("POST") || request.getMethod().equalsIgnoreCase("GET")) {
			if (path.startsWith("/mesh/v2/")) {
				protectd = true;
			}
		}
		return protectd;
	}
}
