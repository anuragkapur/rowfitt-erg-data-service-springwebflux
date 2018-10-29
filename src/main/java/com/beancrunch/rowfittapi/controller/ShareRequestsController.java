package com.beancrunch.rowfittapi.controller;

import com.beancrunch.rowfittapi.domain.ShareRequest;
import com.beancrunch.rowfittapi.repository.ShareRequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "${corsOrigins}")
@RequestMapping("/api/shares")
public class ShareRequestsController {

    @Autowired
    private ShareRequestsRepository repository;

    @PutMapping("/{shareId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ShareRequest> updateShareRequest(
            @PathVariable String shareId,
            @RequestBody ShareRequest shareRequest) {

        return repository
                .findById(shareId)
                .map(s -> {
                    s.setShareStatus(shareRequest.getShareStatus());
                    return s;
                })
                .doOnSuccess(repository::save);
    }
}
