package painless;

import java.util.Map;

public abstract class Executable {
    private final String name;
    private final String source;

    public Executable(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public String str;

    public abstract Object execute(Map<String, Object> input);
}
