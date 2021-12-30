package hello.typeconverter;

import hello.typeconverter.formatter.MyNumberFormatter;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;

public class MyNumberFormatterTest {
    MyNumberFormatter formatter = new MyNumberFormatter();
    @Test
    void parse() throws ParseException {
        Number result = formatter.parse("1,000", Locale.KOREA);
        assertThat(result).isEqualTo(1000L); //Long 타입 주의
    }
    @Test
    void print() {
        String result = formatter.print(1000, Locale.KOREA);
        assertThat(result).isEqualTo("1,000");
    }

}
