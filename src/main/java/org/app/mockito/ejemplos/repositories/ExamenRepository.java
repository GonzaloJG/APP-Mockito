package org.app.mockito.ejemplos.repositories;

import org.app.mockito.ejemplos.models.Examen;

import java.util.List;

public interface ExamenRepository {

    List<Examen> findAll();

    Examen save(Examen examen);
}
