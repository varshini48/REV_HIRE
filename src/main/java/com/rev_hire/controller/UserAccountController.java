package com.rev_hire.controller;

import com.rev_hire.dao.IUserAccountDao;
import com.rev_hire.dao.UserAccountDaoImpl;
import com.rev_hire.model.UserAccount;
import com.rev_hire.service.IUserAccountService;
import com.rev_hire.service.UserAccountServiceImpl;

import java.sql.SQLException;
import java.util.List;

public class UserAccountController {

    private static IUserAccountService userAccountService = new UserAccountServiceImpl();



    public UserAccount login(String email, String password, String role) {
        return userAccountService.login(email, password, role);
    }

    public boolean register(UserAccount user) throws SQLException {
        return userAccountService.addUserAccount(user);
    }

    public boolean resetPassword(String email, String newPassword) {
        return userAccountService.resetPasswordByEmail(email, newPassword);
    }

    public boolean isEmailExists(String email) {
        return userAccountService.emailExists(email);
    }
}
