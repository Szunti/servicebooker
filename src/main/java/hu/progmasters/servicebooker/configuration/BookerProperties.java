package hu.progmasters.servicebooker.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Future;
import javax.validation.constraints.Past;
import java.time.LocalDateTime;

@Validated
@ConfigurationProperties("servicebooker")
@Data
public class BookerProperties {
    @Past
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime minBookableDate;

    @Future
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime maxBookableDate;
}
