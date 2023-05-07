package com.parkr.parkr.user;

import com.parkr.parkr.auth.AuthenticationRequest;
import com.parkr.parkr.common.ApiResponse;
import com.parkr.parkr.lot_summary.ILotSummaryService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController
{
    private final IUserService userService;
    private final ILotSummaryService lotSummaryService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse getUserById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getUserById(id));
    }

    @GetMapping("/current-parking")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse getCurrentParkingData() {
        return ApiResponse.ok(lotSummaryService.getCurrentParkingData());
    }

    @GetMapping("/past-parking")
    @PreAuthorize("hasAuthority('USER')")
    public ApiResponse getPastParkingData() {
        return ApiResponse.ok(lotSummaryService.getPastParkingData());
    }

    @PostMapping("/sign-up")
    public ApiResponse signUp(@RequestBody UserDto userDto) {
        return ApiResponse.ok(userService.signUp(userDto));
    }

    @PostMapping("/sign-in")
    public ApiResponse signIn(@RequestBody AuthenticationRequest request) {
        return ApiResponse.ok(userService.signIn(request));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
