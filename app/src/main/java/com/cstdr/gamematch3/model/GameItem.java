package com.cstdr.gamematch3.model;

/**
 * 游戏图标类
 */
public class GameItem {

    private int image;
    private int personId;
    private String personName;

    private int type;

    public GameItem(int personId, int type, String personName, int image) {
        this.personName = personName;
        this.type = type;
        this.image = image;
        this.personId = personId;
    }

    public int getImage() {
        return image;
    }

//    public int getPersonId() {
//        return personId;
//    }

    public String getPersonName() {
        return personName;
    }


    @Override
    public String toString() {
        return "GameItem{" +
                "image=" + image +
                ", personId=" + personId +
                ", personName='" + personName + '\'' +
                ", type=" + type +
                '}';
    }

    public int getType() {
        return type;
    }
}
