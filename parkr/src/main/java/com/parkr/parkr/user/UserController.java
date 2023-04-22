package com.parkr.parkr.user;

import com.parkr.parkr.auth.AuthenticationRequest;
import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
    private final IUserService userService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse getUserById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getUserById(id));
    }

    @GetMapping("/customers")
    public ApiResponse getAllCustomers() {
        return ApiResponse.ok(userService.getAllCustomers());
    }

    @GetMapping("/owners")
    public ApiResponse getAllOwners() {
        return ApiResponse.ok(userService.getAllOwners());
    }

    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody UserDto userDto) {
        return ApiResponse.ok(userService.signUp(userDto));
    }

    @GetMapping("/sign-in")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse signIn(@RequestBody AuthenticationRequest request) {
        return ApiResponse.ok(userService.signIn(request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
