package com.example.girauser.util;

import com.example.girauser.common.response.CommonResDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "gira-board", fallback = BoardFeignClientFallback.class)
public interface BoardFeignClient {
    @PostMapping("/makeboard")
    ResponseEntity<?> makeBoard(@RequestBody Map<String, String> team);

    @DeleteMapping("/deleteboard")
    ResponseEntity<?> deleteBoard(@RequestBody Map<String, String> team);
}
