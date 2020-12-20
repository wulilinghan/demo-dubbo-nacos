package cn.tlh.admin.service.system;

import cn.tlh.admin.common.base.dto.LogReq;
import cn.tlh.admin.common.pojo.system.SysLog;
import org.springframework.scheduling.annotation.Async;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author TANG
 * @date 2020-11-24
 */
public interface LogService {


    /**
     * 条件分页查询
     * @param logReq 查询条件
     * @return /
     */
    List<SysLog> selectList(LogReq logReq);

    /**
     * 查询用户日志
     * @param logReq 查询条件
     * @return -
     */
    Object queryAllByUser(LogReq logReq);

    /**
     * 保存日志数据
     * @param log 日志实体
     */
    @Async
    void save(SysLog log);

    /**
     * 查询异常详情
     * @param id 日志ID
     * @return Object
     */
    Object findByErrDetail(Long id);

    /**
     * 导出日志
     * @param logs 待导出的数据
     * @param response /
     * @throws IOException /
     */
    void download(List<SysLog> logs, HttpServletResponse response) throws IOException;

    /**
     * 删除所有错误日志
     */
    void delAllByError();

    /**
     * 删除所有INFO日志
     */
    void delAllByInfo();
}
