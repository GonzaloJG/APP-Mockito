package org.app.mockito.ejemplos.repositories;

import org.app.mockito.ejemplos.Datos;
import org.app.mockito.ejemplos.models.Examen;

import java.util.*;

public class ExamenRepositoryImpl implements ExamenRepository{
    @Override
    public List<Examen> findAll() {
        System.out.println("ExamenRepositoryImpl.findAll");
        return Datos.EXAMENES;
        /*Arrays.asList(new Examen(5L, "Matematicas", null),
                            new Examen(6L, "Historia", null),
                            new Examen(7L, "Lenguaje", null));*/
    }

    @Override
    public Examen save(Examen examen) {
        System.out.println("ExamenRepositoryImpl.save");
        return Datos.EXAMEN;
    }
}
