package com.tander.demo.DB;

public class Requests {
    public String getTableType(){
        return "SELECT * FROM tabletype;";
    }
    public String getTableValue(){
        return "SELECT * FROM tablevalue;";
    }
    public String getTableSales(){
        return "SELECT * FROM tablesales;";
    }
    public String getOrderOne(){
        return "SELECT * FROM tablesales, tablevalue WHERE tablesales.rc_id = tablevalue.rc_id " +
                "AND tablesales.ar_id = tablevalue.ar_id  AND tablesales.wh_id = tablevalue.wh_id;";
    }
}
