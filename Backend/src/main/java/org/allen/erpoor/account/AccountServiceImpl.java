package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.AccountEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AccountEntity findAccountByUsername(String username) {
        return accountRepository.findByUsernameAndDeleted(username, false).orElse(null);
    }

    @Override
    public Boolean saveAccount(AccountEntity account) {

        return null;
    }

    @Override
    public Boolean deleteAccountByUsername(String username) {
        return null;
    }

    @Override
    public void createAccount(String username, String password) {
        AccountEntity account = new AccountEntity();
        account.setUsername(username);
        account.setPassword(passwordEncoder.encode(password));
        accountRepository.save(account);
    }

    @Override
    public AccountEntity findAccountByUserId(Integer userId) {
        return accountRepository.findByUserIdAndDeleted(userId, false).orElse(null);
    }
}
