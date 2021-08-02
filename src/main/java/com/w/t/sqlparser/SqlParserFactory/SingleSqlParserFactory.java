package com.w.t.sqlparser.SqlParserFactory;

import com.w.t.sqlparser.SqlParser.*;
import com.w.t.sqlparser.SqlParser.SqlParserAbstract.BaseSingleSqlParser;
import com.w.t.sqlparser.SqlParserBefore.SqlParserUtil;


public class SingleSqlParserFactory {
    public static BaseSingleSqlParser generateParser(String originalSql, String processedSql) {//区分sql语句类型
        if (SqlParserUtil.contains(processedSql, "union") || SqlParserUtil.contains(processedSql, "union all")) {
            return new UnionSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(alter)")) {
            return new AlterSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(drop table)(.+)(ENDOFSQL)")) {
            return new DropSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(create table)(.+)(select)")) {
            return new CreateSelectSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(insert into)(.+)(select)")) {
            return new InsertSelectSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(create table)")) {
            return new CreateSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(insert into)(.+)(select)(.+)(from)(.+)")) {
            return new InsertSelectSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "select")) {
            return new SelectSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "delete")) {
            return new DeleteSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "update")) {
            return new UpdateSqlParser(originalSql, processedSql);
        } else if (SqlParserUtil.contains(processedSql, "(insert into)(.+)(values)(.+)")) {
            return new InsertSqlParser(originalSql, processedSql);
        } else
            return null;
    }

}
