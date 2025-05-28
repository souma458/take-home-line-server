package pt.jose.line_server.service;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for accessing lines from a file using an index-based approach.
 * This implementation indexes the file once at startup with the goal of
 * providing efficient access to individual lines.
 */
@Slf4j
@Service
public class FileLineService {

    @Setter
    private String filePath;
    private final List<Long> lineOffsets = new ArrayList<>();

    /**
     * Builds an index of all line positions in the file.
     */
    public void buildLineIndex() {
        log.info("Building line index for file: {}", filePath);
        lineOffsets.clear();

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            // First line always starts at position 0
            lineOffsets.add(0L);

            long pos = 0;
            while (pos < file.length()) {
                byte b = file.readByte();
                pos++;

                // If we found a newline character, the next line starts right after it
                if (b == '\n') {
                    lineOffsets.add(pos);
                }
            }

            log.info("Indexed {} lines", lineOffsets.size());
        } catch (IOException e) {
            log.error("Error building line index", e);
            throw new RuntimeException("Failed to build line index", e);
        }
    }

    /**
     * Returns the text for a specific line by index (0-based).
     *
     * @param lineIndex The index of the line to retrieve (0-based)
     * @return The text of the requested line
     * @throws IndexOutOfBoundsException if the index is beyond the file bounds
     */
    public String getLine(int lineIndex) {
        if (lineIndex < 0 || lineIndex >= lineOffsets.size()) {
            throw new IndexOutOfBoundsException("Line index out of bounds: " + lineIndex);
        }

        try (RandomAccessFile file = new RandomAccessFile(filePath, "r")) {
            long startOffset = lineOffsets.get(lineIndex);

            // Calculate the length of the line
            long endOffset;
            if (lineIndex < lineOffsets.size() - 1) {
                endOffset = lineOffsets.get(lineIndex + 1);
            } else {
                endOffset = file.length();
            }

            int lineLength = (int) (endOffset - startOffset);
            if (lineLength > 0) {
                // For lines other than the last line, don't include the newline character
                if (lineIndex < lineOffsets.size() - 1) {
                    lineLength -= 1;
                }

                byte[] buffer = new byte[lineLength];
                file.seek(startOffset);
                file.readFully(buffer, 0, lineLength);

                return new String(buffer, StandardCharsets.US_ASCII);
            } else {
                return "";
            }
        } catch (IOException e) {
            log.error("Error reading line at index {}", lineIndex, e);
            throw new RuntimeException("Failed to read line " + lineIndex, e);
        }
    }

}
