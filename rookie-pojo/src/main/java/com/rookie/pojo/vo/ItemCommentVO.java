package com.rookie.pojo.vo;

import java.util.Date;

/**
 * 用于展示用户评论的 VO
 */
public class ItemCommentVO {

    /**
     * 商品评价等级
     */
    private Integer commentLevel;
    /**
     * 评价内容
     */
    private String content;
    /**
     * 规格名称
     */
    private String specName;
    /**
     * 创建时间
     */
    private Date createdTime;
    /**
     * 用户头像
     */
    private String userFace;
    /**
     * 用户昵称
     */
    private String nickname;

    public Integer getCommentLevel() {
        return commentLevel;
    }

    public void setCommentLevel(Integer commentLevel) {
        this.commentLevel = commentLevel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSpecName() {
        return specName;
    }

    public void setSpecName(String specName) {
        this.specName = specName;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getUserFace() {
        return userFace;
    }

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
}
