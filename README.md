# Line Server

A REST service that serves individual lines from a text file.

## Questions

### How does your system work? (if not addressed in comments in source)

The line server is implemented as a Spring Boot REST application that serves individual lines from a text file.  

The desired text file is specified when starting the server using the `run.sh` script.
The server uses a `RandomAccessFile` to read lines from the file without loading the entire file into memory.

Here's how it works:
- Indexing Phase: 
  - When the server starts, it builds an index of all line positions in the file by scanning the file once and recording the byte offsets where each line begins. 
  - This approach allows for efficient random access later.
- Line Retrieval: 
  - When a client requests a line by its index via the `/lines/{lineIndex}` endpoint, the server:
    - Checks if the requested line index is valid.
    - Uses `RandomAccessFile` to jump directly to the correct byte offset using the prebuilt index. 
    - Reads the requested line from without loading the entire file.
    - Returns the line content with a 200 status code, or a 413 error if the line index is out of bounds. 
- Error Handling: 
  - A global exception handler manages errors, including returning appropriate HTTP status codes for different error scenarios.

### How will your system perform with a 1 GB file? a 10 GB file? a 100 GB file?

Overall, the system should be able to handle large files efficiently.

As for specific file sizes:
- 1 GB file: The system will perform well as it only indexes line positions during startup and then accesses 
individual lines directly. Memory usage will be proportional to the number of lines, not the file size.
- 10 GB file: Performance should remain good. The indexing phase will take longer, but line retrieval will still be fast. 
The memory footprint will increase for the index (storing line start positions), not the file content.
- 100 GB file: Indexing will take significantly longer during startup - retrieval operation should remain efficient. 
If the file contains billions of lines, the index itself could become large, but much smaller than the file.

### How will your system perform with 100 users? 10000 users? 1000000 users?

The system's performance with increasing users:
- 100 users: The system should handle this load easily. Spring Boot's embedded Tomcat server can manage this number of 
concurrent connections, and since file access operations are very quick (direct random access), response times should 
remain low.
- 10,000 users: At this scale, the system is likely to start experiencing some performance degradation, due to:
  - Increased contention for file access. 
  - Server resource limitations.
- 1,000,000 users: The current implementation would likely struggle with this load. Improvements could include:
  - Implementing some mechanism to not open/close the file for each request.
  - Adding a caching mechanism for frequently accessed lines.
  - Scaling the service horizontally, by distributing it across multiple nodes.

### What documentation, websites, papers, etc did you consult in doing this assignment?

My main sources of information and documentation for this exercise were:
- [Spring Boot Reference Documentation](https://docs.spring.io/spring-boot/index.html)
- GitHub Copilot: 
  - Used to assist with code generation and suggestions throughout the implementation.
- [Spring Boot Compression](https://howtodoinjava.com/spring-boot/response-gzip-compression/)
- [Enable HTTP2 with Tomcat in Spring Boot](https://www.baeldung.com/spring-boot-http2-tomcat)
- [How to use RandomAccessFile in Java](https://www.codejava.net/java-se/file-io/java-io-how-to-use-randomaccess-file-java-io-package)

### What third-party libraries or other tools does the system use? How did you choose each library or framework you used?

The most relevant libraries and tools used in this project are:
- Spring Boot: Provides a robust framework for building RESTful services, with built-in dependency injection, configuration, and web server.
- Lombok: Reduces boilerplate code through annotations like `@Slf4j` for logging and `@RequiredArgsConstructor` for constructor injection.
- Gradle: Used as the build tool as evidenced by `build.gradle.kts` file.

These tools were chosen based on my familiarity with them and their suitability for building a REST service efficiently - 
this was the main factor as it allowed me to have a working solution quickly and focus on the core functionality of the exercise.

### How long did you spend on this exercise? If you had unlimited more time to spend on this, how would you spend it and how would you prioritize each item?
 
I spent approximately 4 hours on this exercise (between 2/3 hours on building/testing the `line-server` application and 
around 1 hour to create the answers to these questions). 

If I had unlimited time to improve this solution, I would prioritize enhancements in this order:
- Implement a caching layer to store frequently accessed lines, reducing disk reads. This could give me a significant 
performance boost without too much effort.
- Add testing/benchmarking to measure performance - this would help identify bottlenecks and validate improvements.
- Add support for horizontal scaling.
- Add health checks and monitoring.
- Improve error handling and recovery mechanisms.

### If you were to critique your code, what would you have to say about it?

About the code itself, I would say:
- Strengths:
  - Clean and organized code structure.
  - Efficient indexing strategy. 
  - Good exception handling with the desired HTTP status codes. 
  - Easy to read and understand.
- Areas for improvement:
  - No caching mechanism for frequently accessed lines.
  - Creating a new RandomAccessFile for each request could impact performance. 
  - No load/performance tests to validate performance under load. 
  - Similar to the previous point, the solution could benefit from more robust logging, 
especially for performance metrics