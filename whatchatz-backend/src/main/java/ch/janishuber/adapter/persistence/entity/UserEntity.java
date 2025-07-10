package ch.janishuber.adapter.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = @UniqueConstraint(columnNames = {"name"}))
public class UserEntity {
    @Id
    private String uid;

    @Column(name = "name")
    private String name;

    @Column(name = "info")
    private String info;

    public UserEntity() {
    }

    public UserEntity(String uid, String name, String info) {
        this.uid = uid;
        this.name = name;
        this.info = info;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}