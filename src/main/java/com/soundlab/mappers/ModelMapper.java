package com.soundlab.mappers;

public interface ModelMapper<T, D> {
    D map(T source);
}
