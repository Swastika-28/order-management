package com.project.order_management.repo;

import com.project.order_management.model.Token;
import com.project.order_management.model.TokenId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, TokenId> {

    @Query(value = "SELECT t FROM Token t WHERE t.tokenId.customerId = :custId AND t.tokenId.token = :token")
    Optional<Token> findByCustomerAndToken(
            @Param("custId") Long custId,
            @Param("token") String token
    );

    @Query(value = "SELECT t FROM Token t WHERE t.tokenId.customerId = :custId")
    List<Token> findAllByCustomerId(@Param("custId") Long custId);
}