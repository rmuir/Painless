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

import painless.PainlessAnalyzer.*;

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
        final PainlessTypes.PTypes ptypes = PainlessTypes.loadFromProperties();
        final ParseTree root = createParseTree(source);
        Deque<PArgument> parguments = new ArrayDeque<>();
        //arguments.push(new Argument("this", types.getATypeFromPType("void")));
        //arguments.push(new Argument("input", types.getATypeFromPType("map")));
        //Map<ParseTree, Metadata> metadata = PainlessAnalyzer.analyze(types, root, arguments);
        //PainlessAdapter adapter = new PainlessAdapter(root);
        //PainlessAnalyzer analyzer = new PainlessAnalyzer(root, adapter);
        //final byte[] bytes = PainlessWriter.write(source, tree);
        //final PainlessExecutable executable = createExecutable(name, source, parent, bytes);

        //return executable;
        return null;
    }

    private static ParseTree createParseTree(String source) {
        final ANTLRInputStream stream = new ANTLRInputStream(source);
        final PainlessLexer lexer = new PainlessLexer(stream);
        final PainlessParser parser = new PainlessParser(new CommonTokenStream(lexer));

        return parser.source();
    }

    private static PainlessExecutable createExecutable(String name, String source, ClassLoader parent, byte[] bytes) {
        try {
            try {
                FileOutputStream f = new FileOutputStream(new File("/Users/jdconrad/antlr/out.class"), false);
                f.write(bytes);
                f.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            final PainlessClassLoader loader = new PainlessClassLoader(parent);
            final Class<? extends PainlessExecutable> clazz = loader.define(PainlessWriter.CLASS_NAME, bytes);
            final Constructor<? extends PainlessExecutable> constructor = clazz.getConstructor(String.class, String.class);

            return constructor.newInstance(name, source);
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException(exception);
        }
    }

    private PainlessCompiler() {}
}
