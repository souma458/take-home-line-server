package pt.jose.line_server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.jose.line_server.service.FileLineService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LineServerApplicationTests {

	private static final String EXPECTED_CONTENT_TYPE = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private FileLineService fileLineService;

	@BeforeEach
	void setUp(@TempDir Path tempDir) throws IOException {
		// Create a test file with known content
		File testFile = tempDir.resolve("test-file.txt").toFile();
		try (FileWriter writer = new FileWriter(testFile)) {
			writer.write("Line 1\n");
			writer.write("Line 2\n");
			writer.write("Line 3\n");
			writer.write("Line 4\n");
			writer.write("Line 5\n");
		}

		// Set the file path in the service and build the index
		fileLineService.setFilePath(testFile.getAbsolutePath());
		fileLineService.buildLineIndex();
	}

	@Test
	void givenValidLineIndex_whenGetLine_thenReturnLineContent() throws Exception {
		mockMvc.perform(get("/lines/0"))
				.andExpectAll(
						status().isOk(),
						content().string("Line 1"),
						content().contentType(EXPECTED_CONTENT_TYPE)
				);

		mockMvc.perform(get("/lines/2"))
				.andExpectAll(
						status().isOk(),
						content().string("Line 3"),
						content().contentType(EXPECTED_CONTENT_TYPE)
				);
	}

	@Test
	void givenInvalidLineIndex_whenGetLine_thenReturn413() throws Exception {
		// Test out-of-bounds line request
		mockMvc.perform(get("/lines/10"))
			.andExpect(status().isPayloadTooLarge());

		mockMvc.perform(get("/lines/-1"))
			.andExpect(status().isPayloadTooLarge());
	}

}
