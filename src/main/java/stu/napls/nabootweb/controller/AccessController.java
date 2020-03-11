package stu.napls.nabootweb.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import stu.napls.nabootweb.auth.annotation.Auth;
import stu.napls.nabootweb.auth.model.*;
import stu.napls.nabootweb.auth.request.AuthRequest;
import stu.napls.nabootweb.config.GlobalConstant;
import stu.napls.nabootweb.core.dictionary.ResponseCode;
import stu.napls.nabootweb.core.exception.Assert;
import stu.napls.nabootweb.core.response.Response;
import stu.napls.nabootweb.model.User;
import stu.napls.nabootweb.service.UserService;
import stu.napls.nabootweb.socket.model.SocketResponse;
import stu.napls.nabootweb.socket.model.SocketThirdRegister;
import stu.napls.nabootweb.socket.request.SocketRequest;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * @Author Tei Michael
 * @Date 12/27/2019
 */
@RestController
@RequestMapping("/access")
public class AccessController extends BaseController {

    @Resource
    private AuthRequest authRequest;

    @Resource
    private SocketRequest socketRequest;

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public Response login(@RequestBody AuthLogin authLogin) {
        authLogin.setSource(GlobalConstant.SERVICE_ID);
        AuthResponse authResponse = authRequest.login(authLogin);

        Assert.notNull(authResponse, "Authentication failed.");
        Assert.isTrue(authResponse.getCode() == ResponseCode.SUCCESS, authResponse.getMessage());

        return Response.success("Login successfully.", authResponse.getData());
    }

    @PostMapping("/register")
    public Response register(@RequestParam String username, @RequestParam String password, @RequestBody User user) {
        AuthPreregister authPreregister = new AuthPreregister();
        authPreregister.setUsername(username);
        authPreregister.setPassword(password);
        authPreregister.setSource(GlobalConstant.SERVICE_ID);
        AuthResponse authResponse = authRequest.preregister(authPreregister);
        Assert.notNull(authResponse, "Preregistering auth server failed.");
        Assert.isTrue(authResponse.getCode() == ResponseCode.SUCCESS, authResponse.getMessage());
        String uuid = authResponse.getData().toString();

        // TODO Register third services.
        SocketThirdRegister socketThirdRegister = new SocketThirdRegister();
        socketThirdRegister.setUuid(uuid);
        SocketResponse socketResponse = socketRequest.registerFromThird(socketThirdRegister);
        Assert.notNull(socketResponse, "Registering socket server failed");

        user.setUuid(uuid);
        userService.update(user);

        AuthRegister authRegister = new AuthRegister();
        authRegister.setUuid(uuid);
        authResponse = authRequest.register(authRegister);
        Assert.isTrue(authResponse != null && authResponse.getCode() == ResponseCode.SUCCESS, "Register failed. Please contact the administrator.");

        return Response.success("Register successfully");
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    @Auth
    @PostMapping("/logout")
    public Response logout() {
        AuthLogout authLogout = new AuthLogout();
        authLogout.setUuid(getSessionUserUUID());
        AuthResponse authResponse = authRequest.logout(authLogout);
        Assert.notNull(authResponse, "Authentication failed.");
        Assert.isTrue(authResponse.getCode() == ResponseCode.SUCCESS, "Logout failed.");
        return Response.success("Logout successfully.");
    }

}
