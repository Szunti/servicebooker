package hu.progmasters.servicebooker;

import com.fasterxml.jackson.databind.DeserializationFeature;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;

@OpenAPIDefinition(
        info = @Info(
                title = "Service Booker API",
                description = "API for:\n" +
                        "- registering services\n" +
                        "- adding bookable periods to those services\n" +
                        "- read a timetable for the services\n" +
                        "- register customers\n" +
                        "- create bookings for the services by the customers\n",
                version = "1.0.0"
        )
)
@SpringBootApplication
@ConfigurationPropertiesScan
public class ServicebookerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServicebookerApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder.featuresToEnable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }
}
