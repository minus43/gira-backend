package com.example.giraboard.controller;


import com.example.giraboard.common.response.CommonResDto;
import com.example.giraboard.dto.*;
import com.example.giraboard.entity.*;
import com.example.giraboard.service.BoardService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final BoardService boardService;
    private final ObjectMapper objectMapper;

    //보드 생성
    @PostMapping("/makeboard")
    public ResponseEntity<?> makeBoard(@RequestBody Map<String, String> team) {
        boardService.makeBoard(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED,"보드 생성 완료", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
    }
    //보드 삭제
    @DeleteMapping("/deleteboard")
    public ResponseEntity<?> deleteBoard(@RequestBody Map<String, String> team) {
        boardService.deleteBoard(team);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"보드 삭제 완료",null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //수정
    @PutMapping("/updatetheme")
    public ResponseEntity<?> updateTheme(@RequestBody ThemeDto dto) {
        Theme theme = boardService.updateTheme(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"주제 수정 완료",theme);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatetool")
    public ResponseEntity<?> updateTool(@RequestBody Map<String,List<Tool>> toolList) {
        boardService.updateTool(toolList);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"도구 수정 완료", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updateur")
    public ResponseEntity<?> updateUR(@RequestBody Map<String,List<UR>> urList) {
        boardService.updateUR(urList);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"요구사항 & WBS 수정 완료", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatebeerd")
    public ResponseEntity<?> updateBEERD(@RequestParam("erd") MultipartFile erd,
                                      @RequestPart("data") String data) {
        BEDto dto = null;
        try {
            dto = objectMapper.readValue(data, BEDto.class);
            log.info(dto.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String erdUri = boardService.uploadImage(erd,dto.getTeamName()+"erd");
        dto.setErd(erdUri);
        BE be = boardService.updateBEERD(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"벡엔드 erd 수정 완료", be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
    @DeleteMapping("/deletebeerd")
    public ResponseEntity<?> deleteBEERD(@RequestPart("data") String data) {
        BEDto dto = null;
        try {
            dto = objectMapper.readValue(data, BEDto.class);
            log.info(dto.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        boardService.deleteImage(dto.getTeamName()+"erd");
        BE be = boardService.updateBEERD(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "벡엔드 erd 이미지 삭제 완료", be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatebeapi")
    public ResponseEntity<?> updateBEAPI(@RequestParam("api") MultipartFile api,
                                      @RequestPart("data") String data) {
        BEDto dto = null;
        try {
            dto = objectMapper.readValue(data, BEDto.class);
            log.info(dto.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String apiUri = boardService.uploadImage(api,dto.getTeamName()+"api");
        dto.setApi(apiUri);
        BE be = boardService.updateBEAPI(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"벡엔드 erd 수정 완료", be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @DeleteMapping("/deletebeapi")
    public ResponseEntity<?> deleteBEAPI(@RequestPart("data") String data) {
        BEDto dto = null;
        try {
            dto = objectMapper.readValue(data, BEDto.class);
            log.info(dto.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        boardService.deleteImage(dto.getTeamName()+"api");
        BE be = boardService.updateBEAPI(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "벡엔드 api 이미지 삭제 완료", be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/updatefe")
    public ResponseEntity<?> updateFE(@RequestParam("wireframe") MultipartFile wireframe,
                                      @RequestPart("data") String data) {
        FEDto dto = null;
        try {
            dto = objectMapper.readValue(data, FEDto.class);
            log.info(dto.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        String wireframeUri = boardService.uploadImage(wireframe,dto.getTeamName()+"wireframe");
        dto.setWireframe(wireframeUri);
        FE fe = boardService.updateFE(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"프론트엔드 와이어프레임 수정 완료", fe);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @DeleteMapping("/deletefewf")
    public ResponseEntity<?> deleteFEWF(@RequestPart("data") String data) {
        FEDto dto = null;
        try {
            dto = objectMapper.readValue(data, FEDto.class);
            log.info(dto.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        boardService.deleteImage(dto.getTeamName()+"wireframe");
        FE fe = boardService.updateFE(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "벡엔드 와이어프레임 이미지 삭제 완료", fe);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //조회
    @GetMapping("/gettheme")
    public ResponseEntity<?> getTheme(@RequestParam String teamName) {
        ThemeDto theme = boardService.getTheme(teamName);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "주제 조회 완료", theme);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/gettool")
    public ResponseEntity<?> getTool(@RequestParam String teamName) {
        List<Tool> toolList = boardService.getTool(teamName);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"툴 리스트 조회 완료", toolList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/getur")
    public ResponseEntity<?> getUR(@RequestParam String teamName) {
        List<UR> urList = boardService.getUR(teamName);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "요수사항&WBS 리스트 조회 완료", urList);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/getbe")
    public ResponseEntity<?> getBE(@RequestParam String teamName) {
        BEDto be = boardService.getBE(teamName);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"벡엔드 조회 완료",be);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @GetMapping("/getfe")
    public ResponseEntity<?> getFE(@RequestParam String teamName) {
        FEDto fe = boardService.getFE(teamName);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,"프론트엔드 조회 완료",fe);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
