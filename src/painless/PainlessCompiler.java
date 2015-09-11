package painless;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.objectweb.asm.Type;

import painless.PainlessValidator.*;

final class PainlessCompiler {
    static PainlessExecutable compile(String name, String source, ClassLoader parent) {
        final ParseTree root = createParseTree(source);
        Deque<Argument> arguments = new ArrayDeque<>();
        arguments.push(new Argument("this", PainlessValidator.EXECUTABLE_TYPE));
        arguments.push(new Argument("input", PainlessValidator.MAP_TYPE));
        PainlessValidator.validate(root, arguments);
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
