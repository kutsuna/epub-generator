package jp.kutsuna.epub;

import com.fasterxml.jackson.databind.JsonMappingException;
import jp.kutsuna.epub.EPubFileGenerator;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RestController
@EnableAutoConfiguration
public class Main {

    @Autowired
    private EPubFileGenerator ePubFileGenerator;

    @Bean
    public EPubFileGenerator ePubFileGenerator(MeterRegistry registry) {
        return new EPubFileGenerator(registry);
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurerAdapter() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("*");
            }
        };
    }

    @RequestMapping(value="/generate", method = {RequestMethod.POST}, consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<Resource> generate(@RequestBody BookInformation bookInformation) throws IOException, JsonMappingException {
        File epubFile = ePubFileGenerator.generate(bookInformation);

        try {
            InputStreamResource resource = new InputStreamResource(new FileInputStream(epubFile));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/zip"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"generated.epub\"")
                    .body(resource);
        } finally {
            epubFile.delete();
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("spring.devtools.restart.enabled", "false");
        SpringApplication.run(Main.class, args);
    }
}
