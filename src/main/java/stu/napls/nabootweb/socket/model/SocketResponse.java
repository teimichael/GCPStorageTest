package stu.napls.nabootweb.socket.model;

import lombok.Data;

@Data
public class SocketResponse {

    private int code;
    private String message;
    private Object data;

}
