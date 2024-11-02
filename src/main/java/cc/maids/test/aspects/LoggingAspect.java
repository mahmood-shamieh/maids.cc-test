package cc.maids.test.aspects;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Before("execution(* cc.maids.test.services.AuthService.*(..))")
    public void logAuthServiceMethod(JoinPoint joinPoint) {
        logger.info("Authentication method {} invoked", joinPoint.getSignature());
    }

    @Before("execution(* cc.maids.test.services.BooksService.*(..))")
    public void booksServiceMethod() {
        logger.info("Method call detected in BooksService");
    }

    @Before("execution(* cc.maids.test.services.PatronsService.*(..))")
    public void logPatronsServiceArguments(JoinPoint joinPoint) {
        logger.info("PatronsService method {} called with args: {}", joinPoint.getSignature(), joinPoint.getArgs());
    }

    @Around("execution(* cc.maids.test.services.BooksService.*(..)) || execution(* cc.maids.test.services.PatronsService.*(..)) ")
    public Object logExecutionDetails(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;
        logger.info("Method {} executed in {} ms", joinPoint.getSignature(), elapsedTime);
        return result;
    }

    @AfterThrowing(pointcut = "execution(* cc.maids.test.services.BooksService.*(..))", throwing = "exception")
    public void booksServiceException(Exception exception) {
        logger.error("Exception caught at the books Service: {}", exception.getMessage());
    }

    @AfterThrowing(pointcut = "execution(* cc.maids.test.services.PatronsService.*(..))", throwing = "exception")
    public void patronsServiceException(Exception exception) {
        logger.error("Exception caught at the patrons service: {}", exception.getMessage());
    }


}
