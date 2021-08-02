package com.w.t.sqlparser.SQLObject;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.w.t.sqlparser.SqlParserBefore.SqlParserUtil;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Packagename com.w.t.sqlparser.SQLObject
 * @Classname main
 * @Description
 * @Authors Mr.Wu
 * @Date 2021/07/28 15:47
 * @Version 1.0
 */
public class main {
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //String test="select  a from  b " +
        //    "\n"+"where      a=b";
        //test=test.replaceAll("\\s{1,}", " ");
        //System.out.println(test);
        //程序的入口
        String testSql = "select a.title,a.uUername,b.adddate from table a,(select max(adddate) adddate from table where table.title=a.title) b";    //需要解析的sql语句
        testSql = "select tempa.name ,round(a.index_value,2) as index_value,round(a.index_rate,1) as index_rate from jice_gd_mandx_structure_trade  a,(VALUES(1, '一般贸易'), (2, '加工贸易'),(3, '保税物流'), (4, '其它贸易')) AS tempa (id, name) " +
                "where a.index_name=tempa.name and a.report_time = ${report_time} and a.fnid=${fnid} and a.lv=${lv}  and  a.mx_type::varchar = '${mx_type}'  order by tempa.id asc  limit 31  ";
        testSql = "select  row_number() over() as order,com_name,index_value,index_unit  from (select   a.com_name,round(a.index_value,2) as index_value,b.index_unit from jice_gd_industry_top a,jice_gd_industry_dict_top b " +
                "where a.index_type=b.index_type and   a.fnid = ${fnid} and a.report_time = ${report_time} and lv=${lv}  and a.index_type='${index_type}' order by a.index_value desc  limit 10) t";
        //得到解析SQL语句后的类型对象
        String param = "select  row_number() over() as order,com_name,index_value,index_unit  from (select   a.com_name,round(a.index_value,2) as index_value,b.index_unit from jice_gd_industry_top a,jice_gd_industry_dict_top b where a.index_type=b.index_type and   a.fnid = ${fnid} and a.report_time = ${report_time} and lv=${lv}  and a.index_type='${index_type}' order by a.index_value desc  limit 10) t";
        System.out.println("返回参数"+getResultParam(param));
        System.out.println("接口入参"+getParam(param));

    }

    private static String getResultParam(String sql) {
        SqlParserUtil sqlParserUtil = new SqlParserUtil();    //SQL语句解析工具类
        SQLObject sqlObjectObj = sqlParserUtil.getParsedSql(sql);
        String[] singleField = sqlObjectObj.getField().split(",");
        final List<String> collect = Arrays.asList(singleField).stream().map(e -> filterField(e)).collect(Collectors.toList());
        return collect.toString();
    }

    private static String filterField(String e) {
        e = e.trim();
         String[] splitBySpace = e.split("\\s+");
        if(splitBySpace.length>1){
            return splitBySpace[splitBySpace.length-1];
        }
        final String[] splitByPoint = e.split("\\.");
        return splitByPoint[splitByPoint.length-1];
    }


    private static String getParam(String sql) {
        String regex = "\\$\\{(.*?)\\}";
        Pattern compile = Pattern.compile(regex);
        Matcher matcher = compile.matcher(sql);
        Set<String> stringSet = new HashSet<>();
        while (matcher.find()) {
            stringSet.add(matcher.group(1));
        }
        return stringSet.toString();
    }
}
