package painless;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import static painless.PainlessAnalyzer.*;
import static painless.PainlessTypes.*;

final class PainlessCompiler {
    private static class PainlessClassLoader extends ClassLoader {
        PainlessClassLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<? extends PainlessExecutable> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length).asSubclass(PainlessExecutable.class);
        }
    }

    static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        long start = System.currentTimeMillis();
        final PTypes ptypes = loadFromProperties();
        long end = System.currentTimeMillis() - start;
        System.out.println("types: " + end);
        start = System.currentTimeMillis();
        final ParseTree root = createParseTree(source, ptypes);
        end = System.currentTimeMillis() - start;
        System.out.println("tree: " + end);
        final Deque<PArgument> parguments = new ArrayDeque<>();
        parguments.add(new PArgument("this", getPTypeFromCanonicalPName(ptypes, "exec")));
        parguments.add(new PArgument("input", getPTypeFromCanonicalPName(ptypes, "smap")));

        start = System.currentTimeMillis();
        final Map<ParseTree, PMetadata> pmetadata = PainlessAnalyzer.analyze(ptypes, root, parguments);
        end = System.currentTimeMillis() - start;
        System.out.println("analyze: " + end);
        start = System.currentTimeMillis();
        final byte[] bytes = PainlessWriter.write(source, root, pmetadata);
        end = System.currentTimeMillis() - start;
        System.out.println("write: " + end);
        start = System.currentTimeMillis();
        final PainlessExecutable executable = createExecutable(name, source, parent, bytes);
        end = System.currentTimeMillis() - start;
        System.out.println("create: " + end);

        return executable;
    }

    private static ParseTree createParseTree(String source, PTypes ptypes) {
        final ANTLRInputStream stream = new ANTLRInputStream(source);
        final PainlessLexer lexer = new PainlessLexer(stream);
        final PainlessParser parser = new PainlessParser(new CommonTokenStream(lexer));

        parser.setTypes(ptypes.getPNames());

        return parser.source();
    }

    private static PainlessExecutable createExecutable(String name, String source, ClassLoader parent, byte[] bytes) {
        try {
            try {
                FileOutputStream f = new FileOutputStream(new File("/Users/jdconrad/lang/generated/out.class"), false);
                f.write(bytes);
                f.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final PainlessClassLoader loader = new PainlessClassLoader(parent);
            final Class<? extends PainlessExecutable> clazz = loader.define(PainlessWriter.CLASS_NAME, bytes);
            final Constructor<? extends PainlessExecutable> constructor =
                    clazz.getConstructor(String.class, String.class);

            return constructor.newInstance(name, source);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private PainlessCompiler() {}
}
