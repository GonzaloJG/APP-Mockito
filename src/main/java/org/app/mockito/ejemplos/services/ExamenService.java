package org.app.mockito.ejemplos.services;

import org.app.mockito.ejemplos.models.Examen;

import java.util.Optional;

public interface ExamenService {

    Optional<Examen> findExamenByName(String name);

    Examen findExamenPorNombreConPreguntas(String nombre);

    Examen guardar(Examen examen);
}
