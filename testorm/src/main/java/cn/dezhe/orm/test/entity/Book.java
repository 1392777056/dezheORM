package cn.dezhe.orm.test.entity;

import cn.dezhe.orm.annotation.ORMColumn;
import cn.dezhe.orm.annotation.ORMId;
import cn.dezhe.orm.annotation.ORMTable;

/**
 * @Author dezhe
 * @Date 2019/5/31 21:39
 */
@ORMTable(name = "t_book")
public class Book {

    @ORMId
    @ORMColumn(name = "bid")
    private Integer id;

    @ORMColumn(name = "bname")
    private String name;

    @ORMColumn(name = "author")
    private String author;

    @ORMColumn(name = "price")
    private double price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
