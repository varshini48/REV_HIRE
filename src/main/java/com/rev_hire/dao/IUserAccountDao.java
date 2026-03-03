package com.rev_hire.dao;

import com.rev_hire.model.UserAccount;

public interface IUserAccountDao {



    UserAccount login(String email, String password, String role);

    boolean emailExists(String email);

    boolean addUserAccount(UserAccount userAccount);

    boolean updatePasswordByEmail(String email, String newPassword);
}
