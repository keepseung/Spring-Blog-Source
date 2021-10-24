package hello.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Slf4j
public class Scheduler {
    private static final Logger logger = LoggerFactory.getLogger(Scheduler.class);


    //    @Scheduled(cron = "1 * * * * ?")
    public void cronJobSch() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("작업 날짜 시간:: " + strDate);

    }

    // 1초에 한번 실행된다.
//    @Scheduled(fixedDelay = 1000)
    public void scheduleFixedDelayTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("작업 날짜 시간 fixedDelay:: " + strDate);
//        System.out.println("Current Thread : "+ Thread.currentThread().getName());
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Scheduled(fixedRate = 1000)
    public void scheduleFixedRateTask() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        Date now = new Date();
        String strDate = sdf.format(now);
        System.out.println("작업 날짜 시간 fixedRate::" + strDate);
        System.out.println("현재 쓰레드 : "+ Thread.currentThread().getName());

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
