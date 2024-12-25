package com.ehr.patient.feign;

import com.ehr.patient.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@FeignClient("users")
public interface UserServiceInterface {
    @GetMapping("/users/getuserbyid/{userid}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userid);

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id);

    @PutMapping("/users/updateuser")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto);

    @PostMapping("/users/create")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto);
}
