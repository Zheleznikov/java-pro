package ru.otus.appcontainer;

import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Stream;

public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final Class<?> initialConfigClass;
    private Object configClassInstance;

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();


    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        this.initialConfigClass = initialConfigClass;
        processConfig();
    }

    private void processConfig() {
        checkConfigClass();
        checkComponentDuplicatesName();

        initConfigClass();
        initAppComponents();
    }

    private void checkConfigClass() {
        if (!initialConfigClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", initialConfigClass.getName()));
        }
    }

    private void checkComponentDuplicatesName() {
        List<String> names = Arrays.stream(initialConfigClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .map(m -> m.getAnnotation(AppComponent.class).name())
                .toList();

        if (names.size() != new HashSet<>(names).size()) {
            throw new IllegalArgumentException("Given methods have same names");
        }

    }

    private void initConfigClass() {
        try {
            configClassInstance = initialConfigClass.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new IllegalArgumentException("Could not create app class instance");
        }
    }

    private void initAppComponents() {
        Arrays.stream(initialConfigClass.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(AppComponent.class))
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(AppComponent.class).order()))
                .forEach(m -> {
                    try {
                        Object[] params = Arrays.stream(m.getParameterTypes())
                                .map(c -> appComponents.stream()
                                        .filter(component -> isClassAssignableFromAnother(c, component))
                                        .findFirst()
                                        .orElseThrow(() -> new IllegalArgumentException("Could not get non-existent component")))
                                .toArray();

                        Object component = m.invoke(configClassInstance, params);
                        String name = m.getAnnotation(AppComponent.class).name();

                        appComponents.add(component);
                        appComponentsByName.put(name, component);

                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new IllegalArgumentException("Could not invoke method with given params");
                    }
                });
    }


    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        Stream<Object> classes = appComponents.stream()
                .filter(c -> isClassAssignableFromAnother(componentClass, c));

        if (classes.count() > 1) {
            throw new IllegalArgumentException("Could not define which component is taken");
        }

        return (C) appComponents.stream()
                .filter(c -> isClassAssignableFromAnother(componentClass, c))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Could not get non-existent component"));
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        return Optional.ofNullable((C) appComponentsByName.get(componentName))
                .orElseThrow(() -> new IllegalArgumentException("Could not get component"));

    }

    private boolean isClassAssignableFromAnother(Class<?> c, Object obj) {
        return c.isAssignableFrom(obj.getClass());
    }


}
