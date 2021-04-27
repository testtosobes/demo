package com.tander.demo.controller;

import com.tander.demo.DB.ConnectToDB;
import com.tander.demo.DB.Requests;
import com.tander.demo.entities.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Controller
public class MyController {
    ArrayList<Sales> arrS;
    ArrayList<Value> arrV;
    ArrayList<Type> arrT;
    MathContext context = new MathContext(4, RoundingMode.HALF_UP);//округлять до 4 после запятой
    Requests requests = new Requests();
    ConnectToDB connect = new ConnectToDB();
    PreparedStatement stmt;
    Connection c;

    @RequestMapping("/")
    public String home() {
        return "index";
    }

    //Получить данные из БД, обработать средствами Java, тупо перебор и вычисление
    @GetMapping(value = "/load-with-java")
    @ResponseBody
    public ModelAndView readWithJava() throws IOException, SQLException {
        //работает, если в таблице tablesales нет повторов по полям rc_id, ar_id, wh_id, это условие не обсуждали
        c = connect.getConnection();

        ModelAndView mav = new ModelAndView("index");

        mav.addObject("salesTable", readSales());
        mav.addObject("typeTable", readType());
        mav.addObject("valueTable", readValue());

        ArrayList<Double> orderMaxList = new ArrayList<>();
        ArrayList<Double> orderItogList = new ArrayList<>();
        ArrayList<String> orderAvgList = new ArrayList<>();
        ArrayList<String> orderSumList = new ArrayList<>();
        ArrayList<OrderOneItem> orderOneItemList = new ArrayList<>();
        int tmpRC_ID = 0;
        Double tmpAVG = 0d;
        int tmpCount = 0;
        for (Sales arr : arrS) {
            for (Value value : arrV) {
                if (arr.getRc_id().equals(value.getRc_id()) &&
                        arr.getAr_id().equals(value.getAr_id()) &&
                        arr.getWh_id().equals(value.getWh_id())) {

                    BigDecimal result = new BigDecimal(arr.getSales() * value.getPerc(), context);
                    orderOneItemList.add(new OrderOneItem(
                            value.getRc_id(),
                            value.getAr_id(),
                            value.getWh_id(),
                            value.getType_id(),
                            value.getQnty(),
                            value.getPerc(),
                            arr.getSales(),
                            Double.valueOf(String.valueOf(result))));

                    //вычисляем и заполняем AVG
                    if (tmpRC_ID != arr.getRc_id()) {
                        if (tmpCount == 0) {
                            tmpRC_ID = arr.getRc_id();
                            tmpAVG = Double.valueOf(String.valueOf(result));
                            tmpCount = 1;
                        } else {
                            BigDecimal resultAVG = new BigDecimal(tmpAVG / tmpCount, context);//округл до 4х знаков
                            orderAvgList.add(resultAVG + "-" + tmpRC_ID + "rc_id");
                            tmpRC_ID = arr.getRc_id();
                            tmpAVG = Double.valueOf(String.valueOf(result));
                            tmpCount = 1;
                        }
                    } else {
                        tmpCount++;
                        tmpAVG = tmpAVG + Double.parseDouble(String.valueOf(result));
                    }
                }
            }
        }
        BigDecimal resultAVG = new BigDecimal(tmpAVG / tmpCount, context);//округл до 4х знаков
        orderAvgList.add(resultAVG + "-" + tmpRC_ID + "rc_id");

        orderOneItemList.sort(Comparator
                .comparing(OrderOneItem::getRc_id)
                .thenComparing(OrderOneItem::getWh_id));

        return getModelAndView(mav, orderMaxList, orderItogList, orderAvgList, orderSumList, orderOneItemList);
    }

   // @GetMapping(value = "/load-with-postgres")
    @GetMapping(value = "/load-with-postgres")

    @ResponseBody
    public ModelAndView readWithPostgres() throws IOException, SQLException {
        c = connect.getConnection();
        ModelAndView mav = new ModelAndView("index");

        mav.addObject("salesTable", readSales());
        mav.addObject("typeTable", readType());
        mav.addObject("valueTable", readValue());

        ArrayList<Double> orderMaxList = new ArrayList<>();
        ArrayList<Double> orderItogList = new ArrayList<>();
        ArrayList<String> orderAvgList = new ArrayList<>();
        ArrayList<String> orderSumList = new ArrayList<>();
        ArrayList<OrderOneItem> orderOneItemList = new ArrayList<>();
        int tmpRC_ID = 0;
        Double tmpAVG = 0d;
        int tmpCount = 0;

        orderOneItemList.addAll(readOrderOne());
        orderOneItemList.sort(Comparator
                .comparing(OrderOneItem::getRc_id)
                .thenComparing(OrderOneItem::getWh_id));

        //вычисляем и заполняем AVG
        for (OrderOneItem orderOneItem : orderOneItemList) {
            BigDecimal result = new BigDecimal(orderOneItem.getSales() * orderOneItem.getPerc(), context);
            if (tmpRC_ID != orderOneItem.getRc_id()) {
                if (tmpCount == 0) {
                    tmpRC_ID = orderOneItem.getRc_id();
                    tmpAVG = Double.valueOf(String.valueOf(result));
                    tmpCount = 1;
                } else {
                    BigDecimal resultAVG = new BigDecimal(tmpAVG / tmpCount, context);//округл до 4х знаков
                    orderAvgList.add(resultAVG + "-" + tmpRC_ID + "rc_id");
                    tmpRC_ID = orderOneItem.getRc_id();
                    tmpAVG = Double.valueOf(String.valueOf(result));
                    tmpCount = 1;
                }
            } else {
                tmpCount++;
                tmpAVG = tmpAVG + Double.parseDouble(String.valueOf(result));
            }
        }
        BigDecimal resultAVG = new BigDecimal(tmpAVG / tmpCount, context);//округл до 4х знаков
        orderAvgList.add(resultAVG + "-" + tmpRC_ID + "rc_id");

        return getModelAndView(mav, orderMaxList, orderItogList, orderAvgList, orderSumList, orderOneItemList);
    }

    //ORDER_MAX (Выбрать из пары ORDER_ONE - QNTY наибольшее значение?) + ORDER_ITOG
    private ModelAndView getModelAndView(ModelAndView mav, ArrayList<Double> orderMaxList, ArrayList<Double> orderItogList, ArrayList<String> orderAvgList, ArrayList<String> orderSumList, ArrayList<OrderOneItem> orderOneItemList) throws SQLException {
        Double tmpSUM = 0d;
        int whForSum = 0;
        int rcForSum = 0;

        for (int g = 0; g < orderOneItemList.size(); g++) {
            if (orderOneItemList.get(g).getOrderOne() > orderOneItemList.get(g).getQnty()) {
                if (orderOneItemList.get(g).getType_id() == 1) {
                    BigDecimal result = new BigDecimal(orderOneItemList.get(g).getOrderOne() * 1.5, context);//округл до 4х знаков
                    orderItogList.add(Double.parseDouble(String.valueOf(result)));
                } else {
                    BigDecimal result = new BigDecimal(orderOneItemList.get(g).getOrderOne() * 0.85, context);
                    orderItogList.add(Double.parseDouble(String.valueOf(result)));
                }
                orderMaxList.add(orderOneItemList.get(g).getOrderOne());
            } else {
                if (orderOneItemList.get(g).getType_id() == 1) {
                    BigDecimal result = new BigDecimal(orderOneItemList.get(g).getQnty() * 1.5, context);
                    orderItogList.add(Double.parseDouble(String.valueOf(result)));
                } else {
                    BigDecimal result = new BigDecimal(orderOneItemList.get(g).getQnty() * 0.85, context);
                    orderItogList.add(Double.parseDouble(String.valueOf(result)));
                }
                orderMaxList.add(orderOneItemList.get(g).getQnty());
            }

            if (rcForSum == orderOneItemList.get(g).getRc_id()
                    && whForSum == orderOneItemList.get(g).getWh_id()) {
                tmpSUM = tmpSUM + orderOneItemList.get(g).getOrderOne();
            } else {
                if (g == 0) {
                    tmpSUM = orderOneItemList.get(g).getOrderOne();
                } else {
                    BigDecimal resultSUM = new BigDecimal(tmpSUM, context);//округл до 4х знаков
                    orderSumList.add(resultSUM + "-(" + rcForSum + "/" + whForSum + ")");
                    tmpSUM = orderOneItemList.get(g).getOrderOne();
                }
            }
            rcForSum = orderOneItemList.get(g).getRc_id();
            whForSum = orderOneItemList.get(g).getWh_id();
        }

        mav.addObject("orderMaxList", orderMaxList);
        mav.addObject("orderItogList", orderItogList);
        mav.addObject("orderAvgList", orderAvgList);
        mav.addObject("orderSumList", orderSumList);
        mav.addObject("orderOneItemList", orderOneItemList);
        stmt.close();
        c.commit();
        c.close();
        return mav;
    }

    private List<OrderOneItem> readOrderOne() {
        ArrayList<OrderOneItem> all = new ArrayList<>();

        try {
            stmt = c.prepareStatement(requests.getOrderOne());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                BigDecimal result = new BigDecimal(rs.getDouble("sales") * rs.getDouble("perc"), context);//округл до 4х знаков

                all.add(new OrderOneItem(rs.getInt("rc_id"), rs.getInt("ar_id"), rs.getInt("wh_id"),
                        rs.getInt("type_id"), rs.getDouble("qnty"), rs.getDouble("perc"),
                        rs.getDouble("sales"), Double.valueOf(String.valueOf(result))));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return all;
    }

    private List<Sales> readSales() {
        arrS = new ArrayList<>();

        try {
            stmt = c.prepareStatement(requests.getTableSales());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                arrS.add(new Sales(rs.getInt("rc_id"),
                        rs.getInt("ar_id"),
                        rs.getInt("wh_id"),
                        rs.getDouble("sales")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("-- All Operations in 'readSales' method done successfully");

        return arrS;
    }

    private List<Value> readValue() {
        arrV = new ArrayList<>();

        try {
            stmt = c.prepareStatement(requests.getTableValue());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                arrV.add(new Value(
                        rs.getInt("rc_id"),
                        rs.getInt("ar_id"),
                        rs.getInt("wh_id"),
                        rs.getInt("type_id"),
                        rs.getDouble("qnty"),
                        rs.getDouble("perc")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("-- All Operations in 'readValue' method done successfully");

        return arrV;
    }

    private List<Type> readType() {
        arrT = new ArrayList<>();

        try {
            stmt = c.prepareStatement(requests.getTableType());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                arrT.add(new Type(rs.getInt("ar_id"),
                        rs.getInt("type_id")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("-- All Operations in 'readType' method done successfully");

        return arrT;
    }
}