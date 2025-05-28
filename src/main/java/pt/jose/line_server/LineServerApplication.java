package pt.jose.line_server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import pt.jose.line_server.service.FileLineService;

@Slf4j
@SpringBootApplication
public class LineServerApplication {

	public static void main(String[] args) {
		if (args.length < 1) {
			log.error("Error: File path not provided.");
			log.error("Usage: java -jar line-server.jar <file-path>");
			System.exit(1);
		}

		String filePath = args[0];

		ConfigurableApplicationContext context = SpringApplication.run(LineServerApplication.class, args);

		// Get the FileLineService bean and set the file path
		FileLineService fileLineService = context.getBean(FileLineService.class);
		fileLineService.setFilePath(filePath);

		// Initialize the service to build the line index
		fileLineService.buildLineIndex();

		log.info("Line server started with file: {}", filePath);
	}

}
