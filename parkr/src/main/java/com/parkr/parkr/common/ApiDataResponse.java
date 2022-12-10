package com.parkr.parkr.common;

import lombok.Getter;

@Getter
public class ApiDataResponse<T> extends ApiResponse
{
    private T data;

    public ApiDataResponse(T data)
    {
        super(true);
        this.data = data;
    }
}
