package pe.cibertec.melodicvault.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String carpetaClips;

    public WebConfig(@Value("${melodicvault.clips.carpeta:clips}") String carpetaClips) {
        this.carpetaClips = carpetaClips;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String ubicacion = Paths.get(carpetaClips).toAbsolutePath().toUri().toString();
        if (!ubicacion.endsWith("/")) ubicacion += "/";
        registry.addResourceHandler("/clips/**").addResourceLocations(ubicacion);
    }
}