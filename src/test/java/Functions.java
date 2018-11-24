import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import org.junit.Test;

public class Functions {

  @Test
  public void asLambda() {
//    Function<Integer, String> fun = i -> Integer.toHexString(i);
    Function<Integer, String> fun = Integer::toHexString;

    assertThat(fun.apply(255)).isEqualTo("ff");
  }

  @Test
  public void howComparingMagicWorks() {
    Student john = new Student("John Doe", 21);
    Student jane = new Student("Jane Doe", 21);

    Student fakeJohn = new Student("John Doe", -1);

    // john.getName().compareTo(jane.getName())
    // assertThat(
    //  john.getName().compareTo(jane.getName())).isGreaterThan(0);

//    Function<Student, String> fun = st -> st.getName();
//    Function<Student, String> fun = Student::getName;

    // !!! Student::getName should return comparable type !!!

    int compared = comparingBy(Student::getName).compare(john, jane);

    assertThat(compared).isGreaterThan(0);

    int compared2 = comparingBy(Student::getName)
        .compare(john, fakeJohn);

    int compared3 = comparingBy(Student::getName)
        .thenComparing(Student::getAge)
        .compare(john, fakeJohn);

    assertThat(compared2).isEqualTo(0);
    assertThat(compared3).isGreaterThan(0);

    List.of(john, fakeJohn, jane).stream()
        .sorted(comparingBy(Student::getName))
        .forEach(System.out::println);
  }

  private static Comparator<Student> comparingBy(Function<Student, String> function) {
    Comparator<Student> comparator = new Comparator<Student>() {
      @Override
      public int compare(Student o1, Student o2) {
        //return o1.compareTo(o2); won't compile
        String leftProperty = function.apply(o1);
        String rightProperty = function.apply(o2);

        return leftProperty.compareTo(rightProperty);

//        return function.apply(o1)
//            .compareTo(function.apply(o2));
      }
    };
    return comparator;
  }

  private static Comparator<Student> thenComparing(Comparator<Student> other) {
    // we cannot do it as we need current comparator to implement such functionality
    return null;
  }
  
  class Student {
    private final String name;
    private final int age;

    Student(String name, int age) {
      this.name = name;
      this.age = age;
    }

    public String getName() {
      return name;
    }

    public int getAge() {
      return age;
    }

    @Override
    public String toString() {
      return "Student{" +
          "name='" + name + '\'' +
          ", age=" + age +
          '}';
    }
  }

  @Test
  public void application() {
    Converter converter = new Converter();

//    assertThat(converter.convert(27.9, 100)).isEqualTo(2790.0);

//    converter.convert(100);
    AdvancedConverter usdConverter = converter.defineRate(() -> 27.9);
    AdvancedConverter eurConverter = converter.defineRate(() -> 35.0);
    AdvancedConverter trickyConverter =
        converter.defineRate(() -> 27.9)
                 .andThenAdd(this::applyBankFee);

    assertThat(usdConverter.convert(100)).isEqualTo(2790.0);
    assertThat(eurConverter.convert(100)).isEqualTo(3500.0);

    assertThat(trickyConverter.convert(100)).isEqualTo(2790.0 + (2790.0 / 100) * 0.5);
  }

  private double applyBankFee(double sum) {
    return sum * 0.005;
  }

  private class Converter {
    public double convert(double rate, int value) {
      return rate * value;
    }

    AdvancedConverter defineRate(Supplier<Double> rateSupplier) {
      return value -> convert(rateSupplier.get(), value);
    }
  }

  interface AdvancedConverter {
    double convert(int value);

    default AdvancedConverter andThenAdd(AdvancedConverter other) {
      return value -> {
        double converted = convert(value);
        return converted + other.convert((int) converted);
      };
    }
  }
}
