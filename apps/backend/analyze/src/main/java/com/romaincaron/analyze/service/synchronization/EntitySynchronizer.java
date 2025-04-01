package com.romaincaron.analyze.service.synchronization;

public interface EntitySynchronizer<T, DTO> {
    void synchronize(T entity, DTO dto);
}
