package org.allen.erpoor.account;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AccountRepository accountRepository;

    public UserDetailsServiceImpl(AccountRepository repository) {
        this.accountRepository = repository;
    }
    /**
     * 用於根據使用者名稱載入使用者詳細資料的服務實現。
     *
     * @param username 使用者名稱
     * @return 返回對應的 UserDetails 物件
     * @throws UsernameNotFoundException 如果找不到對應的使用者
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) accountRepository.findByUsernameAndDeleted(username, false).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}
