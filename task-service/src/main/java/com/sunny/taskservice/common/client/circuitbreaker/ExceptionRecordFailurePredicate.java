package com.sunny.taskservice.common.client.circuitbreaker;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

@Component
@Slf4j
public class ExceptionRecordFailurePredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable throwable) {
        if (throwable instanceof FeignException feignException && (feignException.status() > 399 && feignException.status() < 500)) {
            log.info("404Error"+feignException.status());
            return false;
        }
        log.info("what Error?"+throwable.getClass());
        return true;
    }
}
