package com.example.jwtserver.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String username;
    private String password;
    private String roles; // user, admin
                          // 만약 role이 하나라면 getRoleList를 만들 필요가 없다.
                          // 혹은 Role 테이블을 따로 만들어도 된다. private Role role;

    public List<String> getRoleList(){
        if (roles.length() > 0){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>(); // null 방지용
   }


}
