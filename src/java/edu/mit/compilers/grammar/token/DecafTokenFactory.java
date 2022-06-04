package edu.mit.compilers.grammar.token;

import com.google.common.base.CaseFormat;
import edu.mit.compilers.grammar.token.decaf.DecafToken;
import edu.mit.compilers.grammar.token.decaf.ReservedWord;
import edu.mit.compilers.grammar.token.decaf.UndefinedTokenException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class DecafTokenFactory {
    private static DecafTokenFactory instance = new DecafTokenFactory();

    public static DecafTokenFactory getInstance() {
        return instance;
    }

    public DecafTokenFactory() {

    }

    private Class findClassByTokenName(String tokenName) {
        Class clazz = null;
        try {
            clazz = Class.forName("edu.mit.compilers.grammar.token.decaf." + tokenName);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            tokenName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tokenName);
            try {
                clazz = Class.forName("edu.mit.compilers.grammar.token.decaf." + tokenName);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        return clazz;
    }

    public DecafToken makeToken(String tokenName, String matchedText) throws UndefinedTokenException {
        if (DecafToken.isReservedWord(tokenName)) {
            return new ReservedWord(matchedText, tokenName);
        }
        var tokenClass = findClassByTokenName(tokenName);
        Constructor<?> tokenConstructor = null;
        try {
            tokenConstructor = tokenClass.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw new UndefinedTokenException(tokenName);
        }
        DecafToken token = null;
        try {
            token = (DecafToken) tokenConstructor.newInstance(matchedText);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        try {
            token.afterMatching();
        } catch (TokenMismatchException ignored) {

        }
        return token;
    }
}
