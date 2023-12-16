package me.matthewe.suggestion.io.utilities;

@FunctionalInterface
public interface Replacer<T> {
    T replace( T t);
}