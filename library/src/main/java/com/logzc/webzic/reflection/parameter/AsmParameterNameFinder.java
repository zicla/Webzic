package com.logzc.webzic.reflection.parameter;

import com.logzc.webzic.exception.ZicException;
import com.logzc.webzic.reflection.AsmHelper;
import lombok.Getter;
import org.objectweb.asm.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * study the spirit org.apache.xbean.recipe.AsmParameterNameLoader
 * Created by lishuang on 2016/7/29.
 */
public class AsmParameterNameFinder implements ParameterNameFinder {

    //only cache a class's info.
    private Map<Constructor, List<String>> constructorCache = new HashMap<>();
    private Map<Method, List<String>> methodCache = new HashMap<>();


    @Override
    public List<String> get(Method method) {


        //check cache.
        if (methodCache.containsKey(method)) {
            return methodCache.get(method);
        }

        //no cache. Fill cache.
        findAllMethodParameters(method.getDeclaringClass());

        return methodCache.get(method);


    }

    @Override
    public List<String> get(Constructor constructor) {
        //check cache.
        if (constructorCache.containsKey(constructor)) {
            return constructorCache.get(constructor);
        }

        //no cache. Fill cache.
        findAllConstructorParameters(constructor.getDeclaringClass());

        return constructorCache.get(constructor);

    }

    public void findAllConstructorParameters(Class clazz) throws ZicException {

        Constructor[] constructors = clazz.getDeclaredConstructors();
        //constructorCache.clear();
        if (constructors.length == 0) {
            return;
        }


        //load parameter names using ASM.
        ClassReader reader = AsmHelper.createClassReader(clazz);
        ParameterNameClassVisitor parameterNameClassVisitor = new ParameterNameClassVisitor(constructors);

        //System.out.println("ready to accept." + System.currentTimeMillis());
        reader.accept(parameterNameClassVisitor, 0);
        //System.out.println("finish accept." + System.currentTimeMillis());
        constructorCache = parameterNameClassVisitor.getConstructorParameters();
    }

    public void findAllMethodParameters(Class clazz) throws ZicException {

        Method[] methods = clazz.getDeclaredMethods();

        //methodCache.clear();
        if (methods.length == 0) {
            return;
        }


        //load parameter names using ASM.
        ClassReader reader = AsmHelper.createClassReader(clazz);
        ParameterNameClassVisitor parameterNameClassVisitor = new ParameterNameClassVisitor(methods);


        //System.out.println("ready to accept." + System.currentTimeMillis());
        reader.accept(parameterNameClassVisitor, 0);
        //System.out.println("finish accept." + System.currentTimeMillis());

        methodCache = parameterNameClassVisitor.getMethodParameters();


    }


    private static class ParameterNameClassVisitor extends ClassVisitor {


        @Getter
        private final Map<Constructor, List<String>> constructorParameters = new HashMap<>();
        @Getter
        private final Map<Method, List<String>> methodParameters = new HashMap<>();

        private final Map<String, Exception> exceptions = new HashMap<>();


        private Map<String, Method> methodMap;
        private Map<String, Constructor> constructorMap;

        //special key.
        public String getKey(String name, String desc) {
            return name + desc;
        }

        //method way
        public ParameterNameClassVisitor(Method[] methods) {
            super(ASM5);

            methodMap = new HashMap<>();
            for (Method method : methods) {
                String key = getKey(method.getName(), Type.getMethodDescriptor(method));
                methodMap.put(key, method);
            }
        }

        //constructor way.
        public ParameterNameClassVisitor(Constructor[] constructors) {
            super(ASM5);

            constructorMap = new HashMap<>();
            for (Constructor constructor : constructors) {
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                Type[] types = new Type[parameterTypes.length];
                for (int j = 0; j < types.length; j++) {
                    types[j] = Type.getType(parameterTypes[j]);
                }

                //we cannot get the name by constructor.getName().
                String key = getKey("<init>", Type.getMethodDescriptor(Type.VOID_TYPE, types));
                constructorMap.put(key, constructor);
            }

        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

            final List<String> parameterNames;

            final boolean isStaticMethod;
            String key = getKey(name, desc);

            //constructor
            if (constructorMap != null) {

                Constructor constructor = constructorMap.get(key);

                if (constructor == null) {
                    return null;
                }
                Class<?>[] parameterTypes = constructor.getParameterTypes();
                parameterNames = new ArrayList<>(parameterTypes.length);
                parameterNames.addAll(Collections.<String>nCopies(parameterTypes.length, null));
                constructorParameters.put(constructor, parameterNames);

                isStaticMethod = false;


            }

            //method
            else {
                Method method = methodMap.get(key);
                if (method == null) {
                    return null;
                }
                Class<?>[] parameterTyps = method.getParameterTypes();

                parameterNames = new ArrayList<>(parameterTyps.length);

                //parameterNames.addAll(Collections.<String>nCopies(parameterTyps.length, null));
                methodParameters.put(method, parameterNames);
                isStaticMethod = Modifier.isStatic(method.getModifiers());

                //System.out.println();
                //System.out.println(method);

            }

            return new ParameterNameMethodVisitor(ASM5, isStaticMethod, parameterNames);
        }
    }

    private static class ParameterNameMethodVisitor extends MethodVisitor {

        int index;
        boolean isStaticMethod;
        List<String> parameterNames;

        public ParameterNameMethodVisitor(int api, boolean isStaticMethod, List<String> parameterNames) {
            super(api);

            this.index=0;
            this.isStaticMethod = isStaticMethod;
            this.parameterNames = parameterNames;

        }

        @Override
        public void visitLocalVariable(String name1, String desc1, String signature1, Label start, Label end, int slot) {

            //System.out.println("VisitLocalVariable:" + name1);
            if (isStaticMethod) {
                parameterNames.add(name1);
            }
            // for non-static the 0th arg is "this" so we need to offset by -1
            //Sometimes slot is 0 1 3 4 5 6 8 9 10 11 NOT continuous.
            else {

                if(index>0){
                    parameterNames.add(name1);
                }

            }

            index++;
        }
    }
}
