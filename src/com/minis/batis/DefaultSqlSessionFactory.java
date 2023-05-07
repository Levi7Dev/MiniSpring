package com.minis.batis;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import com.minis.beans.factory.annotation.Autowired;
import com.minis.jdbc.core.JdbcTemplate;


public class DefaultSqlSessionFactory implements SqlSessionFactory {
    @Autowired
    JdbcTemplate jdbcTemplate;

    String mapperLocations;

    Map<String, MapperNode> mapperNodeMap = new HashMap<>();

    public DefaultSqlSessionFactory() {
    }

    //配置文件中配置了该方法为初始化方法，在bean初始化的时候会执行
    public void init() {
        scanLocation(this.mapperLocations);
    }

    private void scanLocation(String location) {
        //获取资源目录下的文件
        String sLocationPath = this.getClass().getClassLoader().getResource("").getPath() + location;
        File dir = new File(sLocationPath);
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                scanLocation(location + "/" + file.getName());
            } else {
                buildMapperNodes(location + "/" + file.getName());
            }
        }
    }

    private Map<String, MapperNode> buildMapperNodes(String filePath) {
        System.out.println(filePath);
        SAXReader saxReader = new SAXReader();
        URL xmlPath = this.getClass().getClassLoader().getResource(filePath);
        try {
            Document document = saxReader.read(xmlPath);
            Element rootElement = document.getRootElement();

            String namespace = rootElement.attributeValue("namespace");

            Iterator<Element> nodes = rootElement.elementIterator();
            while (nodes.hasNext()) {
                Element node = nodes.next();
                String id = node.attributeValue("id");
                String parameterType = node.attributeValue("parameterType");
                String resultType = node.attributeValue("resultType");
                String sql = node.getText();
                //将信息存入
                MapperNode selectnode = new MapperNode();
                selectnode.setNamespace(namespace);
                selectnode.setId(id);
                selectnode.setParameterType(parameterType);
                selectnode.setResultType(resultType);
                selectnode.setSql(sql);
                selectnode.setParameter("");
                //命名空间+id的形式存入
                this.mapperNodeMap.put(namespace + "." + id, selectnode);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return this.mapperNodeMap;
    }

    @Override
    public SqlSession openSession() {
        SqlSession newSqlSession = new DefaultSqlSession();
        newSqlSession.setJdbcTemplate(jdbcTemplate);
        newSqlSession.setSqlSessionFactory(this);

        return newSqlSession;
    }

    public String getMapperLocations() {
        return mapperLocations;
    }

    public void setMapperLocations(String mapperLocations) {
        this.mapperLocations = mapperLocations;
    }

    public Map<String, MapperNode> getMapperNodeMap() {
        return mapperNodeMap;
    }

    public MapperNode getMapperNode(String name) {
        return this.mapperNodeMap.get(name);
    }
}