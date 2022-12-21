package com.parkr.parkr.address;

import com.parkr.parkr.common.ApiResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/addresses")
@RequiredArgsConstructor
public class AddressController
{
    private final IAddressService addressService;

    @GetMapping("{id}")
    public ApiResponse getAddressById(@PathVariable Long id) {
        return ApiResponse.ok(addressService.getAddressById(id));
    }

    @GetMapping
    public ApiResponse getAllAddresses() {
        return ApiResponse.ok(addressService.getAllAddresses());
    }

    @PostMapping()
    public ApiResponse saveAddress(@RequestBody AddressDto addressDto) {
        return ApiResponse.ok(addressService.saveAddress(addressDto));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteLotSummary(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }
}
