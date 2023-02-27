package com.cstdr.gamematch3.model;

/**
 * 游戏图标类
 */
public class GameItem {

    private int property;
    private int showStatus;
    private int image;
    private String personName;
    private int type;
    private int x;
    private int y;

    public GameItem(int showStatus, int x, int y, int type, int property, String personName, int image) {
        this.showStatus = showStatus;
        this.x = x;
        this.y = y;
        this.personName = personName;
        this.type = type;
        this.image = image;
        this.property = property;
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
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    public int getType() {
        return type;
    }

//    public int getX() {
//        return x;
//    }

//    public void setX(int x) {
//        this.x = x;
//    }

//    public int getY() {
//        return y;
//    }

//    public void setY(int y) {
//        this.y = y;
//    }

    public int getShowStatus() {
        return showStatus;
    }

    public void setShowStatus(int showStatus) {
        this.showStatus = showStatus;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getProperty() {
        return property;
    }

    public void setProperty(int property) {
        this.property = property;
    }
}
