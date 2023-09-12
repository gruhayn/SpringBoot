package com.unitech.api.service;

import com.unitech.api.constants.Constants;
import com.unitech.api.exceptions.*;
import com.unitech.api.models.UserEntity;
import com.unitech.api.repository.RegistrationRepository;
import com.unitech.api.service.impl.MoneyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MoneyServiceTests {

    @Mock
    private CurrencyRateService currencyRateService;

    @Mock
    private RegistrationRepository registrationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private MoneyServiceImpl transferService;

    private UserEntity account1;
    private UserEntity account2;

    private final String AZN = "AZN";
    private final String TL = "TL";



    @BeforeEach
    public void init() {
        account1 = UserEntity.builder()
                .pin("pin1")
                .password("pass1")
                .statusId(1)
                .build();

        account2 = UserEntity.builder()
                .pin("pin2")
                .password("pass2")
                .statusId(1)
                .build();
    }

    @Test
    public void TransferService_Transfer_ThrowsUnacceptableAmountException() {

        assertThrows(UnacceptableAmountException.class, () -> {
            transferService.transfer(
                    account1.getPin(),
                    account2.getPin(),
                    0);
        });

    }

    @Test
    public void TransferService_Transfer_ThrowsSameAccountException() {

        assertThrows(SameAccountException.class, () -> {
            transferService.transfer(
                    account1.getPin(),
                    account1.getPin(),
                    1);
        });

    }

    @Test
    public void TransferService_Transfer_ThrowsTransferToDeactiveAccountException() {

        account2.setStatusId(Constants.UserStatuses.Passive.id);

        when(registrationRepository.findByPin(Mockito.any(String.class)))
                .thenReturn(Optional.of(account2));

        assertThrows(TransferToDeactiveAccountException.class, () -> {
            transferService.transfer(
                    account1.getPin(),
                    account2.getPin(),
                    1);
        });

    }

    @Test
    public void TransferService_Transfer_ThrowsAccountNotFoundException() {

        assertThrows(AccountNotFoundException.class, () -> {
            transferService.transfer(
                    account1.getPin(),
                    account2.getPin(),
                    1);
        });

    }

    @Test
    public void TransferService_Transfer_AmountIsBiggerThanBalanceThrowsUnacceptableAmountException() {

        account1.setBalance(10.0);

        when(registrationRepository.findByPin(Mockito.any(String.class)))
                .thenReturn(Optional.of(account1));

        assertThrows(UnacceptableAmountException.class, () -> {
            transferService.transfer(
                    account1.getPin(),
                    account2.getPin(),
                    11);
        });
    }

    @Test
    public void TransferService_Transfer_CheckIfBalanceChanged() throws Exception{

        account1.setBalance(10.0);

        account2.setBalance(15.0);

        when(registrationRepository.findByPin(account1.getPin()))
                .thenReturn(Optional.of(account1));

        when(registrationRepository.findByPin(account2.getPin()))
                .thenReturn(Optional.of(account2));

        transferService.transfer(
                account1.getPin(),
                account2.getPin(),
                3
        );

        assertThat(account1.getBalance()).isEqualTo(7);
        assertThat(account2.getBalance()).isEqualTo(18);


    }


    @Test
    public void TransferService_GetCurrencyRate_ReturnsRate(){

        double rate = 10.0;

        when(currencyRateService.getCurrencyRate(AZN, TL))
                .thenReturn(rate);

        double result = transferService.getCurrencyRate(AZN, TL);

        assertThat(result).isEqualTo(rate);
    }








    }
