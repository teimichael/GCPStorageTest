package stu.napls.nabootweb.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;
import stu.napls.nabootweb.auth.annotation.Auth;
import stu.napls.nabootweb.config.property.StaticServer;
import stu.napls.nabootweb.core.dictionary.StaticPath;
import stu.napls.nabootweb.core.exception.Assert;
import stu.napls.nabootweb.core.response.Response;
import stu.napls.nabootweb.model.User;
import stu.napls.nabootweb.service.StorageService;
import stu.napls.nabootweb.service.UserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Author Tei Michael
 * @Date 12/28/2019
 */
@RestController
@RequestMapping("/user")
public class UserController extends BaseController{

    @Resource
    private UserService userService;

    @Resource
    private StorageService storageService;

    @Resource
    private StaticServer staticServer;

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    @Auth
    @GetMapping("/get/info")
    public Response getInfo() {
        return Response.success(getSessionUser());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    @Auth
    @GetMapping("/get/list")
    public Response getList() {
        return Response.success(userService.findAllUser());
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "Authorization", value = "Authorization token",
                    required = true, dataType = "string", paramType = "header")})
    @Auth
    @PostMapping("/post/avatar")
    public Response postAvatar(@RequestParam MultipartFile avatar) throws IOException {
        User user = getSessionUser();
        Assert.notNull(user, "Authentication failed");
        String name = storageService.storeImage(avatar, StaticPath.AVATAR, user.getId() + "_avatar");
        user.setAvatar(staticServer.getUrl() + StaticPath.AVATAR + name);
        return Response.success(userService.update(user));
    }
}
