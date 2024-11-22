package com.example.girauser.controller;


import com.example.girauser.dto.UserDto;
import com.example.girauser.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @GetMapping("/signin")
    public ResponseEntity<?> signIn(HttpServletResponse response, @RequestBody UserDto dto ) {
        log.info("signIn");
        try {
            userService.signIn(dto, response);
            return ResponseEntity.ok().body("Sign in successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserDto dto) {
        try {
            userService.signUp(dto);
            return ResponseEntity.ok().body("Sign up successful");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/modify")
    public void modify(@RequestBody UserDto dto) {
        userService.modify(dto);
    }

    @DeleteMapping("/delete")
    public void delete(@RequestBody UserDto dto) {
        userService.delete(dto);
    }

    @GetMapping("/myInfo")
    public void getMyInfo() {
        userService.myInfo();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public void userList() {
        userService.userList();
    }

    @GetMapping("/test1")
    public void getTest1() {
        log.info("test1");
    }

}
