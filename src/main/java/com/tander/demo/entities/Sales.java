package com.tander.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Sales {

    private Integer rc_id;
    private Integer ar_id;
    private Integer wh_id;
    private Double sales;

    @Override
    public String toString() {
        return "Sales{" +
                "rc_id=" + rc_id +
                ", ar_id=" + ar_id +
                ", wh_id=" + wh_id +
                ", sales=" + sales +
                '}';
    }
}
