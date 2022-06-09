package com.revature.ers.services;

import com.revature.ers.dtos.responses.Principal;
import com.revature.ers.util.JwtConfig;
import com.revature.ers.util.annotations.Inject;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;

import java.util.Date;

public class TokenServices {
    @Inject
    private JwtConfig jwtConfig;


    public TokenServices(){

    }

    public TokenServices(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    public String generateToken(Principal subject){
        long now = System.currentTimeMillis();
        JwtBuilder tokenBuilder = Jwts.builder()
                .setId(subject.getId())
                .setIssuer("ers")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + jwtConfig.getExperation()))
                .setSubject(subject.getUsername())
                .claim("role", subject.getRole()) // db is role_id
                .signWith(jwtConfig.getSigAlg(), jwtConfig.getSigningKey());
        return tokenBuilder.compact();
    }

    public  Principal extractRequestDetails(String token){

        try{
            Claims  claims = Jwts.parser()
                    .setSigningKey(jwtConfig.getSigningKey())
                    .parseClaimsJws(token)
                    .getBody();
            return new Principal(claims.getId(),claims.getSubject(),claims.get("role", String.class));
        }catch(Exception e){
            return null;
        }
    }
}
