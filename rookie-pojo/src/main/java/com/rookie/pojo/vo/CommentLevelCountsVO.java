package com.rookie.pojo.vo;

/**
 * 用于展示商品评价数量的vo
 */
public class CommentLevelCountsVO {

    /**
     * 总评
     */
    public Integer totalCounts;
    /**
     * 好评数量
     */
    public Integer goodCounts;
    /**
     * 中评数量
     */
    public Integer normalCounts;
    /**
     * 差评数量
     */
    public Integer badCounts;

    public Integer getTotalCounts() {
        return totalCounts;
    }

    public void setTotalCounts(Integer totalCounts) {
        this.totalCounts = totalCounts;
    }

    public Integer getGoodCounts() {
        return goodCounts;
    }

    public void setGoodCounts(Integer goodCounts) {
        this.goodCounts = goodCounts;
    }

    public Integer getNormalCounts() {
        return normalCounts;
    }

    public void setNormalCounts(Integer normalCounts) {
        this.normalCounts = normalCounts;
    }

    public Integer getBadCounts() {
        return badCounts;
    }

    public void setBadCounts(Integer badCounts) {
        this.badCounts = badCounts;
    }
}
