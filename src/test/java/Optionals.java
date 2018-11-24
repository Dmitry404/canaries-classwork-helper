import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.Test;

public class Optionals {

  @Test
  public void npe() {
    List<String> strings = getStrings();
    if (strings != null) {
      for (String s : strings) {
        System.out.println(s);
      }
    }
  }

  private List<String> getStrings() {
    //don't do like this, return empty list
    return null;
  }

  @Test
  public void emptyCollection() {
    for (String s : getStrings2()) {
      System.out.println(s);
    }
  }

  private List<String> getStrings2() {
    return Collections.emptyList();
  }

  @Test
  public void notCollection() {
    Optional<String> opt1 = Optional.of("string");
    Optional<String> opt2 = Optional.empty();

    //don't do anyhthing like this
    if (opt1.isPresent()) {
      System.out.println(opt1.get());
    }

    if (opt2.isEmpty()) {
      try {
        System.out.println(opt2.get());
      } catch (NoSuchElementException e) {
        System.out.println(e);
      }
    }
  }

  @Test
  public void anotherOptionalExample() {
    School school = new School();

    System.out.println(school.findStudent(key -> key.endsWith("roe")));
    System.out.println(school.findStudent(key -> key.endsWith("doe")));
    System.out.println(school.findStudent(key -> key.endsWith("loe")));

    Student loe = school.findStudent(key -> key.endsWith("loe"));
    if (loe != null) {
      System.out.println(loe.getAge());
    }

    Optional<Student> opt = school.findStudent2(key -> key.endsWith("doe"));
    //don't do anyhthing like this
    if (opt.isPresent()) {
      System.out.println(opt.get());
    }

    // if you just need to print the value if exists
    opt.ifPresent(s -> System.out.println(s));

    // if you need to print the value or print N/A if not exists
    opt.ifPresentOrElse(System.out::println, () -> System.out.println("N/A"));

    school.findStudent3(key -> key.startsWith("john"))
      .map(Student::getAge)
      .filter(age -> age > 20)
      .ifPresent(System.out::println);
  }

  @Test
  public void flatMapExample() {
    School school = new School();
    Student student = new Student("Harry Potter", 11, "the_boy_who_lived");
    school.map.put("harry.potter", student);

    Predicate<String> findHarry = s -> s.equals("harry.potter");

    school.findStudent3(findHarry)
        .map(Student::getName)
        .ifPresent(System.out::println);

    school.findStudent3(findHarry)
        .flatMap(Student::getNickName)
        .ifPresent(System.out::println);
  }

  class School {
    Map<String, Student> map = new HashMap<>();
    {
      map.put("john.doe", new Student("John Doe", 21));
      map.put("jane.roe", new Student("Jane Roe", 20));
      map.put("robert.roe", new Student("Robert Roe", 25));
      map.put("robert.pan", new Student("Robert Pan", 20));
    }

    public Student findStudent(Predicate<String> searchPredicate) {
     for(String key : map.keySet()) {
       if (searchPredicate.test(key)) {
         return map.get(key);
       }
     }
     return null;
    }

    public Optional<Student> findStudent2(Predicate<String> searchPredicate) {
      for(String key : map.keySet()) {
        if (searchPredicate.test(key)) {
          return Optional.of(map.get(key));
        }
      }
      return Optional.empty();
    }

    public Optional<Student> findStudent3(Predicate<String> searchPredicate) {
      return map.keySet().stream()
          .filter(searchPredicate)
          .map(key -> map.get(key))
          .findFirst();
    }
  }

  class Student {
    private final String name;
    private final int age;

    private String nickName;

    public Student(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public Student(String name, int age, String nickName) {
      this(name, age);
      this.nickName = nickName;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    public Optional<String> getNickName() {
      return Optional.ofNullable(nickName);
    }

    @Override
    public String toString() {
      return "Student{" +
          "name='" + name + '\'' +
          ", age=" + age +
          ", nickName=" + nickName +
          '}';
    }
  }

  @Test
  public void classwork() {
    School school = new School();

    // create a series of searches and print first 2 max ages
    List<Integer> ages = Stream.<Predicate<String>>of(
        s -> s.startsWith("john"),
        s -> s.endsWith("roe"),
        s -> s.contains("rob")
    )
        .map(school::findStudent3)
        .flatMap(Optional::stream)
        .map(Student::getAge)
        .sorted(Comparator.reverseOrder())
        .distinct()
        .limit(2)
        .collect(Collectors.toList());

    assertThat(ages).containsExactly(25, 21);
  }
}
