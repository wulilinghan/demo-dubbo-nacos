package cn.tlh.ex.common.vo.req.common;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author TANG
 */
@Getter
@Setter
@NoArgsConstructor
public class Page {
    //当页数
    private Integer pageNum = 1;
    //分页大小
    private Integer pageSize = 20;
    //起始位置
    private Integer start;
    //结束位置
    private Integer end;

    public Page(int pageNum, int pageSize) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.start = (pageNum - 1) * pageSize;
        this.end = pageNum * pageSize;
    }

}