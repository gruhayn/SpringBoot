package com.unitech.api.service.impl;

import com.unitech.api.constants.Constants;
import com.unitech.api.exceptions.AccountNotFoundException;
import com.unitech.api.exceptions.SameAccountException;
import com.unitech.api.exceptions.TransferToDeactiveAccountException;
import com.unitech.api.exceptions.UnacceptableAmountException;
import com.unitech.api.models.UserEntity;
import com.unitech.api.repository.RegistrationRepository;
import com.unitech.api.service.CurrencyRateService;
import com.unitech.api.service.MoneyService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MoneyServiceImpl implements MoneyService
{

    RegistrationRepository repository;
    CurrencyRateService currencyRateService;

    @Override
    public boolean transfer(String fromPin, String toPin, double amount) throws Exception{

        if(amount<=0){
            throw new UnacceptableAmountException("Amount must be greater that 0");
        }

        if(fromPin.equals(toPin)){
            throw new SameAccountException("Use different account for transferring to.");
        }

        Optional<UserEntity> toUserOpt = repository.findByPin(toPin);
        if(toUserOpt.isEmpty()){
            throw new AccountNotFoundException("Account not found: " + toPin);
        }

        Optional<UserEntity> fromUserOpt = repository.findByPin(fromPin);
        if(fromUserOpt.isEmpty()){
            throw new AccountNotFoundException("Account not found: " + fromPin);
        }

        UserEntity toUser = toUserOpt.get();

        if(toUser.getStatusId()!= Constants.UserStatuses.Active.id)
        {
            throw new TransferToDeactiveAccountException("Transfer to deactive account is not allowed.");
        }

        UserEntity fromUser = fromUserOpt.get();

        if(fromUser.getBalance() < amount){
            throw new UnacceptableAmountException("Amount can not be bigger than balance");
        }

        fromUser.setBalance( fromUser.getBalance() - amount );
        toUser.setBalance( toUser.getBalance() + amount );

        repository.save(fromUser);
        repository.save(toUser);

        return true;

    }

    @Override
    @Cacheable("currency")
    @Scheduled(fixedRateString = "60000")
    public double getCurrencyRate(String fromCurrency, String toCurrency) {
        return currencyRateService.getCurrencyRate(fromCurrency, toCurrency);
    }
}
