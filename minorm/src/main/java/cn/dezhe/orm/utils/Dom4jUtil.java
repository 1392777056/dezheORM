package cn.dezhe.orm.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * 基于dom4j的工具类
 */
public class Dom4jUtil {

    /**
     * 通过文件的路径获取xml的document对象
     * @param path  文件路径
     * @return  返回文档对象
     */
    public static Document getXmlByFilePath(String path){
        if (null == path) {
            return null;
        }
        Document document = null;
        try {
            SAXReader reader = new SAXReader();
            document = reader.read(new File(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return document;
    }

    /**
     * 获取某文档中某元素内某属性的值和元素文本信息
     *
     * @param document   xml文档对象
     * @param elementName  元素名
     * @param attrName    属性名
     * @return   返回一个map集合
     */
    public static Map<String,String> elements2Map(Document document,String elementName,String attrName){
        List<Element> propList = document.getRootElement().elements(elementName);
        Map<String, String> map = new HashMap<>();
        for (Element element : propList) {
            String key = element.attribute(attrName).getValue();
            String value = element.getTextTrim();
            map.put(key,value);
        }
        return map;
    }

    /**
     * 针对mapper.xml文件，获取映射信息并保存到Map集合中
     *
     * @param document   xml文档对象
     * @return   返回一个map集合
     */
    public static Map<String,String> elements2Map(Document document){
        Element classElement = document.getRootElement().element("class");
        Map<String, String> map = new HashMap<>();

        Element idElement = classElement.element("id");
        String idKey = idElement.attribute("name").getValue();
        String idValue = idElement.attribute("column").getValue();
        map.put(idKey,idValue);

        List<Element> propElements = classElement.elements("property");
        for (Element element : propElements) {
            String propKey = element.attribute("name").getValue();
            String propValue = element.attribute("column").getValue();
            map.put(idKey,idValue);
        }
        return map;
    }

    /**
     * 针对mapper.xml文件  获取主键的映射信息并保存到map集合中
     *
     * @param document   xml文档对象
     * @return   返回一个map集合
     */
    public static Map<String,String> elementsID2Map(Document document){
        Element classElement = document.getRootElement().element("class");
        Map<String, String> map = new HashMap<>();

        Element idElement = classElement.element("id");
        String idKey = idElement.attribute("name").getValue();
        String idValue = idElement.attribute("column").getValue();
        map.put(idKey,idValue);

        return map;
    }

    /**
     * 获取某文档中某元素内某属性的值
     *
     * @param document   xml文档对象
     * @param elementName  元素名
     * @param attrName    属性名
     * @return   返回一个set集合
     */
    public static Set<String> elements2Set(Document document,String elementName,String attrName) {
        List<Element> mappingList = document.getRootElement().elements(elementName);
        Set<String> set = new HashSet<>();

        for (Element element : mappingList) {
            String value = element.attribute(attrName).getValue();
            set.add(value);
        }
        return set;
    }

    /**
     * 获取文档中某元素内某属性的值
     *
     * @param document    xml文档对象
     * @param elementName  元素名
     * @param attrName     属性名
     * @return   返回一个string类型
     */
    public static String getPropValue(Document document,String elementName,String attrName) {
        Element element = (Element) document.getRootElement().elements(elementName).get(0);
        return element.attribute(attrName).getValue();
    }
}
