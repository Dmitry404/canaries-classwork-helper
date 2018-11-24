import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.Comparator;
import org.junit.Test;

public class D_Comparators {

  @Test
  public void asAnonymousClass() {
    Comparator<String> comparator = new Comparator<String>() {
      @Override
      public int compare(String o1, String o2) {
        return o1.compareTo(o2);
      }
    };

    assertThat(comparator.compare("a", "b")).isLessThan(0);
    assertThat(comparator.compare("c", "b")).isGreaterThan(0);
    assertThat(comparator.compare("b", "b")).isEqualTo(0);
  }

  @Test
  public void asLambda() {
//    Comparator<String> howComparingMagicWorks = (o1, o2) -> o1.compareTo(o2);
    Comparator<String> comparator = String::compareTo;

    assertThat(comparator.compare("a", "b")).isLessThan(0);
    assertThat(comparator.compare("c", "b")).isGreaterThan(0);
    assertThat(comparator.compare("b", "b")).isEqualTo(0);
  }

  @Test
  public void classWork() {
    // create several Comparator anonymous instances which work with Student class and:
    // - compare by last names
    // - compare by last names and if equal, then by first name
    // - if still equal, compare by age
  }

  @Test
  public void compareStudentsUsingLambdas() {
    Comparator<Student> studentsComparator = Comparator.comparing(Student::getLastName)
        .thenComparing(Student::getFirstName)
        .thenComparing(Student::getAge);

    Student john = new Student("John", "Doe", 21);
    Student jane = new Student("Jane", "Doe", 21);

    assertThat(studentsComparator.compare(john, jane)).isLessThan(0);
  }

  class Student {
    private final String firstName;
    private final String lastName;
    private final int age;

    Student(String firstName, String lastName, int age) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.age = age;
    }

    public String getFirstName() {
      return firstName;
    }

    public String getLastName() {
      return lastName;
    }

    public int getAge() {
      return age;
    }
  }
}
