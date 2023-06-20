package Moon.courseCheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class CourseCheckApplication {

	public static void main(String[] args) {
		SpringApplication.run(CourseCheckApplication.class, args);
	}

}
