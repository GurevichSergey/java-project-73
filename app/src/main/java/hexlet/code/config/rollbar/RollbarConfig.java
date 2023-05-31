package hexlet.code.config.rollbar;


import com.rollbar.notifier.Rollbar;
import com.rollbar.notifier.config.Config;
import com.rollbar.spring.webmvc.RollbarSpringConfigBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
        "hexlet.code"
})
public class RollbarConfig {
    @Bean
    public Rollbar rollbar() {

        return new Rollbar(getRollbarConfigs("f0c1cdc217c646a28dd335bdf0a385f4"));
    }

    private Config getRollbarConfigs(String accessToken) {
        return RollbarSpringConfigBuilder.withAccessToken(accessToken)
                .environment("development")
                .build();
    }

}
