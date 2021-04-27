package com.tander.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderOneItem {
    private Integer rc_id;
    private Integer ar_id;
    private Integer wh_id;
    private Integer type_id;
    private Double qnty;
    private Double perc;
    private Double sales;
    private Double orderOne;

    @Override
    public String toString() {
        return "OrderOneItem{" +
                "rc_id=" + rc_id +
                ", ar_id=" + ar_id +
                ", wh_id=" + wh_id +
                ", type_id=" + type_id +
                ", qnty=" + qnty +
                ", perc=" + perc +
                ", sales=" + sales +
                ", orderOne=" + orderOne +
                '}';
    }
}
