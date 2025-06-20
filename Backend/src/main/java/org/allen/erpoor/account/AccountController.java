package org.allen.erpoor.account;

import org.allen.erpoor.account.entity.AccountEntity;
import org.allen.erpoor.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class AccountController {

    @Autowired
    private AccountRepository accountService;

    @GetMapping("/{username}") // 處理 GET 請求，並從路徑中獲取 'username' 變數
    public CommonResponse<AccountEntity> getAccountByUsername(@PathVariable String username) { // 接收 @PathVariable

        // 呼叫服務層方法來查找帳戶，返回 Optional<AccountDto>
        Optional<AccountEntity> accountDtoOptional = accountService.findByUsername(username);

        // 判斷是否找到帳戶
        if (accountDtoOptional.isPresent()) {
            // 如果找到，則從 Optional 中取出 AccountDto 並成功回傳
            return CommonResponse.success(accountDtoOptional.get());
        } else {
            // 如果找不到，則回傳錯誤訊息
            return CommonResponse.error(404, "找不到使用者");
        }
    }
}
