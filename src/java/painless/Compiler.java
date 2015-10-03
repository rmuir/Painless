package painless;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import static painless.Default.*;
import static painless.Types.*;

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

        final Types types = properties == null ? DEFAULT_TYPES : loadFromProperties(properties);
        final Standard standard = properties == null ? DEFAULT_STANDARD : new Standard(types);

        long end = System.currentTimeMillis() - start;
        System.out.println("types: " + end);
        start = System.currentTimeMillis();

        final ParseTree root = createParseTree(source, types);

        end = System.currentTimeMillis() - start;
        System.out.println("tree: " + end);

        final Adapter adapter = new Adapter(types, standard, root);
        adapter.incrementScope();
        adapter.addVariable("this", adapter.standard.execType);
        adapter.addVariable("input", adapter.standard.smapType);

        start = System.currentTimeMillis();

        Analyzer.analyze(adapter);
        adapter.decrementScope();

        end = System.currentTimeMillis() - start;
        System.out.println("analyze: " + end);
        start = System.currentTimeMillis();

        //final byte[] bytes = Writer.write(source, root, pmetadata);

        end = System.currentTimeMillis() - start;
        System.out.println("write: " + end);
        start = System.currentTimeMillis();

        //final Executable executable = createExecutable(name, source, parent, bytes);

        end = System.currentTimeMillis() - start;
        System.out.println("create: " + end);

        //return executable;
        return null;
    }

    private static ParseTree createParseTree(String source, Types types) {
        final ANTLRInputStream stream = new ANTLRInputStream(source);
        final PainlessLexer lexer = new PainlessLexer(stream);
        final PainlessParser parser = new PainlessParser(new CommonTokenStream(lexer));

        parser.setTypes(types.structs.keySet());

        ParseTree root = parser.source();
        System.out.println(root.toStringTree(parser));
        return root;
    }

    private static Executable createExecutable(String name, String source, ClassLoader parent, byte[] bytes) {
        /*try {
            try {
                FileOutputStream f = new FileOutputStream(new File("/Users/jdconrad/lang/generated/out.class"), false);
                f.write(bytes);
                f.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final Loader loader = new Loader(parent);
            final Class<? extends Executable> clazz = loader.define(Writer.CLASS_NAME, bytes);
            final Constructor<? extends Executable> constructor =
                    clazz.getConstructor(String.class, String.class);

            return constructor.newInstance(name, source);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }*/

        return null;
    }

    private Compiler() {}
}
