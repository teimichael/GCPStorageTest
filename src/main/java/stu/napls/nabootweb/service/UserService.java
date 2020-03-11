package stu.napls.nabootweb.service;

import stu.napls.nabootweb.model.User;

import java.util.List;

public interface UserService {
    User update(User user);

    User findUserByUuid(String uuid);

    List<User> findAllUser();

}
