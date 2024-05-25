package org.app.mockito.ejemplos.repositories;

import java.util.List;

public interface PreguntaRepository {

    List<String> findByExamenId(Long id);

    void save(List<String> listaPreguntas);
}
