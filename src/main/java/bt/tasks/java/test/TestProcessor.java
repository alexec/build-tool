package bt.tasks.java.test;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.HashSet;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({"org.junit.Test"})
public class TestProcessor extends AbstractProcessor {

  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

    try {
      for (TypeElement annotation : annotations) {
        FileObject tests =
            processingEnv
                .getFiler()
                .createResource(StandardLocation.CLASS_OUTPUT, "", "META-INF/tests");

        Set<String> types = new HashSet<>();

        try (PrintWriter out = new PrintWriter(tests.openOutputStream())) {
          for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {

            String type = String.valueOf(element.getEnclosingElement().asType());

            if (types.add(type)) {

              processingEnv
                  .getMessager()
                  .printMessage(Diagnostic.Kind.OTHER, "indexed test " + type);

              out.println(type);
            }
          }
        }
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    return true;
  }
}
