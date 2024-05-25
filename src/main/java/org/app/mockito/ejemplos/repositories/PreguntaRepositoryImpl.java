package org.app.mockito.ejemplos.repositories;

import org.app.mockito.ejemplos.Datos;

import java.util.List;

public class PreguntaRepositoryImpl implements PreguntaRepository {

    @Override
    public List<String> findByExamenId(Long id) {
        System.out.println("PreguntaRepositoryImpl.findByExamenId");
        return Datos.PREGUNTAS;
    }

    @Override
    public void save(List<String> listaPreguntas) {
        System.out.println("PreguntaRepositoryImpl.save");
    }
}
