package com.xing.entity;

import java.io.Serializable;

/**
 * query conditions entity
 */
public class QueryPageBean implements Serializable{
    private Integer currentPage;// current page number
    private Integer pageSize;// records in each page
    private String queryString;// query conditions

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }
}