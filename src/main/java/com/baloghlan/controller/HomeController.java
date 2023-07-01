package com.baloghlan.controller;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.rules.SecurityRule;

import java.security.Principal;


@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/home")
public class HomeController {

    @Get
    public HttpResponse<Object> getHome(Principal principal) {
        return HttpResponse.ok(principal.getName());
    }
}
