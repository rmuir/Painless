package painless;

import java.util.Map;

public abstract class PainlessExecutable {
    private final String name;
    private final String source;

    public PainlessExecutable(String name, String source) {
        this.name = name;
        this.source = source;
    }

    public String getName() {
        return name;
    }

    public String getSource() {
        return source;
    }

    public abstract Object execute(Map<String, Object> input);
}
