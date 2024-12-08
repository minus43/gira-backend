package com.example.girauser.util;

import com.example.girauser.common.response.CommonResDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public class BoardFeignClientFallback implements BoardFeignClient {

    @Override
    public ResponseEntity<?> makeBoard(Map<String, String> team) {
        CommonResDto fallbackResponse = new CommonResDto(
                HttpStatus.SERVICE_UNAVAILABLE,
                "보드 생성 서비스를 사용할 수 없습니다. 나중에 다시 시도해주세요.",
                null
        );
        return new ResponseEntity<>(fallbackResponse, HttpStatus.SERVICE_UNAVAILABLE);

    }

    @Override
    public ResponseEntity<?> deleteBoard(Map<String, String> team) {
        CommonResDto fallbackResponse = new CommonResDto(
                HttpStatus.SERVICE_UNAVAILABLE,
                "보드 삭제 서비스를 사용할 수 없습니다. 나중에 다시 시도해주세요.",
                null
        );
        return new ResponseEntity<>(fallbackResponse, HttpStatus.SERVICE_UNAVAILABLE);

    }
}
