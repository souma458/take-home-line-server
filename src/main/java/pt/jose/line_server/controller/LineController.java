package pt.jose.line_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pt.jose.line_server.service.FileLineService;

/**
 * REST controller that exposes an endpoint to retrieve lines from a file.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class LineController {

    private final FileLineService fileLineService;

    /**
     * Gets a line from the file by its index.
     *
     * @param lineIndex The 0-based index of the line to retrieve
     * @return The text of the requested line with status 200, or status 413 if index is out of bounds
     */
    @GetMapping(value = "/lines/{lineIndex}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLine(@PathVariable int lineIndex) {
        String line = fileLineService.getLine(lineIndex);
        return ResponseEntity.ok(line);
    }

}
