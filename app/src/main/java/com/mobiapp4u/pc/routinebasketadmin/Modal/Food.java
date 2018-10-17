package com.mobiapp4u.pc.routinebasketadmin.Modal;

public class Food {

    private String name1;
    private  String image;
    private String description;
    private String price;
    private String menuId;
    private String discount;

    public Food() {
    }

    public Food(String name1, String image, String description, String price, String menuId, String discount) {
        this.name1 = name1;
        this.image = image;
        this.description = description;
        this.price = price;
        this.menuId = menuId;
        this.discount = discount;
    }

    public String getName1() {
        return name1;
    }

    public void setName1(String name1) {
        this.name1 = name1;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }
}
