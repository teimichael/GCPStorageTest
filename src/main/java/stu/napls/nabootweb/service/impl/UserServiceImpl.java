package stu.napls.nabootweb.service.impl;

import org.springframework.stereotype.Service;
import stu.napls.nabootweb.model.User;
import stu.napls.nabootweb.repository.UserRepository;
import stu.napls.nabootweb.service.UserService;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author Tei Michael
 * @Date 12/29/2019
 */
@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserRepository userRepository;

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserByUuid(String uuid) {
        return userRepository.findByUuid(uuid);
    }

    @Override
    public List<User> findAllUser() {
        return userRepository.findAll();
    }
}
