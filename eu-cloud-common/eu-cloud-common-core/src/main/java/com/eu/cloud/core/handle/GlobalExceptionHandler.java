package com.eu.cloud.core.handle;


import com.eu.cloud.core.exception.GlobalException;
import com.eu.cloud.core.exception.GlobalExceptionCode;
import com.eu.cloud.core.wrapper.GlobalResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

/**
 * 全局异常拦截
 *
 * @author jiangxd
 */
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 自定义异常处理
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(GlobalException.class)
    @ResponseBody
    public GlobalResponseWrapper customerExceptionHandler(HttpServletRequest request, GlobalException exception) throws Exception {
        log.info("拦截到抛出的自定义异常", exception);
        return new GlobalResponseWrapper(exception.getCode()).msg(exception.getMessage());
    }

    /**
     * 注解参数异常处理
     * 将全部的异常参数拼接返回
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public GlobalResponseWrapper methodArgumentNotValidExceptionHandler(HttpServletRequest request, MethodArgumentNotValidException exception) throws Exception {
        //获取全部异常
        BindingResult bindingResult = exception.getBindingResult();
        //获取第一条异常进行返回
        String message = bindingResult.getAllErrors().get(0).getDefaultMessage();

        log.info("拦截到请求参数异常", exception);
        return new GlobalResponseWrapper(GlobalExceptionCode.REQUEST_ARGUMENT_EXCEPTION).msg(message);
    }

    /**
     * 处理集合中的注解异常
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public GlobalResponseWrapper constraintViolationExceptionHandler(HttpServletRequest request, ConstraintViolationException exception) throws Exception {
        String message = exception.getConstraintViolations().iterator().next().getMessage();
        log.info("拦截到请求参数异常", exception);
        return new GlobalResponseWrapper(GlobalExceptionCode.REQUEST_ARGUMENT_EXCEPTION).msg(message);

    }

    /**
     * 其他的异常拦截处理
     * 不建议放开这些异常拦截，因为这些拦截无法完全掌控，且其中会包含一些其他不确定因素
     * 拦截这些异常会影响到熔断器的使用
     * 而且如果某些异常程序员无法捕获 , 那么就应该抛出到前端让前端开发人员进行统一处理
     *
     * @param request
     * @param exception
     * @return
     * @throws Exception
     */
    /*@ExceptionHandler(Exception.class)
    @ResponseBody*/
    public GlobalResponseWrapper defaultExceptionHandler(HttpServletRequest request, Exception exception) throws Exception {
        if (exception instanceof NoHandlerFoundException) { //404处理
            log.warn("拦截到 404 异常", exception);
        } else {    //其它全部为 500 异常
            log.error("拦截到服务器内部异常", exception);
        }
        return new GlobalResponseWrapper(GlobalExceptionCode.ERROR).msg(exception.getMessage());
    }


}
