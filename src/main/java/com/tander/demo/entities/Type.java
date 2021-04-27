package com.tander.demo.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Type {
    private Integer ar_id;
    private Integer type_id;

    @Override
    public String toString() {
        return "Type{" +
                "ar_id=" + ar_id +
                ", type_id=" + type_id +
                '}';
    }
}
