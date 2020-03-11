package stu.napls.nabootweb.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BlobVO {

    private String name;

    private String contentType;

    private Long size;

    private String md5;

    private Long createTime;

    private Long updateTime;

}
