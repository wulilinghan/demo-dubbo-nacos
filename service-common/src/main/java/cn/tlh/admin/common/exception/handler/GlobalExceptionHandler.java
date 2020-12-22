package cn.tlh.admin.common.exception.handler;

import cn.tlh.admin.common.exception.customexception.BusinessErrorException;
import cn.tlh.admin.common.exception.customexception.EntityExistException;
import cn.tlh.admin.common.exception.customexception.EntityNotFoundException;
import cn.tlh.admin.common.util.AdminConstants;
import cn.tlh.admin.common.util.ThrowableUtil;
import cn.tlh.admin.common.util.enums.BusinessMsgEnum;
import cn.tlh.admin.common.base.vo.BusinessResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.rpc.RpcException;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authz.AuthorizationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author TANG
 * @date 2020-11-23
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理shiro异常
     *
     * @param e /
     * @return /
     */
    @ExceptionHandler(ShiroException.class)
    public BusinessResponse handleShiroException(ShiroException e) {
        if (e instanceof UnknownAccountException) {
            LOGGER.error("ShiroException -->UnknownAccountException {}", "账号不存在");
            return BusinessResponse.fail("账号不存在");
        } else if (e instanceof IncorrectCredentialsException) {
            LOGGER.error("ShiroException -->IncorrectCredentialsException {}", "密码错误");
            return BusinessResponse.fail("密码错误");
        } else if (e instanceof LockedAccountException) {
            LOGGER.error("ShiroException -->LockedAccountException {}", "账户已被禁用");
            return BusinessResponse.fail("账户已被禁用");
        } else if (e instanceof AuthorizationException) {
            LOGGER.error("ShiroException -->AuthorizationException {}", "没有权限");
            return BusinessResponse.fail("没有权限");
        }
        return BusinessResponse.fail(BusinessMsgEnum.UNEXPECTED_EXCEPTION);
    }

    /**
     * 处理dubbo异常，可在代码中手动抛出rpcException，自定义异常信息
     * throw new RpcException("自定义异常信息");
     *
     * @param e /
     * @return /
     */
    @ExceptionHandler(RpcException.class)
    public BusinessResponse rpcExceptionHandler(RpcException e) {
        LOGGER.error("服务器运行异常 Message: " + e.getMessage());
        return BusinessResponse.fail(e.getMessage());
    }

    /**
     * 系统异常 预期以外异常
     *
     * @param ex /
     * @return /
     */
    @ExceptionHandler(Exception.class)
    public BusinessResponse handleUnexpectedServer(Exception ex) {
        LOGGER.error("系统异常：", ex);
        return BusinessResponse.fail(BusinessMsgEnum.UNEXPECTED_EXCEPTION);
    }

    /**
     * 拦截自定义业务异常，返回业务异常信息
     *
     * @param bex /
     * @return /
     */
    @ExceptionHandler(BusinessErrorException.class)
    public BusinessResponse handleBusinessError(BusinessErrorException bex) {
        String code = bex.getCode();
        String message = bex.getMessage();
        return BusinessResponse.fail(code, message);
    }

    /**
     * 处理 EntityExist
     */
    @ExceptionHandler(value = EntityExistException.class)
    public BusinessResponse entityExistException(EntityExistException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String message = e.getMessage();
        return BusinessResponse.fail("400", message);
    }

    /**
     * 处理 EntityNotFound
     */
    @ExceptionHandler(value = EntityNotFoundException.class)
    public BusinessResponse entityNotFoundException(EntityNotFoundException e) {
        // 打印堆栈信息
        log.error(ThrowableUtil.getStackTrace(e));
        String message = e.getMessage();
        return BusinessResponse.fail("400", message);
    }

    /**
     * 空指针异常
     *
     * @param bex NullPointerException
     * @return /
     */
    @ExceptionHandler(NullPointerException.class)
    public BusinessResponse handleTypeMismatchException(NullPointerException bex) {
        LOGGER.error("空指针异常，{}", bex.getMessage());
        return BusinessResponse.fail("500", "空指针异常了");
    }

    /**
     * 缺少请求参数异常
     *
     * @param ex HttpMessageNotReadableException
     * @return /
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public BusinessResponse handleHttpMessageNotReadableException(MissingServletRequestParameterException ex) {
        LOGGER.error("缺少请求参数，{}", ex.getMessage());
        return BusinessResponse.fail("400", "缺少必要的请求参数");
    }


    //  Validated 请求参数校验异常处理

    /**
     * <1> 处理 form data方式调用接口校验失败抛出的异常
     */
    @ExceptionHandler(BindException.class)
    public BusinessResponse bindExceptionHandler(BindException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return BusinessResponse.fail(collect, "400", AdminConstants.BAD_REQUEST_MSG);
    }

    /**
     * <2> 处理 json 请求体调用接口校验失败抛出的异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BusinessResponse methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<String> collect = fieldErrors.stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());
        return BusinessResponse.fail(collect, "400", AdminConstants.BAD_REQUEST_MSG);
    }

    /**
     * <3> 处理单个参数校验失败抛出的异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public BusinessResponse constraintViolationExceptionHandler(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        List<String> collect = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());
        return BusinessResponse.fail(collect, "400", AdminConstants.BAD_REQUEST_MSG);
    }
}
