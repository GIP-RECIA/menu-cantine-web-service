package fr.recia.menucantine.config.beans;


import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Slf4j
@Component
@ConfigurationProperties(prefix = "app.soffit", ignoreUnknownFields = true)
public class SoffitProperties {
    private String signatureKey;

    @PostConstruct
    public void init() {
        log.info(this.toString());
    }

    @Override
    public String toString() {
        return "\"SoffitProperties\": {" +
                "\n\t\"signatureKey\": \"" + signatureKey + "\"" +
                "\n}";
    }
}