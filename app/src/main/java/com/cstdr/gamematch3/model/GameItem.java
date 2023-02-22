package com.cstdr.gamematch3.model;

/**
 * 游戏图标类
 */
public class GameItem {

    private int image;
    private String personName;

    private int type;

    public GameItem(int type, String personName, int image) {
        this.personName = personName;
        this.type = type;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getPersonName() {
        return personName;
    }


    @Override
    public String toString() {
        return "GameItem{" +
                "image=" + image +
                ", personName='" + personName + '\'' +
                ", type=" + type +
                '}';
    }

    public int getType() {
        return type;
    }
}
