package com.example.demo.service;

import com.example.demo.client.OrderServiceClient;
import com.example.demo.dto.OrderResponse;
import com.example.demo.dto.UserRequest;
import com.example.demo.dto.UserResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    private final OrderServiceClient orderServiceClient;
    
    /**
     * 모든 사용자 조회
     */
    public List<UserResponse> getAllUsers() {
        log.info("모든 사용자 조회 요청");
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }
    
    /**
     * ID로 사용자 조회
     */
    public UserResponse getUserById(Long id) {
        log.info("사용자 조회 요청 - ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + id));
        return convertToResponseDto(user);
    }
    
    /**
     * 이메일로 사용자 조회
     */
    public UserResponse getUserByEmail(String email) {
        log.info("사용자 조회 요청 - 이메일: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. 이메일: " + email));
        return convertToResponseDto(user);
    }
    
    /**
     * 사용자 생성
     */
    @Transactional
    public UserResponse createUser(UserRequest requestDto) {
        log.info("사용자 생성 요청 - 이메일: {}", requestDto.getEmail());
        
        // 이메일 중복 체크
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + requestDto.getEmail());
        }
        
                User user = User.builder()
                        .name(requestDto.getName())
                        .email(requestDto.getEmail())
                        .status(requestDto.getStatus() != null ? requestDto.getStatus() : User.UserStatus.ACTIVE)
                        .build();
        
        User savedUser = userRepository.save(user);
        log.info("사용자 생성 완료 - ID: {}", savedUser.getId());
        
        return convertToResponseDto(savedUser);
    }
    
    /**
     * 사용자 정보 수정
     */
    @Transactional
    public UserResponse updateUser(Long id, UserRequest requestDto) {
        log.info("사용자 수정 요청 - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + id));
        
        // 이메일 변경 시 중복 체크
        if (!user.getEmail().equals(requestDto.getEmail()) && 
            userRepository.existsByEmail(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다: " + requestDto.getEmail());
        }
        
                user.setName(requestDto.getName());
                user.setEmail(requestDto.getEmail());
                if (requestDto.getStatus() != null) {
                    user.setStatus(requestDto.getStatus());
                }
        
        User updatedUser = userRepository.save(user);
        log.info("사용자 수정 완료 - ID: {}", updatedUser.getId());
        
        return convertToResponseDto(updatedUser);
    }
    
    /**
     * 사용자 삭제 (소프트 삭제)
     */
    @Transactional
    public void deleteUser(Long id) {
        log.info("사용자 삭제 요청 - ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. ID: " + id));
        
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);
        
        log.info("사용자 삭제 완료 - ID: {}", id);
    }
    
    /**
     * 활성 사용자 수 조회
     */
    public long getActiveUserCount() {
        log.info("활성 사용자 수 조회 요청");
        return userRepository.countActiveUsers();
    }
    
    /**
     * 사용자의 주문 목록 조회
     */
    public List<OrderResponse> getUserOrders(Long userId) {
        log.info("사용자 주문 목록 조회 요청 - 사용자 ID: {}", userId);
        
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("사용자를 찾을 수 없습니다. ID: " + userId);
        }
        
        try {
            return orderServiceClient.getUserOrders(userId);
        } catch (Exception e) {
            log.error("주문 서비스 호출 실패 - 사용자 ID: {}, 오류: {}", userId, e.getMessage());
            throw new RuntimeException("주문 정보를 조회할 수 없습니다: " + e.getMessage());
        }
    }
    
    /**
     * Entity를 ResponseDto로 변환
     */
    private UserResponse convertToResponseDto(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
