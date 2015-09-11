package painless;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PainlessTypes {
    PainlessTypes(String file) {
        final Properties properties = new Properties();

        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(file)) {
            System.out.println(stream);
            properties.load(stream);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }


    }
}
