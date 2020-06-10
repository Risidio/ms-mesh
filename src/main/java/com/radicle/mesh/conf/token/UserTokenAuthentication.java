package com.radicle.mesh.conf.token;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.ParseException;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.params.MainNetParams;
import org.bouncycastle.util.encoders.Hex;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.crypto.ECDSAVerifier;
import com.nimbusds.jose.jwk.Curve;
import com.nimbusds.jose.jwk.ECKey;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.util.Base64URL;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

public class UserTokenAuthentication {

	private static final Logger logger = LogManager.getLogger(UserTokenAuthentication.class);
	private String token;
	private SignedJWT jwt;
	private JWTClaimsSet claims;

	public UserTokenAuthentication() {
		super();
	}

	public UserTokenAuthentication(String token) {
		try {
			this.token = token;
			logger.info("JWT token: " + token);
			this.jwt = SignedJWT.parse(token);
			this.claims = jwt.getJWTClaimsSet();
			logger.info("JWT claims issuer: " + this.claims.getIssuer());
			logger.info("JWT claims: " + this.claims.getSubject());
		} catch (Exception e) {
			logger.error("Error token: " + token, e);
			throw new RuntimeException("Failed to decode authentication token: " + token);
		}
	}
	
	public static UserTokenAuthentication getInstance(String v1Token) {
		if (!v1Token.startsWith("v1:")) {
			throw new RuntimeException("Authorization header should start with v1:");
		}
		String token = v1Token.substring(3);
		return new UserTokenAuthentication(token);
	}

	public boolean isAuthenticationValid(String address) {
		logger.info("===========================================================================");
		verifyToken(address);
		logger.info("verifyToken: check");
		checkExpiration();
		logger.info("Expiry: check");
		return true;
	}
	
	public String issuerAddressToB58(String iss) {
		org.bitcoinj.core.ECKey key = null;
		try {
			key = org.bitcoinj.core.ECKey.fromPublicOnly(Hex.decode(iss.getBytes()));
		} catch (Exception e) {
			key = org.bitcoinj.core.ECKey.fromPublicOnly(iss.getBytes());
		}
		Address issuerAddress = new Address(MainNetParams.get(), key.getPubKeyHash());
		return issuerAddress.toBase58();
	}

	public String getUsername() throws ParseException {
		return this.claims.getStringClaim("username");
	}

	public String bitcoinAddressToHex(String address) {
		logger.info("address: " + address);
		logger.info("address Base58 decoded and hex: " + new String(Hex.encode(Base58.decode(address))), "UTF-8");
		logger.info("address hexed: " + new String(Hex.encode(Base58.decode(address))), "UTF-8");
		String hexString = new String(Hex.encode(Base58.decode(address)));
		return hexString;
	}

	/**
	 * That the JWT is signed correctly by verifying with the pubkey hex provided.
	 * 
	 * @param address
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException 
	 */
	private void verifyToken(String address) {
		try {
			org.bitcoinj.core.ECKey key = org.bitcoinj.core.ECKey.fromPublicOnly(Hex.decode(address.getBytes()));
			org.spongycastle.math.ec.ECPoint spoint = key.getPubKeyPoint();
			BigInteger xbg = spoint.getAffineXCoord().toBigInteger();
			BigInteger ybg = spoint.getAffineYCoord().toBigInteger();
			ECKey ecKey = new ECKey.Builder(Curve.P_256K, Base64URL.encode(xbg), Base64URL.encode(ybg))
			        .keyUse(KeyUse.SIGNATURE)
			        .keyID("1")
			        .build();
			ECDSAVerifier verifier = new ECDSAVerifier(ecKey);
			if (!jwt.verify(verifier)) {
				throw new RuntimeException("Verification of token failed.");
			}
		} catch (JOSEException e) {
			throw new RuntimeException("Unable to verify jwt.");
		}
	}

	private void checkExpiration()  {
		Date expirationTime = this.claims.getExpirationTime();
		if (expirationTime == null) {
			return;
			//throw new RuntimeException("No expiry set on token");
		}
		long expiry = expirationTime.getTime();
		long nowish = new Date().getTime();
		if (nowish > expiry) {
			throw new RuntimeException("Token has expired.");
		}
	}

	public String getToken() {
		return this.token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public SignedJWT getJwt() {
		return jwt;
	}

	public void setJwt(SignedJWT jwt) {
		this.jwt = jwt;
	}
}
