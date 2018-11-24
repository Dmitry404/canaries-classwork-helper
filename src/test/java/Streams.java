import static org.assertj.core.api.Java6Assertions.assertThat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class Streams {

  @Test
  public void simpleExample() throws IOException {
    // open file from FS
    // read it's content line by line
    // if a line contains string ERROR, put it to the list (you should create it beforehand)
    // stop when (if) you collected 5 elements

    List<String> errors = new ArrayList<>();
    int count = 0;

    BufferedReader bufferedReader = new BufferedReader(
        new FileReader("/tmp/file")
    );

    try (bufferedReader) {
      String line = bufferedReader.readLine();
      while (line != null && count < 5) {
        if (line.contains("ERROR")) {
          errors.add(line);
          count++;
        }
        line = bufferedReader.readLine();
      }
    }

    assertThat(errors).hasSize(5);
  }

  @Test
  public void simpleExampleWithStreams() throws IOException {
    List<String> errors = Files.lines(Path.of("/tmp/file"))
        .filter(line -> line.contains("ERROR"))
        .limit(5)
        .collect(Collectors.toList());

    assertThat(errors).hasSize(5);
  }

  @Test
  public void simpleExampleImproved() throws IOException {
    List<String> errors = new ArrayList<>();
    for (String line : Files.readAllLines(Path.of("/tmp/file"))) {
      if (line.contains("ERROR") && errors.size() < 5) {
        errors.add(line);
      }
    }

    assertThat(errors).hasSize(5);
  }

  @Test
  public void mapExample() throws IOException {
    // collect to list strings with first letters - limit to 4
    List<String> errors = Files.lines(Path.of("/tmp/file"))
        .map(line -> line.substring(0, 2))
        .limit(4)
        .collect(Collectors.toList());

    assertThat(errors).containsExactly("1.", "2.", "3.", "4.");
  }

  @Test
  public void butNotThatSimple() throws IOException {
    // collect strings with index of line

    int count[] = {0};
    List<String> errors = Files.lines(Path.of("/tmp/file"))
        .map(line -> {
          count[0]++;
          return count[0] + ": " + line.substring(2);
        })
        .limit(4)
        .collect(Collectors.toList());

    assertThat(errors).startsWith("1:", "2:", "3:", "4:");


    // ---------
    List<String> errors2 = new ArrayList<>();
    int count2 = 0;
    for (String line : Files.readAllLines(Path.of("/tmp/file"))) {
      if (errors2.size() > 4) {
        break;
      }
      errors2.add(count2++ + ": " + line);
    }

    errors2.forEach(System.out::println);
  }

  @Test
  public void classWork() throws IOException {
    // remove line numbers, split each line on words and print the first 5 not including those which contain ERROR
    List<String> strings = Files.lines(Path.of("/tmp/file"))
        .map(s -> s.substring(2))
        .flatMap(line -> Arrays.stream(line.split(" ")))
        .filter(s -> !s.contains("ERROR"))
        .limit(4)
        .collect(Collectors.toList());

    assertThat(strings).containsExactly("nkksxn", "sndkwskdne", "slmlwefm", "ldmlef");
  }

  @Test
  public void anotherFlatMap() {
    List<Student> students = new ArrayList<>();
    students.add(new Student("John Doe", List.of("CS101", "Physics", "Math")));
    students.add(new Student("Jane Roe", List.of("CS101", "Art")));

    // print unique classes titles
    List<String> classes = students.stream()
        .flatMap(s -> s.getClasses().stream())
        .distinct()
        .sorted()
        .collect(Collectors.toList());

    assertThat(classes).containsExactly("Art", "CS101", "Math", "Physics");
  }

  class Student {
    private final String name;
    private final List<String> classes;

    Student(String name, List<String> classes) {
      this.name = name;
      this.classes = classes;
    }

    public String getName() {
      return name;
    }

    public List<String> getClasses() {
      return classes;
    }
  }

  @Test
  public void collectToSet() {
    Set<String> strings = Stream.of("hello", "set", "hello")
//        .collect(Collectors.toSet());
        .collect(Collectors.toCollection(TreeSet::new));


//    assertThat(strings).contains("hello", "set");
    assertThat(strings).containsExactly("hello", "set");
  }

  @Test
  public void collectToMap() {
    int count[] = {0};
    Map<Integer, String> map = Stream.of("hello", "set", "hello")
        .collect(Collectors.toMap(
            str -> count[0]++,
            str -> str
        ));
    map.forEach((k, v) -> System.out.println(k + ": " + v));

    assertThat(map.keySet()).containsExactly(0, 1, 2);
    assertThat(map.values()).containsExactly("hello", "set", "hello");
  }

  @Test
  public void collectToArray() {
    String[] strings = List.of("hello", "array")
        .toArray(new String[0]);

    assertThat(strings).containsExactly("hello", "array");
  }
}
