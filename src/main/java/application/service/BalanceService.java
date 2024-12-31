package application.service;

import application.model.response.Balance;
import application.repository.BalanceRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BalanceService {

    private BalanceRepository balanceRepository;

    public Optional<Balance> getBalance(String userId) {
        return balanceRepository.findById(userId);
    }
}
