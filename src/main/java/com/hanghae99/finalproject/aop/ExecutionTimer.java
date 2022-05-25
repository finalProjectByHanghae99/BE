package com.hanghae99.finalproject.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Aspect
@Component
public class ExecutionTimer {


//API 타입별 시간측정을 위해서 @PointCut을 사용 ,
//    // 조인포인트를 어노테이션으로 설정
//    @Pointcut("@annotation(com.hanghae99.finalproject.aop.ExeTimer)")
//    private void timer(){};
//

//    @Around("timer()") // method/ class 타입 레벨에서 @ExeTime 어노테이션을 사용해 api 실행 시간 확인
    @Around("bean(*Controller)") // 현재 모든 컨트롤러 실행 속도 측정
    public Object AssumeExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        StopWatch stopWatch = new StopWatch();

        stopWatch.start();
        Object proceed = joinPoint.proceed(); // 조인포인트의 메서드 실행
        stopWatch.stop();

        long totalTimeMillis = stopWatch.getTotalTimeMillis();

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getMethod().getName();

        log.info("실행 메서드: {}, 실행시간 = {}ms", methodName, totalTimeMillis);
        return proceed;
    }
}
