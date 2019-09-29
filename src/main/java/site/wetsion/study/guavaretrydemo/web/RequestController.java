package site.wetsion.study.guavaretrydemo.web;

import com.github.rholder.retry.RetryException;
import com.github.rholder.retry.Retryer;
import com.github.rholder.retry.RetryerBuilder;
import com.github.rholder.retry.StopStrategies;
import com.google.common.base.Predicates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

/**
 * @author weixin
 * @version 1.0
 * @CLassName RequestController
 * @date 2019/9/29 2:38 PM
 */
@Slf4j
public class RequestController {

    public static void main(String[] args) {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                log.info("request start");
                RestTemplate restTemplate = new RestTemplate();
                String result = null;
                try {
                    result = restTemplate.getForObject("http://localhost:8080/", String.class);
                    log.info("result: [{}]", result);
                } catch (Exception e) {
                    log.info("exception: [{}]", e.getMessage());
                    throw e;
                }
                return result;
            }
        };

        Retryer<String> retryer = RetryerBuilder.<String>newBuilder()
                .retryIfResult(Predicates.isNull())
                .retryIfExceptionOfType(IOException.class)
                .retryIfRuntimeException()
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .build();

        try {
            retryer.call(callable);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            e.printStackTrace();
        }
    }
}
