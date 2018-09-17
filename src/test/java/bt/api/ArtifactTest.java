package bt.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ArtifactTest {

  @Test
  public void simple() {
    Artifact artifact = Artifact.valueOf("g:a:v");
    assertEquals("g", artifact.getGroupId());
    assertEquals("a", artifact.getArtifactId());
    assertEquals("jar", artifact.getType());
    assertEquals("v", artifact.getVersion());
    assertNull(artifact.getClassifier());
    assertEquals("g:a:v", artifact.toString());
  }

  @Test
  public void type() {
    Artifact artifact = Artifact.valueOf("g:a:t:v");
    assertEquals("g", artifact.getGroupId());
    assertEquals("a", artifact.getArtifactId());
    assertEquals("t", artifact.getType());
    assertEquals("v", artifact.getVersion());
    assertNull(artifact.getClassifier());
    assertEquals("g:a:t:v", artifact.toString());
  }

  @Test
  public void classifier() {
    Artifact artifact = Artifact.valueOf("g:a:t:v:c");
    assertEquals("g", artifact.getGroupId());
    assertEquals("a", artifact.getArtifactId());
    assertEquals("t", artifact.getType());
    assertEquals("v", artifact.getVersion());
    assertEquals("c", artifact.getClassifier());
    assertEquals("g:a:t:v:c", artifact.toString());
  }
}
