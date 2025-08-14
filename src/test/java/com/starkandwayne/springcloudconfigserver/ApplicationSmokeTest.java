package com.starkandwayne.springcloudconfigserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

@SpringBootTest
@TestPropertySource(properties = {
    // Disable CredHub during tests to avoid needing external CredHub
    "scs.credhub.enabled=false",
    // Provide minimal required oauth client props to satisfy any conditional beans
    "scs.dashboard.client-id=test",
    "scs.dashboard.client-secret=test",
    // Point config server to a local dummy git repository created below
    "spring.cloud.config.server.git.uri=file:./target/test-classes/dummy-config-repo"
})
class ApplicationSmokeTest {

    static {
        // Initialize a minimal local git repo so Config Server can start
        Path repoPath = Path.of("target", "test-classes", "dummy-config-repo");
        try {
            Files.createDirectories(repoPath);
            // Only init if not already a git repo
            if (Files.notExists(repoPath.resolve(".git"))) {
                try (Git git = Git.init().setDirectory(repoPath.toFile()).call()) {
                    Path readme = repoPath.resolve("README.md");
                    if (Files.notExists(readme)) {
                        Files.writeString(readme, "# Dummy config repo for tests\n");
                    }
                    git.add().addFilepattern("README.md").call();
                    git.commit().setMessage("Initial commit").setAuthor("Test","test@example.com").call();
                }
            }
        } catch (IOException | GitAPIException e) {
            throw new RuntimeException("Failed to initialize dummy git repository for tests", e);
        }
    }

    @Test
    void contextLoads() {
        // Basic context load smoke test.
    }
}
