package com.sunny.projectservice.client;

import com.sunny.projectservice.exception.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404 && methodKey.equals("getUser")) {
            return new ResourceNotFoundException("Not Found User");
        }
        return null;
    }
}
