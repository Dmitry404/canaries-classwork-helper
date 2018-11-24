import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.function.Supplier;
import org.junit.Test;

public class A_Lambdas {
  @Test
  public void anonymousClasses() {
    Reader reader = new Reader() {
      @Override
      public String read() {
        return "data_string";
      }
    };

    String data = reader.read();

    assertThat(data).isEqualTo("data_string");
  }

  @Test
  public void lambda() {
    Reader reader = () -> "data_string";

    String data = reader.read();

    assertThat(data).isEqualTo("data_string");
  }

  @Test
  public void lambdaInMethodCall() {
    assertThat(

        transformResult(new Reader() {
          @Override
          public String read() {
            return "data_string";
          }
        })

    ).isEqualTo("DATA_STRING");

    assertThat(

        transformResult(() -> "data_string2")

    ).isEqualTo("DATA_STRING2");
  }

  private String transformResult(Reader reader) {
    return reader.read().toUpperCase();
  }

  @Test
  public void supplier() {
    Reader reader = new Reader() {
      @Override
      public String read() {
        return "data_string";
      }
    };

    Supplier<String> supplier = new Supplier<String>() {
      @Override
      public String get() {
        return "data_string";
      }
    };

    assertThat(reader.read()).isEqualTo("data_string");
    assertThat(supplier.get()).isEqualTo("data_string");


    Reader reader2 = () -> "";
    Supplier<String> supplier2 = () -> "";
  }

  private int instanceVariableInt = 100;
  private static int staticVariableInt = 200;

  @Test
  public void variablesClosure() {
    int localInt = 300;
    Reader reader1 = new Reader() {
      @Override
      public String read() {
        //localInt += 300;
        return "data_string_" + localInt + "_" + instanceVariableInt + "_" + staticVariableInt;
      }
    };

    Reader reader2 = () -> "data_string2_" + localInt + "_" + instanceVariableInt + "_" + staticVariableInt;

    assertThat(reader1.read()).isEqualTo("data_string_300_100_200");
    assertThat(reader2.read()).isEqualTo("data_string2_300_100_200");
  }

  @Test
  public void methodReference() {
    A_Lambdas obj = new A_Lambdas();

    Reader reader1 = this::readString;
    Reader reader2 = obj::readString;
    Reader reader3 = A_Lambdas::readStringStatic;

    //Reader reader4 = String::strip;
    Reader reader4 = String::new;

    assertThat(reader4.read()).isBlank();

    assertThat(reader1.read()).isEqualTo("data_string_100");
    assertThat(reader2.read()).isEqualTo("data_string_100");
    assertThat(reader3.read()).isEqualTo("data_string_200");
  }

  private String readString() {
    return "data_string_100";
  }

  private String readString(int i) {
    return "smth";
  }

  private static String readStringStatic() {
    return "data_string_200";
  }

  @FunctionalInterface
  interface Reader {
    String read();
  }

  @Test
  public void classWork() {
    //functional interface Writer receives a string, returns nothing
    //functional interface ReaderWriter receives a string, returns string

    //make them to work with generic types
  }

  @Test
  public void supplierApplications() {
    Supplier<String> supplier = () -> "debug enabled: " + debugEnabled;

    log("debug enabled: " + debugEnabled);
    log(() -> "debug enabled: " + debugEnabled);
  }

  private boolean debugEnabled;

  private void log(String logString) {
    if (debugEnabled) {
      System.out.println(logString);
    }
  }

  private void log(Supplier<String> stringSupplier) {
    if (debugEnabled) {
      System.out.println(stringSupplier.get());
    }
  }
}
