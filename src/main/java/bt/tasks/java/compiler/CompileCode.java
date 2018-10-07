package bt.tasks.java.compiler;

import bt.api.EventBus;
import bt.api.Module;
import bt.api.Repository;
import bt.api.Subscribe;
import bt.api.Task;
import bt.api.events.CodeCompiled;
import bt.api.events.ModuleFound;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CompileCode implements Task {
  private static final Logger LOGGER = LoggerFactory.getLogger(CompileCode.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final JavaCompiler JAVA_COMPILER = ToolProvider.getSystemJavaCompiler();
  @Inject private EventBus eventBus;
  @Inject private Repository repository;

  @Subscribe
  public void consume(ModuleFound event) throws Exception {
    Module module = event.getModule();
    Path sourceSet = module.getSourceSet();

    Path output = module.getCompiledCode();
    CompilationOpts compilationOpts = getCompilationOpts(sourceSet);

    List<String> sourceFiles = getSourceFiles(sourceSet);

    if (sourceFiles.isEmpty()) {
      return;
    }

    Optional<Long> maxSourceLastModified =
        Files.walk(sourceSet)
            .filter(file -> file.toString().endsWith(".java"))
            .map(file -> file.toFile().lastModified())
            .max(Long::compareTo);

    if (!canSkip(sourceSet, output, maxSourceLastModified)) {

      if (!Files.exists(output)) {
        Files.createDirectories(output);
      }

      LOGGER.debug("compiling {} to {}", sourceSet, output);

      List<String> arguments = new ArrayList<>();
      arguments.add("-source");
      arguments.add(String.valueOf(compilationOpts.getSource()));
      arguments.add("-target");
      arguments.add(String.valueOf(compilationOpts.getTarget()));
      arguments.add("-cp");
      arguments.add(repository.getClassPath(sourceSet));
      arguments.add("-d");
      arguments.add(output.toAbsolutePath().toString());
      arguments.addAll(sourceFiles);

      LOGGER.debug("{}", arguments);

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
    eventBus.emit(new CodeCompiled(module));
  }

  private boolean canSkip(Path sourceSet, Path output, Optional<Long> maxSourceLastModified)
      throws IOException {
    if (output.toFile().exists()) {
      Optional<Long> maxLastModified =
          Files.walk(output)
              .filter(file -> file.toString().endsWith(".class"))
              .map(file -> file.toFile().lastModified())
              .max(Long::compareTo);

      if (maxLastModified.isPresent()
          && maxSourceLastModified.isPresent()
          && maxLastModified.get() >= maxSourceLastModified.get()) {
        LOGGER.debug("skipping {}, no changes since last compilation", sourceSet);
        return true;
      }
    }
    return false;
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
