package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.AccountEntity;

/**
 * 提供帳號管理相關的服務操作，如查詢、儲存與刪除帳號。
 */
public interface AccountService {

    /**
     * 根據使用者名稱查詢帳號資料。
     *
     * @param username 使用者名稱（登入帳號）
     * @return 回傳對應的 {@link AccountEntity}，若查無帳號則回傳 null
     */
    AccountEntity findAccountByUsername(String username);

    /**
     * 儲存或更新帳號資料。
     *
     * @param account 欲儲存的帳號實體
     * @return 若儲存成功則回傳 true，否則回傳 false
     */
    Boolean saveAccount(AccountEntity account);

    /**
     * 根據使用者名稱刪除帳號資料。
     *
     * @param username 欲刪除的使用者名稱
     * @return 若刪除成功則回傳 true，否則回傳 false（例如找不到帳號）
     */
    Boolean deleteAccountByUsername(String username);

    /**
     * 建立一個新的帳號。
     *
     * @param username 新帳號的使用者名稱
     * @param password 新帳號的密碼
     */
    void createAccount(String username, String password);

    /**
     * 根據使用者 ID 查詢帳號資料。
     *
     * @param userId 使用者 ID
     * @return 回傳對應的 {@link AccountEntity}，若查無帳號則回傳 null
     */
    AccountEntity findAccountByUserId(Integer userId);
}
