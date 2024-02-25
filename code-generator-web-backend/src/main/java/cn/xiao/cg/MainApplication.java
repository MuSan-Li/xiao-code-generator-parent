package cn.xiao.cg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

/**
 * 主类（项目启动入口）
 *
 * @author xiao
 */
@Slf4j
@SpringBootApplication
public class MainApplication {


    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
        log.info("================ Spring Boot is running! ================");
    }

}
