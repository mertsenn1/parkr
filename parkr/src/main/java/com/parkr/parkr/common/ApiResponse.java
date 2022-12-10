package com.parkr.parkr.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ApiResponse
{
    private boolean success;

    public static ApiResponse ok()
    {
        return new ApiResponse(true);
    }

    public static <T> ApiResponse ok(T data)
    {
        return new ApiDataResponse<>(data);
    }
}