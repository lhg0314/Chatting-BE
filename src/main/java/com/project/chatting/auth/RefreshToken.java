package com.project.chatting.auth;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = 60 * 60 * 24 * 14)
public class RefreshToken implements Serializable {
	
	public RefreshToken (String id, String accessToken, String refreshToken) {
		this.id= id;
		this.accessToken= accessToken;
		this.refreshToken=refreshToken;
		this.name="";
	}

    @Id
    private String id;

    @Indexed
    private String accessToken;

    private String refreshToken;
    
    private String name;

    public void updateAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

}