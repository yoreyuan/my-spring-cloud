package yore.entity;

import lombok.Data;

/**
 * Created by yore on 2019/7/6 15:30
 */
@Data
public class User implements java.io.Serializable{
    private Long id;
    private String name;
    private int age;
}
