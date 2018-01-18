package com.caocao.shardingjdbc.console;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;

import java.util.concurrent.CountDownLatch;

/**
 * @author liuke1@geely.com
 * @version 1.0
 * @since v1.0 2017/11/8 13:54

 */
@ServletComponentScan
@MapperScan(value="com.caocao.shardingjdbc.console.dal.dao")
@SpringBootApplication
//@PropertySource(value={"/etc/caocao/console/console.properties"})
public class Bootstrap {
    @Bean
    public CountDownLatch closeLatch() {
        return new CountDownLatch(1);
    }
    public static void main(String[] args) throws InterruptedException {
        SpringApplication application = new SpringApplication(Bootstrap.class);
        ApplicationContext ctx = application.run(args);
        CountDownLatch closeLatch = ctx.getBean(CountDownLatch.class);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> closeLatch.countDown()));
        closeLatch.await();
        System.out.println("init success!");
    }


}
