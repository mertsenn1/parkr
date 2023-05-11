package com.parkr.parkr.user;

import com.parkr.parkr.auth.AuthenticationRequest;
import com.parkr.parkr.common.ApiResponse;
import com.parkr.parkr.lot_summary.ILotSummaryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "parkr")
public class UserController
{
    private final IUserService userService;
    private final ILotSummaryService lotSummaryService;

    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getUserById(@PathVariable Long id) {
        return ApiResponse.ok(userService.getUserById(id));
    }

    @GetMapping("/current-parking")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getCurrentParkingData() {
        return ApiResponse.ok(userService.getCurrentParkingData());
    }

    @GetMapping("/past-parking")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getPastParkingData() {
        return ApiResponse.ok(userService.getPastParkingData());
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getRecentParkingData() {
        return ApiResponse.ok(userService.getRecentParkingData());
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
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        userService.deleteUser(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
