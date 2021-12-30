package hello.typeconverter.converter;

import hello.typeconverter.type.IpPort;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import static org.assertj.core.api.Assertions.assertThat;

public class ConversionServiceTest {

    @Test
    void conversionService() {
//등록
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(new StringToIntegerConverter());
        conversionService.addConverter(new IntegerToStringConverter());
        conversionService.addConverter(new StringToIpPortConverter());
        conversionService.addConverter(new IpPortToStringConverter());

        assertThat(conversionService.convert("10",Integer.class)).isEqualTo(10);
        assertThat(conversionService.convert(10,String.class)).isEqualTo("10");

        IpPort ipPort = conversionService.convert("127.0.0.1:8080",IpPort.class);
        assertThat(ipPort).isEqualTo(new IpPort("127.0.0.1", 8080));
        String ipPortString = conversionService.convert(new IpPort("127.0.0.1",8080), String.class);
        assertThat(ipPortString).isEqualTo("127.0.0.1:8080");
    }
}
