package com.baloghlan.security;

import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.errors.OauthErrorResponseException;
import io.micronaut.security.token.event.RefreshTokenGeneratedEvent;
import io.micronaut.security.token.refresh.RefreshTokenPersistence;
import jakarta.inject.Singleton;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.HashMap;
import java.util.Map;

import static io.micronaut.security.errors.IssuingAnAccessTokenErrorCode.INVALID_GRANT;

@Singleton
public class RefreshTokenPersistenceImpl implements RefreshTokenPersistence {
    private final Map<String, Authentication> refreshTokens = new HashMap<>();

    @Override
    public void persistToken(RefreshTokenGeneratedEvent event) {
        refreshTokens.put(event.getRefreshToken(), event.getAuthentication());
    }

    @Override
    public Publisher<Authentication> getAuthentication(String refreshToken) {
        return Flux.create(emitter -> {
            Authentication authentication = refreshTokens.get(refreshToken);
            if (authentication != null) {
                emitter.next(authentication);
                emitter.complete();
            } else {
                emitter.error(new OauthErrorResponseException(INVALID_GRANT, "refresh token not found", null));
            }
        }, FluxSink.OverflowStrategy.ERROR);
    }
}
