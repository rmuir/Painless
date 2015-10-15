package painless;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.invoke.MethodHandle;
import java.util.Map;
import java.util.Properties;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import static painless.Default.*;
import static painless.Definition.*;

final class Compiler {
    private static class Loader extends ClassLoader {
        Loader(ClassLoader parent) {
            super(parent);
        }

        public Class<? extends Executable> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length).asSubclass(Executable.class);
        }
    }

    static Executable compile(final String name, final String source,
                              final ClassLoader parent, final Properties properties) {
        long start = System.currentTimeMillis();

        final Definition definition = properties == null ? DEFAULT_DEFINITION : loadFromProperties(properties);
        final Standard standard = properties == null ? DEFAULT_STANDARD : new Standard(definition);
        final Caster caster = properties == null ? DEFAULT_CASTER : new Caster(definition, standard);

        long end = System.currentTimeMillis() - start;
        System.out.println("definition: " + end);
        start = System.currentTimeMillis();

        final ParseTree root = createParseTree(source, definition);

        end = System.currentTimeMillis() - start;
        System.out.println("tree: " + end);

        final Adapter adapter = new Adapter(definition, standard, caster, source, root);
        adapter.incrementScope();
        adapter.addVariable("this", adapter.standard.execType);
        adapter.addVariable("input", adapter.standard.smapType);
        adapter.addVariable("*runtime", adapter.standard.mapType);

        start = System.currentTimeMillis();

        Analyzer.analyze(adapter);
        adapter.decrementScope();

        end = System.currentTimeMillis() - start;
        System.out.println("analyze: " + end);
        start = System.currentTimeMillis();

        final byte[] bytes = Writer.write(adapter);

        end = System.currentTimeMillis() - start;
        System.out.println("write: " + end);
        start = System.currentTimeMillis();

        final Executable executable = createExecutable(name, source, definition.runtime, parent, bytes);

        end = System.currentTimeMillis() - start;
        System.out.println("create: " + end);

        return executable;
    }

    private static ParseTree createParseTree(String source, Definition definition) {
        final ANTLRInputStream stream = new ANTLRInputStream(source);
        final PainlessLexer lexer = new PainlessLexer(stream);
        final PainlessParser parser = new PainlessParser(new CommonTokenStream(lexer));

        parser.setTypes(definition.structs.keySet());

        ParseTree root = parser.source();
        System.out.println(root.toStringTree(parser));
        return root;
    }

    private static Executable createExecutable(String name, String source, Map<String, MethodHandle> runtime,
                                               ClassLoader parent, byte[] bytes) {
        try {
            try {
                FileOutputStream f = new FileOutputStream(new File("/Users/jdconrad/lang/generated/out.class"), false);
                f.write(bytes);
                f.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final Loader loader = new Loader(parent);
            final Class<? extends Executable> clazz = loader.define(Writer.CLASS_NAME, bytes);
            final java.lang.reflect.Constructor<? extends Executable> constructor =
                    clazz.getConstructor(String.class, String.class, Map.class);

            return constructor.newInstance(name, source, runtime);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private Compiler() {}
}
