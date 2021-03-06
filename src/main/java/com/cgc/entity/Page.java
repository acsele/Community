package com.cgc.entity;

public class Page {

    private int limit = 10;  //每页显示上限
    private int rows;   //数据总数
    private int current = 1;    //当前页码
    private String path;    //复用分页的链接

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }

    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if (current >= 1) {
            this.current = current;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    //从第几页开始显示
    public int getOffset() {
        return (current - 1) * limit;
    }

    //获取总页数
    public int getTotal() {
        return rows % limit == 0 ? rows / limit : (rows / limit + 1);
    }

    //获取起始页
    public int getFrom() {
        int from = current - 2;
        return Math.max(from, 1);
    }

    //获取结束页
    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return Math.min(to, total);
    }
}
