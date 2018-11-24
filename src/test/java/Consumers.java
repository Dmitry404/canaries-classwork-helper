import static org.assertj.core.api.Java6Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Consumers {

  private final ByteArrayOutputStream output = new ByteArrayOutputStream();

  @Before
  public void setUp() throws Exception {
    System.setOut(new PrintStream(output));
  }

  @After
  public void tearDown() throws Exception {
    output.reset();
  }

  @Test
  public void asInterface() {
    Consumer<String> consumer = new Consumer<String>() {
      @Override
      public void accept(String s) {
        System.out.println(s);
      }
    };

    consumer.accept("hello_consumer");

    assertThat(output.toString()).isEqualTo("hello_consumer\n");
  }

  @Test
  public void asLambda() {
    Consumer<String> consumer1 = (s) -> System.out.println(s);
    Consumer<String> consumer2 = System.out::print;


    consumer1.accept("hello_consumer");

    assertThat(output.toString()).isEqualTo("hello_consumer\n");

    output.reset();
    consumer2.accept("hello_consumer2");

    assertThat(output.toString()).isEqualTo("hello_consumer2");
  }

  @Test
  public void clearTheList() {
//    Consumer<List<String>> consumer = list -> list.clear();
    Consumer<List<String>> consumer = List::clear;

    List<String> strings = new ArrayList<>(List.of("hello", "consumer"));

    consumer.accept(strings);

    assertThat(strings).isEmpty();
  }

  @Test
  public void composition() {
    Consumer<List<String>> consumer1 = list -> list.add("hello");
    Consumer<List<String>> consumer2 = list -> list.add("consumer");
    Consumer<List<String>> consumer3 = list -> list.add("!!!");

    List<String> list = new ArrayList<>();

    consumer1
        .andThen(consumer2
            .andThen(consumer3))
        .accept(list);

    assertThat(list).containsExactly("hello", "consumer", "!!!");

    List<String> list2 = new ArrayList<>();

    Consumer<List<String>> consumer4 = consumer1.andThen(consumer3);

    consumer4.accept(list2);

    assertThat(list2).containsExactly("hello", "!!!");
  }

  @Test
  public void applications() {
    List.of("hello", "from", "forEach")
        .forEach(System.out::println);

    assertThat(output.toString()).isEqualTo("hello\nfrom\nforEach\n");
  }
}
