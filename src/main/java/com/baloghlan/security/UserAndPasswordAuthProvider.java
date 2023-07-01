package com.baloghlan.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationProvider;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.HashMap;
import java.util.Map;

@Singleton
public class UserAndPasswordAuthProvider implements AuthenticationProvider {
    private final Map<String, String> users = new HashMap<>(); // Username and password pairs
    @Inject
    private PasswordEncoderService passwordEncoderService;

    @PostConstruct
    public void loadUsers() {
        users.put("admin", passwordEncoderService.encode("testtest"));
    }

    @Override
    public Publisher<AuthenticationResponse> authenticate(HttpRequest<?> httpRequest,
                                                          AuthenticationRequest<?, ?> authenticationRequest) {
        return Flux.create(emitter -> {
            String username = String.valueOf(authenticationRequest.getIdentity());
            String password = String.valueOf(authenticationRequest.getSecret());
            String passwordHash = users.get(String.valueOf(authenticationRequest.getIdentity()));
            if (passwordHash != null && passwordEncoderService.matches(password, passwordHash)) {
                emitter.next(AuthenticationResponse.success(username));
                emitter.complete();
            } else {
                emitter.error(AuthenticationResponse.exception("Bad credentials"));
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
