package com.skillbox.socialnetwork.main.aspect;

import com.skillbox.socialnetwork.main.service.PersonService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@Aspect
public class LoggingAspect {
    private final Log log = LogFactory.getLog(getClass());

    private final PersonService userService;

    @Autowired
    public LoggingAspect(PersonService userService) {
        this.userService = userService;
    }

    @Around(value = "@annotation(methodLogWithTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, MethodLogWithTime methodLogWithTime) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = null;
        Throwable toThrow = null;
        String result = "void";

        try {
            proceed = joinPoint.proceed();
            if (proceed != null) {
                result = mask(proceed.toString());
            }
        } catch (Throwable t) {
            toThrow = t;
            result = !methodLogWithTime.exceptionMessage().equals("")
                    ? methodLogWithTime.exceptionMessage()
                    : toThrow.getMessage();
        }
        long executionTime = System.currentTimeMillis() - start;

        String args = "";
        String message;
        if(methodLogWithTime.fullMessage().equals("")){
            args = " input: " + mask(Arrays.toString(joinPoint.getArgs())) + ", ";
            message = methodLogWithTime.message();
        }else{
            message = methodLogWithTime.fullMessage();
        }

        String user = methodLogWithTime.userAuth() ? "User: " + userService.getAuthUserEmail() : "";

        log.info((message + " " + user +
                " [" + joinPoint.getSignature().getName() + "] " +
                args + "output: " + result + ", executed in " + executionTime + "ms").trim());

        if (toThrow != null)
            throw toThrow;
        return proceed;
    }

    private String mask(String string) {
        String result = string;
        if (string.matches(".*(password.*|token)=.*")) {
            result = string.replaceAll("(password.*|token)=[^,\\s)]+", "secret=*****");
        }
        return result;
    }

}