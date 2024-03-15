package com.audrey.refeat.domain.user.component;

import com.audrey.refeat.common.exception.ErrorCode;
import com.audrey.refeat.domain.user.entity.UserInfo;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static long access;
    private static long refresh;

    private static final long ONE_MINUTE = 1000L * 60;
    private static final String EC_PUBLIC_KEY_STR = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAE5aBqrd8oIJk3yZqhutM2s467RIQbZtvrCA1gJW1NMR010f9/sE5l5TYdvX7j4WlVO/Iy1CRkS90mN5fqtbKIIQ==";
    private static final String EC_PUBLIC_KEY_REFRESH_STR = "MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEZK5fP2TxaqO0ftsw0EVOTBjuXSrm8kgJXSrrt8oehGOFezHbrxhwxzptuVDxArNauSqvhUBIGkpJu7FIoLgsEg==";
    private static final String EC_PRIVATE_KEY_STR = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCC0evqAWTJGVnjPkI3dILbqZQPGhiV+DqUFUrr2gU1X6Q==";
    private static final String EC_PRIVATE_KEY_REFRESH_STR = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDjn3Q5g/P2BbN2mtfTaB/L4tCvQzXcaEZIVKeHKLExGA==";

    private static PublicKey EC_PUBLIC_KEY;
    private static PublicKey EC_PUBLIC_KEY_REFRESH;
    public static SecretKey EC_PRIVATE_KEY;
    public static SecretKey EC_PRIVATE_KEY_REFRESH;
    final KeyFactory keyPairGenerator = KeyFactory.getInstance("EC");

    @Value("${token.access}")
    public void setAccess(String value) {
        access = Long.parseLong(value);
    }

    @Value("${token.refresh}")
    public void setRefresh(String value) {
        refresh = Long.parseLong(value);
    }


    private static String removeEncapsulationBoundaries(String key) {
        return key.replaceAll("\n", "")
                .replaceAll(" ", "")
                .replaceAll("-{5}[a-zA-Z]*-{5}", "");
    }

    public JwtTokenProvider() throws NoSuchAlgorithmException, InvalidKeySpecException {
        EC_PRIVATE_KEY = Keys.hmacShaKeyFor(Base64.getDecoder().decode(removeEncapsulationBoundaries(EC_PRIVATE_KEY_STR)));
        EC_PRIVATE_KEY_REFRESH = Keys.hmacShaKeyFor(Base64.getDecoder().decode(removeEncapsulationBoundaries(EC_PRIVATE_KEY_REFRESH_STR)));
    }

    public ErrorCode verifyToken(String token, boolean refresh) {
        if (!StringUtils.hasText(token)) {
            return ErrorCode.TOKEN_NOT_FOUND;
        }
        try {
            Jwts.parser().verifyWith((refresh ? EC_PRIVATE_KEY_REFRESH : EC_PRIVATE_KEY)).build().parseSignedClaims(token);
            return ErrorCode.AUTH_SUCCESS;
        } catch (ExpiredJwtException e) {
            if (refresh) {
                return ErrorCode.REFRESH_TOKEN_EXPIRED;
            }
            return ErrorCode.TOKEN_EXPIRED;
        } catch (UnsupportedJwtException e) {
            return ErrorCode.TOKEN_NOT_SUPPORTED;
        } catch (IllegalStateException | MalformedJwtException | SignatureException e) {
            return ErrorCode.TOKEN_NOT_VALID;
        }
    }

    public String createAccessToken(UserInfo userInfo) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ONE_MINUTE * access);
        return Jwts.builder()
                .header().add("typ", "JWT").add("alg", "HS256").and()
                .subject(String.valueOf(userInfo.getId()))
                .claim("role", userInfo.getAuthority().name())
                .claim("email", userInfo.getEmail())
                .issuedAt(now)
                .expiration(validity)
                .signWith(EC_PRIVATE_KEY)
                .compact();
    }

    public String createRefreshToken(Long subject) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + ONE_MINUTE * refresh);
        return Jwts.builder()
                .header().add("typ", "JWT").add("alg", "HS256").add("type", "refresh").and()
                .subject(String.valueOf(subject))
                .issuedAt(now)
                .expiration(validity)
                .signWith(EC_PRIVATE_KEY_REFRESH)
                .compact();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, boolean refresh) {
        final Claims claims = extractAllClaims(token, refresh);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token, boolean refresh) {
        return Jwts
                .parser()
                .verifyWith((refresh ? EC_PRIVATE_KEY_REFRESH : EC_PRIVATE_KEY))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
