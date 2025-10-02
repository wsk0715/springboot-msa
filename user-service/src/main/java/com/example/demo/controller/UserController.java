package com.example.demo.controller;

import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    /**
     * 모든 사용자 조회
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("GET /api/v1/users - 모든 사용자 조회 요청");
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * ID로 사용자 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        log.info("GET /api/v1/users/{} - 사용자 조회 요청", id);
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 이메일로 사용자 조회
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        log.info("GET /api/v1/users/email/{} - 이메일로 사용자 조회 요청", email);
        UserResponse user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * 사용자 생성
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest requestDto) {
        log.info("POST /api/v1/users - 사용자 생성 요청: {}", requestDto.getEmail());
        UserResponse createdUser = userService.createUser(requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    /**
     * 사용자 정보 수정
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UserRequest requestDto) {
        log.info("PUT /api/v1/users/{} - 사용자 수정 요청", id);
        UserResponse updatedUser = userService.updateUser(id, requestDto);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 사용자 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("DELETE /api/v1/users/{} - 사용자 삭제 요청", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 활성 사용자 수 조회
     */
    @GetMapping("/count/active")
    public ResponseEntity<Long> getActiveUserCount() {
        log.info("GET /api/v1/users/count/active - 활성 사용자 수 조회 요청");
        long count = userService.getActiveUserCount();
        return ResponseEntity.ok(count);
    }

    /**
     * 사용자의 주문 목록 조회
     */
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable Long id) {
        log.info("GET /api/v1/users/{}/orders - 사용자 주문 목록 조회 요청", id);
        List<OrderResponse> orders = userService.getUserOrders(id);
        return ResponseEntity.ok(orders);
    }

}
