package application.repository;

import application.model.Balance;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface BalanceRepository extends CrudRepository<Balance, String> {}
