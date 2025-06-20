package org.allen.erpoor.account;


import org.allen.erpoor.account.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * AccountRepository 介面。
 * 繼承 JpaRepository 以提供 AccountEntity 的基本 CRUD 操作。
 *
 */
@Repository // 標記此介面為一個 Spring 管理的 Repository 組件
public interface AccountRepository extends JpaRepository<AccountEntity, Integer> {

    /**
     * 根據使用者名稱 (username) 查找一個帳戶。
     * 不套用軟刪除的查詢，無論帳戶是否被標記為刪除都會被找到。
     *
     * @param username 要查找的使用者名稱
     * @return 包含找到的 AccountEntity 的 Optional 物件，如果找不到則為空的 Optional
     */
    Optional<AccountEntity> findByUsername(String username);

    /**
     * 根據使用者名稱和刪除標記 (deleted) 查找一個帳戶。
     * 用於軟刪除的查詢，可選是否查找被標記為刪除的帳戶。
     *
     * @param username 要查找的使用者名稱
     * @param deleted  帳戶的刪除狀態 (true 或 false)
     * @return 包含找到的 AccountEntity 的 Optional 物件，如果找不到則為空的 Optional
     */
    Optional<AccountEntity> findByUsernameAndDeleted(String username, Boolean deleted);

    /**
     * 根據使用者 ID (user_id) 和刪除標記 (deleted) 查找一個帳戶。
     * 用於軟刪除的查詢，可選是否查找被標記為刪除的帳戶。
     *
     * @param userId   要查找的使用者 ID
     * @param deleted  帳戶的刪除狀態 (true 或 false)
     * @return 包含找到的 AccountEntity 的 Optional 物件，如果找不到則為空的 Optional
     */
    Optional<AccountEntity> findByUserIdAndDeleted(Integer userId, Boolean deleted);
}
