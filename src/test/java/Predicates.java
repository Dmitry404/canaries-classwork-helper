import static java.util.function.Predicate.*;
import static org.assertj.core.api.Java6Assertions.assertThat;

import java.util.function.Predicate;
import org.junit.Test;

public class Predicates {

  @Test
  public void asAnonymousClass() {
    Predicate<String> blankStringPredicate = new Predicate<String>() {
      @Override
      public boolean test(String s) {
        return s.isBlank();
      }
    };

    assertThat(blankStringPredicate.test("   ")).isTrue();
  }

  @Test
  public void asLambda() {
//    Predicate<String> blankStringPredicate = s -> s.isBlank();
    Predicate<String> blankStringPredicate = String::isBlank;

    assertThat(blankStringPredicate.test("   ")).isTrue();
  }

  @Test
  public void negation() {
    Predicate<String> blankStringPredicate = String::isBlank;
    Predicate<String> nonBlankPredicate = blankStringPredicate.negate();

    Predicate<String> nonBlankPredicate2 = not(blankStringPredicate);

    assertThat(nonBlankPredicate.test("   ")).isFalse();
    assertThat(nonBlankPredicate.test(" d ddd")).isTrue();

    assertThat(not(blankStringPredicate).test("etsttest")).isTrue();
  }

  @Test
  public void classWork() {
    // create MyPredicate interface
    // remember Generalized types
    // implement and/or methods (remember default methods)
    // check what will happen if you pass null on the following clause
    // p1.and(null).test(val);
  }
}
