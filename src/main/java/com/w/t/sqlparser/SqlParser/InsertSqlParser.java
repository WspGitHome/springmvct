package com.w.t.sqlparser.SqlParser;

import com.w.t.sqlparser.SqlParser.SqlParserAbstract.BaseSingleSqlParser;
import com.w.t.sqlparser.SqlSegment.SegmentRegExps;
import com.w.t.sqlparser.SqlSegment.SqlSegment;
/**
 * Created by CH on 2016/10/25.
 */
public class InsertSqlParser extends BaseSingleSqlParser {

	/**
	 * 　* 构造函数，传入原始Sql语句，进行劈分。
	 * 　* @param originalSql
	 *
	 *
	 * @param originalSql
	 * @param processedSql
	 */
	public InsertSqlParser(String originalSql, String processedSql) {
		super(originalSql, processedSql);
	}

	@Override
	protected void initializeSegments() {
		//insert into .. (...) values(...);
		for(String regExp : SegmentRegExps.insertRegExps){//InsertSqlParser的正则表达式初始化
			segments.add(new SqlSegment(regExp));
		}
	}
	@Override
	public void Deal(String start, String body, String end) {
		// TODO Auto-generated method stub
		if(sqlObjectObj != null){
			//如果(start) (body) (end) = (insert into)(.+)([(])
			if(start.equals("insert into")){
				if(sqlObjectObj.typeName == null){
					sqlObjectObj.typeName = "insert";
				}
				if(sqlObjectObj.tableName == null){
					//tablename是等于body，但是body是在原sql语句上进行了小写处理的，所以需要通过getOriSql进行还原
					sqlObjectObj.tableName = getOriSql(originalSql,body);
				}
			}
			if(start.equals(") values (")){
				//如果(start) (body) (end) = ([)] values [(])(.+)([)])
				//body = values(?,?,?)	//body为问号中的值，暂时不需要
			}
			if(end.equals(") values")){
				//如果(start) (body) (end) = [(])(.+)([)] values)
				//field，但是body是在原sql语句上进行了小写处理的，所以需要通过getOriSql进行还原
				sqlObjectObj.field = getOriSql(originalSql,body);;
			}
		}
	}
}
