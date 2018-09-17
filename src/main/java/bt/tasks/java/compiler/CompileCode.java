package bt.tasks.java.compiler;

import bt.api.Dependency;
import bt.api.Repository;
import bt.api.Task;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompileCode implements Task<CompiledCode> {
  private static final Logger LOGGER = LoggerFactory.getLogger(CompileCode.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
  @Inject private Map<Path, List<Dependency>> dependencies;
  @Inject private Repository repository;

  @Override
  public CompiledCode run() throws Exception {
    Map<Path, List<Path>> compiledCode = new HashMap<>();
    for (Path sourceSet : dependencies.keySet()) {
      Path output = getTarget(sourceSet);
      CompilationOpts compilationOpts = getCompilationOpts(sourceSet);
      List<Path> classPath =
          dependencies.get(sourceSet).stream().map(repository::get).collect(Collectors.toList());

      List<String> sourceFiles = getSourceFiles(sourceSet);

      if (sourceFiles.isEmpty()) {
        continue;
      }

      compiledCode.put(output, classPath);

      Optional<Long> maxSourceLastModified =
          Files.walk(sourceSet)
              .filter(file -> file.toString().endsWith(".java"))
              .map(file -> file.toFile().lastModified())
              .max(Long::compareTo);

      if (output.toFile().exists()) {
        Optional<Long> maxLastModified =
            Files.walk(output)
                .filter(file -> file.toString().endsWith(".class"))
                .map(file -> file.toFile().lastModified())
                .max(Long::compareTo);

        if (maxLastModified.isPresent()
            && maxSourceLastModified.isPresent()
            && maxLastModified.get() >= maxSourceLastModified.get()) {
          LOGGER.info("skipping {}, no changes since last compilation", sourceSet);
          continue;
        }
      }

      if (!Files.exists(output)) {
        Files.createDirectory(output);
      }

      LOGGER.info("compiling {} to {}", sourceSet, output);

      List<String> arguments = new ArrayList<>();
      arguments.add("-source");
      arguments.add(String.valueOf(compilationOpts.getSource()));
      arguments.add("-target");
      arguments.add(String.valueOf(compilationOpts.getTarget()));
      arguments.add("-cp");
      arguments.add(classPath.stream().map(String::valueOf).collect(Collectors.joining(":")));
      arguments.add("-d");
      arguments.add(output.toAbsolutePath().toString());
      arguments.addAll(sourceFiles);

      LOGGER.info("{}", arguments);

      int exitCode =
          JAVA_COMPILER.run(
              null,
              new LogOutputStream(LOGGER::info),
              new LogOutputStream(LOGGER::warn),
              arguments.toArray(new String[0]));

      if (exitCode != 0) {
        throw new IllegalStateException();
      }
    }

    return new CompiledCode(compiledCode);
  }

  private Path getTarget(Path sourceSet) throws IOException {
    Path parent = Paths.get("target", "java");
    Files.createDirectories(parent);
    return parent.resolve(sourceSet.getFileName());
  }

  private List<String> getSourceFiles(Path javaSources) throws IOException {
    return Files.find(
            javaSources,
            Integer.MAX_VALUE,
            (path, basicFileAttributes) -> path.toString().endsWith(".java"))
        .map(String::valueOf)
        .collect(Collectors.toList());
  }

  private CompilationOpts getCompilationOpts(Path javaSources) throws java.io.IOException {
    Path compilationOptsPath = javaSources.resolve(Paths.get("compilation-opts.json"));

    return Files.exists(compilationOptsPath)
        ? OBJECT_MAPPER.readValue(compilationOptsPath.toFile(), CompilationOpts.class)
        : new CompilationOpts();
  }
}
