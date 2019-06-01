package cn.dezhe.orm.core;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Author dezhe
 * @Date 2019/5/31 16:29
 */
public class ORMSession {

    private Connection connection;

    public ORMSession(Connection connection) {
        this.connection = connection;
    }

    /**
     * 保存方法
     */
    public void save(Object entity) throws Exception{

        String insertSQL = "";

        //1.从ORMConfig获得映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;
        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(entity.getClass().getName())){
                String tableName = mapper.getTableName();
                String insertSQL1 = "insert into "+ tableName +"( ";
                String insertSQL2 = ") values ( ";

                //得到当前类所有的属性
                Field[] fields = entity.getClass().getDeclaredFields();
                for (Field field : fields) {
                    field.setAccessible(true);
                    //得到字段名
                    String columnName = mapper.getPropMapper().get(field.getName());
                    //根据属性得到它的值
                    String columnValue = field.get(entity).toString();
                    //拼接sql语句
                    insertSQL1 += columnName + ",";
                    insertSQL2 += "'"+columnValue+"',";
                }
                insertSQL = insertSQL1.substring(0,insertSQL1.length()-1) + insertSQL2.substring(0,insertSQL2.length()-1)+")";
                break;
            }
        }

        //将sql输出到控制台上
        System.out.println("dezheORM"+insertSQL);

        //通过JDBC发送并执行sql
        PreparedStatement preparedStatement = connection.prepareStatement(insertSQL);
        preparedStatement.executeUpdate();
        preparedStatement.close();

    }

    /**
     * 根据主键进行数据删除，  delete from 表名 where 主键  =  值
     * @param entity  实体类
     */
    public void delete(Object entity) throws Exception{

        String delSQL = "delete from ";

        //1.从ORMConfig获得映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;
        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(entity.getClass().getName())){
                String tableName = mapper.getTableName();
                delSQL += tableName + " where ";
                //得到主键名
                Object[] idProp = mapper.getIdMapper().keySet().toArray();  //idProp[0]
                Object[] idColumn = mapper.getIdMapper().values().toArray(); //idColumn[0]

                Field field = entity.getClass().getDeclaredField(idProp[0].toString());
                field.setAccessible(true);
                String idVal = field.get(entity).toString();

                delSQL += idColumn[0].toString() + " = " + idVal;


                break;
            }
        }
        //将sql输出到控制台上
        System.out.println("dezheORMdele"+delSQL);

        //通过JDBC发送并执行sql
        PreparedStatement preparedStatement = connection.prepareStatement(delSQL);
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * 根据主键进行查询
     * @param clz  查询的实体类
     * @param id   主键
     * @return  返回对应的实体类
     */
    public Object findOne(Class clz,Object id) throws Exception{

        String querySQL = "select *　from ";
        //1.从ORMConfig获得映射信息的集合
        List<Mapper> mapperList = ORMConfig.mapperList;
        for (Mapper mapper : mapperList) {
            if (mapper.getClassName().equals(clz.getName())){
                String tableName = mapper.getTableName();

                //得到主键名
                Object[] idProp = mapper.getIdMapper().keySet().toArray();  //idProp[0]

                querySQL += tableName + " where " + idProp[0].toString() + " = " + id;
                break;
            }
        }

        //将sql输出到控制台上
        System.out.println("dezheORMFindne"+querySQL);

        //通过JDBC发送并执行sql
        PreparedStatement preparedStatement = connection.prepareStatement(querySQL);
        ResultSet resultSet = preparedStatement.executeQuery();

        //封装结果集，返回对象
        if (resultSet.next()){
            //查询到一行数据   有数据   目前属性的值都是初始值
            Object obj = clz.newInstance();
            for (Mapper mapper : mapperList) {
                if (mapper.getClassName().equals(clz.getName())){
                    Map<String, String> propMapper = mapper.getPropMapper();

                    Set<String> keySet = propMapper.keySet();
                    for (String prop : keySet) {
                        String column = propMapper.get(prop);
                        Field field = clz.getDeclaredField(prop);
                        field.setAccessible(true);
                        field.set(obj,resultSet.getObject(column));
                    }
                    break;
                }
            }
            preparedStatement.close();
            resultSet.close();
            return obj;
        } else {
            System.out.println("没有查到数据");
            return null;
        }

    }

    /**
     * 关闭连接，释放资源
     */
    public void close() throws Exception {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }
}
