package cn.dezhe.orm.test.dao;

import cn.dezhe.orm.core.ORMConfig;
import cn.dezhe.orm.core.ORMSession;
import cn.dezhe.orm.test.entity.Book;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author dezhe
 * @Date 2019/6/1 10:29
 */
public class BookDao {

    private ORMConfig config;

    @Before
    public void init(){
        config = new ORMConfig();
    }

    @Test
    public void testSave() throws Exception{
        ORMSession session = config.buildORMSession();
        Book book = new Book();
        book.setId(2);
        book.setName("java");
        book.setAuthor("德哲");
        book.setPrice(9.9);
        session.save(book);

        session.close();
    }

    @Test
    public void testFindByOne() throws Exception{
        ORMSession session = config.buildORMSession();
        Book one = (Book) session.findOne(Book.class, 2);
        System.out.println(one);
        session.close();
    }

    @Test
    public void testdelete() throws Exception{
        ORMSession session = config.buildORMSession();
        Book book = new Book();
        book.setId(2);
        session.delete(book);

        session.close();
    }
}
