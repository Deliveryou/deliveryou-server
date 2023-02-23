package com.delix.deliveryou.resolver;

import com.delix.deliveryou.spring.model.BankAccount;
import com.delix.deliveryou.spring.model.Currency;
import graphql.kickstart.tools.GraphQLQueryResolver;
import org.springframework.stereotype.Component;

import java.util.UUID;
@Component
public class BankAccountResolver implements GraphQLQueryResolver {
    public BankAccount bankAccount(UUID id) {
        return BankAccount.builder().id(id).name("name 1").currency(Currency.USD).build();
    }
}
