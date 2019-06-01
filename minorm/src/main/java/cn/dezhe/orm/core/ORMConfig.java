package cn.dezhe.orm.core;

import cn.dezhe.orm.utils.AnnotationUtil;
import cn.dezhe.orm.utils.Dom4jUtil;
import org.dom4j.Document;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 该类用来解析封装框架的核心配置文件中的数据
 */
public class ORMConfig {

    /**
     * classpath路径  配置文件路径
     */
    private static String classpath;

    /**
     * 核心配置文件
     */
    private static File cfgFile;

    /**
     * property  标签中的数据
     */
    private static Map<String,String> propConfig;

    /**
     * 映射配置文件路径
     */
    private static Set<String> mappingSet;

    /**
     * 实体类
     */
    private static Set<String> entitySet;

    /**
     * 映射信息
     */
    public static List<Mapper> mapperList;

    static {
        //得到classpath路径 （根路径）
        classpath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        try {
            //防止中文乱码
            classpath = URLDecoder.decode(classpath,"utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //得到核心配置文件
        cfgFile = new File(classpath +"dezheORM.cfg.xml");
        //判断文件是否为空
        if (cfgFile.exists()){
            Document document = Dom4jUtil.getXmlByFilePath(cfgFile.getPath());
            propConfig = Dom4jUtil.elements2Map(document,"property","name");
            mappingSet = Dom4jUtil.elements2Set(document,"mapping","resource");
            entitySet = Dom4jUtil.elements2Set(document,"entity","package");
        } else {
            cfgFile = null;
            System.out.println("未找到核心配置文件dezheORM.cfg.xml");
        }
    }

    /**
     * 从propConfig集合中获取数据 并连接数据库
     * @return
     */
    private Connection getConnection() throws Exception {
        String url = propConfig.get("connection.url");
        String driverClass = propConfig.get("connection.driverClass");
        String username = propConfig.get("connection.username");
        String password = propConfig.get("connection.password");
        Class.forName(driverClass);
        Connection connection = DriverManager.getConnection(url, username, password);
        //开启自动提交事务
        connection.setAutoCommit(true);
        return connection;
    }

    /**
     * 获取到映射文件里数据
     * @throws Exception   抛异常
     */
    private void getMapping() throws Exception {

        mapperList = new ArrayList<>();

        //1.解析xxx.mapper.xml文件拿到映射数据
        for (String xmlPath : mappingSet) {
            Document document = Dom4jUtil.getXmlByFilePath(classpath + xmlPath);
            String className = Dom4jUtil.getPropValue(document, "class", "name");
            String tableName = Dom4jUtil.getPropValue(document, "class", "table");
            Map<String, String> id_id = Dom4jUtil.elementsID2Map(document);
            Map<String, String> mapping = Dom4jUtil.elements2Map(document);

            Mapper mapper = new Mapper();
            mapper.setClassName(className);
            mapper.setTableName(tableName);
            mapper.setIdMapper(id_id);
            mapper.setPropMapper(mapping);
            mapperList.add(mapper);
        }


        //2.解析实体类中的注解拿到映射数据
        for (String annotationPath : entitySet) {
            Set<String> nameSet = AnnotationUtil.getClassNameByPackage(annotationPath);
            for (String entityName : nameSet) {
                Class clz = Class.forName(entityName);
                String className = AnnotationUtil.getClassName(clz);
                String tableName = AnnotationUtil.getTableName(clz);
                Map<String, String> id_id = AnnotationUtil.getIdMapper(clz);
                Map<String, String> mapping = AnnotationUtil.getPropMapping(clz);

                Mapper mapper = new Mapper();
                mapper.setClassName(className);
                mapper.setTableName(tableName);
                mapper.setIdMapper(id_id);
                mapper.setPropMapper(mapping);
                mapperList.add(mapper);
            }
        }

    }

    /**
     * 创建ORMSession对象
     * @return
     */
    public ORMSession buildORMSession() throws Exception {

        //1.连接数据库
        Connection connection = getConnection();

        //2.得到映射数据
        getMapping();

        //3.创建ORMSession对象
        return new ORMSession(connection);
    }
}
