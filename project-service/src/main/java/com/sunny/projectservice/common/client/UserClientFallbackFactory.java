package com.sunny.projectservice.common.client;

import com.sunny.projectservice.exception.ResourceNotFoundException;
import com.sunny.projectservice.exception.ServiceUnavailable;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;

import java.rmi.server.ServerNotActiveException;

@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            @Override
            public ResponseEntity<Void> userExist(String userEmail) {
                if(cause instanceof FeignException && ((FeignException) cause).status() == 404) {
                    throw new ResourceNotFoundException("Can't find anyone to invite");
                }
                throw new ServiceUnavailable("User Service Unavailable");
            }
        };
    }
}
