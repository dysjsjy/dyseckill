package com.dyseckill.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class GoodsDTO {

    private BigDecimal seckillPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private BigDecimal goodsPrice;
    private Date createDate;
    private Date updateDate;
    private String goodsDetail;

}
