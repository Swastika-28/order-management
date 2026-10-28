package com.project.order_management.service.common;

import com.project.order_management.model.Customer;
import com.project.order_management.model.Token;
import com.project.order_management.model.TokenId;
import com.project.order_management.repo.TokenRepository;
import com.project.order_management.util.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    @Value("${application.token.expiry.min}")
    private String expiryMins;

    @Transactional
    public void saveToken(Customer customer, String tokenValue) {
        LocalDateTime now = LocalDateTime.now(ZoneId.of(Constants.IST_TIMEZONE));
        LocalDateTime newExpiry = now.plusMinutes(Long.parseLong(expiryMins));

        List<Token> customerTokens = tokenRepository.findAllByCustomerId(customer.getId());

        Optional<Token> activeToken = customerTokens.stream()
                .filter(t -> !t.isUsed() && t.getExpiryTimestamp().isAfter(now))
                .findFirst();

        Token tokenToSave;

        if (activeToken.isPresent()) {
            tokenToSave = activeToken.get();

            tokenRepository.delete(tokenToSave);

            TokenId newTokenId = new TokenId(customer.getId(), tokenValue);
            tokenToSave = Token.builder()
                    .tokenId(newTokenId)
                    .used(false)
                    .expiryTimestamp(newExpiry)
                    .build();
        } else {
            TokenId tokenId = new TokenId(customer.getId(), tokenValue);
            tokenToSave = Token.builder()
                    .tokenId(tokenId)
                    .used(false)
                    .expiryTimestamp(newExpiry)
                    .build();
        }

        tokenRepository.save(tokenToSave);
    }
}