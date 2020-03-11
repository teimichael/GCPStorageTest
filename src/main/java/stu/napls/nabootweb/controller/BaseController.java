package stu.napls.nabootweb.controller;

import stu.napls.nabootweb.model.User;
import stu.napls.nabootweb.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

public class BaseController {

    @Resource
    private UserService userService;

    @Resource
    HttpSession httpSession;

    public User getSessionUser() {
        return userService.findUserByUuid(this.httpSession.getAttribute("uuid").toString());
    }

    public String getSessionUserUUID() {
        return this.httpSession.getAttribute("uuid").toString();
    }
}
