package com.example.moneymanager.model.bean;
/*
* 表示收入或支出具体类型的类
* */
public class TypeBean {
    int id;
    String typename;    //类型名称
    int imageId;    //未被选中的图片id
    int sImageId;   //被选中的图片id
    int kind;   //支出：0 收入：1

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getsImageId() {
        return sImageId;
    }

    public void setsImageId(int simageId) {
        this.sImageId = simageId;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public TypeBean() {
    }

    public TypeBean(int id, String typename, int imageId, int sImageId, int kind) {
        this.id = id;
        this.typename = typename;
        this.imageId = imageId;
        this.sImageId = sImageId;
        this.kind = kind;
    }
}
