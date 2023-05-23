package com.parkr.parkr.user;

import com.parkr.parkr.auth.AuthenticationRequest;
import com.parkr.parkr.car.CarDto;
import com.parkr.parkr.common.ApiResponse;
import com.parkr.parkr.common.CarUpdateOperationModel;
import com.parkr.parkr.common.ValidateTokenRequest;
import com.parkr.parkr.lot_summary.ILotSummaryService;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.ConstraintViolationException;
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

    @GetMapping("/cars")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ApiResponse getCars() {
        return ApiResponse.ok(userService.getCars());
    }

    @PostMapping("/add-vehicle")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_OWNER') or hasAuthority('ADMIN')")
    public ApiResponse saveCar(@RequestBody CarDto carDto) {
        return ApiResponse.ok(userService.addCar(carDto));
    }

    @PutMapping("/edit-vehicle")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('LOT_OWNER') or hasAuthority('ADMIN')")
    public ApiResponse updateCar(@RequestBody CarUpdateOperationModel carModel) {
        return ApiResponse.ok(userService.updateCar(carModel));
    }

    @DeleteMapping("/cars/{id}")
    @PreAuthorize("hasAuthority('USER') or hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCar(@PathVariable Long id) {
        userService.deleteCar(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
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

    @PostMapping("/validate-token")
    public ApiResponse validateToken(@RequestBody ValidateTokenRequest request) {
        return ApiResponse.ok(userService.validateToken(request.getToken()));
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<?> handleException(UserNotFoundException e) {
        return new ResponseEntity<> (e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
