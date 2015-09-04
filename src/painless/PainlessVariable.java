package painless;

import org.objectweb.asm.Type;

final class PainlessVariable {
    private final String name;
    private final Type type;

    private final int scope;
    private final int slot;

    PainlessVariable(final String name, final Type type, final int scope, final int slot) {
        this.name = name;
        this.type = type;

        this.slot = slot;
        this.scope = scope;
    }

    String getName() {
        return name;
    }

    Type getType() {
        return type;
    }

    int getScope() {
        return scope;
    }

    int getSlot() {
        return slot;
    }
}
