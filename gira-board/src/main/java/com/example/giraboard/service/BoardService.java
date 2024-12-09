package com.example.giraboard.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.giraboard.dto.*;
import com.example.giraboard.entity.*;
import com.example.giraboard.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BoardService {
    private final ThemeRepositoy themeRepositoy;
    private final ToolRepository toolRepository;
    private final URRepositoy urRepositoy;
    private final BERepositoy beRepositoy;
    private final FERepositoy feRepositoy;

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    
    public void makeBoard(Map<String, String> team) {
        String teamName=team.get("teamName");
        Theme theme = Theme.builder()
                .teamName(teamName)
                .build();
        themeRepositoy.save(theme);
        
        Tool tool = Tool.builder()
                .teamName(teamName)
                .build();
        toolRepository.save(tool);
        
        UR ur = UR.builder()
                .teamName(teamName)
                .build();
        urRepositoy.save(ur);
        
        BE be = BE.builder()
                .teamName(teamName)
                .build();
        beRepositoy.save(be);
        
        FE fe = FE.builder()
                .teamName(teamName)
                .build();
        feRepositoy.save(fe);
        
    }

    public void deleteBoard(Map<String, String> team) {
        String teamName=team.get("teamName");
        themeRepositoy.deleteAllByTeamName(teamName);
        toolRepository.deleteAllByTeamName(teamName);
        urRepositoy.deleteAllByTeamName(teamName);
        beRepositoy.deleteAllByTeamName(teamName);
        feRepositoy.deleteAllByTeamName(teamName);
    }

    public Theme updateTheme(ThemeDto dto) {
        Theme theme = themeRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        theme.setTitle(dto.getTitle());
        theme.setContent(dto.getContent());
        theme.setWriter(dto.getWriter());
        themeRepositoy.save(theme);
        return theme;
    }

    public void updateTool(Map<String, List<Tool>> toolList) {
        List<Tool> addTool = toolList.get("addTool");
        log.info(addTool.toString());
        if(!addTool.isEmpty()) toolRepository.saveAll(addTool);
        List<Tool> removeTool = toolList.get("removeTool");
        if(!removeTool.isEmpty())toolRepository.deleteAll(removeTool);
        List<Tool> editTool = toolList.get("editTool");
        if(!editTool.isEmpty())toolRepository.saveAll(editTool);
    }

    public void updateUR(Map<String, List<UR>> urList) {
        List<UR> addUR = urList.get("addUR");
        if(!addUR.isEmpty())urRepositoy.saveAll(addUR);
        List<UR> removeUR = urList.get("removeUR");
        if(!removeUR.isEmpty())urRepositoy.deleteAll(removeUR);
        List<UR> editUR = urList.get("editUR");
        if(!editUR.isEmpty())urRepositoy.saveAll(editUR);
    }

    public BE updateBEERD(BEDto dto) {
        BE be = beRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        be.setErd(dto.getErd());
        be.setWriter(dto.getWriter());
        beRepositoy.save(be);
        return be;
    }

    public BE updateBEAPI(BEDto dto) {
        BE be = beRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        be.setApi(dto.getApi());
        be.setWriter(dto.getWriter());
        beRepositoy.save(be);
        return be;
    }

    public FE updateFE(FEDto dto) {
        FE fe = feRepositoy.findByTeamName(dto.getTeamName()).orElseThrow();
        fe.setWireframe(dto.getWireframe());
        fe.setWriter(dto.getWriter());
        feRepositoy.save(fe);
        return fe;
    }

    public ThemeDto getTheme(String teamName) {
        Theme theme = themeRepositoy.findByTeamName(teamName).orElseThrow();
        return theme.toDto();
    }

    public List<Tool> getTool(String teamName) {
        return toolRepository.findAllByTeamName(teamName);
    }

    public List<UR> getUR(String teamName) {
        return urRepositoy.findAllByTeamName(teamName);
    }

    public BEDto getBE(String teamName) {
        BE be = beRepositoy.findByTeamName(teamName).orElseThrow();
        return be.toDto();
    }

    public FEDto getFE(String teamName) {
        FE fe = feRepositoy.findByTeamName(teamName).orElseThrow();
        return fe.toDto();
    }

    /**
     * 이미지 업로드
     *
     * @param file 업로드할 이미지 파일
     * @return 업로드된 이미지의 URL
     */
    public String uploadImage(MultipartFile file, String type) {
        // 파일명 검증
        String originalFilename = file.getOriginalFilename();
        log.info(originalFilename);
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("파일명이 유효하지 않습니다.");
        }

        String key =type;

        try {
            // ObjectMetadata 생성 및 설정
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            // S3에 이미지 업로드
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream(), metadata);

            amazonS3.putObject(putObjectRequest);

            // 업로드된 이미지의 URL 생성
            return amazonS3.getUrl(bucketName, key).toString();
        } catch (IOException e) {
            // 예외 처리: 필요에 따라 커스텀 예외로 변경 가능
            throw new RuntimeException("이미지 업로드 중 오류가 발생했습니다.", e);
        }
    }

    public void deleteImage(String fileName) {
        amazonS3.deleteObject(bucketName, fileName);
    }

    /**
     * 파일 URL 생성
     *
     * @param key 파일의 키
     * @return 파일의 URL
     */
    private String getFileUrl(String key) {
        return amazonS3.getUrl(bucketName, key).toString();
    }

    /**
     * 파일 확장자 추출
     *
     * @param filename 파일명
     * @return 파일 확장자
     */
    private String getFileExtension(String filename) {
        int lastIndex = filename.lastIndexOf(".");
        if (lastIndex == -1 || lastIndex == filename.length() - 1) {
            return ""; // 확장자가 없는 경우 빈 문자열 반환
        }
        return filename.substring(lastIndex);
    }
}
