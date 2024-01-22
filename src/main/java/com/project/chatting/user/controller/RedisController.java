package com.project.chatting.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class RedisController {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	
	@Autowired 
	private StringRedisTemplate stringRedisTemplate; 
	
	@RequestMapping(value="/redisTest", method= RequestMethod.POST)
	@ResponseBody
	public void addRedisKey() {
		ValueOperations<String, String> vop = redisTemplate.opsForValue();
		
		vop.set("a", "1");
		vop.set("b", "2");
	}
	
	@RequestMapping(value="/redisTest", method= RequestMethod.GET)
	@ResponseBody
	public String getRedisKey(@RequestParam(value="key") String key) {
		ValueOperations<String, String> vop = stringRedisTemplate.opsForValue();
		
		String value = (String)vop.get(key);
		
		return value;
	}
}