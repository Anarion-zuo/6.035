package edu.mit.compilers.grammar.token;

import com.google.common.base.CaseFormat;

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
        } catch (ClassNotFoundException e) {
            tokenName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, tokenName);
            try {
                clazz = Class.forName("edu.mit.compilers.grammar.token.decaf." + tokenName);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        }
        return clazz;
    }

//    public Token make(String tokenName) throws ClassNotFoundException {
//        var clazz = Class.forName("edu.mit.compilers.grammar.token.decaf." + tokenName);
//        clazz.getConstructor();
//    }
}
