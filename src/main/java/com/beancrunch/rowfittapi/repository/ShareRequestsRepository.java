package com.beancrunch.rowfittapi.repository;

import com.beancrunch.rowfittapi.domain.ShareRequest;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;

public interface ShareRequestsRepository extends ReactiveCrudRepository<ShareRequest, String> {

}
