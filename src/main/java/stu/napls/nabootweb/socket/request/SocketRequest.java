package stu.napls.nabootweb.socket.request;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import stu.napls.nabootweb.config.property.SocketServer;
import stu.napls.nabootweb.socket.model.SocketResponse;
import stu.napls.nabootweb.socket.model.SocketThirdRegister;

import javax.annotation.Resource;

@Component
public class SocketRequest {

    @Resource
    private SocketServer socketServer;

    private static final String THIRD_REGISTER = "/access/third/register";

    private RestTemplate restTemplate = new RestTemplate();

    public SocketResponse registerFromThird(SocketThirdRegister socketThirdRegister) {
        return restTemplate.postForObject(socketServer.getUrl() + THIRD_REGISTER, socketThirdRegister, SocketResponse.class);
    }
}
