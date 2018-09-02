package bt.tasks.source.compiler;

import bt.api.Task;
import bt.tasks.source.finder.SourceSets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Compile implements Task<CompiledCode> {
  private static final Logger LOGGER = LoggerFactory.getLogger(Compile.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
  @Inject private SourceSets sourceSets;

  @Override
  public CompiledCode run() throws Exception {
    Set<Path> compiledCode = new HashSet<>();
    for (Path sourceSet : sourceSets.getSourceSets()) {

      Path javaSources = sourceSet.resolve(Paths.get("java"));
      List<String> sourceFiles = getSourceFiles(javaSources);

      if (sourceFiles.isEmpty()) {
        continue;
      }

      CompilationOpts compilationOpts = getCompilationOpts(javaSources);
      Path output = getTarget(sourceSet);
      if (!Files.exists(output)) {
        Files.createDirectory(output);
      }

      LOGGER.info("compiling {} to {}", javaSources, output);

      List<String> classPath = getClassPath(compilationOpts);
      List<String> arguments = new ArrayList<>();
      arguments.add("-source");
      arguments.add(String.valueOf(compilationOpts.getSource()));
      arguments.add("-target");
      arguments.add(String.valueOf(compilationOpts.getTarget()));
      arguments.add("-cp");
      arguments.add(String.join(":", classPath));
      arguments.add("-d");
      arguments.add(output.toAbsolutePath().toString());
      arguments.addAll(sourceFiles);

      LOGGER.info("{}", arguments);

      JAVA_COMPILER.run(
          null,
          new LogOutputStream(LOGGER::info),
          new LogOutputStream(LOGGER::warn),
          arguments.toArray(new String[0]));

      compiledCode.add(output);
    }

    return new CompiledCode(compiledCode);
  }

  private Path getTarget(Path sourceSet) {
    String fileName = sourceSet.getFileName().toString();
    return Paths.get("target", fileName.equals("main") ? "classes" : fileName + "-classes");
  }

  private List<String> getSourceFiles(Path javaSources) throws IOException {
    return Files.find(
            javaSources,
            Integer.MAX_VALUE,
            (path, basicFileAttributes) -> path.toString().endsWith(".java"))
        .map(String::valueOf)
        .collect(Collectors.toList());
  }

  private List<String> getClassPath(CompilationOpts compilationOpts) {
    return compilationOpts
        .getDependencies()
        .stream()
        .map(Artifact::valueOf)
        .map(
            artifact ->
                System.getProperty("user.home")
                    + "/.m2/repository/"
                    + artifact.getGroupId().replaceAll("\\.", "/")
                    + "/"
                    + artifact.getArtifactId()
                    + "/"
                    + artifact.getVersion()
                    + "/"
                    + artifact.getArtifactId()
                    + "-"
                    + artifact.getVersion()
                    + ".jar")
        .collect(Collectors.toList());
  }

  private CompilationOpts getCompilationOpts(Path javaSources) throws java.io.IOException {
    Path compilationOptsPath = javaSources.resolve(Paths.get("compilation-opts.json"));

    return Files.exists(compilationOptsPath)
        ? OBJECT_MAPPER.readValue(compilationOptsPath.toFile(), CompilationOpts.class)
        : new CompilationOpts();
  }

  @Override
  public String toString() {
    return "Compile{" + "sourceSets=" + sourceSets + '}';
  }
}
