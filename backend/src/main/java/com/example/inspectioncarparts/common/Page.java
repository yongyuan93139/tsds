package com.example.inspectioncarparts.common;

import java.util.List;

/**
 * 自定义分页对象
 * @param <T> 分页数据类型
 */
public class Page<T> {
    /**
     * 当前页码
     */
    private long current;

    /**
     * 每页大小
     */
    private long size;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 总页数
     */
    private long pages;

    /**
     * 当前页数据列表
     */
    private List<T> records;

    public Page() {
    }

    public Page(long current, long size) {
        this.current = current;
        this.size = size;
    }

    /**
     * 从MyBatis-Plus的Page对象转换为自定义Page对象
     */
    public static <T> Page<T> from(com.baomidou.mybatisplus.extension.plugins.pagination.Page<T> page) {
        Page<T> result = new Page<>();
        result.setCurrent(page.getCurrent());
        result.setSize(page.getSize());
        result.setTotal(page.getTotal());
        result.setPages(page.getPages());
        result.setRecords(page.getRecords());
        return result;
    }

    public long getCurrent() {
        return current;
    }

    public void setCurrent(long current) {
        this.current = current;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }
}
