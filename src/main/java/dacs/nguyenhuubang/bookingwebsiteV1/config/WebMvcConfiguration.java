package dacs.nguyenhuubang.bookingwebsiteV1.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    public void addViewControllers(ViewControllerRegistry registry) {///login controller handle by this
        registry.addViewController("/forgot-password").setViewName("auth-forgot-password");
    }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path routeUploadDir = Paths.get("./cities-images");
        String routeUploadPath = routeUploadDir.toFile().getAbsolutePath();
        registry.addResourceHandler("/cities-images/**").addResourceLocations("file:/" + routeUploadPath + "/");

        Path routeUploadDir1 = Paths.get("./vehicle-images");
        String routeUploadPath1 = routeUploadDir1.toFile().getAbsolutePath();
        registry.addResourceHandler("/vehicle-images/**").addResourceLocations("file:/"+routeUploadPath1+"/");
    }
}
