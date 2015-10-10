package painless;

import java.util.Map;

public abstract class Executable {
    private final String name;
    private final String source;

    public final Runtime runtime;

    public Executable(final String name, final String source, final Runtime runtime) {
        this.name = name;
        this.source = source;
        this.runtime = runtime;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public abstract Object execute(Map<String, Object> input);
}
